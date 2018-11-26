什么是Web？简单的理解，Web就是一个网页，包含前端页面+后台的请求处理。学JavaWeb肯定要会点前端的，仅仅使用纯html+css能写出个大概页面，JS/TS等框架什么的用的时候学就行了，能看懂就行。至于后台的请求处理，比较复杂，我们从最基本的说起：

最初刚学计算机网络的时候，我对请求(Request)的理解就是一个客户端Client向一个服务器Server按照TCP协议发个消息，然后会有回应(Response)。而现在的Web已经由传统的C/S架构逐渐演变成Browser/Server架构，浏览器Browser端其实也就是将Client端封装了一层，屏蔽了不同客户端的区别(比如用ping命令请求的客户端，用curl请求的客户端等等)，简化使用。Server端都是基于Http的，大家都是按照同一种协议进行交流，简化开发模式和节约成本啥的。

>浏览器端有火狐，谷歌，ie等，服务器端有Apache(饿怕吃)、IIS、Nginx、Tomcat、JBoss等。刚接触Web的时候也不懂Apache和Tomcat是什么，其实就是一个可能运行了Servlet或者其他用来处理http请求的服务器，服务器这个名词还是挺抽象的，可以理解为一个由许多类和接口编译生成的二进制然后起的一个进程。

目前的B/S网络架构大多采用以下的流程：

* 你在(包含CSS/JS/IMG/Cookie等局部缓存)浏览器里输入www.baidu.com
  * 浏览器请求DNS解析将这个域名解析成ip地址，向这个服务器发起get请求![Screen Shot 2018-11-27 at 12.32.45 AM](/Users/gyx/Desktop/Screen Shot 2018-11-27 at 12.32.45 AM.png)
    * 服务器一般有多台，根据负载均衡算法(也就是一个分配具体服务器的算法)平均所有用户的请求，然后查询数据库，缓存等等，接着返回数据。
      * 浏览器解析完这个返回数据后，发现有些静态资源(如CSS，比如html中的src=一个链接)需要加载，然后又会去发起对这些资源的http请求，而这些请求很可能会在CDN上，那么CDN服务器又会处理这个用户的请求。比如我请求www.baidu.com，查看请求：![Screen Shot 2018-11-27 at 12.41.22 AM](/Users/gyx/Desktop/Screen Shot 2018-11-27 at 12.41.22 AM.png)

