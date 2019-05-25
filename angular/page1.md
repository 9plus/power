## 搭建一个弹幕查询页面(1)

上一节将Angular与SpringBoot整合到了一块，整个应用的前后端大致是有了一个架子。本篇文章开始将把前端的弹幕查询页面写出来，我会新建一个工程来写前端页面，大家测试的时候可以直接在前后端搭好的架子中修改前端代码。

### 新建工程 && 导入Angular Material

没有安装Angular的同学先安装angular。

```
npm install -g @angular/cli
```

新建工程

```
ng new search
```

导入Angular Material

```
ng add @angular/material
```

依次选择主题，加入动画、手势支持。(默认就回车yes回车yes就好了)。

将Angular运行起来：

```
ng serve
```

此时访问浏览器地址localhost:4200，可以看到Angular的欢迎页面。

### 路由跳转

我们让用户一访问这个应用时，直接重定向到home页面，并在home页面进行弹幕的查询。

首先删除app.component.html中的其他代码，只留下`<router-outlet></router-outlet>`。因为一个Angular只有一个根模块，即app.module.ts文件，在该ts文件中，通过以下代码指定了引导组件为AppComponent。

```
boostrap: [AppComponent]
```

在AppComponent所声明的文件app.component.ts文件中通过@Component标注了它的模板html为app.component.html。

```
templateUrl: './app.component.html'
```

此时，app.component.html就是整个应用的根页面。而router-outlet标签会自动被替换为它的子组件的html页面。我们将home页面的内容写在home组件的html中，这样符合Angular的模块化组件化的特性。看代码也更清晰。

因此，首先在命令行(app目录下)执行

```
ng generate component home
```

也可以执行缩写形式的`ng g c home`，g和c就分别代表generate与component。这条命令会创建一个home目录，该目录下的4个文件就是home组件的基本组成文件了。同时还会更新根模块文件，将HomeComponent加入declarations属性中，代表这个组件就属于我这个模块了。

接下来，在app-routing.module.ts文件中添加路由设定。代码如下：

```typescript
import {HomeComponent} from './home/home.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: '**',
    redirectTo: 'home'
  },
];
```

如果用户直接访问地址：localhost:4200，就会进入第一种情况，path为空，重定向到home组件，如果是其他任意地址，也会通过redirectTo重定向到home组件。

此时前端页面显示：home workds！

### MatCardModule

完成路由的设定之后，我们修改home的界面显示，我们用\<mat-card>来作为页面的container。

同样，首先新建一个material.module.ts文件用来管理所有的Angular Material模块。可以通过

```
ng generate module material --module = app.module.ts
```

Angular会为我们补齐material后面的.module.ts。

因为这个MaterialModule模块只起到管理Angular Material的组件导入的功能，因此不需要declarations来定义属于它的组件，也不需要import来引入模块给它的子组件用。只需要用exports将Angular Material的模块导出给根模块就行。代码如下：

```typescript
import { NgModule } from '@angular/core';
import { MatCardModule } from '@angular/material';

@NgModule({
  exports: [
    MatCardModule
  ]
})
export class MaterialModule { }
```

修改home.component.html为

```html
<mat-card style="width: 48%; height: 74%; border: 1px solid #000000">
  <mat-card-content>
    ttt
  </mat-card-content>
</mat-card>
```

因为篇幅原因，css样式最终肯定是写在css文件中，这里仅为了最简单的展示效果，需要源码的同学可以关注公众号：Plus技术栈，回复angular获取源码链接。

此时的界面为：

![1558167724387](C:\Users\g00452792\AppData\Roaming\Typora\typora-user-images\1558167724387.png)

### MatInputModule

然后我们添加一个用来输入用户名的输入框，导入MatFormFieldModule， MatIconModule， MatInputModule这三个模块。在\<mat-card-content>标签下加入以下html：

```html
<mat-form-field appearance="outline">
    <mat-label>Click me</mat-label>
    <input matInput placeholder="Username">
    <mat-icon matSuffix>casino</mat-icon>
    <mat-hint>Hint</mat-hint>
</mat-form-field>
```

此时效果如下：

![1558168546483](C:\Users\g00452792\AppData\Roaming\Typora\typora-user-images\1558168546483.png)

### MatIconModule

上面导入了MatIconModule模块，Angular Material提供了许多icon，我们可以去<https://material.io/tools/icons/>中自行查找。

### MatTabModule

接着，我们添加弹幕显示区域。利用MatTab可以很好的切换不同房间的弹幕。导入MatTabsModule。添加以下html：

```html
<mat-tab-group>
  <mat-tab label="First"> Content 1 </mat-tab>
  <mat-tab label="Second"> Content 2 </mat-tab>
  <mat-tab label="Third"> Content 3 </mat-tab>
</mat-tab-group>
```

此时效果为：

![1558168911465](C:\Users\g00452792\AppData\Roaming\Typora\typora-user-images\1558168911465.png)

### MatPaginatorModule

添加分页。Angular Material是有关于表格分页的模块，但是并没有适合我们这种场景下的分页，需要自己实现一个。因此，再导入MatButtonModule与MatSelectModule。加入如下html。

```html
<div style="float: right;">
    <mat-form-field>
        <mat-label>Page</mat-label>
        <mat-select>
            <mat-option *ngFor="let page of [1, 2 ,3]" [value]="page">{{page}}</mat-option>
        </mat-select>
    </mat-form-field>

    <button mat-icon-button ><mat-icon>first_page</mat-icon></button>
    <button mat-icon-button ><mat-icon>chevron_left</mat-icon></button>
    <button mat-icon-button ><mat-icon>chevron_right</mat-icon></button>
    <button mat-icon-button ><mat-icon>last_page</mat-icon></button>
</div>
```

效果如下：

![1558169308355](C:\Users\g00452792\AppData\Roaming\Typora\typora-user-images\1558169308355.png)

自此，整个页面的效果如下：

![1558169538402](C:\Users\g00452792\AppData\Roaming\Typora\typora-user-images\1558169538402.png)

整个页面还有许多不完善，我将在后续的文章中补充上去。欢迎大家关注本专栏。