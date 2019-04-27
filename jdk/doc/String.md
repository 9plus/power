*本文基于JDK10*

```java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {
```

* Serializable接口

  只有实现Serializable接口的类才可以被序列化和反序列化，具体原因现在还不明白

* Comparable接口

  只有实现Comparable<T>接口的类才可以进行比较，因为他们都是实现这个接口的comparaTo方法进行比较的，但是可以参考`Arrays.sort()`方法，在Java核心技术卷1lambda表达式一章有提到，只有实现了Comparable接口的类才能被比较，Comparable接口中只有一个方法`comparaTo(T o)`

* CharSequence接口

  一个只读的字符序列，包括length(), charAt(int index), codePoints()等方法，StringBuild和StringBuffer也实现了这个接口

  ## 成员变量

> **private final byte[] value;**

String用final和byte[]修饰的变量value来保存字符串的值。
* 常量
  final指明字符串是常量，这导致String在类似`str=str+"ab"`这样类似的代码中的行为是：创建一个新的String变量，将str和"ab"组合起来赋给新变量，这样对于时间效率和空间效率都有影响，因此在需要进行循环或者多次用到+号的时候，建议使用StringBuild和StringBuffer。

> **private final byte coder;**

用来标识是LATIN1编码还是UTF16编码。从JDK9开始，String的数据存储格式从JDK8的

```text
private final char[] value;
```

变成了

```text
private final byte[] value;
```

在JDK8及之前版本中，将字符存储在char数组中，每个字符将使用两个字节(十六位)。从许多不同的应用程序收集的数据表明字符串是堆使用的主要组成部分，而且，大多数String对象只包含Latin-1字符，这些字符只需要一个字节的存储空间，因此char型的String对象的内部数组有一半空间未使用。

解决办法就是定义这个coder标识。通过`coder`判断使用LATIN1还是UTF16，当字符串都能用LATIN1表示，值就是0，否则就是1。从`length()`方法可以看出，在处理字符串长度时，如果是0，则长度除以2。

> **private int hash;**
>
> **private static final long serialVersionUID = -6849794470754667710L;**

哈希值，字符串序列化UID

> **static final boolean COMPACT_STRINGS;**
>
> ```java
> static {
>     COMPACT_STRINGS = true;
> }
> ```

如果COMPACT_STRINGS值为false，那么String的数据的编码格式为UTF16

如果COMPACT_STRINGS值为true，那么String的数据的编码格式为LATIN1

> **privat**e static final ObjectStreamField[] serialPersistentFields =**
> ​        new ObjectStreamField[0];**

私有的序列化字段

>     @Native static final byte LATIN1 = 0;
>     @Native static final byte UTF16  = 1;

LATIN1和UTF16字段的定义

## 构造函数

```java
public String(char value[], int offset, int count) {
    this(value, offset, count, rangeCheck(value, offset, count));
}
```
对于类似于这个函数的构造函数，构造一个从offset开始count个字符形成一个字符串。rangeCheck会检查value.length()-count是否大于offset。

String构造函数的具体实现是参考

```java
public String(int[] codePoints, int offset, int count) {
    checkBoundsOffCount(offset, count, codePoints.length);
    if (count == 0) {
        this.value = "".value;
        this.coder = "".coder;
        return;
    }
    if (COMPACT_STRINGS) {
        byte[] val = StringLatin1.toBytes(codePoints, offset, count);
        if (val != null) {
            this.coder = LATIN1;
            this.value = val;
            return;
        }
    }
    this.coder = UTF16;
    this.value = StringUTF16.toBytes(codePoints, offset, count);
}
```
判断是LATIN1编码还是UTF16编码，分别调用两种编码的byte转换形式。

其他构造函数中`hibyte`表示高8位，如果都为0，直接左移8位。

## 方法

`length()`会右移coder()位。

---







> 本文基于JDK1.8

上篇文章学习了下String中的哈希值的作用，本篇开始正式进入String类。文章将以如下的模式铺展开。

- 类的整体结构
- 成员变量
- 内部方法

话不多说，进入整体。

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