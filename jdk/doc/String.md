> 本文基于JDK1.8

上篇文章学习了下String中的哈希值的作用，本篇开始正式进入String类。文章将按以下几块展开。

- 类的整体结构
- 成员变量
- 内部方法

话不多说，进入正题。

## 类的整体结构

String作为Java最基本最常用的类，我们应当对其内部实现有一个清晰的了解。先看String类的定义：

```java
public final class String
	implements java.io.Serializable, Comparable<String>, CharSequence {
	
	...
}
```

String类实现了Serializable可序列接口，Comparable可比较接口，CharSequence包含一些字符处理方法的接口，这些暂且不管。

我们仔细聊聊**final**这个关键字。

### final修饰有什么用？

emmm，final可以修饰哪些呢？final可以修饰类，成员变量，方法。

被final修饰的类不可被继承，被final修饰的成员变量不可变，被final修饰的方法子类无法覆盖(重写)。

也就说String类是无法被继承的。

### String类为什么要设计成不可继承？

将方法或类声明为final主要目的是：确保它们不会在子类中改变语义。String基本约定中最重要的一条是immutable(不可变性)，假如String是可继承的，那么你的StringChild类就有可能被复写为mutable(可变)的，这样就打破了成为共识的基本约定。

简单说来，整个JDK体系中依赖了String的不可变性，不可继承就是为了守护不可变性。

但是这里仅仅是把类型声明为了final，这不足以保证String类的不可变性。String不可变，关键是因为SUN公司的工程师，在后面所有String的方法里很小心的没有去动String类中用来存字符的array里的元素，没有暴露内部成员字段。通过底层的实现 + 声明时的final的双重保证实现了不可变性。

### String类的不可变性有什么好处？

不可变对象，顾名思义就是创建后不可以改变的对象。请看如下代码：

```java
String s = "ABC"; 
s.toLowerCase();
```

toLowerCase()并没有改变“ABC”的值，而是创建了一个新的String类”abc”,然后将新的实例的指向变量s。

相对于可变对象，不可变对象有很多优势。

- 1）不可变对象可以提高String Pool的效率和安全性。如果你知道一个对象是不可变的，那么需要拷贝这个对象的内容时，就不用复制它的本身而只是复制它的地址，复制地址（通常一个指针的大小）需要很小的内存效率也很高。对于同时引用这个“ABC”的其他变量也不会造成影响。
- 2）不可变对象对于多线程是安全的，因为在多线程同时进行的情况下，一个可变对象的值很可能被其他进程改变，这样会造成不可预期的结果，而使用不可变对象就可以避免这种情况。

当然也有其他方面原因，但是最Java把String设成immutable最大的原因应该就是效率和安全的。

## 成员变量

```java
/** The value is used for character storage. */
private final char value[];

/** Cache the hash code for the string */
private int hash; // Default to 0

/** use serialVersionUID from JDK 1.0.2 for interoperability */
private static final long serialVersionUID = -6849794470754667710L;

/** Class String is special cased within the Serialization Stream Protocol. */
private static final ObjectStreamField[] serialPersistentFields =
    new ObjectStreamField[0];
```

以上是String中的所有成员变量，hash在[哈希篇]已经详细分析，暂且不说，说一说其他三个。

### 字符存储

在咱们这个版本，也就是JDK8时，是将字符存储在char数组中，每个字符将使用两个字节(十六位)。从许多不同的应用程序收集的数据表明字符串是堆使用的主要组成部分，而且，大多数String对象只包含Latin-1字符，这些字符只需要一个字节的存储空间，因此char型的String对象的内部数组有一半空间未使用。

但是从JDK9开始，空间占用方面有了一个优化。String的数据存储格式从

```java
private final char[] value;
```

变成了

```java
private final byte[] value;
```

在JDK9及以后的版本，String源码内部多定义了一个变量

```java
private final byte coder;
```

通过`coder`判断使用LATIN1还是UTF16，当字符串都能用LATIN1表示，值就是0，否则就是1。从以下源码可以看出，在处理字符串长度时，如果是char，则长度除以2。

```java
static final boolean COMPACT_STRINGS;

static {
    COMPACT_STRINGS = true;
}

public int length(){
    return value.length >> coder();
}

byte coder(){
    return COMPACT_STRINGS ? coder : UTF16;
}

@Native static final byte LATIN1 = 0;
@Native static final byte UTF16 = 1;
```

