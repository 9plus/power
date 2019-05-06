>  本文基于JDK1.8

本篇文章主要是关于String类的内部方法的分析，目录如下：

- 构造函数分析
- “比较”方法
- String对“+”的重载

### 构造函数分析

#### 1.默认构造函数

```java
public String() {
   this.value = "".value;
}
```

该构造方法会创建空的字符序列，注意这个构造方法的使用，因为创造不必要的字符串对象是不可变的。因此不建议采取下面的创建 String 对象：

```java
String str = new String()
str = "sample";
```

这样的结果显而易见，会产生了不必要的对象。

#### 2.使用字符串类型的对象来初始化

```java
public String(String original){
  this.value = original.value;
  this.hash = original.hash;
}
```

这里将直接将源 String 中的 value 和 hash 两个属性直接赋值给目标 String。因为 String 一旦定义之后是不可以改变的，所以也就不用担心改变源 String 的值会影响到目标 String 的值。

#### 3.使用字符数组来构造

```java
public String(char value[]){
    ...
}
public String(char value[], int offset, int count){
    ...
}
```

这里值得注意的是：当我们使用字符数组创建 String 的时候，会用到`Arrays.copyOf`方法或`Arrays.copyOfRange`方法。这两个方法是将原有的字符数组中的内容逐一的复制到 String 中的字符数组中。会创建一个新的字符串对象，随后修改的字符数组不影响新创建的字符串。

#### 4.使用字节数组来构建String

在 Java 中，String 实例中保存有一个 char[] 字符数组，char[] 字符数组是以 unicode 码来存储的，String 和 char 为内存形式。

byte 是网络传输或存储的序列化形式，所以在很多传输和存储的过程中需要将 byte[] 数组和 String 进行相互转化。所以 String 提供了一系列重载的构造方法来将一个字符数组转化成 String，提到 byte[] 和 String 之间的相互转换就不得不关注编码问题。

```java
String(byte[] bytes,Charset charset);
```

该构造方法是指通过 charset 来解码指定的 byte 数组，将其解码成 unicode 的 char[] 数组，构造成新的 String。

这里的 bytes 字节流是使用 charset 进行编码的，想要将他转换成 unicode 的 char[] 数组，而又保证不出现乱码，那就要指定其解码方式

同样的，使用字节数组来构造 String 也有很多种形式，按照是否指定解码方式分的话可以分为两种：

```java
public String(byte bytes[]){
  this(bytes, 0, bytes.length);
}
public String(byte bytes[], int offset, int length){
    checkBounds(bytes, offset, length);
    this.value = StringCoding.decode(bytes, offset, length);
}
```

如果我们在使用 byte[] 构造 String 的时候，使用的是下面这四种构造方法（带有 charsetName 或者 charset 参数）的一种的话，那么就会使用`StringCoding.decode()`方法进行解码，使用的解码的字符集就是我们指定的 charsetName 或者 charset。

```java
String(byte bytes[]);
String(byte bytes[], int offset, int length);
String(byte bytes[], Charset charset);
String(byte bytes[], String charsetName);
String(byte bytes[], int offset, int length, Charset charset);
String(byte bytes[], int offset, int length, String charsetName);
```

我们在使用 byte[] 构造 String 的时候，如果没有指明解码使用的字符集的话，那么 StringCoding 的 decode 方法首先调用系统的默认编码格式，如果没有指定编码格式则默认使用 ISO-8859-1 编码格式进行编码操作。这里就不展示代码了，有兴趣的同学可以去源码中搜索关键字"ISO-8859-1"。

#### 5.一个特殊的保护类型的构造方法

String 除了提供了很多公有的供程序员使用的构造方法以外，还提供了一个保护类型的构造方法（Java 7），我们看一下他是怎么样的：

```java
String(char[] value,boolean share) {
    // assert share : "unshared not supported";
    this.value = value;
}
```

从代码中我们可以看出，该方法和 String(char[] value) 有两点区别：

- 第一个区别：该方法多了一个参数：boolean share，其实这个参数在方法体中根本没被使用。注释说目前不支持 false，只使用 true。那可以断定，加入这个 share 的只是为了区分于 String(char[] value) 方法，不加这个参数就没办法定义这个函数，只有参数是不同才能进行重载。
- 第二个区别：具体的方法实现不同。我们前面提到过 String(char[] value) 方法在创建 String 的时候会用到 Arrays 的 copyOf 方法将 value 中的内容逐一复制到 String 当中，而这个 String(char[] value, boolean share) 方法则是直接将 value 的引用赋值给 String 的 value。那么也就是说，这个方法构造出来的 String 和参数传过来的 char[] value 共享同一个数组。

为什么 Java 会提供这样一个方法呢？

