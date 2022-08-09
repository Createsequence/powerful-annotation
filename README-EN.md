# powerful-annotation

![qq群](https://img.shields.io/badge/qq%E7%BE%A4-540919540-yellowgreen)![CRANE](https://img.shields.io/github/license/Createsequence/crane) ![maven--central](https://img.shields.io/badge/maven--central-0.0.1-green)

------

[DOCUMENT_EN](README-EN.md)

[中文文档](README.md)

------

## Introduction

`powerful-annotation` is a compact and independent Java annotation enhancement library. It is used to solve some pain points in the use of Java annotations in daily development, and provides more powerful annotation search capabilities, more flexible and convenient repeatable annotations, and annotation synthesis mechanism similar to spring

Its design inspiration comes from `spring-core`. In a non spring environment, `powerful-annotation` is a friendly alternative to spring annotation mechanism.

## DOCUMENT

[Wiki-CN](https://gitee.com/CreateSequence/powerful-annotation/wikis/Home)

## FEATURES

- No dependency：no other dependencies will be brought to the user's project；
- Get started quickly：most functions can be used through static methods；
- Powerful annotation search support：provide more powerful annotation search capabilities, and support searching annotations from complex hierarchical structures including class, method, attribute or meta annotation of annotations；
- Better repeatable annotation support：provide more convenient ways to build or obtain repeatable annotations；
- Annotation synthesis support：supports a spring like annotation synthesis mechanism, and can "merge" multiple related arbitrary annotations into a specific type of annotation according to a specific policy. The content of the annotation will achieve effects such as aliasing or mirroring according to the specified processing policy；

## INSTALL

Introducing Maven dependency into XML files

~~~xml
<dependency>
    <groupId>top.xiajibagao</groupId>
    <artifactId>powerful-annotation</artifactId>
    <version>${last-version}</version>
</dependency>
~~~

## QUICK START

After adding Maven dependency, we can use most of the functions by `top.xiajibagao.powerfulannotation.helper.Annotations`. Here are some basic examples:

### 1.Search annotation

<img src="https://img.xiajibagao.top/image-20220805152432933.png" alt="image-20220805152432933" style="zoom:50%;" />

~~~java
// not search hierarchy, not search meta annotations
Annotations.getDirectAnnotation(Foo.class, Annotation1.class); // annotation1
Annotations.getAllDirectAnnotations(Foo.class, Annotation2.class); // []
Annotations.getAllDirectRepeatableAnnotations(Foo.class, Annotation1.class); // [annotation1]
Annotations.isDirectAnnotationPersent(Foo.class, Annotation2.class); // false

// not search hierarchy, search meta annotations
Annotations.getIndirectAnnotation(Foo.class, Annotation2.class); // annotation2
Annotations.getAllIndirectAnnotations(Foo.class, Annotation2.class); // [annotation2]
Annotations.getAllIndirectRepeatableAnnotations(Foo.class, Annotation2.class); // [annotation2]
Annotations.isIndirectAnnotationPersent(Foo.class, Annotation2.class); // true

// search hierarchy, not search meta annotations
Annotations.findDirectAnnotation(Foo.class, Annotation3.class); // annotation3
Annotations.findAllDirectAnnotations(Foo.class, Annotation2.class); // [annotation2]
Annotations.findAllDirectRepeatableAnnotations(Foo.class, Annotation3.class); // [annotation3]
Annotations.isDirectAnnotationFound(Foo.class, Annotation2.class); // true

// search hierarchy, search meta annotations
Annotations.findIndirectAnnotation(Foo.class, Annotation3.class); // annotation3
Annotations.findAllIndirectAnnotations(Foo.class, Annotation2.class); // [annotation2, annotation2, annotation2]
Annotations.findAllIndirectRepeatableAnnotations(Foo.class, Annotation3.class); // [annotation3]
Annotations.isIndirectAnnotationFound(Foo.class, Annotation2.class); // true
~~~

### 2.Get repeatable annotation

<img src="https://img.xiajibagao.top/image-20220805172946487.png" alt="image-20220805172946487" style="zoom: 50%;" />

~~~java
// This function can be used with annotation search
Annotations.getAllDirectRepeatableAnnotations(Foo.class, Annotation1.class); // annotation1 * 7
Annotations.getAllDirectRepeatableAnnotations(Foo.class, Annotation2.class); // annotation2 * 3
Annotations.getAllDirectRepeatableAnnotations(Foo.class, Annotation3.class); // annotation1 * 1
~~~

### 3.Build repeatable annotation

In addition to supporting the JDK's annotation ` @Repeatable `, the above reusable annotation related APIs also support the extended annotation ` @Repeatable by `:

~~~java
// Use @Repeatableby to specify a container for repeatable annotations
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
// Use @Repeatable to specify a container for repeatable annotations
@Repeatable(Annotation2.class)
private @interface Annotation1 {
    String value() default "";
}
private @interface Annotation2 {
    Annotation1[] values() default {};
}
~~~

This annotation supports mixed use with ` @Repeatable '.

### 4.Synthesize annotation

annotation synthesize use like `MergedAnnotation` of  spring. It supports "merging" between multiple annotations and the alias mechanism based on attribute annotation. This function can also be used in combination with annotation search.

**Attribute mirroring**

~~~java
private @interface Annotation1 {
    @MirrorFor(attribute = "name")
    String value() default "";
    @MirrorFor(attribute = "value")
    String name() default "";
}
@Annotation1("foo")
public class Foo {}

// synthesize
Annotation1 annotation = Annotations.getSynthesizedAnnotation(Foo.class, Annotation1.class);
annotation.name(); // "foo"
annotation.value(); // "foo"
~~~

**Attribute aliasing**

~~~java
private @interface Annotation1 {
    @AliasFor(attribute = "name")
    String value() default "";
    String name() default "";
}
@Annotation1(value = "foo", name = "xxx")
public class Foo {}

// synthesize
Annotation1 annotation = Annotations.getSynthesizedAnnotation(Foo.class, Annotation1.class);
annotation.name(); // "foo"
annotation.value(); // "foo"
~~~

**Annotation synthesize**

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

// synthesize
Annotation1 annotation1 = Annotations.getSynthesizedAnnotation(Foo.class, Annotation1.class);
annotation1.value(); // "foo"
Annotation2 annotation2 = Annotations.getSynthesizedAnnotation(Foo.class, Annotation2.class);
annotation2.name(); // "foo"
~~~

## Contributing && Support

If you encounter problems, find bugs, or have any good ideas in use, welcome to issues or join QQ group: 540919540 feedback!