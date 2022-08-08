# powerful-annotation

![qq群](https://img.shields.io/badge/qq%E7%BE%A4-540919540-yellowgreen)![CRANE](https://img.shields.io/github/license/Createsequence/crane) ![maven--central](https://img.shields.io/badge/maven--central-0.0.1-green)

------

[DOCUMENT_EN](README-EN.md)

[中文文档](README.md)

------

## Introduction

`powerful-annotation` is a compact and independent Java annotation enhancement library. It is used to solve some pain points in the use of Java annotations in daily development, and provides more powerful annotation search capabilities, more flexible and convenient repeatable annotations, and annotation synthesis mechanism similar to spring

Its design inspiration comes from `spring-core.annotation`. In a non spring environment, `powerful-annotation` is a friendly alternative to spring annotation mechanism.

## DOCUMENT

[Wiki-CN](https://gitee.com/CreateSequence/powerful-annotation/wikis/Home)

## FEATURES

- No dependency：after the introduction, no other dependencies will be brought to the user's project；
- Get started quickly：It is a uniform static method encapsulation for all components. Users can use most of the functions through a static tool class；
- More powerful annotation search：Provide more powerful annotation search capabilities, and support searching annotations from complex hierarchical structures including class, method, attribute or meta annotation of annotations；
- More convenient repeatable annotations：It provides a more convenient way to obtain repeatable annotations, and provides more ways to create repeatable annotations on the basis of the native `@ repeatable`；
- Support synthetic annotation mechanism：It supports a spring like annotation synthesis mechanism, and can "merge" multiple related arbitrary annotations into a specific type of annotation according to a specific policy. The content of the annotation will achieve effects such as aliasing or mirroring according to the specified processing policy；

## INSTALL

Introducing Maven dependency into XML files

~~~xml
<dependency>
    <groupId>top.xiajibagao</groupId>
    <artifactId>powerful-annotation</artifactId>
    <version>${last-version}</version>
</dependency>
~~~

##  Participation, contribution and technical support

If you encounter problems, find bugs, or have any good ideas in use, welcome to issues or join QQ group: 540919540 feedback!