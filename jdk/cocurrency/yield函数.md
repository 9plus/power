## 线程调度背景

  JVM实现了一个有优先权、基于优先级的调度程序。Java程序中的每一个线程都被分配了一定的优先权，使用定义好的范围内的一个正整数表示。

  JVM和下层的操作系统约定：操作系统必须选择有最高优先级的Java线程运行。但是**这个约定对于操作系统来说并不总是这样，这意味着操作系统有时可能会选择运行一个更低优先级的线程**。

**抢占与时间片**

  Java并不限定以时间片运行，但是大多数操作系统却有这样的要求。

  抢占意味着只有拥有高优先级的线程可以优先于低优先级的线程执行，但是当线程拥有相同优先级时，不能相互抢占。而受到时间片管制。

## 线程的优先权

- 记住当线程的优先级没有指定时，所有线程都携带普通优先级。

- 优先级可以用从1到10的范围指定。10表示最高优先级，1表示最低优先级，5是普通优先级。
- 记住优先级最高的线程在执行时被给予优先。但是不能保证线程在启动时就进入运行状态。
- 与在线程池中等待运行机会的线程相比，当前正在运行的线程可能总是拥有更高的优先级。
- 由调度程序决定哪一个线程被执行。
- `t.setPriority()`用来设定线程的优先级。
- 记住在线程开始方法被调用之前，线程的优先级应该被设定。
- 你可以使用常量，如MIN_PRIORITY,MAX_PRIORITY，NORM_PRIORITY来设定优先级

## Yield

> **v.	出产(作物); 产生(收益、效益等); 提供; 屈服; 让步; 放弃; 缴出;**
>
> **n.	产量; 产出; 利润;**

调用`yield()`方法的线程告诉虚拟机它乐意让其他线程占用自己的位置。这表明该线程没有在做一些紧急的事情。注意，这仅是一个暗示，并不能保证不会产生任何硬性。

以下是它源码：

```java
/**
 * A hint to the scheduler that the current thread is willing to yield
 * its current use of a processor. The scheduler is free to ignore this
 * hint.
 *
 * <p> Yield is a heuristic attempt to improve relative progression
 * between threads that would otherwise over-utilise a CPU. Its use
 * should be combined with detailed profiling and benchmarking to
 * ensure that it actually has the desired effect.
 *
 * <p> It is rarely appropriate to use this method. It may be useful
 * for debugging or testing purposes, where it may help to reproduce
 * bugs due to race conditions. It may also be useful when designing
 * concurrency control constructs such as the ones in the
 * {@link java.util.concurrent.locks} package.
 */
public static native void yield();
```

作为一个静态原生方法，`yield()`会告诉调度器，它愿意将运行机会交给线程池中拥有相同优先级的线程或者**自己**。`yield()`让当前线程有"运行状态"进入就绪状态，从而让其他具有优先级的等待线程获取执行权；但是，并不能保证在当前线程调用yield()之后，其它具有相同优先级的线程就一定能获得执行权，也有可能是当前线程又进入到"运行状态"继续运行。

> *举个例子：一帮朋友在排队上公交车，轮到Yield的时候，他突然说：我不想先上去了，咱们大家来竞赛上公交车。然后所有人就一块冲向公交车，*
>
> *有可能是其他人先上车了，也有可能是Yield先上车了。**
>
> **但是线程是有优先级的，优先级越高的人，就一定能第一个上车吗？这是不一定的，优先级高的人仅仅只是第一个上车的概率大了一点而已，*
>
> *最终第一个上车的，也有可能是优先级最低的人。并且**所谓的优先级执行，是在大量执行**次数中才能体现出来的。*

