## 30行代码带你了解Serializable

前面在分析String源码的过程中有看到，String类实现了**Serializable**接口，并定义了一个**serialVersionUID**变量。我们都知道，Serializable接口是为了让String对象可以被序列化与反序列化的，本着实践出真知的精神，我们一起来探索下如果不实现这个接口，会出现什么问题，加深下理解。

## Serializable接口测试

以下是Serializable类的源码：

```java
public interface Serializable {
}
```

可以看到该类的内部实现完全为空，在Java IO体系中仅起一个标记的作用。那么这个标记具体是如何发挥作用的呢？我们测试一下：

首先定义一个User对象：

```java
import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
```

> 在idea中右键Generate...或者快捷键`Alt + Insert` 可以一键生成构造函数/set/get等。输入psvm一键生成`public static void main`，输入sout一键生成`System.out.println()`。更多可以用`ctrl + j`(mac上是`command + j`)查看。序列化的UID也可以一键生成，同学们可以自行搜索。

接着我们定义一个类来读写这个User类的对象。

```java
public class SerializableTest {

    private static void write() {
        User user = new User("1001", "Bob");
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("Z:\\workspace\\practice\\user.txt"));
            objectOutputStream.writeObject(user);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        write();
    }
}
```

运行上述代码后，可以查看user.txt文件，其中的数据是以二进制的形式存在，有很多乱码，有一些关键词User，String。

```
�� sr thinking.in.java.common.User        L idt Ljava/lang/String;L nameq ~ xpt 1001t Bob
```

此时User对象已经被持久化到文件中，接着我们将User实现Serializable接口的代码去掉，看会发生什么。

```java
java.io.NotSerializableException: thinking.in.java.common.User
	at java.io.ObjectOutputStream.writeObject0(ObjectOutputStream.java:1184)
	at java.io.ObjectOutputStream.writeObject(ObjectOutputStream.java:348)
	at thinking.in.java.SerializableTest.write(SerializableTest.java:13)
	at thinking.in.java.SerializableTest.main(SerializableTest.java:30)
```

抛出了以上异常，提示不可序列化的异常，然后我们到ObjectOutputStream类中的1184行看一下，这一部分的代码是这样的：

```java
if (obj instanceof String) {
    writeString((String) obj, unshared);
} else if (cl.isArray()) {
    writeArray(obj, desc, unshared);
} else if (obj instanceof Enum) {
    writeEnum((Enum<?>) obj, desc, unshared);
} else if (obj instanceof Serializable) {
    writeOrdinaryObject(obj, desc, unshared);
} else {
    if (extendedDebugInfo) {
        throw new NotSerializableException(
            cl.getName() + "\n" + debugInfoStack.toString());
    } else {
        throw new NotSerializableException(cl.getName());
    }
}
```

如上所示，在else if中通过判断`obj instanceof Serializable`，如果对象没有实现序列化接口，就无法序列化。可以想见，Java中的每一处序列化都进行了类似的检查，也就是说，**没有实现Serializable接口的对象是无法通过IO操作持久化**。

然后，我们测试反序列化，将文件中持久化的对象转换为Java对象。

```java
public class SerializableTest {

    private static void read() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("Z:\\workspace\\practice\\user.txt"));
            User user = (User) inputStream.readObject();
            System.out.println(user);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        read();
    }
}
```

打印信息为:

```java
thinking.in.java.common.User@58372a00
```

此时如果将User实现Serializable接口的代码部分去掉，发现也无法将文本转换为序列化对象，反序列化异常：

```java
java.io.InvalidClassException: thinking.in.java.common.User; class invalid for deserialization
	at java.io.ObjectStreamClass$ExceptionInfo.newInvalidClassException(ObjectStreamClass.java:169)
	at java.io.ObjectStreamClass.checkDeserialize(ObjectStreamClass.java:874)
	at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2043)
	at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1573)
	at java.io.ObjectInputStream.readObject(ObjectInputStream.java:431)
	at thinking.in.java.SerializableTest.read(SerializableTest.java:23)
	at thinking.in.java.SerializableTest.main(SerializableTest.java:30)
```

通过这个异常信息，我们进入到ObjectInputStream类的源码中看看它是如何检查的。在874行，ObjectStreamClass进行了如下反序列的检查：

```java
void checkDeserialize() throws InvalidClassException {
    requireInitialized();
    if (deserializeEx != null) {
        throw deserializeEx.newInvalidClassException();
    }
}
```

这里判断`deserializaed`这个变量是否为null，如果不为null，就会抛出反序列化异常。关于这个变量变量是如何被赋值以及整个ObjectInputStream的反序列化过程由于过于复杂，就不在这里详细说明了。感兴趣的同学可以去看看这篇博客：<https://yueyemaitian.iteye.com/blog/2078090>。



## SerialVersionUID

对于JVM来说，要进行持久化的类必须要有一个标记，只有持有这个标记JVM才允许类创建的对象可以通过其IO系统转换为字节数据，从而实现持久化，而这个标记就是Serializable接口。而在反序列化的过程中则需要使用serialVersionUID来确定由那个类来加载这个对象，所以我们在实现Serializable接口的时候，一般还会要去尽量显示地定义serialVersionUID，如：

```java
private static final long serialVersionUID = 1L; 
```

在反序列化的过程中，如果接收方为对象加载了一个类，如果该对象的serialVersionUID与对应持久化时的类不同，那么反序列化的过程中将会导致InvalidClassException异常。例如，在之前反序列化的例子中，我们故意将User类的serialVersionUID改为2L，如：

```java
private static final long serialVersionUID = 2L; 
```

那么此时，在反序例化时就会导致异常，如下：

```java
java.io.InvalidClassException: thinking.in.java.common.User; local class incompatible: stream classdesc serialVersionUID = 1, local class serialVersionUID = 2
	at java.io.ObjectStreamClass.initNonProxy(ObjectStreamClass.java:699)
	at java.io.ObjectInputStream.readNonProxyDesc(ObjectInputStream.java:1885)
	at java.io.ObjectInputStream.readClassDesc(ObjectInputStream.java:1751)
	at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2042)
	at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1573)
	at java.io.ObjectInputStream.readObject(ObjectInputStream.java:431)
	at thinking.in.java.SerializableTest.read(SerializableTest.java:23)
	at thinking.in.java.SerializableTest.main(SerializableTest.java:30)
```

如果我们在序列化中没有显示地声明serialVersionUID，则序列化运行时将会根据该类的各个方面计算该类默认的serialVersionUID值。但是，Java官方强烈建议所有要序列化的类都显示地声明serialVersionUID字段，因为如果高度依赖于JVM默认生成serialVersionUID，可能会导致其与编译器的实现细节耦合，这样可能会导致在反序列化的过程中发生意外的InvalidClassException异常。因此，为了保证跨不同Java编译器实现的serialVersionUID值的一致，实现Serializable接口的必须显示地声明serialVersionUID字段。

此外serialVersionUID字段地声明要尽可能使用private关键字修饰，这是因为该字段的声明只适用于声明的类，该字段作为成员变量被子类继承是没有用处的!有个特殊的地方需要注意的是，数组类是不能显示地声明serialVersionUID的，因为它们始终具有默认计算的值，不过数组类反序列化过程中也是放弃了匹配serialVersionUID值的要求。

## 结论

通过上面的测试，相信大家对Serializable接口算是有了具体的体会了。事实上，序列化就是将对象转换为字节序列的过程，反序列化就是把持久化的字节文件数据恢复为对象的过程。



