#### Compatator接口

> Array

* `Array.sort(array，comparator)`

  比较器需要是一个实例，虽然它没有状态，但是需要这个实例来调用方法。

#### Cloneable接口

为一个包含对象引用的变量建立副本时会导致变量和副本都是同一个对象的引用

> clone

* `Object.clone()`

  对于浅拷贝来说，如果两个对象共享的子对象是不可变的，那么这种共享就是安全的。

  对于深拷贝，需要重新定义clone方法，且需要定义为public，原方法为protected

#### lambda表达式

lambda表达式就是一个代码块，以及必须传入代码的变量规范。 