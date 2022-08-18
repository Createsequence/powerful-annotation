package top.xiajibagao.powerfulannotation.annotation;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import top.xiajibagao.powerfulannotation.annotation.attribute.AnnotationAttribute;
import top.xiajibagao.powerfulannotation.annotation.attribute.CacheableAnnotationAttribute;
import top.xiajibagao.powerfulannotation.helper.Annotations;
import top.xiajibagao.powerfulannotation.helper.AssertUtils;
import top.xiajibagao.powerfulannotation.helper.ReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link HierarchicalAnnotation}的基本实现
 *
 * @author huangchengxing
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public class GenericHierarchicalAnnotation<R> implements HierarchicalAnnotation<R> {

    /**
     * 根对象
     */
    @EqualsAndHashCode.Include
    protected R root;

    /**
     * 源对象
     */
    private final R source;

    /**
     * 与根对象的垂直距离
     */
    @EqualsAndHashCode.Include
    private final int verticalIndex;

    /**
     * 与根对象的水平距离
     */
    @EqualsAndHashCode.Include
    private final int horizontalIndex;

    /**
     * 注解对象
     */
    private final Annotation annotation;

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
    public GenericHierarchicalAnnotation(Annotation annotation, R root, R source, int verticalIndex, int horizontalIndex) {
        AssertUtils.notNull(annotation, "annotation must not null");
        this.root = root;
        this.source = source;
        this.verticalIndex = verticalIndex;
        this.horizontalIndex = horizontalIndex;
        this.annotation = annotation;
        this.attributeMap = loadAnnotationAttributes(annotation);
    }

    /**
     * 解析注解对象的所有属性，并将其封装为{@link CacheableAnnotationAttribute}
     *
     * @param annotation 注解对象
     * @return 属性注解
     */
    protected Map<String, AnnotationAttribute> loadAnnotationAttributes(Annotation annotation) {
        return Stream.of(ReflectUtils.getDeclaredMethods(annotation.annotationType()))
            .filter(Annotations::isAttributeMethod)
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
     * 替换注解属性
     *
     * @param attributeName 属性名称
     * @param attribute 注解属性
     * @return 旧的注解属性
     */
    @Override
    public AnnotationAttribute putAttribute(String attributeName, AnnotationAttribute attribute) {
        AssertUtils.notNull(attribute, "attribute must not null");
        return attributeMap.put(attributeName, attribute);
    }
}
