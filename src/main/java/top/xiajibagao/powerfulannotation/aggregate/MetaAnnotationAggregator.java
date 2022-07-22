package top.xiajibagao.powerfulannotation.aggregate;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingRegistry;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * {@link AnnotationAggregator}的基本实现，表示基于一个源注解，
 * 与该源注解的层级结构中的元注解聚合而来的聚合注解
 *
 * @author huangchengxing
 */
@Getter
public class MetaAnnotationAggregator implements AnnotationAggregator<Annotation>, Annotation {

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
    private final Annotation source;

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
     * 是否至少调用过一次{@link #refresh()}
     */
    private boolean refreshed;

    /**
     * 创建一个聚合注解
     *
     * @param source 数据源
     * @param root 根对象
     * @param verticalIndex 垂直坐标
     * @param horizontalIndex 水平坐标
     */
    public MetaAnnotationAggregator(
        Annotation source, Object root, int verticalIndex, int horizontalIndex, Predicate<Annotation> filter, RepeatableMappingRegistry mappingRegistry) {
        Assert.notNull(source, "source must not null");
        Assert.notNull(filter, "filter must not null");
        Assert.notNull(mappingRegistry, "mappingRegistry must not null");
        this.source = source;
        this.root = root;
        this.verticalIndex = verticalIndex;
        this.horizontalIndex = horizontalIndex;
        this.annotationFilter = filter;
        this.mappingRegistry = mappingRegistry;
        this.aggregatedAnnotationMap = new LinkedHashMap<>();
    }

    /**
     * 加载聚合注解
     */
    private void loadAnnotation(Annotation annotation, int verticalIndex, int horizontalIndex) {
        AggregatedAnnotation<? extends Annotation>  aggregatedAnnotation = new GenericAggregatedAnnotation<>(
            annotation, source, verticalIndex, horizontalIndex
        );
        registerAggregatedAnnotation(aggregatedAnnotation);
    }

    /**
     * 属性
     */
    private void refresh() {
        loadAnnotation(source, 0, 0);
        AnnotationScanner.DIRECTLY_AND_META_ANNOTATION.scan(
            (vIndex, hIndex, annotation) -> loadAnnotation(annotation, vIndex + 1, aggregatedAnnotationMap.size()),
            source.annotationType(), getAnnotationFilter()
        );
        aggregatedAnnotationMap.keySet().forEach(mappingRegistry::register);
    }

    /**
     * 向当前容器注册聚合注解
     *
     * @param aggregatedAnnotation 要聚合的注解
     */
    protected void registerAggregatedAnnotation(AggregatedAnnotation<? extends Annotation> aggregatedAnnotation) {
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
        if (!isRefreshed()) {
            refresh();
        }
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
        if (!isRefreshed()) {
            refresh();
        }
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
        if (!isRefreshed()) {
            refresh();
        }
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
        if (!isRefreshed()) {
            refresh();
        }
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

    /**
     * 获取源注解类型
     */
    @Override
    public Class<? extends Annotation> annotationType() {
        return getSource().annotationType();
    }

}