### 序列化与反序列化

何谓序列化？java的对象随着JVM的运行而被保持在内存中，随着JVM的停止而丢弃消亡。很多时候，这些对象或是不可重建的，或是重建对象将付出巨大的代价，JVM运行时，少量的对象被保持在内存中是可以接受的。然而一旦JVM需要被停止，或在运行过程中建立了过多的对象，对象的数量多到影响操作系统的正常运行乃至多到物理内存都存不下时，这些对象只能想办法保存起来。

提到保存对象，或者说是持久化。最熟知的莫过于保存到文件系统或数据库。这种做法一般涉及到自定义存储格式以及繁琐的数据转换。Java序列化就是java提供的非常简单易用的对象保存为字节数组的方法。除此以外，使用RMI(远程方法调用)，或用网络传递对象时，都会用到对象序列化。

**序列化就是对象到字节码的过程，反序列化就是从字节码到对象的过程。**

很多时候用默认机制序列化对象是不合理的。

或者对象的某个属性引用的对象不能支持序列化接口，或者对象中包含了一些敏感的数据如银行卡的账号和密码，或者对象包含的数据并非都是有意义的比如临时变量。这时，我们可以指定那些数据可以序列化，那些是不需要的。

限定序列化的数据的方法有两种，一种是用瞬态修饰符**transient**修饰不用序列化的属性。另外一种是添加一个**serialPersistentFields**域来声明序列化时要包含的域。

在源码中

```java
private static final long serialVersionUID = -6849794470754667710L;
private static final ObjectStreamField[] serialPersistentFields =
    new ObjectStreamField[0];
```

serialVersionUID是一个序列化版本号，Java 通过这个 UID 来判定反序列化时的字节流与本地类的一致性，如果相同则可以进行反序列化，不同就会异常。至于为什么用定义参与序列化的域的静态字段serialPersistentFields的数组长度为0，在一番搜索之后也没有找到具体原因，期待有大神给出解释。

## 内部方法

**默认构造方法**

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

**使用字符串类型的对象来初始化**

```
public String(String original){
  this.value = original.value;
  this.hash = original.hash;
}
```

这里将直接将源 String 中的 value 和 hash 两个属性直接赋值给目标 String。因为 String 一旦定义之后是不可以改变的，所以也就不用担心改变源 String 的值会影响到目标 String 的值。

**使用字符数组来构造**

```
public String(char value[]){
    this.value = Arrays.copyOf(value, value.length);
}
public String(char value[], int offset, int count){
  if(offset<0){
    throw new StringIndexOutOfBoundsException(offset);
  }
  if(count<=0){
    if(count<0){
     throw new String IndexOutOfBoundsException(count);
    }
    if(offset <= value.length){
      this.value = "".value;
      return;
    }
  }

 //Note:offset or count might be near-1>>>1.
  if(offset > value.length - count){
      throw new StringIndexOutOfBoundsException(offset+count);
  }
 this.value=Arrays.copyOfRange(value,offset,offset+count);
}
```

这里值得注意的是：当我们使用字符数组创建 String 的时候，会用到 Arrays.copyOf 方法或 Arrays.copyOfRange 方法。这两个方法是将原有的字符数组中的内容逐一的复制到 String 中的字符数组中。会创建一个新的字符串对象，随后修改的字符数组不影响新创建的字符串。

**使用字节数组来构建 String**

在 Java 中，String 实例中保存有一个 char[] 字符数组，char[] 字符数组是以 unicode 码来存储的，String 和 char 为内存形式。

byte 是网络传输或存储的序列化形式，所以在很多传输和存储的过程中需要将 byte[] 数组和 String 进行相互转化。所以 String 提供了一系列重载的构造方法来将一个字符数组转化成 String，提到 byte[] 和 String 之间的相互转换就不得不关注编码问题。

```
String(byte[] bytes, Charset charset)
```

该构造方法是指通过 charset 来解码指定的 byte 数组，将其解码成 unicode 的 char[] 数组，构造成新的 String。

这里的 bytes 字节流是使用 charset 进行编码的，想要将他转换成 unicode 的 char[] 数组，而又保证不出现乱码，那就要指定其解码方式

