> 参考：
>
> https://www.cnblogs.com/duanxz/p/3709608.html
>
> https://www.cnblogs.com/kubidemanong/p/9520071.html   谈到了JVM偏向锁

### synchronized 

synchronized是我们经常用到的与线程相关的关键字。 

### 关于内置锁 

synchronized是Java实现**内置锁**的方式。 

什么叫**内置锁**？我的定义是，由Java内部提供的、不需要显式的获取与释放的、用来实现多线程同步功能的一种东西，因为掌握了这个东西就能在线程竞争中拿到权限，拿到资源，所以被称为内置锁。 

如何理解呢？假设我们把任意需要线程同步的代码当作是一个房间，那么这个房间的门就是这个内置锁，每个房间与生俱来就带有一扇门，没有门的房间无法提供我们想要的功能，也不能被称之为“房间”。这个门是“房间”的定义决定每个房间必须有个门，内置锁是Java规定的每块代码(资源)能会有这样一个锁。然后，我们通过synchronized关键可以拿到这个锁的钥匙。 

Java中的内置锁是互斥锁，也就是说这扇门只准一个人通过，最多只有一个线程能获得该锁(打开门，获取资源)。当线程A尝试去获得线程B持有的内置锁时，线程A必须等待或者阻塞，直到线程B释放这个锁，如果线程B不释放这个锁，那么线程A将永远等待下去。 

我们可以这样记忆：synchronized = 内置锁。 

>  Tips：一种说法说**Java**的每个对象都有一个内置锁，它的意思就是每个功能正常的可以被称为**"**房间**"**的房子都有一扇门。为什么要有内置锁？因为这是为了方便线程同步，这是**Java**的设计者设计的。与内置锁相对应的是**java.util.concurrent.locks**中的显式锁，后面会讲到。

### 内置锁的使用方式

java中对于synchronized内置锁的使用方式，或者说synchronized作用范围，一共三种。

* 同步对象，指定任意非静态对象加锁 

```java
private void test() {
    synchronized (objects) {
    // TODO ..
    }
}
```

* 同步方法，等同于对this对象加锁

```java
private synchronized void test() {
    // TODO ..
}
```

* 同步类，等同于给静态方法/静态成员变量加锁

```java
 private static synchronized void test() {
     // TODO ..
 }
```



**为什么synchronied在方法上等于同对this对象加锁？** 

因为线程在执行同步方法时具有**排它性**，当任意一个线程进入到一个对象的任意一个同步方法时，这个对象的所有**同步方法**都为锁定了，在此期间，其他任何线程都不能访问这个对象的任意一个同步方法，直到这个线程执行完它所调用的同步方法并从中退出，从而导致它释放了该对象的同步锁之后。**在一个对象被某个线程锁定之后，其他线程是可以访问这个对象的所有非同步方法的**。

假设有3个方法M1，M2，M3都自增一个公有变量num。其中M1, M2都是synchronized的，M3是普通方法。如果主线程和子线程分别调用M1 M2，则num自增的过程会一直在这两个线程间切换(计算机实现导致)，但是num一直是连续的，表现出了排它性，因为另一个线程进入不了另一个同步方法。如果主线程和子线程分别调用M1 M3，则会出现M1修改了num，但是M3还在打印没修改的num，这是因为另一个线程可以进入非同步方法。代码见ThreadTest3.java



**同步对象的分析**

同步对象也可以叫做同步块，通过锁定一个指定的对象，来对同步块中包含的代码进行同步。而同步方法是对这个方法块里的代码进行同步，这种情况下锁定的对象就是同步方法所属的主体对象自身。如果这个方法是静态同步方法呢？那么线程锁定的就不是这个类的对象了，也不是这个类自身，而是这个类对应的java.lang.Class类型的对象。同步方法和同步块之间的相互制约只限于同一个对象之间，所以静态同步方法只受它所属类的其他静态同步方法的制约，而和这个类的实例(对象)没有关系。

假设一个变量a=0，正常情况下多线程一个修改a的线程为T1，一个读取a的线程为T2。使用synchronied同步的代码块中，每隔一段时间T2会把a的值同步到T1上去。这个时间是不固定，换句话说就是T2在读取变量a时不能确定是不是最新的值。**但是，当synchronized块结束后，一定会同步一下变量a，此时你能看到最新值。**这个实验说明：synchronized如果循环给一个变量自增，在循环过程中也会时不时将变量的值同步出去，但是在同步块结束之后，一定会同步一次。**代码见ThreadTest1.java。**