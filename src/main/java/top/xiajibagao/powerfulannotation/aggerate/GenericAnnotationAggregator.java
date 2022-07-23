package top.xiajibagao.powerfulannotation.aggerate;

import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link AnnotationAggregator}的基本实现
 *
 * @param <T> 数据源类型
 * @author huangchengxing
 */
@Getter
public class GenericAnnotationAggregator<T> implements AnnotationAggregator<T> {

    /**
     * 根对象
     */
    protected final T root;

    /**
     * 与根对象的垂直距离
     */
    protected final int verticalIndex;

    /**
     * 与根对象的水平距离
     */
    protected final int horizontalIndex;

    /**
     * 被聚合的注解
     */
    @Getter(AccessLevel.PROTECTED)
    protected final Map<Class<? extends Annotation>, Collection<AggregatedAnnotation<Annotation>>> aggregatedAnnotationMap;

    /**
     * 创建一个聚合注解
     *
     * @param root 根对象
     * @param verticalIndex 垂直坐标
     * @param horizontalIndex 水平坐标
     */
    public GenericAnnotationAggregator(
        T root, int verticalIndex, int horizontalIndex) {
        this.root = root;
        this.verticalIndex = verticalIndex;
        this.horizontalIndex = horizontalIndex;
        this.aggregatedAnnotationMap = new LinkedHashMap<>();
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
        AggregatedAnnotation<Annotation> aggregatedAnnotation = new GenericAggregatedAnnotation<>(
            annotation, this, verticalIndex, horizontalIndex
        );
        aggregatedAnnotationMap.computeIfAbsent(aggregatedAnnotation.annotationType(), t -> new ArrayList<>())
            .add(aggregatedAnnotation);
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
     * 获取全部聚合注解
     *
     * @return 全部的聚合注解
     */
    @Override
    public Collection<AggregatedAnnotation<Annotation>> getAllAnnotations() {
        return aggregatedAnnotationMap.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    /**
     * 获取指定类型的聚合注解
     *
     * @param annotationType 注解类型
     * @param <A> 注解类型
     * @return 聚合注解
     */
    @SuppressWarnings("unchecked")
    @Override
    public <A extends Annotation> Collection<AggregatedAnnotation<A>> getAnnotationsByType(Class<A> annotationType) {
        return aggregatedAnnotationMap.get(annotationType).stream()
            .map(annotation -> (AggregatedAnnotation<A>)annotation)
            .collect(Collectors.toList());
    }

    /**
     * 获取指定层级中的聚合注解
     *
     * @param verticalIndex 垂直索引
     * @return 聚合注解
     */
    @Override
    public Collection<AggregatedAnnotation<Annotation>> getAnnotationByVerticalIndex(int verticalIndex) {
        return getAllAnnotations().stream()
            .filter(annotation -> ObjectUtil.equals(annotation.getVerticalIndex(), verticalIndex))
            .collect(Collectors.toList());
    }

}
