package top.xiajibagao.powerfulannotation.aggregate;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * {@link AggregatedAnnotation}的聚合
 *
 * @param <T> 数据源类型
 * @author huangchengxing
 */
public interface AnnotationAggregator<T> extends RepeatableContainer, Hierarchical {

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

}
