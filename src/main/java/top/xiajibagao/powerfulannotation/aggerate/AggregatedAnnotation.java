package top.xiajibagao.powerfulannotation.aggerate;

import top.xiajibagao.powerfulannotation.aggerate.attribute.AnnotationAttribute;
import top.xiajibagao.powerfulannotation.aggerate.attribute.AnnotationAttributeValueProvider;
import top.xiajibagao.powerfulannotation.helper.Hierarchical;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * <p>表示一个处于聚合状态的注解对象，该注解对象的属性以{@link AnnotationAttribute}的形式存在，
 * 通过替换{@link AnnotationAttribute}可以使该聚合注解返回与原始注解不一样的属性值。
 *
 * @param <T> 注解类型
 * @author huangchengxing
 * @see AnnotationAttribute
 * @see AnnotationAttributeValueProvider
 * @see Hierarchical
 */
public interface AggregatedAnnotation<T extends Annotation>
    extends Annotation, Hierarchical, AnnotationAttributeValueProvider {

    /**
     * 获取注解对象
     *
     * @return 注解对象
     */
    T getAnnotation();

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
     * 替换属性值
     *
     * @param attributeName 属性名称
     * @param operator 替换操作
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
