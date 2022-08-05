# powerful-annotation

![qq群](https://img.shields.io/badge/qq%E7%BE%A4-540919540-yellowgreen)![CRANE](https://img.shields.io/github/license/Createsequence/crane) ![maven--central](https://img.shields.io/badge/maven--central-0.0.1-green)

### 简介

powerful-annotation 是一个小巧的、无依赖的 java 注解增强库。它用于解决日常开发中 java 注解使用的一些痛点，提供包括更强大的注解搜索能力，更灵活更便利的可重复注解，与类似 spring 的注解合成机制。

它的设计灵感来自于 `spring-core` 中的注解包，在非 `spring` 环境下， `powerful-annotation` 是 `spring` 注解机制的友好替代。

### 文档

[Wiki-CN](https://gitee.com/CreateSequence/powerful-annotation/wikis/Home)

### 特性

- 无依赖：只在编译期依赖了 lombok，引入后不会为用户的项目再带来其他的依赖；
- 快速上手：为各项组件统一的静态方法封装，用户通过一个静态工具类即可使用大部分功能；
- 更强大的注解搜索：提供更强大的注解搜索能力，支持从包括类、方法、属性或者注解的元注解等复杂层级结构中搜索注解；
- 更灵活更便利的可重复注解：提供更便利的可重复注解获取方式，并且在原生 `@Repeatable` 的基础上，提供了更多可重复注解的创建途径；
- 支持合成注解机制：支持类似 spring 注解合成机制，可以将多个相关的任意注解按照特定的策略“合并”为一个特定类型的注解，该注解的属性将根据指定的处理策略实现诸如别名，或者镜像等效果；

### 快速上手

引入 maven 依赖即可使用：

~~~xml
<dependency>
    <groupId>top.xiajibagao</groupId>
    <artifactId>powerful-annotation</artifactId>
    <version>${last-version}</version>
</dependency>
~~~

###  参与贡献和技术支持

如果在使用中遇到了问题、发现了 bug ，又或者是有什么好点子，欢迎在 issues 或者加入 QQ 群：540919540 反馈！
