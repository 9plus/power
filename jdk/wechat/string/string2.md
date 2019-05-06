>  本文基于JDK1.8

[上篇文章](<https://zhuanlan.zhihu.com/p/63952624>)学习了String中的哈希值的作用，本篇开始正式进入String类。文章将按以下几块展开。

* 从类的声明看其不可变性

* 成员变量是为何

关于内部方法将在下一篇文章中详解，话不多说，进入正题。

### 从类的声明看其不可变性

String作为Java最基本最常用的类，我们应当对其内部实现有一个清晰的了解。先看String类的定义：

```java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {
    ....
}
```

String类实现了Serializable可序列接口，Comparable可比较接口，CharSequence包含一些字符处理方法的接口，这些暂且不管。

我们仔细聊聊**final**这个关键字。

#### final修饰有什么用？

emmm，final可以修饰哪些呢？final可以修饰类，成员变量，方法。

被final修饰的类不可被继承，被final修饰的成员变量不可变，被final修饰的方法子类无法覆盖(重写)。

也就说String类是无法被继承的。

#### String类为什么要设计成不可继承？

将方法或类声明为final主要目的是：确保它们不会在子类中改变语义。String基本约定中最重要的一条是**immutable**(不可变性)，假如String是可继承的，那么你的StringChild类就有可能被覆写为mutable(可变)的，这样就打破了成为共识的基本约定。

简单说来，整个JDK体系中依赖了String的不可变性，不可继承就是为了守护不可变性。

但是这里仅仅是把类声明为了final，这不足以保证String类的不可变性。String不可变，关键是因为SUN公司的工程师，在后面所有String的方法里很小心的没有去动String类中用来存字符的array里的元素，没有暴露内部成员字段。通过底层的实现 + 声明时的final的双重保证实现了不可变性。

#### String类的不可变性有什么好处？

不可变对象，顾名思义就是创建后不可以改变的对象。请看如下代码：

```java
String s = "ABC"; 
s.toLowerCase();
```

toLowerCase()并没有改变“ABC”的值，而是创建了一个新的String实例”abc”，然后将新的实例的指向变量s。

相对于可变对象，不可变对象有很多优势。

- 1）不可变对象可以提高String Pool的效率和安全性。如果你知道一个对象是不可变的，那么需要拷贝这个对象的内容时，就不用复制它的本身而只是复制它的地址，复制地址（通常一个指针的大小）需要很小的内存效率也很高。对于同时引用这个“ABC”的其他变量也不会造成影响。
- 2）不可变对象对于多线程是安全的，因为在多线程同时进行的情况下，一个可变对象的值很可能被其他进程改变，这样会造成不可预期的结果，而使用不可变对象就可以避免这种情况。

当然也有其他方面原因，但是最初Java把String设成immutable最大的原因应该就是效率和安全的。

### 成员变量是为何

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

#### 字符存储

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

#### 序列化与反序列化

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