#### 问题记录

* Mysql的存储引擎架构？作用？

* Mysql服务器架构？
* 各存储引擎之间的主要区别？以及重要性？
* Mysql原理



#### Mysql服务器逻辑架构

>  第一层：连接/线程处理

这一层服务并不是Mysql独有的，大多数基于网络的客户端/服务器的工具或者服务都有类似的架构，比如连接处理，授权认证，安全等等。

每个客户端都会在服务器的进程中拥有一个给MySQL查询的线程，这些查询线程会轮流在服务器的某个CPU核心或者CPU中运行，服务器会负责缓存线程。

>  第二层：核心服务

查询解析、分析、优化、缓存以及所有的内置函数

>  第三层：存储引擎

Mysql包含许多不同的存储引擎，存储引擎负责MySQL中数据的存储和提取。第二层与第三层是通过封装的API进行通信的，这些API屏蔽了存储引擎之间的差距。存储引擎API包含几十个底层函数。

#### 并发控制

本章主要讨论MySQL在两个层面的并发控制：

* 服务器层
* 存储引擎层

##### 读写锁

当有多个客户端同时处理数据时，需要进行并发控制。并发控制可以通过实现一个**读锁**和一个**写锁**来解决问题。这两种锁又叫做**共享锁**和**排他锁**。

##### 锁粒度

描述锁定数据量大小的一个概念。

加锁需要消耗资源：包括获得锁、检查锁是否已经解除、释放锁等，都会增加系统的开销