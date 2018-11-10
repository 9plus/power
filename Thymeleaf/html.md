[TOC]

#### `<div>`

div用来定义文档中的分区或者节，把文档分割为独立的、不同的部分。常用的内部标签有id和class。

div会自动换行，不用为每一个div都加上类或id，class用于某一类元素，id用于标识单独的唯一元素，可以对同一个div应用class或id属性，但id用于标识单独的唯一元素。

#### `th,td,tr`

td，td都是单元格标签，tr表示一行。这三个常用来定义表格

* th，表头单元格-包含表头信息。一般呈现为居中的粗体文本。
* td，标准单元格-包含数据，通常为左对齐的普通文本。

#### Thymeleaf

类似于JSP的一个前端渲染框架，通过在标签前面加一个th。

Thymeleaf中可以用fragment替换一段代码，并使用th:replace和th:include进行替换

* th:include只替换节点间的内容
* th:replace节点类型也替换了

#### headd和head的区别

header是一个类似于div的语义化标签，可用来表明一段开始时的头部，而head是html中的框架性标签，写了head表明后面开始写标题啊什么的，可以用header代替head。