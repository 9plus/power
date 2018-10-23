*本文基于JDK10*

```java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {
```

* Serializable接口

  只有实现Serializable接口的类才可以被序列化和反序列化，具体原因现在还不明白

* Comparable接口

  只有实现Comparable<T>接口的类才可以进行比较，具体原因不明，但是可以参考`Arrays.sort()`方法，在Java核心技术卷1lambda表达式一章有提到，只有实现了Comparable接口的类才能被比较，Comparable接口中只有一个方法`comparaTo(T o)`

* CharSequence接口

  一个只读的字符序列，包括length(), charAt(int index), codePoints()等方法，StringBuild和StringBuffer也实现了这个接口

> private final byte[] value;

String用final和byte[]修饰的变量value来保存字符串的值。
* 常量
  final指明字符串是常量，这导致String在类似`str=str+"ab"`这样类似的代码中的行为是：创建一个新的String变量，将str和"ab"组合起来赋给新变量，这样对于时间效率和空间效率都有影响，因此在需要进行循环或者多次用到+号的时候，建议使用StringBuild和StringBuffer。
