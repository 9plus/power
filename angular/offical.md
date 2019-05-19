Angular应用至少会有一个根模块(module)，通常还会有许多特性模块(feature modules)。

组件定义视图(Component defines views)。也就是说在angular中通过一个个component来实现界面视图的显示。

组件使用服务(Component use service)。一些与视图无关的代码逻辑我们可以抽出来作为服务。服务可以作为**依赖**注入到组件中。

*directives*： 指令

*binding markup*： 绑定标记

---

### 注解

@Injectable 后面跟的是服务(*service*)

@Component 后面跟的是组件(*component*)

@NgModule 后面跟的是模块(module)，一个app中至少有一个根模块，0个或者几个特性模块。

@Pipe 管道

@Directive 指令

注解也叫做元数据(*metadata*)

#### @NgModule

- `declarations`（可声明对象表） —— 那些属于本 NgModule 的[组件](https://www.angular.cn/guide/architecture-components)、*指令*、*管道*。
- `exports`（导出表） —— 属于当前模块，但是要给其他模块使用的组件的集合，一般是declarations的子集
- `imports`（导入表） —— 本模块的组件依赖的组件表
- `providers` —— 本模块向全局服务中贡献的那些[服务](https://www.angular.cn/guide/architecture-services)的创建器。 这些服务能被本应用中的任何部分使用。（你也可以在组件级别指定服务提供商，这通常是首选方式。）
- `bootstrap` —— 应用的主视图，称为*根组件*。它是应用中所有其它视图的宿主。只有*根模块*才应该设置这个 `bootstrap` 属性。