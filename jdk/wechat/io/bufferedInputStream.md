BufferedInputStream是缓冲输入流。继承体系为：

```java
  Object(java.io)
  -- InputStream(java.io)
    -- FilterInputStream(java.io)
      -- BufferedInputStream(java.io)
```

BufferedInputStream的作用是为另一个流添加一些功能，例如，提供"缓冲功能"以及支持"mark()标记"和"reset()重置方法"。

其本质上就是通过定义一个内部数组作为缓冲区来实现的。例如，在以下代码中：

```java
DataInputStream in = new DataInputStream
                      (new BufferedInputStream
                        (new FileInputStream("D:\\a.txt")));
```

我们给文件输入流提供了缓冲的功能，构造了一个Data输入流。

当我们调用in的read()方法时，BufferedInputStream会将a.txt文件转换的文件输入流的数据分批填入到缓冲区中。每当缓冲区中的数据被读完之后，输入流会再次填充数据缓冲区；如此反复，直到我们读完输入流数据位置。

## 成员变量分析

类中共8个成员变量，因篇幅问题，我们一段一段的看。

```java
// 默认的缓冲大小 8M
private static int DEFAULT_BUFFER_SIZE = 8192;

// 最大的buffer大小
private static int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

// 缓冲数组
protected volatile byte buf[];

// 缓冲数组的原子更新器
// 该成员变量与buf数组的volatile关键字共同组成了buf数组的原子更新功能实现。
// 即在多线程中操作BufferedInputStream对象时
// buf和bufUpdater都具有原子性(不同的线程访问到的数据都是相同的)
private static final
    AtomicReferenceFieldUpdater<BufferedInputStream, byte[]> bufUpdater =
    AtomicReferenceFieldUpdater.newUpdater
    (BufferedInputStream.class,  byte[].class, "buf");
```

BufferedInputStream通过

```java
buf = new byte[size];
```

来初始化缓冲区的大小，即buf.length就是整个类的缓冲大小。`bufUpdater`的作用主要是在多线程访问buf，即调用read()方法的时候，拿到的结果是一样的。

```java
// 当前缓冲区的有效字节数。
// 注意，这里是指缓冲区的有效字节数，而不是输入流中的有效字节数。
protected int count;

// 当前缓冲区的位置索引
// 注意，这里是指缓冲区的位置索引，而不是输入流中的位置索引。
protected int pos;

// 当前缓冲区的标记位置
// markpos和reset()配合使用才有意义.操作步骤：
// 1.通过mark() 函数，保存pos的值到markpos中。
// 2.通过reset() 函数，会将pos的值重置为markpos。接着通过read()读取数据时，就会从mark()保存的位置开始读取。
protected int markpos = -1;

// marklimit是标记的最大值。
// 当我们调用mark(int readlimit)时，就会赋予以当前pos到marklimit为首尾的这段buffer重复读的功能
protected int marklimit;
```

这块代码，定义了三个位置相关的变量。我们可以用下面这幅图来理解

![1558494406139](C:\Users\g00452792\AppData\Roaming\Typora\typora-user-images\1558494406139.png)

我们知道调用无参的read()方法，会返回一个字节。pos就是代表着要被读取的下一个字节的位置，count代表着当前buffer有效的字节的尾端。pos总是小于等于count的。图中所示的n代表这个buffer还有n个字节可以被读取，每次读取，pos++，当n <= 0，即pos >= count，代表着当前buffer已被读完。此时有两种情况：

* 从数据源再读一段buffer填充在count尾端，并右移count。
* 剩下的空间放不下从数据源新读的buffer了，将pos置0，count等于新读进来的buffer的长度。

markpos默认初始化为-1，代表未开始标记功能，当调用mark(int)方法之后，markpos 就会等于当前的pos值。当我们继续读取字节，pos增加，调用reset()方法时pos会等于之前的markpos值，实现重复读的功能。marklimit等于mark方法的参数readlimit，代表重复读的右边界。

## 函数分析

```java
// 获取输入流
private InputStream getInIfOpen() throws IOException {
    InputStream input = in;
    if (input == null)
        throw new IOException("Stream closed");
    return input;
}

// 获取缓冲区
private byte[] getBufIfOpen() throws IOException {
    byte[] buffer = buf;
    if (buffer == null)
        throw new IOException("Stream closed");
    return buffer;
}

// 构造函数，默认初始化一个大小为8M的缓冲区
public BufferedInputStream(InputStream in) {
    this(in, DEFAULT_BUFFER_SIZE);
}

// 构造函数，构造一个指定大小的缓冲区。
public BufferedInputStream(InputStream in, int size) {
    super(in);
    if (size <= 0) {
        throw new IllegalArgumentException("Buffer size <= 0");
    }
    buf = new byte[size];
}
```

上面这块没什么好说的。我们看下面比较关键的fill方法与read方法：

