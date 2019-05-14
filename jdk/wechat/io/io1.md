# IO源码解析(1)--面向字节的IO流

> 本文基于JDK1.8

### 前言

有过IO流使用经验的同学们肯定有过疑惑，Java中有各种各样的"流"，它们具体都有哪些？相互之间有着怎样的关系呢？这么一块无论是日常工作还是面试都会经常被用到问到的知识应该如何学习？别急，从本篇开始，本栈将会持续分析JDK中的IO源码，和你一起学习Java中的IO。

JDK中的IO源码在java.io包下，在1.8的版本中，该目录下共有86个类(或接口)。整个IO体系的基础是以下4个类：

* 读写字节：InputStream和OutputStream
* 读写字符(Unicode)：Reader和Writer

在了解这几个类之前，我们先搞清楚Java中的字符与字节的关系。

#### Char占几个字节？

之前在String源码中分析过，JDK8中String类用的是char数组来存储字符值的，到JDK9才开始用byte数组来存储。这里有一个问题，在Java中的char究竟占几个字节？毕竟c++中的char只占一个字节。

对于char这个类型，在Java中是用来表示字符的，可是世界上有那么多字符，如果有一个char只用一个字节来表示，那么就意味着只能存储256种，显然不合适，而如果有两个字节，那么就可以存储65536种。这个数量符合大多数国家的语言字符的个数。于是Java团队默认使用unicode作为编码，一个char作为2个字节来存储。 
这里就有两个问题了：

- java的char一定会有两个字节吗? 
- 中文字符可以存储在char里面吗? 

先来回答第一个问题。char一定是两个字节吗?不是的，这个跟我们选用的字符编码有关，如果采用”ISO-8859-1”编码，那么一个char只会有一个字节。如果采用”UTF-8”或者“GB2312”、“GBK”等编码格式呢?这几种编码格式采用的是动态长度的，如果是英文字符，大家都是一个字节。如果是中文，”UTF-8”是三个字，而”GBK”和”GB2312”是两个字节。而对于”unicode”而言，无论如何都是两个字节。 
然后再回答第二个问题，对于一个char如果用”ISO-8859-1”来存储的话，肯定无法存储一个中文，而对于”UTF-8”、“GB2312”、“GBK”而言大多数中文字符是可以存储的。

### InputStream

我们来看一下类的继承图：

![](C:\Users\g00452792\Pictures\io\InputStream.png)

事实上，由于InputStream是整个继承体系中的顶层类，同时根据数据源的不同，每一种数据源都从InputStream派生了子类，导致了整个IO体系的庞大。但是，虽然整个继承体系很庞大，它还是有迹可循的。

做过关于IO操作的读者知道，我们很少单独使用哪个类来实现IO操作，平时都是几个类合起来使用，这其实体现了一种装饰器模式(*Decorator pattern*)。

例如，输入流在默认情况下是不被缓冲区缓存的，也就是说，每次请求读字节时都会使得操作系统再分发一个字节。相比之下，请求一个数据块并将其置于缓冲区会显得更加高效。如果我们想使用缓冲机制，以及用于文件的数据输入流，那么就需要使用下面这种相当恐怖的构造器序列：

```java
DataInputStream din = new DataInputStream(
							new BufferedInputStream(
                            	new FileInputStream("test.txt")));
```

这样确实显得很麻烦，但是却带来了极大的灵活性，健壮优美符合设计模式(~~最啰嗦的语言，没有之一~~)。不多说了，我们还是继续看源码吧:)

先看看InputStream的UML类图(+代表公有，-代表私有)：

![](C:\Users\g00452792\Pictures\io\InputStream类图.png)

##### read()

InputStream中的核心方法就是read方法，源码如下：

```java
public abstract int read() throws IOException; // 抽象方法，在子类中实现
public int read(byte b[]) throws IOException;
public int read(byte b[], int off, int len) throws IOException;
```

从源码角度，当我们调用`read(byte b[])`方法时，实际上是调用`read(byte b[], int off, int len)`，并将偏移量与读取长度设置为0。在真正被调用的`read(byte[], int, int)`中，会调用第一行的抽象`read()`方法，该抽象方法会被子类实现。

> Java Effective推荐：在一个函数的参数过多时，缩减函数参数并为其他参数指定默认值。这样有助于提升代码的可读性。在前端JavaScript以及TypeScript语言中，由于不支持重载函数，可以将其他的参数设置为可选参数，并为其设置默认值。

从使用者角度，我们调用read方法，并将从输入流中获取的字节序列存到传入的byte数组。read方法返回读入的字节数(可能小于len)，或者在遇到输入源结尾时返回-1。

##### long skip(long n)

在输入流中跳过n个字节，返回实际跳过的字节数(如果碰到输入流的结尾，则可能小于n)。

##### int available()

返回在不阻塞的情况下可获取的字节数(阻塞意味着当前线程将失去它对资源的占用)。

##### void close()

关闭这个输入流。InputStream唯一实现的接口就是Closeable。

##### void mark(int readlimit)

在输入流的当前位置打一个标记(并非所有的流都支持这个特性)。如果从输入流中已经读入的字节多于readlimit个，则这个流允许忽略这个标记。

##### void reset()

返回到最后一个标记，随后对read的调用将重新读入这些字节。如果当前没有任何标记，则这个流不被重置。

##### boolean markSupported()

如果这个流支持打标记，则返回true。

### OutputStream

我们来看一下OutputStream的继承图：

![](C:\Users\g00452792\Pictures\io\OutputStream.png)

OutputStream实现了Closeable和Flushable接口，代表着可关闭，可冲刷。我们再看看OutputStream的类图，里面一共有哪些方法：

![](C:\Users\g00452792\Pictures\io\OutputStream类图.png)

一共5个方法。

##### write()

我们也可以向输出流中写入字节，比如OutputStream中的write方法，源码如下：

```java
public abstract void write(int b) throws IOException;
public void write(byte b[]) throws IOException;
public void write(byte b[], int off, int len) throws IOException;
```

从源码角度，与输入流的read方法相同，提供只有一个参数的write方法，并将另外两个参数设置默认值为0。在真正被调用的`write(byte[], int off, int len)`方法会调用抽象方法`write(byte)`。

从使用者角度，我们调用write方法，将字节序列写入到输出流中。

##### void flush()

冲刷输出流，也就是将所有缓冲的数据发送到目的地

##### void close()

**冲刷**并关闭输出流。
