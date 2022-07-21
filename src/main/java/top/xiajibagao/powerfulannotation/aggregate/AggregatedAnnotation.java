package top.xiajibagao.powerfulannotation.aggregate;

import top.xiajibagao.powerfulannotation.aggregate.attribute.AnnotationAttribute;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * 表示一个处于聚合状态的注解对象，该注解对象通常可通过{@link AnnotationAggregator}访问
 *
 * @param <T> 注解类型
 * @author huangchengxing
 */
public interface AggregatedAnnotation<T extends Annotation>
    extends Annotation, Hierarchical, AnnotationAttributeValueProvider {
    
    /**
     * 获取注解对象
     *
     * @return java.lang.annotation.Annotation
     */
    T getAnnotation();

    /**
     * 获取注解对象类型，该方法返回值应与{@link #getAnnotation()}返回注解对象的{@link Annotation#annotationType()}相同
     *
     * @return java.lang.Class<? extends java.lang.annotation.Annotation>
     */
    @Override
    default Class<? extends Annotation> annotationType() {
        return getAnnotation().annotationType();
    }

    /**
     * 注解是否存在该属性，且该属性的值类型是指定类型或其子类
     *
     * @param attributeName 属性名
     * @param attributeType 返回值类型
     * @return 是否存在该属性
     */
    boolean hasAttribute(String attributeName, Class<?> attributeType);

    /**
     * 获取注解属性
     *
     * @param attributeName 注解属性
     * @return top.xiajibagao.powerfulannotation.aggregate.attribute.AnnotationAttribute
     */
    AnnotationAttribute getAttribute(String attributeName);

    /**
     * 获取全部的属性值
     *
     * @return java.util.Collection<top.xiajibagao.powerfulannotation.aggregate.attribute.AnnotationAttribute>
     */
    Collection<AnnotationAttribute> getAllAttribute();

    /**
     * 替换属性值
     *
     * @param attributeName 属性名称
     * @param operator 操作
     */
    void replaceAttribute(String attributeName, UnaryOperator<AnnotationAttribute> operator);

    /**
     * 获取属性值
     *
     * @param attributeName 属性名称
     * @param attributeType 属性类型
     * @return java.lang.Object
     */
    @Override
    default Object getAttributeValue(String attributeName, Class<?> attributeType) {
        return hasAttribute(attributeName, attributeType) ? getAttribute(attributeName).getValue() : null;
    }

}
