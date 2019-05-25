在前面的文章中已经把前后端的框架搭好，本篇文章我们来写一下斗鱼弹幕爬虫的Java版。可能有人会说Python版本的爬虫到处都可以搜到，而且用Python的scrapy、beautifulsoup等库又快又方便，那么为什么我们还要用Java写爬虫呢？

事实是这样没错，但是这次的弹幕爬虫严格来讲主要涉及到**网络数据传输**，并不需要构造html标签，直接用socket + mybatis请求到数据后存入数据库即可(我会说是python的多线程不熟悉吗)，其实也很方便，也不用去加脚本加py文件来实现。总而言之就两个字，简单。

### 下载斗鱼协议文档

斗鱼弹幕爬虫原理很简单：

* 客户端依次发送，登陆请求、加入房间请求、加入弹幕组请求。
* 斗鱼的服务端返回弹幕

当然由于是一个长链接，还需要另起一个心跳线程(*heart thread*)来不断发送alive消息，以保持连接。

**Step1** 下载协议文档

去[斗鱼第三方开放平台](<http://dev-bbs.douyutv.com/forum.php?mod=forumdisplay&fid=37>)下载两份文档：

* [《斗鱼第三方开放平台API文档v2.2》](http://dev-bbs.douyutv.com/forum.php?mod=attachment&aid=MjkzfDUyNDM0MDQ2fDE1NTgyNTE4NTR8MHw0MDQ%3D)
* [《斗鱼弹幕服务器第三方接入协议v1.6.2》](http://dev-bbs.douyutv.com/forum.php?mod=attachment&aid=MjkxfDliYjA3ZDM4fDE1NTgyNTE4OTF8MHwzOTk%3D)

**Step2** 了解协议

根据第三方接入协议所述，我们需要按照斗鱼消息协议格式向斗鱼的弹幕服务器发送请求。

### 发送消息

就像TCP的3次握手一样，首先由客户端发出第一次握手，我们需要先发出登陆请求，为了扩展，我们将所有请求都认为是msg，先写一个请求函数。

```java
public static void sendRequest(Socket client, String msg){
    try {
        int msgLength = 4 + 4 + msg.length() + 1;
        byte[] dataLength = intToBytes(msgLength);
        byte[] dataHead = intToBytes(DyUtil.CODE); // CODE = 689
        byte[] data = msg.getBytes(StandardCharsets.ISO_8859_1); // 指定String转byte的编码格式

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

        byteArray.write(dataLength);
        byteArray.write(dataLength);
        byteArray.write(dataHead);
        byteArray.write(data);
        byteArray.write(0);

        OutputStream out = client.getOutputStream();
        out.write(byteArray.toByteArray());
        out.flush();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

我们定义一个发送请求的函数，这里的`client`就是我们的连接，`msg`就是我们具体的请求类型。学过计算机网络的应该知道，两个网络节点之间的交互是通过套接字来进行的，而Java中的`Socket`类就可以被作为网络中的套接字，`client`将会通过以下方式进行初始化：

```java
Socket client = new Sokcet("openbarrage.douyutv.com", "8601");
```

指定弹幕服务器与端口号。在我的电脑上不知道为什么在请求`openbarrage.douyutv.com`时会报：Unknow Host。因此我直接使用它的ip来初始化Socket。

我们在命令行ping一下`openbarrage.douyutv.com`，就可以看到它的ip地址：119.96.201.28

因此我采用的初始化方式为：

```java
Socket client = new Sokcet("119.96.201.28", "8601");
```

然后我们看如何构造斗鱼的请求头。

![image-20190519155912829](/Users/gyx/Library/Application Support/typora-user-images/image-20190519155912829.png)

可以看到，该请求头一共是 4 字节的长度 + 8 字节的头部 + 数据部。那么表格中的消息长度是多少呢？

```
消息长度 = 头部 + 数据部
```

这里将消息长度这个信息重复了两次，但是第一次不算在长度的计算之中，所以消息长度为 8 + 数据的长度 + 1(结尾的'\0')。即9 + 数据长度。

我们需要将数据分5块，分别是4字节消息长度，4字节消息长度，4字节的消息类型+加密字段+保留字段，数据部和最后一字节的结尾符(0代表'\0'的字节形式)，写入byte数组输出流中。(学习Java IO的可以关注我的另一个专栏)。

这些数据都需要转成字节形式，再看斗鱼协议，里面说斗鱼的消息格式都是小端模式(不像字符串有String.getBytes()函数，int得自己定义了)。我们定义两个转换函数：

```java
private static byte[] intToBytes(int data) {
    byte[] src = new byte[4];
    src[3] = (byte) ((data >> 24) & 0xFF);
    src[2] = (byte) ((data >> 16) & 0xFF);
    src[1] = (byte) ((data >> 8) & 0xFF);
    src[0] = (byte) (data & 0xFF);
    return src;
}
```

解释一下这个函数，int是4字节32位的，将data右移24位后，左边补0，得到最左边的8位(位于最右边)。此时通过与上0xFF，即与上0000 0000 0000 0000 0000 0000 1111 1111。将左边24位都清0，留下右边8位，存在byte数组中。

```java
private static int bytesToInt(byte[] src) {
    return ((src[0] & 0xFF)
            | ((src[1] & 0xFF) << 8)
            | ((src[2] & 0xFF) << 16)
            | ((src[3] & 0xFF) << 24));
}
```

### 接受消息

```java
public static String receiveMsg(Socket client) {
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
    try{
        InputStream inputStream = client.getInputStream();

        int dataLength = get4bytesResponse(inputStream); // 读取消息长度
        int dataLength2 = get4bytesResponse(inputStream);
        int msgType = get4bytesResponse(inputStream);

        if (dataLength <= 8 || dataLength >= 1032) {
            return INVALID_MSG;
        }

        dataLength = dataLength - 8;

        int len;
        int readLen = 0;
        byte[] bytes = new byte[dataLength];
        while ((len = inputStream.read(bytes, 0 , dataLength - readLen)) != -1) {
            byteArray.write(bytes, 0 ,len);
            readLen += len;
            if (readLen == dataLength) {
                break;
            }
        }

    } catch (IOException e){
        e.printStackTrace();
    }

    return byteArray.toString();
}
```

在接受消息时，先拿到返回消息的长度，以便在下面申请buffer的时候不需要申请多余的buffer，然后将几个无用数据(都是4字节)都读出来。

消息长度-8就是真正的数据返回长度。通过循环读入，直到读入的数据长度等于斗鱼告诉我们的消息长度为止。

### 爬虫线程

然后我们来写一个爬虫线程类。

首先发送登陆请求与加入弹幕组请求：

```java
private void connectToDy() {
    try {
        if (client != null) {
            client.close();
        }
        client = new Socket(DyUtil.HOST, DyUtil.PORT);
    } catch (IOException e) {
        e.printStackTrace();
    }

    String loginMsg = "type@=loginreq/roomid@="+ String.valueOf(roomId) + "/";
    DyUtil.sendRequest(client, loginMsg);

    String joinGroupMsg =  "type@=joingroup/rid@=" + String.valueOf(roomId) +"/gid@=-9999/";
    DyUtil.sendRequest(client, joinGroupMsg);
}
```

然后在连接到斗鱼后，开始处理消息：

```java
public void run(){

    connectToDy();

    while (true) {
        String s = DyUtil.receiveMsg(client);
        if (s.equals(DyUtil.INVALID_MSG)) {
            connectToDy();
            continue;
        }

        Map<String, String> m = DyUtil.getMsg(s);
        String danMu = m.get("txt");
        if (danMu != null && !danMu.equals("")) {
            String name = m.get("nn");
            String cardName = m.get("bnn");
            String cardLevel = m.get("bl");
            String roomId = m.get("rid");
            String level = m.get("level");
            String time = DyUtil.DF.format(new Date());

            System.out.println("Insert Success! " + time + " " + cardLevel + "级"  + cardName + " [" + name + "] : " + danMu);
        }

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

这里的消息处理函数并非像Python那样通过正则来取，而是：

```java
public static Map<String, String> getMsg(String s) {
    String[] messages = s.split("/");
    Map<String, String> m = new HashMap<>();
    for (String message : messages) {
        String[] ms = message.split("@=");
        if (ms.length >= 2) {
            m.put(ms[0], ms[1]);
        }
    }
    return m;
}
```

将消息用'/'划分，然后另key为@=前面的值，value为@=后面的值。

### 心跳线程

```java
public class AliveThread implements Runnable {

    private Socket client;

    @Override
    public void run() {
        try {
            client = new Socket(DyUtil.HOST, DyUtil.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String keepliveMsg = "type@=mrkl/";
        while (true) {
            DyUtil.sendRequest(client, keepliveMsg);
            System.out.println(DyUtil.DF.format(new Date()) + " keep alive ***********************");
            try{
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
```

### 主函数

创建一个主函数类后，直接用psmv一键生成public static void main。

```java
public class Main {
    public static void main(String[] args) {
        Thread t1 = new Thread(new CrawlerThread(74960)); //60937
        Thread t2 = new Thread(new CrawlerThread(60937));
        Thread t3 = new Thread(new AliveThread());
        t1.start();
        t2.start();
        t3.start();

        while (Thread.activeCount() > 1) {
            Thread.yield();
        }
    }
}
```

效果如下：

![image-20190519163827304](/Users/gyx/Library/Application Support/typora-user-images/image-20190519163827304.png)

当然，后续将会用线程池优化一下，事实上，上面的代码已经加入了springboot与mysql了。码字不易，想要源码的同学可以关注我的公众号：Plus技术栈，回复angular获取源码下载链接。