```java
private void fill() throws IOException {
    byte[] buffer = getBufIfOpen();
    if (markpos < 0)
        pos = 0;            /* no mark: throw away the buffer */
    else if (pos >= buffer.length)  /* no room left in buffer */
        if (markpos > 0) {  /* can throw away early part of the buffer */
            int sz = pos - markpos;
            System.arraycopy(buffer, markpos, buffer, 0, sz);
            pos = sz;
            markpos = 0;
        } else if (buffer.length >= marklimit) {
            markpos = -1;   /* buffer got too big, invalidate mark */
            pos = 0;        /* drop buffer contents */
        } else if (buffer.length >= MAX_BUFFER_SIZE) {
            throw new OutOfMemoryError("Required array size too large");
        } else {            /* grow buffer */
            int nsz = (pos <= MAX_BUFFER_SIZE - pos) ?
                pos * 2 : MAX_BUFFER_SIZE;
            if (nsz > marklimit)
                nsz = marklimit;
            byte nbuf[] = new byte[nsz];
            System.arraycopy(buffer, 0, nbuf, 0, pos);
            if (!bufUpdater.compareAndSet(this, buffer, nbuf)) {
                // Can't replace buf if there was an async close.
                // Note: This would need to be changed if fill()
                // is ever made accessible to multiple threads.
                // But for now, the only way CAS can fail is via close.
                // assert buf == null;
                throw new IOException("Stream closed");
            }
            buffer = nbuf;
        }
    count = pos;
    int n = getInIfOpen().read(buffer, pos, buffer.length - pos);
    if (n > 0)
        count = n + pos;
}

public synchronized int read() throws IOException {
    if (pos >= count) {
        fill();
        if (pos >= count)
            return -1;
    }
    return getBufIfOpen()[pos++] & 0xff;
}
```

如果我们调用读一个字节的无参的read()方法时，首先判断pos是否大于等于count，即buffer是否读完了。如果读完了，就填充，按照我们上面分析，填充有两种情况，在尾端添加新数据或者重置当前的缓冲区。

因此在fill()方法中：

* 如果未开启标记功能，读buffer.length - pos的长度，把缓冲区buf尽量读满（实际上读的大小可能小于这个长度）。
* 如果开启标记功能，且pos还小于buffer.length时，同上，尽量读满缓冲区。
* 如果开启标记功能，且pos大于等于buffer.length了，没有多余空间了。这里又有4种情况：（1）如果markpos不是标记起始位置，为了重复读的功能不受影响，必须保存从markpos到pos的这一段buffer，就把这段buffer往左移到以0开始的起始位置去，这样右边就有空间继续读了。（2）如果当前缓冲的长度大于marklimit了，那么pos肯定也大于marklimit了，此时属于无效mark，关闭标记功能并另pos=0。（3）buffer的大小超过了允许大小，抛出OutOfMemoryError。（4）取2倍pos，marklimit以及MAX_BUFFER_SIZE三者中最小的数为size，创建一个byte数组，并将当前缓冲区的所有数据拷贝到这个扩大后的数组中，另这个扩大后的数组为当前类的缓冲区。

再看另外两个read方法，这两个read方法就不是读一个字节了，而是将流中的数组写入到传进来的数组b中：

```java
// 将缓冲区的数据写入到数组b中，off是b的起始位置，len是写入长度
private int read1(byte[] b, int off, int len) throws IOException {
    int avail = count - pos; // 当前缓冲区剩余可写字节数
    if (avail <= 0) {
        //加速机制。如果写入的长度大于缓冲区的长度并且没有标记，直接从数据源读。
        if (len >= getBufIfOpen().length && markpos < 0) {
            return getInIfOpen().read(b, off, len);
        }
        fill();
        avail = count - pos;
        if (avail <= 0) return -1;
    }
    int cnt = (avail < len) ? avail : len;
    System.arraycopy(getBufIfOpen(), pos, b, off, cnt);
    pos += cnt;
    return cnt;
}
```

read1方法中，如果当前缓冲区还有空间，取剩余空间与写入byte数组的长度中较小的一方为写入到byte数组的字节长度。并将pos右移该长度。

如果当前缓冲区没有数据可写了，需要判断是否能用加速机制。我们先做一个计算题：

* 如果要写入byte数组的长度len大于缓冲区的长度buffer.length，那么我们需要做 **(len / buffer.length) + 1** 次的 从数据源读字节到缓冲区 -- 读取缓冲区全部数据 -- 清空缓冲区 的操作。
* 如果直接从数据源读，则只要读一次。

如果不能加速，代表这次要写入byte数组的长度len小于buffer.length。那么通过fill函数，要不就给buf数据扩容，要不就因为标记的位置无效了，而重置buf。

```java
// 将缓冲区的数据写入到数组b中，off是b的起始位置，len是写入长度
public synchronized int read(byte b[], int off, int len)
    throws IOException
{
    getBufIfOpen(); // Check for closed stream
    if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
        throw new IndexOutOfBoundsException();
    } else if (len == 0) {
        return 0;
    }

    int n = 0;
    for (;;) {
        int nread = read1(b, off + n, len - n);
        if (nread <= 0)
            return (n == 0) ? nread : n;
        n += nread;
        if (n >= len)
            return n;
        // if not closed but no bytes available, return
        InputStream input = in;
        if (input != null && input.available() <= 0)
            return n;
    }
}
```

