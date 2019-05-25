## 初识Angular

Angular 诞生于2009年，由Misko Hevery 等人创建，后为Google所收购。是一款优秀的前端框架。从09年到今天，发展了十年，Angular的整个生态还是比较成熟的。

Angular分为**Angular1**(也叫AngularJS)和**Angular2+**的版本，Angular2及以后Angular4、5等版本都是基于[TypeScript语言](<https://zh.wikipedia.org/wiki/TypeScript>)，而Angular1是基于JavaScript语言，这两种版本的Angular完全可以当做两个框架。

在本专栏中将使用最新基于TypeScript的Angular7。TypeScript是JavaScript的超集，语法有点像是前端的Java。学起来也是挺简单。TS的语法就不多做介绍了，大家可以去官方文档看一看，中文官方文档[见此](<https://www.tslang.cn/docs/home.html>)。

在本篇中，我将带你们搭建一个最简单Angular编译运行环境，创建一个工程，达到能访问的效果。

## 新建工程

> [Angular官方文档](<https://angular.io/guide/quickstart>)  英文好的同学建议看看。

在安装Angular之前，需要先安装Node.js，Angular要求Node.js的版本为8.x或者10.x。这里请大家自行安装，安装完成后使用`node -v`命令查看版本。安装node的同时也会安装npm这个包管理器，我们将通过npm命令安装Angular。

**Step1**

任意目录下执行以下命令，安装angular cli。

```shell
npm install -g @angular/cli
```

**Step2**

找一个目录，创建Angular工程，指定工程名(如my-app)。

```shell
ng new my-app
```

默认可以连续按两个回车。在安装了angular cli之后，我们可以使用很多ng的命令。

**Step3**

启动这个新建的工程

```shell
cd my-app
ng serve --open
```

`ng serve`命令启动angular服务，同时监听你的文件改动，一旦发生变化，工程就会立刻重新编译，达到实时刷新的效果。这称之为**热部署**。

`--open`或者`-o`选项会自动打开浏览器http://localhost:4200/

此时可以访问上述网址，看到Angular的欢迎界面。

## 工程分析

```shell
my-app/
├── angular.json           本工程的一些配置
├── e2e                    自动化测试目录
├── node_modules           第三方依赖包存放目录
├── package.json           npm工具的配置文件，这里列出了本工程依赖的第三方包
├── package-lock.json      package.json的补充文件
├── README.md              说明文件
├── src                    代码目录
├── tsconfig.json          TypeScript的配置文件
└── tslint.json            tslint的配置文件，定义了TypeScript代码质量检查规则，不用管

3 directories, 6 files
```

my-app的目录如上所示。其他暂时不用管。

在my-app目录下我们可以执行一些npm的命令，这些命令在package.json中有列出。

```shell
"scripts": {
  "ng": "ng",
  "start": "ng serve",
  "build": "ng build",
  "test": "ng test",
  "lint": "ng lint",
  "e2e": "ng e2e"
 },
```

比如，`npm run start`就是执行`ng -serve`。如果想在启动的时候指定端口为8080，可以这样修改：

```shel
"start": "ng serve --port 8080",
```

所以执行`npm run start`也可以启动Angular服务。



了解了Angular工程的一些启动配置后，再看看代码。

在浏览器打开默认的网址http://localhost:4200/所展示的页面html写在`my-app/src/app/app.component.html`中。

Angular2以后是基于component(组件)的架构。我们在`my-app/src/app/`下输入

```shell
ng generate component example
```

可以看到生成了4个文件，并更新了`app.module.ts`。可以知道Angular中的一个componet包含了：

```
example/
├── example.component.css
├── example.component.html
├── example.component.spec.ts
└── example.component.ts

0 directories, 4 files
```

# To Be Continue

上面只是简单的讲了讲Angular的环境搭建，那么有了这样一个搭好的工程，如何修改如果实现我们自己想要的页面呢。这里推荐大家去看一下[Angular官方教程](<https://angular.io/tutorial>)，它里面的Hero例子写的非常好。

除了看官方例子外，也可以等待我之后的更新，接下来我将会写Angular Material的相关使用，对Angular不熟悉的同学可以在接下来的几篇文章中弄明白如何使用Angular。