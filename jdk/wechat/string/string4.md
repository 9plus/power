> 本文基于JDK1.8

在方法篇中我们有对String类中的构造方法进行了一个分析，对于类而言，为了让客户端(即类的使用者)获取它自身的一个实例，除了上篇文章写的提供一个公有的构造器，还有一种管理对象创建的方法：类可以提供一个公有的**静态工厂方法**(static factory method)，即一个返回类的实例的静态方法。

本篇文章将通过String中的valueOf()方法，谈一谈静态工厂方法的好处，目录如下：

- `valueOf()`方法分析
- `(String)`、`toString`及`valueOf`的区别
- 静态工厂方法有什么好处？

### valueOf( )方法分析

String中的`valueOf()`有多种重载形式。

**Object对象与数据的重载：**

```java
String valueOf(Object obj);
String valueOf(char data[]);
String valueOf(char data[], int offset, int count);
```

**6种基本类型的重载**:

```java
String valueOf(boolean b);
String valueOf(char c);
String valueOf(int i);
String valueOf(long l);
String valueOf(float f);
String valueOf(double d);
```

String类中除了各种形式的`valueOf()`的重载函数，还有一个`copyValueOf()`函数。看看它的实现：

```java
public static String copyValueOf(char data[], int offset, int count) {
    return new String(data, offset, count);
}

public static String copyValueOf(char data[]) {
    return new String(data);
}
```

这两个函数是等同于

```java
String valueOf(char data[], int offset, int count);
String valueOf(char data[]);
```

这两个函数的，那么当初为什么要设计这样两个copy函数呢？

在浅析String类的时候我们说过，JDK8的底层是由char[]实现的，从JDK9才变成了以byte[]来存储。而在早期的上述两个String构造器的实现中，是直接将参数的char[]数组作为String的value属性。如果作为参数的字符数组变化，将会导致String内容变化。

就像`valueOf`的源码的注释提到的:

> The contents of the subarray are copied; subsequent modification of the character array does not affect the returned string.

字符数组的内容会被拷贝，字符数组中的子串的修改将不会影响返回的字符串。即以下情况修改`data`并不会影响s1与s2的值：

```java
char[] data = "123456789";
String s1 = String.valueOf(data); // s1 = "123456789"
String s2 = String.copyValueOf(data); // s2 = "123456789"

data[0] = '9';
```

### (String)、toString及valueOf的区别

在日常学习工作中，常常需要将对象转换成String方便打印调试等等，转String有三种方式:`(String)object`、`object.toString()`以及`String.valueOf(object)`。

那么那种方法更为好用呢？它们的区别又是什么呢？我们一起看一看。

#### 强制类型转换(String)

所谓强制类型转换，就是将A类型对象套上一层B类型，将A类型对象当成B类型对象处理，这样可以调用B的方法，在编译过程中将不会报错。在强制转换之后你在IDE中的代码提示也能看到B类型特有的方法。但是在运行过程中，如果A不能转换成B，你调用了B对象的方法，就会报错。

就像你把石头当成救生圈，你调用石头的"沉没"功能运行没问题，一旦调用救生圈的"浮起"功能，就会报错。但是你把石头当成武器类型，调用"攻击"功能(如果JVM中能识别的话)就是ok的。

#### toString

我们知道Object类是所有类的父类，因此Java中的任意对象都可以调用`toString()`方法，如果在调用的类中没有重写`toString()`方法，将调用Object类中的`toString`：

```java
public String toString() {
    return getClass().getName() + "@" + Integer.toHexString(hashCode());
}
```

注意：调用的时候对象不能为null。

#### valueOf

从其源码中可以看出，它调用的仍然是toString()方法。

```java
1public static String valueOf(Object obj) {
2    return (obj == null) ? "null" : obj.toString();
3}
```

但是你不需要担心对象是否为null了，如果为null，将返回"null"。

### 静态工厂方法有什么好处？

创建对象、获取实例时可以用静态工厂方法。比如Boolean类的简单示例：

```java
public static Boolean valueOf(boolean b) {
    return b ? Boolean.TRUE : Boolean.FALSE;
}
```

注意，静态工厂方法与设计模式中的工厂方法模式不同。我们通过静态工厂方法来获取实例，而不是构造器，这样做具有几大优势：

#### 静态工厂方法具有名称

当一个类重载了多个构造器时，复杂的参数列表我们构造起来比较麻烦，不够简洁。我们可以通过静态工厂方法，确切的描述正在被返回的对象，这种方法更加方便使用。

这种常常用在处理包含有多个，顺序也难记的构造器的类上。

#### 不必每次调用时都创建一个新对象

使用预先创建好的实例对象，重复使用，这样有助于类总能严格控制在某个时刻哪些实例应该存在。这种类被称作**实例受控的类**(instance-controlled)。实例受控有几点好处：

- 确保类是一个Singleton或者不可实例化。
- 确保不可变类不会存在两个相等的实例。

单例好理解，那么什么叫不可实例化呢？有时候我们需要把一些代码拆出来放在公有类或者说工具类中，一般这种类里面都是静态方法，工具类的实例是没必要的，为了防止他人不小心实例化了这个类，我们把类的构造器设置为私有以强化其不可实例的特性。于静态工厂方法而言，我们可以这样写代码：

```java
public static Animal getInstance() {
    throw new Exception....
}
```

如果是单例，则返回已经预构建的对象，如果是不可实例化，则抛出异常，
除此之外，不可变类--成员变量都是不可变的类 可以确保不会有两个相等的实例。

#### 可以返回原返回类型的任何子类型的对象

比如上述代码中的`getInstance()`，我们可以返回猫，狗。

#### 创建参数化类型实例的时候，它们使代码变得更加简洁。

这一条其实在JDK7以及被优化过了。主要就是针对

```java
Map<String, List<List<String>> > m = new HashMap<String, List<List<String>>>();
```

这种类型参数比较冗长的代码。通过静态工厂方法，比如:

```java
public static <K, V> HashMap<K, V> newInstance() {
    return new HashMap<K, V>();
}
```

就可以让代码更加简洁。

静态工厂方法当然有一些坏处，这里就不一一细表了，感兴趣的同学可以去看看Effective Java的第一条目，讲的就是静态工厂方法。