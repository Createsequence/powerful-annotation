package top.xiajibagao.powerfulannotation.annotation.attribute;

import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * <p>用于在{@link HierarchicalAnnotation}中表示注解的某个特定属性，
 * 等同于绑定了调用对象的{@link Method}方法。<br>
 * 提供了默认实现{@link CacheableAnnotationAttribute},
 * 与扩展接口{@link WrappedAnnotationAttribute}。
 *
 * @author huangchengxing
 * @see WrappedAnnotationAttribute
 */
public interface AnnotationAttribute {

    /**
     * 获取注解对象
     *
     * @return 注解对象
     */
    Annotation getAnnotation();

    /**
     * 获取注解属性对应的方法
     *
     * @return 注解属性对应的方法
     */
    Method getAttribute();

    /**
     * 获取声明属性的注解类
     *
     * @return 声明注解的注解类
     */
    default Class<?> getAnnotationType() {
        return getAttribute().getDeclaringClass();
    }

    /**
     * 获取属性名称
     *
     * @return 属性名称
     */
    default String getAttributeName() {
        return getAttribute().getName();
    }

    /**
     * 获取注解属性
     *
     * @return 注解属性
     */
    Object getValue();

    /**
     * 该注解属性的值是否等于默认值
     *
     * @return 该注解属性的值是否等于默认值
     */
    boolean isValueEquivalentToDefaultValue();

    /**
     * 获取属性类型
     *
     * @return 属性类型
     */
    default Class<?> getAttributeType() {
        return getAttribute().getReturnType();
    }

    /**
     * 当前注解属性是否已经被{@link WrappedAnnotationAttribute}包装
     *
     * @return boolean
     */
    default boolean isWrapped() {
        return false;
    }

}