- **性能好**：这个很简单，一个是直接给数组赋值（相当于直接将 String 的 value 的指针指向char[]数组），一个是逐一拷贝，当然是直接赋值快了。
- **节约内存**：该方法之所以设置为 protected，是因为一旦该方法设置为公有，在外面可以访问的话，如果构造方法没有对 arr 进行拷贝，那么其他人就可以在字符串外部修改该数组，由于它们引用的是同一个数组，因此对 arr 的修改就相当于修改了字符串，那就破坏了字符串的不可变性。
- **安全的**：对于调用他的方法来说，由于无论是原字符串还是新字符串，其 value 数组本身都是 String 对象的私有属性，从外部是无法访问的，因此对两个字符串来说都很安全。

#### 6.Java7加入的新特性

在 Java 7 之前有很多 String 里面的方法都使用上面说的那种“性能好的、节约内存的、安全”的构造函数。 比如：substringreplaceconcatvalueOf等方法。

实际上它们使用的是 public String(char[], ture) 方法来实现。

但是在 Java 7 中，substring 已经不再使用这种“优秀”的方法了

为什么呢？ 虽然这种方法有很多优点，但是他有一个致命的缺点，对于 sun 公司的程序员来说是一个零容忍的 bug，那就是他很有可能造成内存泄露。

看一个例子，假设一个方法从某个地方（文件、数据库或网络）取得了一个很长的字符串，然后对其进行解析并提取其中的一小段内容，这种情况经常发生在网页抓取或进行日志分析的时候。

下面是示例代码：

```java
1String aLongString = "...averylongstring...";
2String aPart = aLongString.substring(20, 40);
3return aPart;
```

在这里 aLongString 只是临时的，真正有用的是 aPart，其长度只有 20 个字符，但是它的内部数组却是从 aLongString 那里共享的，因此虽然 aLongString 本身可以被回收，但它的内部数组却不能释放。这就导致了内存泄漏。如果一个程序中这种情况经常发生有可能会导致严重的后果，如内存溢出，或性能下降。

新的实现虽然损失了性能，而且浪费了一些存储空间，但却保证了字符串的内部数组可以和字符串对象一起被回收，从而防止发生内存泄漏，因此新的 substring 比原来的更健壮。

### “比较”方法

```java
boolean equals(Object anObject); // 比较对象
boolean contentEquals(StringBuffer sb);
boolean equals(Object anObject); // 比较对象
boolean contentEquals(StringBuffer sb); // 与字符串比较内容
boolean contentEquals(Char Sequencecs); // 与字符比较内容
boolean equalsIgnoreCase(String anotherString); // 忽略大小写比较字符串对象
int compareTo(String anotherString); // 比较字符串
int compareToIgnoreCase(String str); // 忽略大小写比较字符串
boolean regionMatches(int toffset,String other,
                     into offset,int len); // 局部匹配
boolean regionMatches(boolean ignoreCase,int toffset,匹配
                     String other,into offset,
                     int len); // 可忽略大小写局部
```

字符串有一系列方法用于比较两个字符串的关系。 前四个返回 boolean 的方法很容易理解，前三个比较就是比较 String 和要比较的目标对象的字符数组的内容，一样就返回 true, 不一样就返回false，核心代码如下：

```java
int n = value.length; 
while (n-- ! = 0) {
  if (v1[i] != v2[i])
    return false;
    i++;
}
```

v1,v2分别代表 String 的字符数组和目标对象的字符数组。 第四个和前三个唯一的区别就是他会将两个字符数组的内容都使用 toUpperCase 方法转换成大写再进行比较，以此来忽略大小写进行比较。相同则返回 true，不想同则返回 false。

### String对"+"的重载

我们知道，Java 是不支持重载运算符，String 的 “+” 是 java 中唯一的一个重载运算符，那么 java 使如何实现这个加号的呢？我们先看一段代码：

```java
public static void main(String[] args) {
     String string = "hello";
     String string2 = string + "world";
}
```

然后我们将这段代码的实际执行情况贴出来看看：

```java
public static void main(String args[]){
     String string = "hollo";
     String string2 = (new StringBuilder(String.valueOf(string))).append("world").toString();
}
```

看了反编译之后的代码我们发现，其实 String 对 “+” 的支持其实就是使用了 StringBuilder 以及他的 append、toString 两个方法。

### 小结

- 一旦 String 对象在内存(堆)中被创建出来，就无法被修改。特别要注意的是，String 类的所有方法都没有改变字符串本身的值，都是返回了一个新的对象。
- 如果你需要一个可修改的字符串，应该使用 StringBuffer 或者StringBuilder。否则会有大量时间浪费在垃圾回收上，因为每次试图修改都有新的String 对象被创建出来。
- 如果你只需要创建一个字符串，你可以使用双引号的方式，如果你需要在堆中创建一个新的对象，你可以选择构造函数的方式。

以上分析的方法只是String中的一部分，其实这么多Api光靠记是记不住的，用到的时候再看，熟练了自然就记住了。