package top.xiajibagao.powerfulannotation.aggregate;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import top.xiajibagao.powerfulannotation.helper.FuncUtils;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingRegistry;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * {@link AnnotationAggregate}的基本实现
 *
 * @author huangchengxing
 */
@Getter
public class GenericAnnotationAggregate<T> implements AnnotationAggregate<T> {

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
     * 获取聚合注解的来源
     */
    private final T source;

    /**
     * 被聚合的注解
     */
    private final Map<Class<? extends Annotation>, Collection<AggregatedAnnotation<? extends Annotation>>> aggregatedAnnotationMap;

    /**
     * 可重复注解映射关系注册表
     */
    private final RepeatableMappingRegistry mappingRegistry;

    /**
     * 注解过滤器
     */
    private final Predicate<Annotation> annotationFilter;

    /**
     * 创建一个聚合注解
     *
     * @param source 数据源
     * @param root 根对象
     * @param verticalIndex 垂直坐标
     * @param horizontalIndex 水平坐标
     */
    public GenericAnnotationAggregate(
        T source, Object root, int verticalIndex, int horizontalIndex, Predicate<Annotation> filter, RepeatableMappingRegistry mappingRegistry) {
        Assert.notNull(source, "source must not null");
        Assert.notNull(filter, "filter must not null");
        Assert.notNull(mappingRegistry, "mappingRegistry must not null");
        this.source = source;
        this.root = root;
        this.verticalIndex = verticalIndex;
        this.horizontalIndex = horizontalIndex;
        this.annotationFilter = ObjectUtil.defaultIfNull(filter, FuncUtils.alwaysTrue());
        this.mappingRegistry = mappingRegistry;
        this.aggregatedAnnotationMap = new LinkedHashMap<>();
    }

    /**
     * 将注解加载到当前聚合中
     *
     * @param verticalIndex 与被扫描对象的垂直距离
     * @param horizontalIndex 与被扫描对象的水平距离
     * @param annotation 注解对象
     */
    @Override
    public void process(int verticalIndex, int horizontalIndex, Annotation annotation) {
        registerAggregatedAnnotation(verticalIndex, horizontalIndex , annotation);
    }

    /**
     * 向当前容器注册聚合注解
     */
    protected void registerAggregatedAnnotation(int verticalIndex, int horizontalIndex, Annotation annotation) {
        AggregatedAnnotation<? extends Annotation>  aggregatedAnnotation = new GenericAggregatedAnnotation<>(
            annotation, source, verticalIndex, horizontalIndex
        );
        aggregatedAnnotationMap.computeIfAbsent(aggregatedAnnotation.annotationType(), t -> new ArrayList<>())
            .add(aggregatedAnnotation);
    }

    /**
     * 注解是否存在
     *
     * @param annotationType 属性类型
     * @return boolean
     */
    @Override
    public boolean isPresent(Class<? extends Annotation> annotationType) {
        return aggregatedAnnotationMap.containsKey(annotationType);
    }

    /**
     * 获取指定类型的注解
     *
     * @param annotationType 注解类型
     * @param <A> 注解类型
     * @return java.util.Collection<top.xiajibagao.powerfulannotation.aggregate.AggregatedAnnotation>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <A extends Annotation> Collection<AggregatedAnnotation<A>> getAnnotationsByType(Class<A> annotationType) {
        return aggregatedAnnotationMap.get(annotationType).stream()
            .map(annotation -> (AggregatedAnnotation<A>)annotation)
            .collect(Collectors.toList());
    }

    /**
     * 获取全部聚合注解
     *
     * @return java.util.Collection<top.xiajibagao.powerfulannotation.aggregate.AggregatedAnnotation<? extends java.lang.annotation.Annotation>>
     */
    @Override
    public Collection<AggregatedAnnotation<? extends Annotation>> getAllAnnotations() {
        return aggregatedAnnotationMap.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    /**
     * 获取当前聚合中的全部的可重复注解
     *
     * @param annotationType 注解类型
     * @return java.util.Collection<A>
     */
    @Override
    public <A extends Annotation> Collection<A> getRepeatableAnnotations(Class<A> annotationType) {
        mappingRegistry.register(annotationType);
        List<A> annotations = getAnnotationsByType(annotationType).stream()
            .map(AggregatedAnnotation::getAnnotation)
            .collect(Collectors.toList());
        getAllAnnotations().stream()
            .filter(annotation -> mappingRegistry.isContainerOf(annotationType, annotation.annotationType()))
            .map(annotation -> mappingRegistry.getElementsFromContainer(annotation, annotationType))
            .forEach(annotations::addAll);
        return annotations;
    }

}
