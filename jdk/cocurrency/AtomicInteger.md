## **sun.misc.Unsafe**

在AtomicInteget类中有这么一行：

```java
private static final Unsafe unsafe = Unsafe.getUnsafe();
```

Unsafe类在jdk源码的多个类中用到，为什么起名为unsafe呢？

因为这个类里面都是native方法，利用JNI绕开JVM，所分配的内存需要手动free(不被GC回收)。JNI保证了高效。

在源码中：

```java
// setup to use Unsafe.compareAndSwapInt for updates
private static final Unsafe unsafe = Unsafe.getUnsafe();
private static final long valueOffset;

static {
    try {
        valueOffset = unsafe.objectFieldOffset
            (AtomicInteger.class.getDeclaredField("value")); // reflect
    } catch (Exception ex) { throw new Error(ex); }
}
```

通过反射，获取域value在堆内存的偏移量。偏移量在AtomicInteger中十分重要。

------

通常情况下，++i或者--i不是线程安全的，这里面有三个独立的操作：

- 获取变量当前值
- 为该值+1/-1
- 写入新的值

在没有额外资源可以利用改的情况下，只能使用加锁才能保证读-改-写这三个操作是原子性的

------

## 重排序

<https://blog.csdn.net/fanrenxiang/article/details/80623884>

<https://www.cnblogs.com/chenyangyao/p/5269622.html>