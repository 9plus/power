# Java IO框架

在正式进入Java的IO体系学习之前，先大致浏览一下整个IO的框架

### 面向字节的输入流



可以看到：

* InputStream 是以字节为单位的输入流的超类。InputStream提供了read()接口从输入流中读取字节数据。
* AudioInputStream 是字节音频输入流。它是具有指定音频格式和长度的输入流。
* ByteArrayInputStream 是字节数组输入流。它包含一个内部缓冲区，该缓冲区包含从流中读取的字节；通俗点说，它的内部缓冲区就是一个字节数组，而ByteArrayInputStream本质就是通过字节数组来实现的。
* FileInputStream 是文件输入流。它通常用于对文件进行读取操作。
* PipedInputStream 是管道输入流，它和PipedOutputStream一起使用，能实现多线程间的管道通信。
* FilterInputStream 是过滤输入流。它是DataInputStream和BufferedInputStream的超类。
* SequenceInputStream 用在多个流的合并，表示其他输入流的逻辑串联，从第一个流起点读取，直到末尾，接着读取第二个流，依此类推，直到最后一个输入流的末尾。
* StringBufferInputStream 是字符串输入流，类似于ByteArrayInputStream，它只需要一个字符串就能创建。
* ObjectStream 是对象输入流。它和ObjectOutputStream一起，用来提供对“基本数据或对象”的持久存储。

### 面向字节的输出流

可以看到：

* OutputStream 是以字节为单位的输出流的超类。OutputStream提供了write()接口从输出流中读取字节数据。
* ByteArrayOutputStream 是字节数组输出流。写入ByteArrayOutputStream的数据被写入一个 byte 数组。缓冲区会随着数据的不断写入而自动增长。可使用 toByteArray() 和 toString() 获取数据
* PipedOutputStream 是管道输出流，它和PipedInputStream一起使用，能实现多线程间的管道通道
* FilterOutputStream 是过滤输出流。它是DataOutputStream，BufferedOutputStream和PrintStream的超类。
* FileOutputStream 是文件输出流。它通常用于向文件进行写入操作。
* ObjectOutputStream 是对象输出流。它和ObjectInputStream一起，用来提供对“基本数据或对象”的持久存储。

### 面向字符的输入流

可以看到

* Reader 是以字符为单位的输入流的超类。它提供了read()接口来取字符数据。
* BufferedReader 是字符缓冲输入流。它的作用是为另一个输入流添加缓冲功能。
* LineNumberReader 是行输入流，可以读取第M行到第N行的字符。
* CharArrayReader 是字符数组输入流。它用于读取字符数组，它继承于Reader。操作的数据是以字符为单位！
* FilterReader 是字符类型的过滤输入流。
* PushbackReader 是推回数据流，当你读取的字符流不是你想要的，你可以调用它的unread方法把它推回去。
* InputStreamReader 是字节转字符的输入流。它是字节流通向字符流的桥梁：它使用指定的 charset 读取字节并将其解码为字符。
* FileReader 是字符类型的文件输入流。它通常用于对文件进行读取操作。
* PipedReader 是字符类型的管道输入流。它和PipedWriter一起是可以通过管道进行线程间的通讯。在使用管道通信时，必须将PipedWriter和PipedReader配套使用。
* StringReader 是字符类型的字符串输入流。用于读取字符串。

### 面向字符的输入流

可以看到：

* Writer 是以字符为单位的输出流的超类。它提供了write()接口往其中写入数据。
* BufferedWriter 是字符缓冲输出流。它的作用是为另一个输出流添加缓冲功能。
* CharArrayWriter 是字符数组输出流。它用于读取字符数组，它继承于Writer。操作的数据是以字符为单位。
* FilterWriter 是字符类型的过滤输出流。
* OutputStreamWriter 是字节转字符的输出流。它是字节流通向字符流的桥梁：它使用指定的 charset 将字节转换为字符并写入。
* FileWriter 是字符类型的文件输出流。它通常用于对文件进行读取操作。
* PipedWriter 是字符类型的管道输出流。它和PipedReader一起是可以通过管道进行线程间的通讯。在使用管道通信时，必须将PipedWriter和PipedWriter配套使用。
* PrintWriter 是字符类型的打印输出流。它是用来装饰其它输出流，能为其他输出流添加了功能，使它们能够方便地打印各种数据值表示形式。
* StringReader 是字符类型的字符串输出流。用于写出字符串。



在认识了Java IO的总结框架之后。我们进入各个类，学习他们的源码，作用与用法。

