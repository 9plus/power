## 浅析Serializable

前面在分析String源码的过程中有看到，String类实现了**Serializable**接口，并定义了一个**serialVersionUID**变量。我们都知道，Serializable接口是为了让String对象可以被序列化与反序列化的，本着实践出真知的精神，我们一起来探索下如果不实现这个接口，会出现什么问题，加深下理解。

### 持久化到文件中

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

如上所示，在else if中通过判断`obj instanceof Serializable`，如果对象没有实现序列化接口，就无法序列化。可以想见，Java中的每一处序列化都进行了这样的检查，也就是说，**没有实现Serializable接口的对象是无法通过IO操作持久化**。

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

