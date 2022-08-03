# powerful-annotation

![CRANE](https://img.shields.io/github/license/Createsequence/crane) ![maven--central](https://img.shields.io/badge/maven--central-0.0.1-green)

### 简介
一个小巧的 java 注解增强库，让注解更加好用。

### 特性

- 只在编译期依赖了 lombok，引入后不会为用户的项目再带来其他的依赖；
- 提供统一的静态方法封装，一个类搞定所有功能；
- 提供更灵活更强大的注解搜索能力，支持搜索包括类、方法、属性等对象的层级结构；
- 在原生 `@Repeatable` 的基础上，为可重复的注解提供更多实现方式；
- 支持类似 spring 但是更灵活的注解合成机制，可以将任意注解按照特定的策略聚合为一个特定类型的注解，该注解的属性将根据指定的处理策略实现各种效果；

### 快速上手

引入 maven 依赖即可使用：

~~~xml
<dependency>
    <groupId>top.xiajibagao</groupId>
    <artifactId>powerful-annotation</artifactId>
    <version>${last-version}</version>
</dependency>
~~~

准备三个普通类与三个注解类：

- 普通类 `Foo`，他有一个父类 `FooSuper`，一个父接口 `FooInterface`;
- 注解类 `@Annotation1` 和 `@Annotation2`，它们都有一个元注解 `@Annotation3`；

令 `Foo`、`FooSuper` 和 `FooInterface` 都同时添加 `@Annotation2` 和 `@Annotation1` 注解：

![image-20220803132654142](https://img.xiajibagao.top/image-20220803132654142.png)

#### 1.注解搜索

参考 spring，`powerful-annotation` 提供 `get` 和 `find` 两种语义的搜索：

-  `get`  ：只搜索对象直接声明的注解；
-  `find` ：搜索对象及对象的层级结构；

同时，针对是否查找元注解，还额外提供 `indirect` 和 `direct` 两种语义的搜索：

- `direct`：只搜索注解本身，而不搜索它的元注解；
- `indirect`：搜索元素本身，并搜索它的元注解；

针对上述的例子，`powerful-annotation` 提供以下方法：

~~~java
// 查找Foo上的注解
Annotation2 annotation = Annotations.getDirectAnnotation(Foo.class, Annotation2.class);

// 查找Foo上的注解，及关联的元注解

// 查找Foo和层级结构中的注解

// 查找Foo和层级结构中的注解，及关联的元注解

~~~







#### 2.可重复注解

#### 3.注解合成