同样的，使用字节数组来构造 String 也有很多种形式，按照是否指定解码方式分的话可以分为两种：

```
public String(byte bytes[]){
  this(bytes, 0, bytes.length);
}
public String(byte bytes[], int offset, int length){
    checkBounds(bytes, offset, length);
    this.value = StringCoding.decode(bytes, offset, length);
}
```

如果我们在使用 byte[] 构造 String 的时候，使用的是下面这四种构造方法（带有 charsetName 或者 charset 参数）的一种的话，那么就会使用 StringCoding.decode 方法进行解码，使用的解码的字符集就是我们指定的 charsetName 或者 charset。

```
String(byte bytes[])
String(byte bytes[], int offset, int length)
String(byte bytes[], Charset charset)
String(byte bytes[], String charsetName)
String(byte bytes[], int offset, int length, Charset charset)
String(byte bytes[], int offset, int length, String charsetName)
```

我们在使用 byte[] 构造 String 的时候，如果没有指明解码使用的字符集的话，那么 StringCoding 的 decode 方法首先调用系统的默认编码格式，如果没有指定编码格式则默认使用 ISO-8859-1 编码格式进行编码操作。主要体现代码如下：

```
static char[] decode(byte[] ba, int off, int len){
    String csn = Charset.defaultCharset().name();
    try{ //use char set name decode() variant which provide scaching.
         return decode(csn, ba, off, len);
    } catch(UnsupportedEncodingException x){
        warnUnsupportedCharset(csn);
    }

    try{
       return decode("ISO-8859-1", ba, off, len);  } 
    catch(UnsupportedEncodingException x){
       //If this code is hit during VM initiali zation, MessageUtils is the only way we will be able to get any kind of error message.
       MessageUtils.err("ISO-8859-1 char set not available: " + x.toString());
       // If we can not find ISO-8859-1 (are quired encoding) then things are seriously wrong with the installation.
       System.exit(1);
       return null;
    }
}
```

**使用 StringBuffer 和 StringBuilder 构造一个 String**
 作为 String 的两个“兄弟”，StringBuffer 和 StringBuilder 也可以被当做构造 String 的参数。

```
public String(StringBuffer buffer) {
   synchronized(buffer) {
   this.value = Arrays.copyOf(buffer.getValue(), buffer.length());
   } 
} 
public String(StringBuilder builder) {
    this.value = Arrays.copyOf(builder.getValue(), builder.length());
}
```

当然，这两个构造方法是很少用到的，因为当我们有了 StringBuffer 或者 StringBuilfer 对象之后可以直接使用他们的 toString 方法来得到 String。

> 关于效率问题，Java 的官方文档有提到说使用StringBuilder 的 toString 方法会更快一些，原因是 StringBuffer 的 toString 方法是 synchronized 的，在牺牲了效率的情况下保证了线程安全。

StringBuilder 的 toString() 方法：

```
@Override
public String toString(){
  //Create a copy, don't share the array
  return new String(value,0,count);
}
```

StringBuffer 的 toString() 方法：

```
@Override
public synchronized String toString(){
  if (toStringCache == null){
    toStringCache = Arrays.copyOfRange(value, 0, count);
  }
  return new String(toStringCache, true);
}
```

**一个特殊的保护类型的构造方法**
 String 除了提供了很多公有的供程序员使用的构造方法以外，还提供了一个保护类型的构造方法（Java 7），我们看一下他是怎么样的：

```
String(char[] value, boolean share) {
 // assert share : "unshared not supported";
 this.value = value;
}
```

从代码中我们可以看出，该方法和 String(char[] value) 有两点区别：

- 第一个区别：该方法多了一个参数：boolean share，其实这个参数在方法体中根本没被使用。注释说目前不支持 false，只使用 true。那可以断定，加入这个 share 的只是为了区分于 String(char[] value) 方法，不加这个参数就没办法定义这个函数，只有参数是不同才能进行重载。
- 第二个区别：具体的方法实现不同。我们前面提到过 String(char[] value) 方法在创建 String 的时候会用到 Arrays 的 copyOf 方法将 value 中的内容逐一复制到 String 当中，而这个 String(char[] value, boolean share) 方法则是直接将 value 的引用赋值给 String 的 value。那么也就是说，这个方法构造出来的 String 和参数传过来的 char[] value 共享同一个数组。

