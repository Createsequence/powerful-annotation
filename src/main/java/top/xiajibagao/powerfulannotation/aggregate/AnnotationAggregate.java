package top.xiajibagao.powerfulannotation.aggregate;

import cn.hutool.core.util.ObjectUtil;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <p>{@link AggregatedAnnotation}的聚合体。<br />
 * 通常情况下，表示一组直接或间接来自于{@link #getRoot()}返回对象的注解，
 * 这些注解在聚合体中根据一定的索引排序并保持层级结构，
 * 并以{@link AggregatedAnnotation}的形式存在于该聚合体中。<br />
 * 此外，聚合体支持直接从聚合中获取可重复注解。
 *
 * <p>注解聚合体可作为{@link AnnotationProcessor}使用，
 * 当将其传入{@link AnnotationScanner}中时，
 * 应当允许将扫描到的注解注册到当前聚合中。
 *
 * @param <T> 数据源类型
 * @author huangchengxing
 * @see AggregatedAnnotation
 * @see AnnotationProcessor
 */
public interface AnnotationAggregate<T> extends RepeatableContainer, Hierarchical, AnnotationProcessor {

    /**
     * 获取聚合注解的来源
     *
     * @return T
     */
    T getSource();

    /**
     * 注解是否存在
     *
     * @param annotationType 属性类型
     * @return boolean
     */
    boolean isPresent(Class<? extends Annotation> annotationType);

    /**
     * 获取全部聚合注解
     *
     * @return java.util.Collection<top.xiajibagao.powerfulannotation.aggregate.AggregatedAnnotation<? extends java.lang.annotation.Annotation>>
     */
    Collection<AggregatedAnnotation<? extends Annotation>> getAllAnnotations();

    /**
     * 获取指定类型的注解
     *
     * @param annotationType 注解类型
     * @param <A> 注解类型
     * @return java.util.Collection<top.xiajibagao.powerfulannotation.aggregate.AggregatedAnnotation>
     */
    <A extends Annotation> Collection<AggregatedAnnotation<A>> getAnnotationsByType(Class<A> annotationType);

    /**
     * 获取指定注解
     *
     * @param annotationType 注解类型
     * @param selector 选择器
     * @param <A> 注解类型
     * @return top.xiajibagao.powerfulannotation.aggregate.AggregatedAnnotation
     */
    default <A extends Annotation> AggregatedAnnotation<A> getAnnotation(
        Class<A> annotationType, HierarchySelector<AggregatedAnnotation<A>> selector) {
        return getAnnotationsByType(annotationType).stream()
            .reduce(selector::choose)
            .orElse(null);
    }

    /**
     * 获取与{@link #getRoot()}返回对象具有指定垂直距离的聚合注解
     *
     * @param verticalIndex 垂直索引
     * @return java.util.Collection<top.xiajibagao.powerfulannotation.aggregate.AggregatedAnnotation<? extends java.lang.annotation.Annotation>>
     */
    default Collection<AggregatedAnnotation<? extends Annotation>> getAnnotation(int verticalIndex) {
        return getAllAnnotations().stream()
            .filter(annotation -> ObjectUtil.equals(annotation.getVerticalIndex(), verticalIndex))
            .collect(Collectors.toList());
    }

}
