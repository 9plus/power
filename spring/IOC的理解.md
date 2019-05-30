## 什么是IOC?

IOC全称为控制反转(*Inversion Of Control*)，别名依赖注入(*Dependency Injection*)。

控制反转即指我们**获取依赖的方式发生了反转。**

假设存在如下情况，A依赖C对象，B依赖C、D对象。在利用控制反转前可能是这样的：

![image-20190530233432379](/Users/gyx/Library/Application Support/typora-user-images/image-20190530233432379.png)

A需要在内部创建一个C对象，B也需要在内部创建C、D对象(也可能与A共用C)。

但是在控制反转后可能是这样的：

![image-20190530233553092](/Users/gyx/Library/Application Support/typora-user-images/image-20190530233553092.png)

通过一个中间层隔离了AB与CD之间的耦合，由IOC Service Provider来管理C、D对象，A、B只需向IOC Service Provider发送一个请求注入的消息即可。

这样最直接的好处就是统一管理，代码便于修改与维护。

简单可理解的例子有很多。

比如装修新房，非控制反转就是自己打造家具，自己装修。控制反转就是下个订单，上门包装修一条龙。

又或者出门穿衣，非控制反转即自己去衣柜找衣服穿上出门，控制反转则是眼神示意一下，就有人拿衣服帮你穿上再出门。

## IOC实现的方式

**构造方法注入：**

IOC Service Provider通过特殊配置(指定包名、类名、构造函数名、参数名、参数类型等等)，将你需要或是依赖的对象传递到你的构造函数的参数中。因此如果是在如下代码中：

```java
public Person(String name) {
    this.name = name;
}
```

构造函数的参数`name`就被IOC Service Provider赋值了，我们不需要担心`this.name`等于空值。

构造方法注入方式比较直观，对象被构造完成后，即进入就绪状态，可以马上使用。这就好比你刚进酒吧的门，服务生已经将你喜欢的啤酒摆上了桌面一样。坐下就可以马上享受一份清凉与惬意。

**setter方法注入**

setter方法类似构造函数，也是由IOC Service Provider先扫描一遍。事实上因为都交由IOC Service Provider处理，整个程序启动有一个先纳入管理，再一一分配的过程。通过类似于构造函数的配置的将对象注入到setter方法参数上，然后赋值。代码如下：

```java
public class Person {
    String name;
    
    public void setName(String name) {
        this.name = name;
    }
}
```

setter方法注入虽然不像构造注入那样，让对象构造完成后即可使用，但相对来说更宽松一些，可以在对象构造完再注入。这就好比你可以到酒吧坐下后再决定要点什么啤酒，可以是哈啤也可以是其他的，随意性比较强。如果你不急着喝，这种方式当然是最适合你的。

**接口注入**

相比较于前面两种方法，接口注入就比较麻烦。被注入对象如果想要IOC Service Provider为其注入依赖对象。就必须实现某个接口。这个接口提供了一个方法，用来为其注入依赖对象，IOC Service Provider最终通过这些接口来了解应该为被注入对象注入什么依赖对象。

这就像你同样在酒吧点酒，为了让服务生理解你的意思，你就必须带上一顶啤酒杯式的帽子，看起来有点多此一举了。

![只想要一杯啤酒，有必要吗](https://cdn.shoplightspeed.com/shops/603272/files/12635897/windy-city-novelties-baseball-bases-loaded-beer-ha.jpg)

## 三种方式的比较

* **构造方法注入**。优点在于对象在构造完成后，就进入了就绪状态，可以马上使用。缺点就是，当依赖对象比较多，构造方法的参数列表会比较长。而通过反射构造对象的时候，对相同类型的参数的处理会比较困难，维护和使用也比较麻烦。
* **setter方法注入**。因为方法可以命名，所以setter方法注入在描述性上要比构造方法注入好一点。另外，setter方法可以被继承，允许设置默认值。缺点在于无法在构造完成后马上就可以用。
* **接口注入**。接口注入现在不被提倡使用，处于"退役状态"。它强制被注入对象实现不必要的接口。带有侵入性。