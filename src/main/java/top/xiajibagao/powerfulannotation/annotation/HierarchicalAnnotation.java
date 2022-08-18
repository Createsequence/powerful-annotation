package top.xiajibagao.powerfulannotation.annotation;

import top.xiajibagao.powerfulannotation.annotation.attribute.AnnotationAttribute;
import top.xiajibagao.powerfulannotation.annotation.proxy.AnnotationAttributeValueProvider;
import top.xiajibagao.powerfulannotation.helper.Hierarchical;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

/**
 * <p>表示一个处于{@link AnnotatedElement}层级结构中的注解对象，
 * 该注解对象的属性以{@link AnnotationAttribute}的形式存在，
 * 通过替换该对象可以使该实例返回与原始注解不一样的属性值。
 *
 * @param <R> 根对象类型
 * @author huangchengxing
 * @see AnnotationAttribute
 * @see AnnotationAttributeValueProvider
 * @see Hierarchical
 */
public interface HierarchicalAnnotation<R>
    extends Annotation, Hierarchical, AnnotationAttributeValueProvider {

    /**
     * 当对比两个对象时，只要{@link #getRoot()}、{@link #getHorizontalIndex()}
     * 与{@link #getVerticalIndex()}的返回值一致，则认为两者相等
     *
     * @param target 对象
     * @return 是否
     */
    @Override
    boolean equals(Object target);

    /**
     * 获取哈希值，该值应当总是由{@link #getRoot()}、{@link #getHorizontalIndex()}
     * 与{@link #getVerticalIndex()}的返回值计算得到
     *
     * @return 哈希值
     */
    @Override
    int hashCode();

    /**
     * 获取根对象
     *
     * @return 根对象
     */
    @Override
    R getRoot();

    /**
     * 获取源对象
     *
     * @return 源对象
     */
    R getSource();

    /**
     * 获取注解对象
     *
     * @return 注解对象
     */
    Annotation getAnnotation();

    /**
     * 获取注解对象类型，该方法返回值应与{@link #getAnnotation()}返回注解对象的{@link Annotation#annotationType()}相同
     *
     * @return 注解类型
     */
    @Override
    default Class<? extends Annotation> annotationType() {
        return getAnnotation().annotationType();
    }

    /**
     * 注解是否存在指定类型的属性
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
     * @return 注解属性
     */
    AnnotationAttribute getAttribute(String attributeName);

    /**
     * 获取全部的注解射弩了
     *
     * @return 注解属性
     */
    Collection<AnnotationAttribute> getAllAttribute();

    /**
     * 替换注解属性
     *
     * @param attributeName 属性名称
     * @param attribute 注解属性
     * @return 旧的注解属性
     */
    AnnotationAttribute putAttribute(String attributeName, AnnotationAttribute attribute);

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
