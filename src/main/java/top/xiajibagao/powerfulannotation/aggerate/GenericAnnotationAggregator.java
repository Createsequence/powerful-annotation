package top.xiajibagao.powerfulannotation.aggerate;

import lombok.AccessLevel;
import lombok.Getter;
import top.xiajibagao.powerfulannotation.annotation.GenericHierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.helper.Assert;
import top.xiajibagao.powerfulannotation.helper.CollUtils;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingRegistry;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link AnnotationAggregator}的基本实现
 *
 * @param <T> 数据源类型
 * @author huangchengxing
 */
public class GenericAnnotationAggregator<T> implements AnnotationAggregator<T> {

    /**
     * 根对象
     */
    @Getter
    protected final T root;

    /**
     * 与根对象的垂直距离
     */
    @Getter
    protected final int verticalIndex;

    /**
     * 与根对象的水平距离
     */
    @Getter
    protected final int horizontalIndex;

    /**
     * 可重复注解映射关系注册表
     */
    protected final RepeatableMappingRegistry repeatableMappingRegistry;

    /**
     * 被聚合的注解
     */
    @Getter(AccessLevel.PROTECTED)
    protected final Map<Class<? extends Annotation>, Collection<HierarchicalAnnotation<Annotation>>> aggregatedAnnotationMap;

    /**
     * 注解是否已经注册到可重复注解映射表中
     */
    private boolean repeatableAggregatedAnnotationRegistered;

    /**
     * 创建一个注解
     *
     * @param root 根对象
     * @param verticalIndex 垂直坐标
     * @param horizontalIndex 水平坐标
     */
    public GenericAnnotationAggregator(T root, int verticalIndex, int horizontalIndex) {
        this(root, verticalIndex, horizontalIndex, null);
    }

    /**
     * 创建一个注解
     *
     * @param root 根对象
     * @param verticalIndex 垂直坐标
     * @param horizontalIndex 水平坐标
     * @param repeatableMappingRegistry 可重复注解映射表
     */
    public GenericAnnotationAggregator(
        T root, int verticalIndex, int horizontalIndex, RepeatableMappingRegistry repeatableMappingRegistry) {
        this.root = root;
        this.verticalIndex = verticalIndex;
        this.horizontalIndex = horizontalIndex;
        this.aggregatedAnnotationMap = new LinkedHashMap<>();
        this.repeatableMappingRegistry = repeatableMappingRegistry;
    }

    /**
     * 向当前聚合中注册注解
     *
     * @param verticalIndex 垂直索引。一般表示与扫描器扫描的{@link #root}相隔的层级层次。默认从1开始
     * @param horizontalIndex 水平索引，一般用于衡量两个注解对象之间被扫描到的先后顺序。默认从1开始
     * @param annotation 被扫描到的注解对象
     */
    @Override
    public void accept(int verticalIndex, int horizontalIndex, Annotation annotation) {
        if (repeatableAggregatedAnnotationRegistered) {
            repeatableAggregatedAnnotationRegistered = false;
        }
        HierarchicalAnnotation<Annotation> hierarchicalAnnotation = new GenericHierarchicalAnnotation<>(
            annotation, this, verticalIndex, horizontalIndex
        );
        aggregatedAnnotationMap.computeIfAbsent(hierarchicalAnnotation.annotationType(), t -> new ArrayList<>())
            .add(hierarchicalAnnotation);
    }

    /**
     * 注解是否存在
     *
     * @param annotationType 属性类型
     * @return 是否
     */
    @Override
    public boolean isPresent(Class<? extends Annotation> annotationType) {
        return aggregatedAnnotationMap.containsKey(annotationType);
    }

    /**
     * 获取全部注解
     *
     * @return 全部的注解
     */
    @Override
    public Collection<HierarchicalAnnotation<Annotation>> getAllAnnotations() {
        return aggregatedAnnotationMap.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    /**
     * 获取指定类型的注解
     *
     * @param annotationType 注解类型
     * @param <A> 注解类型
     * @return 注解
     */
    @SuppressWarnings("unchecked")
    @Override
    public <A extends Annotation> Collection<HierarchicalAnnotation<A>> getAnnotationsByType(Class<A> annotationType) {
        return aggregatedAnnotationMap.get(annotationType).stream()
            .map(annotation -> (HierarchicalAnnotation<A>)annotation)
            .collect(Collectors.toList());
    }

    /**
     * 获取指定层级中的注解
     *
     * @param verticalIndex 垂直索引
     * @return 注解
     */
    @Override
    public Collection<HierarchicalAnnotation<Annotation>> getAnnotationByVerticalIndex(int verticalIndex) {
        return getAllAnnotations().stream()
            .filter(annotation -> Objects.equals(annotation.getVerticalIndex(), verticalIndex))
            .collect(Collectors.toList());
    }

    /**
     * 获取聚合中的全部可重复注解，包括该类型的注解对象，以及被嵌套在其他容器注解中的该类型注解对象
     *
     * @param annotationType 注解类型
     * @return 可重复注解对象
     * @throws IllegalArgumentException 当{@link #repeatableMappingRegistry}为空时抛出
     */
    @Override
    public <A extends Annotation> Collection<A> getRepeatableAnnotations(Class<A> annotationType) {
        Assert.notNull(repeatableMappingRegistry, "no repeatable mapping registry available");
        // 注册未解析注解
        Collection<HierarchicalAnnotation<Annotation>> annotations = getAllAnnotations();
        registerRepeatableAggregatedAnnotationIfNecessary(annotations);
        repeatableMappingRegistry.register(annotationType);
        // 通过映射表将注解转换为指定的元素注解
        return annotations.stream()
            .filter(annotation -> repeatableMappingRegistry.isContainerOf(annotationType, annotation.annotationType())
                || Objects.equals(annotationType, annotation.annotationType()))
            .map(HierarchicalAnnotation::getAnnotation)
            .map(annotation -> repeatableMappingRegistry.getElementsFromContainer(annotation, annotationType))
            .filter(CollUtils::isNotEmpty)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }
    
    /**
     * 若{@link #repeatableAggregatedAnnotationRegistered}为{@code false}，
     * 则将目前{@link #aggregatedAnnotationMap}中全部可重复注解注册到关系映射注册表中
     */
    private void registerRepeatableAggregatedAnnotationIfNecessary(Collection<HierarchicalAnnotation<Annotation>> annotations) {
        if (!repeatableAggregatedAnnotationRegistered) {
            repeatableAggregatedAnnotationRegistered = true;
            annotations.stream()
                .map(HierarchicalAnnotation::annotationType)
                .forEach(repeatableMappingRegistry::register);
        }
    }

}
