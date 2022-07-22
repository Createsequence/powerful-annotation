package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 支持扫描具有层次结构的{@link AnnotatedElement}的通用注解扫描器
 *
 * @author huangchengxing
 */
@Getter
public class GenericTypeHierarchyScanner extends AbstractHierarchyScanner<GenericTypeHierarchyScanner> {

    /**
     * 是否扫描元注解
     */
    private final boolean includeMetaAnnotation;

    /**
     * 是否允许扫描父类
     */
    private final boolean includeSuperClass;

    /**
     * 是否允许扫描父接口
     */
    private final boolean includeInterfaces;

    /**
     * 构造一个类注解扫描器
     *
     * @param includeSuperClass 是否允许扫描父类
     * @param includeInterfaces 是否允许扫描父接口
     * @param filter            过滤器
     * @param excludeTypes      不包含的类型
     */
    protected GenericTypeHierarchyScanner(
        boolean includeSuperClass, boolean includeInterfaces, boolean includeMetaAnnotation,
        Predicate<Class<?>> filter, Set<Class<?>> excludeTypes) {
        super(filter, excludeTypes);
        this.includeSuperClass = includeSuperClass;
        this.includeInterfaces = includeInterfaces;
        this.includeMetaAnnotation = includeMetaAnnotation;
    }

    /**
     * 从注解的元素中获得需要遍历的类，
     * 若已经是{@link Class}，则直接返回；
     * 若是{@link Member}，则返回其{@link Member#getDeclaringClass()}
     *
     * @param annotatedElement 注解元素
     */
    @Override
    protected Class<?> getClassFormAnnotatedElement(AnnotatedElement annotatedElement) {
        if (annotatedElement instanceof Class) {
            return (Class<?>)annotatedElement;
        }
        if (annotatedElement instanceof Member) {
            return ((Member)annotatedElement).getDeclaringClass();
        }
        throw new IllegalArgumentException(CharSequenceUtil.format("cannot get class from element [{}]", annotatedElement));
    }
    
    /**
     * 扫描目标类，若有必要，则将其接口、父类与声明的注解类都加入队列
     *
     * @param nextClassQueue 下一轮处理的类
     * @param targetClass 当前正在处理的类
     */
    @Override
    protected void collectToQueue(List<Class<?>> nextClassQueue, Class<?> targetClass) {
        // 扫描父类
        scanSuperClassIfNecessary(nextClassQueue, targetClass);
        // 扫描接口
        scanInterfaceIfNecessary(nextClassQueue, targetClass);
        // 扫描父接口
        scanMetaAnnotationIfNecessary(nextClassQueue, targetClass);
    }

    /**
     * 若{@link #includeInterfaces}为{@code true}，则将目标类的父接口也添加到nextClasses
     *
     * @param nextClasses 下一个类集合
     * @param targetClass 目标类型
     */
    protected void scanInterfaceIfNecessary(List<Class<?>> nextClasses, Class<?> targetClass) {
        if (includeInterfaces) {
            final Class<?>[] interfaces = targetClass.getInterfaces();
            if (ArrayUtil.isNotEmpty(interfaces)) {
                CollUtil.addAll(nextClasses, interfaces);
            }
        }
    }

    /**
     * 若{@link #includeSuperClass}为{@code true}，则将目标类的父类也添加到nextClasses
     *
     * @param nextClassQueue 下一个类队列
     * @param targetClass    目标类型
     */
    protected void scanSuperClassIfNecessary(List<Class<?>> nextClassQueue, Class<?> targetClass) {
        if (includeSuperClass) {
            final Class<?> superClass = targetClass.getSuperclass();
            if (!ObjectUtil.equals(superClass, Object.class) && ObjectUtil.isNotNull(superClass)) {
                nextClassQueue.add(superClass);
            }
        }
    }

    /**
     * 若{@link #includeMetaAnnotation}为{@code true}，则将目标类声明的注解也添加到nextClasses
     *
     * @param nextClasses 下一个类集合
     * @param targetClass 目标类型
     */
    protected void scanMetaAnnotationIfNecessary(List<Class<?>> nextClasses, Class<?> targetClass) {
        if (includeMetaAnnotation) {
            Stream.of(targetClass.getDeclaredAnnotations())
                .map(Annotation::annotationType)
                .forEach(nextClasses::add);
        }
    }

    /**
     * 从目标类中获得待处理的注解
     *
     * @param source
     * @param index
     * @param targetClass
     * @return java.lang.annotation.Annotation[]
     */
    @Override
    protected Annotation[] getAnnotationsFromTargetClass(AnnotatedElement source, int index, Class<?> targetClass) {
        return source.getDeclaredAnnotations();
    }

}