**为什么 Java 会提供这样一个方法呢？**

- **性能好：**这个很简单，一个是直接给数组赋值（相当于直接将 String 的 value 的指针指向char[]数组），一个是逐一拷贝，当然是直接赋值快了。
- **节约内存：**该方法之所以设置为 protected，是因为一旦该方法设置为公有，在外面可以访问的话，如果构造方法没有对 arr 进行拷贝，那么其他人就可以在字符串外部修改该数组，由于它们引用的是同一个数组，因此对 arr 的修改就相当于修改了字符串，那就破坏了字符串的不可变性。
- **安全的：**对于调用他的方法来说，由于无论是原字符串还是新字符串，其 value 数组本身都是 String 对象的私有属性，从外部是无法访问的，因此对两个字符串来说都很安全。

### Java 7 加入的新特性

在 Java 7 之前有很多 String 里面的方法都使用上面说的那种“性能好的、节约内存的、安全”的构造函数。
 比如：`substring` `replace` `concat` `valueOf` 等方法

> 实际上它们使用的是 public String(char[], ture) 方法来实现。

**但是在 Java 7 中，substring 已经不再使用这种“优秀”的方法了**

```
public String substring(int beginIndex, int endIndex){
  if(beginIndex < 0){
    throw new StringIndexOutOfBoundsException(beginIndex);
  }
  if(endIndex > value.length){
    throw new StringIndexOutOfBoundsException(endIndex);
  }
  intsubLen = endIndex-beginIndex;
  if(subLen < 0){
    throw new StringIndexOutOfBoundsException(subLen);
  }
  return ((beginIndex == 0) && (endIndex == value.length)) ? this  : newString(value, beginIndex, subLen);
}
```

**为什么呢？**
 虽然这种方法有很多优点，但是他有一个致命的缺点，对于 sun 公司的程序员来说是一个零容忍的 bug，那就是他很有可能造成**内存泄露**。

看一个例子，假设一个方法从某个地方（文件、数据库或网络）取得了一个很长的字符串，然后对其进行解析并提取其中的一小段内容，这种情况经常发生在网页抓取或进行日志分析的时候。

下面是示例代码：

```
String aLongString = "...averylongstring...";
String aPart = data.substring(20, 40);
return aPart;
```

在这里 aLongString 只是临时的，真正有用的是 aPart，其长度只有 20 个字符，但是它的内部数组却是从 aLongString 那里共享的，因此虽然 aLongString 本身可以被回收，但它的内部数组却不能释放。这就导致了内存泄漏。如果一个程序中这种情况经常发生有可能会导致严重的后果，如内存溢出，或性能下降。

> 新的实现虽然损失了性能，而且浪费了一些存储空间，但却保证了字符串的内部数组可以和字符串对象一起被回收，从而防止发生内存泄漏，因此新的 substring 比原来的更健壮。

### 其他方法

length() 返回字符串长度

```
public int length(){
  return value.length;
}
```

isEmpty() 返回字符串是否为空

```
public boolean isEmpty(){
  return value.length == 0;
}
```

charAt(int index)  返回字符串中第（index+1）个字符（数组索引）

```
public char charAt(int index){
  if((index < 0) || (index >= value.length)){
    throw new StringIndexOutOfBoundsException(index);
  }
  return value[index];
}
```

`char[] toCharArray()` 转化成字符数组
 `trim()`去掉两端空格
 `toUpperCase()`转化为大写
 `toLowerCase()`转化为小写

**需要注意**
 `String concat(String str)` 拼接字符串
 `String replace(char oldChar, char newChar)` 将字符串中的
 oldChar 字符换成 newChar 字符

> 以上两个方法都使用了 `String(char[] value, boolean share)` concat 方法和 replace 方法，他们不会导致元数组中有大量空间不被使用，因为他们一个是拼接字符串，一个是替换字符串内容，不会将字符数组的长度变得很短，所以使用了共享的 char[] 字符数组来优化。

`boolean matches(String regex)`  判断字符串是否匹配给定的regex正则表达式
 `boolean contains(CharSequence s)` 判断字符串是否包含字符序列 s
 `String[] split(String regex, int limit)` 按照字符 regex将字符串分成 limit 份
 `String[] split(String regex)` 按照字符 regex 将字符串分段

