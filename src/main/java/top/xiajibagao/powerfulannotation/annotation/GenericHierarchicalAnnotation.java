package top.xiajibagao.powerfulannotation.annotation;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import top.xiajibagao.powerfulannotation.annotation.attribute.AnnotationAttribute;
import top.xiajibagao.powerfulannotation.annotation.attribute.CacheableAnnotationAttribute;
import top.xiajibagao.powerfulannotation.helper.AnnotationUtils;
import top.xiajibagao.powerfulannotation.helper.ReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link HierarchicalAnnotation}的基本实现
 *
 * @author huangchengxing
 */
@Getter
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public class GenericHierarchicalAnnotation<T extends Annotation> implements HierarchicalAnnotation<T> {

    /**
     * 根对象
     */
    private final Object root;

    /**
     * 与根对象的垂直距离
     */
    private final int verticalIndex;

    /**
     * 与根对象的水平距离
     */
    private final int horizontalIndex;

    /**
     * 注解对象
     */
    private final T annotation;

    /**
     * 注解属性
     */
    @Getter(AccessLevel.PACKAGE)
    @EqualsAndHashCode.Exclude
    private final Map<String, AnnotationAttribute> attributeMap;

    /**
     * 创建一个通用注解
     *
     * @param annotation 注解对象
     * @param root 根对象
     * @param verticalIndex 垂直索引
     * @param horizontalIndex 水平索引
     */
    public GenericHierarchicalAnnotation(T annotation, Object root, int verticalIndex, int horizontalIndex) {
        this.root = root;
        this.verticalIndex = verticalIndex;
        this.horizontalIndex = horizontalIndex;
        this.annotation = annotation;
        this.attributeMap = loadAnnotationAttributes(annotation);
    }

    /**
     * 创建一个通用注解
     *
     * @param annotation 注解对象
     */
    public GenericHierarchicalAnnotation(T annotation) {
        this.root = this;
        this.verticalIndex = 0;
        this.horizontalIndex = 0;
        this.annotation = annotation;
        this.attributeMap = loadAnnotationAttributes(annotation);
    }

    /**
     * 解析注解对象的所有属性，并将其封装为{@link CacheableAnnotationAttribute}
     *
     * @param annotation 注解对象
     * @return 属性注解
     */
    protected Map<String, AnnotationAttribute> loadAnnotationAttributes(T annotation) {
        return Stream.of(ReflectUtils.getDeclaredMethods(annotation.annotationType()))
            .filter(AnnotationUtils::isAttributeMethod)
            .collect(Collectors.toMap(Method::getName, method -> new CacheableAnnotationAttribute(annotation, method)));
    }

    /**
     * 注解是否存在该属性，且该属性的值类型是指定类型或其子类
     *
     * @param attributeName 属性名
     * @param attributeType 返回值类型
     * @return 是否存在该属性
     */
    @Override
    public boolean hasAttribute(String attributeName, Class<?> attributeType) {
        return Optional.ofNullable(attributeMap.get(attributeName))
            .filter(method -> ReflectUtils.isAssignable(attributeType, method.getAttributeType()))
            .isPresent();
    }

    /**
     * 获取注解属性
     *
     * @param attributeName 注解属性
     * @return 注解属性
     */
    @Override
    public AnnotationAttribute getAttribute(String attributeName) {
        return attributeMap.get(attributeName);
    }

    /**
     * 获取全部的注解射弩了
     *
     * @return 注解属性
     */
    @Override
    public Collection<AnnotationAttribute> getAllAttribute() {
        return attributeMap.values();
    }

    /**
     * 替换属性值
     *
     * @param attributeName 属性名称
     * @param operator 操作
     */
    @Override
    public void replaceAttribute(String attributeName, UnaryOperator<AnnotationAttribute> operator) {
        final AnnotationAttribute old = attributeMap.get(attributeName);
        attributeMap.put(attributeName, operator.apply(old));
    }

}
