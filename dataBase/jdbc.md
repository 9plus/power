应用程序通过通信协议与数据库连接，进行数据的增删改查。

jdbc全名Java DataBase Connectivity，是Java数据库连接的规范标准，具体而言，它定义一组标准类和接口。

### JDBC因何而生？

不同数据库有不同的api接口，而应用程序跨平台或者换数据库也是常有的事，为了有一个统一的接口，设计了jdbc。

应用程序-->JDBC API-->MySQL JDBC驱动程序-->MySQL协议-->MySQL

应用程序-->JDBC API-->Oracle JDBC驱动程序-->Oracle协议-->Oracle

如上所示，这样只需更换驱动程序(一般更改classpath，换一个jar包)即可。

然而事实上由于程序中依赖了特定的api或者其他原因，还是要修改应用程序。

### JDBC驱动程序分类

* ODBC 最简单，平台限制
* Native. 最快
* Net Driver 使用中介服务器，最慢
* Native Protocol Driver 性能OK