**getBytes**

在创建 String 的时候，可以使用 byte[] 数组，将一个字节数组转换成字符串，同样，我们可以将一个字符串转换成字节数组，那么 String 提供了很多重载的 getBytes 方法。

```
public byte[] getBytes(){
  return StringCoding.encode(value, 0, value.length);
}
```

但是，值得注意的是，在使用这些方法的时候一定要注意编码问题。比如：
 `String s = "你好，世界！"; byte[] bytes = s.getBytes();`
 这段代码在不同的平台上运行得到结果是不一样的。由于没有指定编码方式，所以在该方法对字符串进行编码的时候就会使用系统的默认编码方式。

> 在中文操作系统中可能会使用 GBK 或者 GB2312 进行编码，在英文操作系统中有可能使用 iso-8859-1 进行编码。这样写出来的代码就和机器环境有很强的关联性了，为了避免不必要的麻烦，要指定编码方式。

```
public byte[] getBytes(String charsetName) throws UnsupportedEncodingException{
  if (charsetName == null) throw new NullPointerException();
  return StringCoding.encode(charsetName, value, 0, value.length);
}
```

### 比较方法

`boolean equals(Object anObject)；` 比较对象
 `boolean contentEquals(String Buffersb)；` 与字符串比较内容
 `boolean contentEquals(Char Sequencecs)；` 与字符比较内容
 `boolean equalsIgnoreCase(String anotherString)；`忽略大小写比较字符串对象
 `int compareTo(String anotherString)；` 比较字符串
 `int compareToIgnoreCase(String str)；` 忽略大小写比较字符串
 `boolean regionMatches(int toffset, String other, int ooffset, int len)`局部匹配
 `boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len)` 可忽略大小写局部匹配

字符串有一系列方法用于比较两个字符串的关系。 前四个返回 boolean 的方法很容易理解，前三个比较就是比较 String 和要比较的目标对象的字符数组的内容，一样就返回 true, 不一样就返回false，核心代码如下：

```
int n = value.length; 
while (n-- ! = 0) {
  if (v1[i] != v2[i])
    return false;
    i++;
}
```

> v1 v2 分别代表 String 的字符数组和目标对象的字符数组。 第四个和前三个唯一的区别就是他会将两个字符数组的内容都使用 toUpperCase 方法转换成大写再进行比较，以此来忽略大小写进行比较。相同则返回 true，不想同则返回 false

**equals方法：**

```
public boolean equals(Object anObject) {
     if (this == anObject) {
         return true;
     } 
    if (anObject instanceof String) {
       String anotherString = (String) anObject;
       int n = value.length;
       if (n == anotherString.value.length) {
           char v1[] = value;
           char v2[] = anotherString.value;
           int i = 0;
           while (n-- != 0) {
             if (v1[i] != v2[i])
             return false;
             i++;
           }
           return true;
       }
   } 
   return false;
}
```

该方法首先判断 this == anObject ？，也就是说判断要比较的对象和当前对象是不是同一个对象，如果是直接返回 true，如不是再继续比较，然后在判断 anObject 是不是 String 类型的，如果不是，直接返回 false，如果是再继续比较，到了能终于比较字符数组的时候，他还是先比较了两个数组的长度，不一样直接返回 false，一样再逐一比较值。 虽然代码写的内容比较多，但是可以很大程度上提高比较的效率。值得学习！！！

> StringBuffer 需要考虑线程安全问题，加锁之后再调用

**contentEquals 有两个重载：**

- contentEquals((CharSequence) sb) 方法
   contentEquals((CharSequence) sb) 分两种情况，一种是 `cs instanceof AbstractStringBuilder`，另外一种是参数是 String 类型。具体比较方式几乎和 equals 方法类似，先做“宏观”比较，在做“微观”比较。

下面这个是 equalsIgnoreCase 代码的实现：

```
 public boolean equalsIgnoreCase(String anotherString) {
 return (this == anotherString) ? true : (anotherString != null) && (anotherString.value.length == value.length) && regionMatches(true, 0, anotherString, 0, value.length);
 }
```

看到这段代码，眼前为之一亮。使用一个三目运算符和 && 操作代替了多个 if 语句。