read函数首先检查buf是否为null，然后判断off和len的范围。用n来计算总共读到的字节数，因为已经判断过len是为0了，如果n==0，代表nread读不到字节，返回-1。否则就是一直循环，直到n大于等于len为止。

```java
public synchronized long skip(long n) throws IOException {
    getBufIfOpen(); // Check for closed stream
    if (n <= 0) {
        return 0;
    }
    long avail = count - pos;

    if (avail <= 0) {
        // If no mark position set then don't keep in buffer
        if (markpos <0)
            return getInIfOpen().skip(n);

        // Fill in buffer to save bytes for reset
        fill();
        avail = count - pos;
        if (avail <= 0)
            return 0;
    }

    long skipped = (avail < n) ? avail : n;
    pos += skipped;
    return skipped;
}
```

skip函数用来跳过一定数量字节。令当前缓冲区的未写出字节数为avail，如果avail小于等于0：

* 无标记，直接在源输入流跳过n个字节。
* 有标记，将被标记的那段缓存往左移动首处，然后重新计算avail，如果仍然小于等于0，则返回0，代表跳过了0个字节

如果avail大于0，跳过n与avail中较小的数量的字节，返回这个较小的数。

```java
public synchronized int available() throws IOException {
    int n = count - pos;
    int avail = getInIfOpen().available();
    return n > (Integer.MAX_VALUE - avail)
                ? Integer.MAX_VALUE
                : n + avail;
}
```

available()函数返回还有多少字节可读。n代表当前缓冲区的可读字节数，avail代表数据源的可读字节数。返回 (n + avail) 与 MAX_VALUE 中较小的一方。

```java
public synchronized void mark(int readlimit) {
    marklimit = readlimit;
    markpos = pos;
}

public synchronized void reset() throws IOException {
    getBufIfOpen(); // Cause exception if closed
    if (markpos < 0)
        throw new IOException("Resetting to invalid mark");
    pos = markpos;
}

public boolean markSupported() {
    return true;
}
```

mark()标记函数，reset()为重置函数。markSupported()返回true表示当前类支持mark。

```java
public void close() throws IOException {
    byte[] buffer;
    while ( (buffer = buf) != null) {
        if (bufUpdater.compareAndSet(this, buffer, null)) {
            InputStream input = in;
            in = null;
            if (input != null)
                input.close();
            return;
        }
        // Else retry in case a new buf was CASed in fill()
    }
}
```

关闭输入流。

## 实例：

```java
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.SecurityException;
 
/**
 * BufferedInputStream 测试程序
 *
 * @author skywang
 */
public class BufferedInputStreamTest {
 
  private static final int LEN = 5;
 
  public static void main(String[] args) {
    testBufferedInputStream() ;
  }
 
  /**
   * BufferedInputStream的API测试函数
   */
  private static void testBufferedInputStream() {
 
    // 创建BufferedInputStream字节流，内容是ArrayLetters数组
    try {
      File file = new File("bufferedinputstream.txt");
      InputStream in =
         new BufferedInputStream(
           new FileInputStream(file), 512);
 
      // 从字节流中读取5个字节。“abcde”，a对应0x61，b对应0x62，依次类推...
      for (int i=0; i<LEN; i++) {
        // 若能继续读取下一个字节，则读取下一个字节
        if (in.available() >= 0) {
          // 读取“字节流的下一个字节”
          int tmp = in.read();
          System.out.printf("%d : 0x%s\n", i, Integer.toHexString(tmp));
        }
      }
 
      // 若“该字节流”不支持标记功能，则直接退出
      if (!in.markSupported()) {
        System.out.println("make not supported!");
        return ;
      }
        
      // 标记“当前索引位置”，即标记第6个位置的元素--“f”
      // 1024对应marklimit
      in.mark(1024);
 
      // 跳过22个字节。
      in.skip(22);
 
      // 读取5个字节
      byte[] buf = new byte[LEN];
      in.read(buf, 0, LEN);
      // 将buf转换为String字符串。
      String str1 = new String(buf);
      System.out.printf("str1=%s\n", str1);
 
      // 重置“输入流的索引”为mark()所标记的位置，即重置到“f”处。
      in.reset();
      // 从“重置后的字节流”中读取5个字节到buf中。即读取“fghij”
      in.read(buf, 0, LEN);
      // 将buf转换为String字符串。
      String str2 = new String(buf);
      System.out.printf("str2=%s\n", str2);
 
      in.close();
    } catch (FileNotFoundException | SecurityException | IOException e) {
      e.printStackTrace();
    }
  }
}
```

程序中读取的bufferedinputstream.txt的内容如下：

```
abcdefghijklmnopqrstuvwxyz
0123456789
ABCDEFGHIJKLMNOPQRSTUVWXYZ
```

运行结果：

```java
0 : 0x61
1 : 0x62
2 : 0x63
3 : 0x64
4 : 0x65
str1=01234
str2=fghij
```



