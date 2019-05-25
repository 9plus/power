> 本文基于JDK1.8，首发于公众号：**Plus技术栈**

缓冲输出流BufferedOutputStream是与缓冲输入流BufferedInputStream相对应的面向字节的IO类。该类可以用write进行写出字节，并调用flush冲刷掉残留在缓存区的字节。

BufferedOutputStream的继承体系为：

```java
Object (java.lang)
  -- OutputStream (java.io)
    --FilterOutputStream (java.io)
      --BufferedOutputStream (java.io)
```

相比较缓冲输入流而言，缓冲输出流的方法少很多。我们一起来看看吧。

## 成员变量分析

内部共两个成员：

```java
protected byte buf[];
protected int count;
```

![成员变量图](/Users/gyx/Desktop/公众号图片/io/成员变量图.png)

可以从图看出来，buf代表缓冲区，count代表缓冲区中可写出的字节数。

## 方法分析

先看构造函数

```java
public BufferedOutputStream(OutputStream out) {
    this(out, 8192);
}

public BufferedOutputStream(OutputStream out, int size) {
    super(out);
    if (size <= 0) {
        throw new IllegalArgumentException("Buffer size <= 0");
    }
    buf = new byte[size];
}
```

默认初始化缓冲区大小为8M，与缓冲输入流一致。如果指定了大小，则按指定大小初始化。

除构造函数之外，BufferedOutputStream的方法可以分为**写**与**冲刷**。

我们先看写：

```java
public synchronized void write(int b) throws IOException {
    if (count >= buf.length) {
        flushBuffer();
    }
    buf[count++] = (byte)b;
}
```

此函数作用为写出一个字节，首先判断缓冲区是否超出(溢出)，如果超出，则调用`flushBuffer()`，将缓冲区的buffer写出去，并将count置0。如果未超出，在buf中存下当前字节，并右移count。

```java
public synchronized void write(byte b[], int off, int len) throws IOException {
    if (len >= buf.length) {
        /* If the request length exceeds the size of the output buffer,
               flush the output buffer and then write the data directly.
               In this way buffered streams will cascade harmlessly. */
        flushBuffer();
        out.write(b, off, len);
        return;
    }
    if (len > buf.length - count) {
        flushBuffer();
    }
    System.arraycopy(b, off, buf, count, len);
    count += len;
}
```

还有一个从数组往外写的write方法，与上篇文章缓冲输入流的`read1()`一样，这里也存在加速机制。

* 首先判断要写的长度是否超出buf的长度，如果超出了，直接把已经缓存过的字节冲刷掉，并直接向output写字节数组b中的内容。
* 如果不能用加速机制，就判断字节数组要写出的长度len + count 是否大于buf的长度，大于了就先冲刷掉当前缓存区中的数据，然后把字节数组里面的内容放到缓冲区中。

`System.arraycopy(b, off, buf, count, len)`就是将字节数组b中从off开始长度为len的数组拷贝到buf从count开始处。

上述的两个写方法都进行了：

* 判断当前内部缓冲区空间
* 空间不够则冲刷掉内存缓冲区数据

的操作，这个好理解，但是都是调用的私有的`flushBuffer()`冲刷内部缓存，那么作为用户来说，提供给我们调用的公有的`flush()`又有什么用呢？

![bufferedOutput](/Users/gyx/Desktop/公众号图片/io/bufferedOutput.jpeg)

看图说话，当我们为了效率更高而使用缓冲输出流时，向外写出了一部分字节，但是可能存在BufferedOutputStream内部的buffer未满而一直等待我们继续写出，在这里有一个**常见的误区**：

网上有种说法是为了避免丢失字节，我们在每次退出或者close输出流的时候都要调用flush。

事实上，此时如果我们close掉输出流，close会再调用一次flush方法，将结果全部输出。只有当往文件或者网络中写出字节，同时没有关闭输出流，才会导致字节残留在输出流的缓存区中。我们将在后面进行测试。

```java
public synchronized void flush() throws IOException {
    flushBuffer();
    out.flush();
}
```

flush内部首先将内部缓冲区冲刷掉，同时由于Java的IO流是装饰器模式，调用继承自装饰类FilterOutputStream的out变量的flush方法，将其他也实现了缓冲功能的类也冲刷掉。

```java
private void flushBuffer() throws IOException {
    if (count > 0) {
        out.write(buf, 0, count);
        count = 0;
    }
}
```

内部flush的逻辑很简单，将buf的字节全部写出，同时另count为0。

## 测试

我们主要测试一下flush的功能，我们初始化缓冲区大小为5个字节。(注：最后看结果时用的vim打开，idea似乎识别有问题)

**不调用flush()，调用close()**

```java
public class BufferedOutputStreamTest {
    public static void main(String[] args) {
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream("src/test.txt"), 5);
            outputStream.write(0x01);
//            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

输出为：`^A`，可见这个字节输出了。

**不调用flush()，不调用close()**

```java
public class BufferedOutputStreamTest {
    public static void main(String[] args) {
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream("src/test.txt"), 5);
            outputStream.write(0x01);
//            outputStream.flush();
//            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

输出为：空，可见字节丢失了。

补充：如果你测试的时候定义了一个长度大于5的字节数组，缓冲输出流可能直接用加速机制全部写出了。