### hashCode

```
public int hashCode(){
  int h = hash;
  if(h == 0 && value.length > 0){
    char val[] = value;
    for(int i = 0; i < value.length; i++){
      h = 31 * h + val[i];
    }
    hash = h;
  }
  return h;
}
```

> hashCode 的实现其实就是使用数学公式：s[0] * 31^(n-1) + s[1] * 31^(n-2) + ... + s[n-1]

所谓“冲突”，就是在存储数据计算 hash 地址的时候，我们希望尽量减少有同样的 hash 地址。如果使用相同 hash 地址的数据过多，那么这些数据所组成的 hash 链就更长，从而降低了查询效率。

所以在选择系数的时候要选择尽量长的系数并且让乘法尽量不要溢出的系数，因为如果计算出来的 hash 地址越大，所谓的“冲突”就越少，查找起来效率也会提高。

> 现在很多虚拟机里面都有做相关优化，使用 31 的原因可能是为了更好的分配 hash 地址，并且 31 只占用 5 bits。

在 Java 中，整型数是 32 位的，也就是说最多有 2^32 = 4294967296 个整数，将任意一个字符串，经过 hashCode 计算之后，得到的整数应该在这 4294967296 数之中。那么，最多有 4294967297 个不同的字符串作 hashCode 之后，肯定有两个结果是一样的。

> hashCode 可以保证相同的字符串的 hash 值肯定相同，但是 hash 值相同并不一定是 value 值就相同。

**substring**
 前面我们介绍过，java 7 中的 substring 方法使用
 String(value, beginIndex, subLen) 方法创建一个新的 String 并返回，这个方法会将原来的 char[] 中的值逐一复制到新的 String 中，两个数组并不是共享的，虽然这样做损失一些性能，但是有效地避免了内存泄露。

**replaceFirst、replaceAll、replace区别**
 `String replaceFirst(String regex, String replacement)`
 `String replaceAll(String regex, String replacement)`
 `String replace(Char Sequencetarget, Char Sequencereplacement)`

```
public String replace(char oldChar, char newChar){
  if(oldChar != newChar){
    int len = value.length;
    int i = -1;
    char[] val = value; /*avoid get field opcode*/
    while (++i < len){
      if (val[i] == oldChar){
        break;
      }
    }
    if( i < len ){
      char buf[] = new char[len];
      for (intj=0; j<i; j++){
        buf[j] = val[j];
      }
      while (i < len){
        char c = val[i];
        buf[i] = (c == oldChar) ? newChar : c;
        i++;
      }
      return new String(buf,true);
    }
   }
  return this;
}
```

- replace 的参数是 char 和 CharSequence，即可以支持字符的替换, 也支持字符串的替换
- replaceAll 和 replaceFirst 的参数是 regex，即基于规则表达式的替换

> 比如可以通过 replaceAll (“\d”, “*”)把一个字符串所有的数字字符都换成星号;

相同点是都是全部替换，即把源字符串中的某一字符或字符串全部换成指定的字符或字符串，如果只想替换第一次出现的，可以使用 replaceFirst()，这个方法也是基于规则表达式的替换。另外,如果replaceAll() 和r eplaceFirst() 所用的参数据不是基于规则表达式的，则与replace()替换字符串的效果是一样的，即这两者也支持字符串的操作。

**copyValueOf 和 valueOf**
 String 的底层是由 char[] 实现的，早期的 String 构造器的实现呢，不会拷贝数组的，直接将参数的 char[] 数组作为 String 的 value 属性。字符数组将导致字符串的变化。

为了避免这个问题，提供了 copyValueOf 方法，每次都拷贝成新的字符数组来构造新的 String 对象。

> 现在的 String 对象，在构造器中就通过拷贝新数组实现了，所以这两个方面在本质上已经没区别了。

valueOf()有很多种形式的重载，包括：

```
 public static String valueOf(boolean b) {
       return b ? "true" : "false";
 } 

public static String valueOf(char c) {
       char data[] = {c};
       return new String(data, true);
 }

 public static String valueOf(int i) {
       return Integer.toString(i);
 }

 public static String valueOf(long l) {
       return Long.toString(l);
 }

 public static String valueOf(float f) {
       return Float.toString(f);
 } 

public static String valueOf(double d) {
     return Double.toString(d);
}
```

