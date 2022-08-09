# powerful-annotation

![qq群](https://img.shields.io/badge/qq%E7%BE%A4-540919540-yellowgreen)![powerful-annotation](https://img.shields.io/github/license/Createsequence/crane) ![maven--central](https://img.shields.io/badge/maven--central-0.0.1-green)

------

[DOCUMENT_EN](README-EN.md)

[中文文档](README.md)

------

## 简介

`powerful-annotation` 是一个小巧的、无依赖的 java 注解增强库。它用于解决日常开发中 java 注解使用的一些痛点，提供包括更强大的注解搜索能力，更灵活更便利的可重复注解，与类似 spring 的注解合成机制。

它的设计灵感来自于 `spring-core` 中的注解包，在非 `spring` 环境下， `powerful-annotation` 是 `spring` 注解机制的友好替代。

## 文档

[Wiki-CN](https://gitee.com/CreateSequence/powerful-annotation/wikis/Home)

## 特性

- 无依赖：引入后不会为用户的项目再带来其他的依赖；
- 快速上手：用户通过一个静态工具类即可使用大部分功能；
- 注解搜索支持：提供更强大的注解搜索能力，支持从包括类、方法、属性或者注解的元注解等复杂层级结构中搜索注解；
- 可重复注解支持：提供更便利的可重复注解获取方式，支持更多可重复注解构建方式；
- 注解合成支持：支持类似 spring 注解合成机制，可以将多个相关的任意注解按照特定的策略“合并”为一个特定类型的注解，从而变相支持注解之间的“继承”；

## 安装

引入 maven 依赖即可使用：

~~~xml
<dependency>
    <groupId>top.xiajibagao</groupId>
    <artifactId>powerful-annotation</artifactId>
    <version>${last-version}</version>
</dependency>
~~~

## 快速使用

添加 maven 依赖后，引入通用工具类 `Annotations` 即可使用大部分功能。下面演示一些基本的功能：

### 1.注解搜索

<img src="https://img.xiajibagao.top/image-20220805152432933.png" alt="image-20220805152432933" style="zoom:50%;" />

~~~java
// 不搜索层级结构，不搜索元注解
Annotations.getDirectAnnotation(Foo.class, Annotation1.class); // annotation1
Annotations.getAllDirectAnnotations(Foo.class, Annotation2.class); // []
Annotations.getAllDirectRepeatableAnnotations(Foo.class, Annotation1.class); // [annotation1]
Annotations.isDirectAnnotationPersent(Foo.class, Annotation2.class); // false

// 不搜索层级结构以及搜索元注解
Annotations.getIndirectAnnotation(Foo.class, Annotation2.class); // annotation2
Annotations.getAllIndirectAnnotations(Foo.class, Annotation2.class); // [annotation2]
Annotations.getAllIndirectRepeatableAnnotations(Foo.class, Annotation2.class); // [annotation2]
Annotations.isIndirectAnnotationPersent(Foo.class, Annotation2.class); // true

// 搜索层级结构，不搜索元注解
Annotations.findDirectAnnotation(Foo.class, Annotation3.class); // annotation3
Annotations.findAllDirectAnnotations(Foo.class, Annotation2.class); // [annotation2]
Annotations.findAllDirectRepeatableAnnotations(Foo.class, Annotation3.class); // [annotation3]
Annotations.isDirectAnnotationFound(Foo.class, Annotation2.class); // true

// 搜索层级结构以及元注解
Annotations.findIndirectAnnotation(Foo.class, Annotation3.class); // annotation3
Annotations.findAllIndirectAnnotations(Foo.class, Annotation2.class); // [annotation2, annotation2, annotation2]
Annotations.findAllIndirectRepeatableAnnotations(Foo.class, Annotation3.class); // [annotation3]
Annotations.isIndirectAnnotationFound(Foo.class, Annotation2.class); // true
~~~

### 2.获取可重复注解

<img src="https://img.xiajibagao.top/image-20220805172946487.png" alt="image-20220805172946487" style="zoom: 50%;" />

~~~java
// 该功能支持与注解搜索配合使用
Annotations.getAllDirectRepeatableAnnotations(Foo.class, Annotation1.class); // annotation1 * 7
Annotations.getAllDirectRepeatableAnnotations(Foo.class, Annotation2.class); // annotation2 * 3
Annotations.getAllDirectRepeatableAnnotations(Foo.class, Annotation3.class); // annotation1 * 1
~~~

### 3.构建可重复注解

上述可重复注解相关 API 除支持基于 JDK 自带的 `@Repeatable` 外，还额外支持扩展注解 `@RepeatableBy`：

~~~java
// 使用@RepeatableBy指定可重复注解的容器
@RepeatableBy(annotation = Annotation2.class, attribute = "annotations")
private @interface Annotation1 {
    String value() default "";
}
private @interface Annotation2 {
    Annotation1[] annotations() default {};
}
~~~

该写法等同于：

~~~java
// 使用原生的@Repeatable指定可重复注解的容器
@Repeatable(Annotation2.class)
private @interface Annotation1 {
    String value() default "";
}
private @interface Annotation2 {
    Annotation1[] values() default {};
}
~~~

该注解支持与 `@Repeatable` 混合使用。

### 4.注解合成

合成注解使用类似 `spring` 的 `MergedAnnotation`，支持多个注解之间的“合并”，与基于属性注解的别名机制，该功能同样支持与注解搜索结合使用。

**属性镜像**

~~~java
private @interface Annotation1 {
    @MirrorFor(attribute = "name")
    String value() default "";
    @MirrorFor(attribute = "value")
    String name() default "";
}
@Annotation1("foo")
public class Foo {}

// synthesis
Annotation1 annotation = Annotations.getSynthesizedAnnotation(Foo.class, Annotation1.class);
annotation.name(); // "foo"
annotation.value(); // "foo"
~~~

**属性别名**

~~~java
private @interface Annotation1 {
    @AliasFor(attribute = "name")
    String value() default "";
    String name() default "";
}
@Annotation1(value = "foo", name = "xxx")
public class Foo {}

// synthesis
Annotation1 annotation = Annotations.getSynthesizedAnnotation(Foo.class, Annotation1.class);
annotation.name(); // "foo"
annotation.value(); // "foo"
~~~

**元注解合成**

~~~java
// meta annotation
private @interface Annotation1 {
    String name() default "";
}
// child annotation
@Annotation1("default")
private @interface Annotation2 {
    @AliasFor(annotation = Annotation1.class, attribute = "name")
    String value() default "";
}
// child annotation only
@Annotation2("foo")
public class Foo {}

Annotation1 annotation1 = Annotations.getSynthesizedAnnotation(Foo.class, Annotation1.class);
annotation1.value(); // "foo"
Annotation2 annotation2 = Annotations.getSynthesizedAnnotation(Foo.class, Annotation2.class);
annotation2.name(); // "foo"
~~~

##  参与贡献和技术支持

如果在使用中遇到了问题、发现了 bug ，又或者是有什么好点子，欢迎在 issues 或者加入 QQ 群：540919540 反馈！
