## 前言

[Angular Material](<https://material.angular.io/>)(下称Material)的组件样式至少是可以满足一般的个人开发需求(~~我真是毫无设计天赋~~)，也是Angular官方推荐的组件。我们通过用这个UI库来快速实现自己的idea。

在上节中我们安装了Angular，并新建了一个简单工程。这节中我们将会将Material导入工程中，简单写一个HelloWorld的例子，并讲解Angular模块中的declarations、imports、providers以及bootstrap等概念。

## 环境安装

Material简单说来就是一个库，于后端来说，无论是Java中的`import`、C++中的`#include`还是Python中的`import`，都是为了导入第三方库。那么在前端，也是需要导入UI库的，导入的UI库包含了代码逻辑(controller)与视图界面(view)。

> 参考[Quick Start](<https://material.angular.io/guide/getting-started>)安装

简单说来有4步：

**Step1**加入工程依赖

假设我已经通过`ng new`的命令创建了一个工程hello-world。进入到该工程中，执行npm install命令。

```shell
cd hello-world
npm install --save @angular/material @angular/cdk @angular/animations
```

使用-save选项将会把后面的三个依赖加入到hello-world/package.json文件中的dependencies属性下。

**Step2** 导入动画模块

step1中虽然已经加入了第三方依赖并install安装到了node_modules中，但实际上我们还没有执行import操作，将其导入到代码中。

在我们工程的根模块hello-world/src/app/app.module.ts文件中导入浏览器动画模块

```typescript
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

@NgModule({
  ...
  imports: [
    ...,
    BrowserAnimationsModule
  ],
  ...
})
export class AppModule { }
```

**Step3** 导入组件模块 

在hello-world/src/app目录下新建一个material.module.ts文件。并写下如下代码：

```typescript
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material';

@NgModule({
    exports: [
        MatButtonModule,
    ]
})
export class MaterialModule {}
```

这个文件中我们导入了两个包：`NgModule`以及`MatButtonModule`，在第四行中的@NgModule用来标注紧跟着的`MaterialModule`类是一个module类，我们定义这个类，专门用来管理Material相关的模块导入，通过exports关键字，所有import过该`MaterialModule`的模块的作用范围了组件都可以直接使用exports出来的模块。

`MatMaterialModule`是Angular Material中的按钮组件，从`@angular/material`中导入。我们将会在根模块app.module.ts文件中import这个Material模块，这样这个应用都可以使用Angular Material的组件了。

然后在根模块app.module.ts中导入MaterialModule:

```typescript
import { MaterialModule } from './material.module'

@NgModule({
  ...
  imports: [
    MaterialModule
  ],
  ...
})
export class AppModule { }
```

**Step4** 添加主题

在hello-world/src/style.css文件中引入主题的选择：

```css
@import "~@angular/material/prebuilt-themes/indigo-pink.css";
```

官方提供了4种主题，我们可以通过替换最后的xxx.css来进行选择：

- `deeppurple-amber.css`
- `indigo-pink.css`
- `pink-bluegrey.css`
- `purple-green.css`

在每一种主题下，Angular Material都给我们搭配的按钮的一些配色。我们可以在[官网]([https://material.angular.io](https://material.angular.io/))的右上角切换主题。我们也可以自定义主题，具体参考它的[guide](<https://material.angular.io/guide/theming>)，虽然我挺喜欢折腾的，但这个还是......不细说了。

**Step5** 手势支持

这个主要是指当你把鼠标移到按钮上面的悬浮提示的组件样式。(和css样式中的hover的区别可能在于这个进行了扩展？)

首先还是用npm，安装hammers，并加入到package.json的依赖中去:

```shell
npm install --save hammerjs
```

然后在hello-world/src/main.ts文件中引入

```typescript
import 'hammerjs';
```

**Step6** Icon依赖

Angular Material也有自己的一套[icon库](<https://material.io/tools/icons/?style=baseline>)，里面还挺丰富的。我们修改hello-world/src/index.html即可引入。

```html
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
```

index.html文件是Angular工程的页面入口，这个文件的body长这样：

```html
<body>
  <app-root></app-root>
</body>
```

app-root指的就是在src/app/app.component.ts中定义的'app-root'。

```typescript
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
```

在angular中，我们想复用一个组件，就可以根据这个组件的selector值，直接引用过来，这个后面具体再说。

### 模块解析

前面比较难理解的可能是为了引入Material自己的MatButtonModule组件时，将其加入到了export中。这里我们说一下Angular中的模块，各位同学可以对照着app.module.ts文件看一下。

Angular中的模块是一个带有 `@NgModule()` 装饰器的类。`@NgModule()` 装饰器是一个函数，它接受一个元数据对象，该对象的属性用来描述这个模块。其中最重要的属性如下:

* declarations: **可声明对象表**，把属于该模块的组件，定义在这个属性中。
* imports: **导入表**，本模块的组件依赖的东西，都import进来，这样declare在声明表的组件都可以找到这些依赖
* exports: **导出表**，其他模块要用到本模块的组件，我们将其导出，供其他模块使用
* providers: 本模块向属于本模块的组件提供一些服务的创建器，因为Angular中有依赖注入，当我们创建了一个服务时，必须在想要注入这个服务的对应模块的providers属性里写上它，以便于创建注入。
* bootstrap: 应用的主视图，称为*根组件*。它是应用中所有其它视图的宿主。只有*根模块*才应该设置这个 `bootstrap` 属性

所有，在我们现在这个工程中，一共有两个模块：根模块，Material管理模块。

当我们在根模块import了Material管理模块后，该Material模块中export出来的组件都可以让属于根模块的组件使用。

### Hello World

我们实现如下效果：



整个app目录如下:

```
app/
├── app-routing.module.ts
├── app.component.css
├── app.component.html
├── app.component.spec.ts
├── app.component.ts
├── app.module.ts
├── hello-world.html
└── material.module.ts
```

#### material.module.ts

首先在material.module.ts文件中引入Angular Material的按钮组件和弹出框组件：

* MatButtonModule
* MatDialogModule

```typescript
import { NgModule } from '@angular/core';
import { MatButtonModule,
        MatDialogModule } from '@angular/material';

@NgModule({
    exports: [
        MatButtonModule,
        MatDialogModule
    ]
})
export class MaterialModule {}
```

#### app.module.ts

引入MaterialModule。在这个例子中，我们在点击按钮后会出现一个弹出框，这个弹出框也是一个动态组件，动态组件需要在declarations和entryComponents中声明:

```typescript
import { MaterialModule } from './material.module'
@NgModule({
  declarations: [
    AppComponent,
    HelloWorldDialogComponent
  ],
  entryComponents: [HelloWorldDialogComponent],
  imports: [
    MaterialModule
  ],
  ...
})
export class AppModule { }
```

#### app.component.html

我们在html中只用的了一个button，并指定它为Angular Material中的raised-button(关于Angular的button后面会提到)。指定点击事件为`openDialog()`。

```html
<button mat-raised-button (click)="openDialog()">Hello World</button>
```

#### app.component.ts && hello-world.html

在ts文件中，我们声明了两个component，其中`HelloWorldDialogComponent`为动态的弹框组件，接受一个字符串参数。当我们点击页面上的button之后，触发`openDialog()`方法，传递一个'Hello World!'字符串过去，显示在动态组件的html中。

```typescript
import { Component, Inject } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'hello-world';

  constructor(public dialog: MatDialog) {}

  openDialog() {
    this.dialog.open(HelloWorldDialogComponent, {
      data: 'Hello World!'
    });
  }
}

@Component({
  selector: 'hello-world',
  templateUrl: './hello-world.html',
})
export class HelloWorldDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: string) {}
}
```

在动态组件中我们指定了templateUrl是当前目录下的hello-world.html文件。

```html
<h1 mat-dialog-title>Angular</h1>
<div mat-dialog-content>
    {{data}}
</div>
```

除了以上写法外，我们还可以直接用TAB键上的符号书写html模版，不用再新建一个文件了。

```typescript
@Component({
  selector: 'hello-world',
  templateUrl: `
    <h1 mat-dialog-title>Angular</h1>
    <div mat-dialog-content>
    {{data}}
    </div>
    		`,
})
export class HelloWorldDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: string) {}
}
```

### To Be Continue

本节主要写的是如何搭环境，以及Angular的模块的一些理解。后面将详细介绍Angular Material中的组件的使用。有问题的同学欢迎留言交流，想要获取源码的同学可以关注我的公众号: Plus技术栈，回复Angular获取链接。