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