可以看到这些方法可以将六种基本数据类型的变量转换成 String 类型。

**intern()方法**
 `public native String intern();` 该方法返回一个字符串对象的内部化引用。
 String 类维护一个初始为空的字符串的对象池，当 intern 方法被调用时，如果对象池中已经包含这一个相等的字符串对象则返回对象池中的实例，否则添加字符串到对象池并返回该字符串的引用。

### String 对 “+” 的重载

我们知道，Java 是不支持重载运算符，String 的 “+” 是 java 中唯一的一个重载运算符，那么 java 使如何实现这个加号的呢？我们先看一段代码：

```
public static void main(String[] args) {
     String string = "hello";
     String string2 = string + "world";
}
```

然后我们将这段代码的实际执行情况贴出来看看：

```
public static void main(String args[]){
     String string = "hollo";
     String string2 = (new StringBuilder(String.valueOf(string))).append("world").toString();
}
```

看了反编译之后的代码我们发现，其实 String 对 “+” 的支持其实就是使用了 StringBuilder 以及他的 append、toString 两个方法。

**String.valueOf和Integer.toString的区别**
 接下来我们看以下这段代码，我们有三种方式将一个 int 类型的变量变成呢过String类型，那么他们有什么区别？

```
int i = 5;
String i1 = "" + i;
String i2 = String.valueOf(i);
String i3 = Integer.toString(i);
```

第三行和第四行没有任何区别，因为 String.valueOf(i) 也是调用
 Integer.toString(i) 来实现的。
 第二行代码其实是 String i1 = (new StringBuilder()).append(i).toString();

> 首先创建了一个 StringBuilder 对象，然后再调用 append 方法，再调用 toString 方法。

------

### switch 对字符串支持的实现

还是先上代码：

```
public class switchDemoString {
     public static void main(String[] args) {
         String str = "world";
         switch (str) {
         case "hello": 
              System.out.println("hello");
              break;
         case "world":
             System.out.println("world");
             break;
         default: break;
       }
    }
}
```

对编译后的代码进行反编译：

```
public static void main(String args[]) {
       String str = "world";
       String s;
       switch((s = str).hashCode()) {
          case 99162322:
               if(s.equals("hello"))
                   System.out.println("hello");
               break;
          case 113318802:
               if(s.equals("world"))
                   System.out.println("world");
               break;
          default: break;
       }
  }
```

看到这个代码，你知道原来字符串的 switch 是通过 equals() 和 hashCode() 方法来实现的。记住，switch 中只能使用整型，比如 byte，short，char(ackii码是整型) 以及 int。还好 hashCode() 方法返回的是 int 而不是 long。

> 通过这个很容易记住 hashCode 返回的是 int 这个事实。仔细看下可以发现，进行 switch 的实际是哈希值，然后通过使用 equals 方法比较进行安全检查，这个检查是必要的，因为哈希可能会发生碰撞。

因此性能是不如使用枚举进行 switch 或者使用纯整数常量，但这也不是很差。因为 Java 编译器只增加了一个 equals 方法，如果你比较的是字符串字面量的话会非常快，比如 ”abc” ==”abc” 。如果你把 hashCode() 方法的调用也考虑进来了，那么还会再多一次的调用开销，因为字符串一旦创建了，它就会把哈希值缓存起来。
 因此如果这个 siwtch 语句是用在一个循环里的，比如逐项处理某个值，或者游戏引擎循环地渲染屏幕，这里 hashCode() 方法的调用开销其实不会很大。

> 其实 swich 只支持一种数据类型，那就是整型，其他数据类型都是转换成整型之后在使用 switch 的。

### 总结

- 一旦 String 对象在内存(堆)中被创建出来，就无法被修改。

> 特别要注意的是，String 类的所有方法都没有改变字符串本身的值，都是返回了一个新的对象。

- 如果你需要一个可修改的字符串，应该使用 StringBuffer 或者
   StringBuilder。

> 否则会有大量时间浪费在垃圾回收上，因为每次试图修改都有新的String 对象被创建出来。

- 如果你只需要创建一个字符串，你可以使用双引号的方式，如果你需要在堆中创建一个新的对象，你可以选择构造函数的方式。