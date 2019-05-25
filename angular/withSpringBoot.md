上节写了一个很简单的引入了Angular Material的Hello World demo，本节让我们将Angular6与Springboot2.1组合起来，它们将会被打成一个war包。

### 创建一个Maven工程

首先，创建一个包含两个模块的Maven工程：一个后端模块，一个前端模块。

我们把后端模块叫做plus-server，前端模块叫做plus-web，同时它们有一个父模块plus-parent。整个结构图比较简单：

![](C:\Users\g00452792\Pictures\angular\plus-parent.png)

我们通过tree命令，现在的目录大致是这样：

```
plus/
├── plus.iml
├── plus-server
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   └── resources
│       └── test
│           └── java
├── plus-web
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   └── resources
│       └── test
│           └── java
└── pom.xml

14 directories, 4 files
```

然后修改plus-parent的pom文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.plus</groupId>
    <artifactId>plus-parent</artifactId>
    <packaging>pom</packaging>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.0.RELEASE</version>
        <relativePath/>
    </parent>
    
    <modules>
        <module>plus-server</module>
        <module>plus-web</module>
    </modules>


</project>
```

然后修改后端模块的pom文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>plus-parent</artifactId>
        <groupId>com.plus</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>plus-server</artifactId>
    <packaging>war</packaging>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-integration</artifactId>
        </dependency>
        <dependency>
            <groupId>com.plus</groupId>
            <artifactId>crawler-web</artifactId>
            <version>${project.version}</version>
            <scope>runtime</scope>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <configuration>
                    <packagingExcludes>WEB-INF/lib/tomcat-*.jar</packagingExcludes>
                    <warName>web</warName>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

首先通过packaging标签选择打包方式为war，war包与jar的区别在于war包是将前端和后端代码一块编译成一个包，而jar包不包含前端，并且然后在build下面添加maven-war-plugin的插件。

在上面这步中我们已经把前端的模块plus-web引入了进来，接下来我们去修改前端模块。

前端模块的pom文件长这样：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>plus-parent</artifactId>
        <groupId>com.plus</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>plus-web</artifactId>


</project>
```

### 创建Spring Boot 应用

我们按照最简单最小化的原则来创建这个应用，仅仅创建一个controller：

```java
package com.plus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {
    
    @GetMapping
    public String home() {
        return "forward:/index.html";
    }
}
```

这里的index.html就是前端的Angular被编译之后拷贝过来的入口index.html。

### 创建Angular 应用

根据我前面几篇文章说的，创建一个Angular项目。

首先cd到plus-web/src/main目录下，执行以下命令，创建一个Angular工厂。

```
ng new web
```

修改plus-web的pom文件：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.eirslett</groupId>
            <artifactId>frontend-maven-plugin</artifactId>
            <version>1.3</version>
            <configuration>
                <nodeVersion>v8.11.3</nodeVersion>
                <npmVersion>6.3.0</npmVersion>
                <workingDirectory>src/main/web/</workingDirectory>
            </configuration>
            <executions>
                <execution>
                    <id>install node and npm</id>
                    <goals>
                        <goal>install-node-and-npm</goal>
                    </goals>
                </execution>
                <execution>
                    <id>npm install</id>
                    <goals>
                        <goal>npm</goal>
                    </goals>
                </execution>
                <execution>
                    <id>npm run build</id>
                    <goals>
                        <goal>npm</goal>
                    </goals>
                    <configuration>
                        <arguments>run build</arguments>
                    </configuration>
                </execution>
                <execution>
                    <id>prod</id>
                    <goals>
                        <goal>npm</goal>
                    </goals>
                    <configuration>
                        <arguments>run-script build</arguments>
                    </configuration>
                    <phase>generate-resources</phase>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

我们使用frontend-maven-plugin来构建整个Angular项目。

在这个pom文件中我们指定了这个项目中npm和node的版本，指定了Angular的工程的相对路径在src/main/web下。当我们执行`mvn clean install`这条命令的时候，它会按照这个pom文件中写的顺序依次执行：

* 安装node和npm
* 执行npm install
* 执行npm run build

### 配置后端pom去找到Angular的编译结果

最后我们给plus-server的pom文件指定如何寻找Angular的编译的包。

```xml
<plugin>
    <artifactId>maven-resources-plugin</artifactId>
    <executions>
        <execution>
            <id>copy-resources</id>
            <phase>validate</phase>
            <goals>
                <goal>copy-resources</goal>
            </goals>
            <configuration>
                <outputDirectory>${project.build.directory}/classes/resources/</outputDirectory>
                <resources>
                    <resource>
                        <directory>${project.parent.basedir}/plus-web/src/main/web/dist/web/</directory>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Angular编译的结果放在了dist目录下，我们把它拷贝到plus-server/target/classes/resources目录。然后打成一个war包就可以了。

### 运行

首先在根目录，也就是plus目录下执行`mvn clean install`。然后进入plus-server目录中执行：

```
mvn spring-boot:run
```

直接启动springboot应用，访问浏览器的localhost:8080。就能看到欢迎界面

![](C:\Users\g00452792\Pictures\angular\welcome.PNG)

学习Angular的同学欢迎关注专栏。需要项目源码的可以关注我的公众号：Plus技术栈，回复angular获取链接。本专栏将同步到我的公众号。