package top.xiajibagao.powerfulannotation.aggerate;

import top.xiajibagao.powerfulannotation.helper.Hierarchical;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * {@link AggregatedAnnotation}的聚合体，表示一组具有一定关联关系的聚合注解。
 *
 * @param <T> 数据源类型
 * @author huangchengxing
 */
public interface AnnotationAggregator<T> extends Hierarchical, AnnotationProcessor {

    /**
     * 获取聚合注解的来源
     *
     * @return 注解的来源
     */
    @Override
    T getRoot();

    /**
     * 注解是否存在
     *
     * @param annotationType 属性类型
     * @return 是否
     */
    boolean isPresent(Class<? extends Annotation> annotationType);

    /**
     * 获取全部聚合注解
     *
     * @return 全部的聚合注解
     */
    Collection<AggregatedAnnotation<Annotation>> getAllAnnotations();

    /**
     * 获取指定层级中的聚合注解
     *
     * @param verticalIndex 垂直索引
     * @return 聚合注解
     */
    Collection<AggregatedAnnotation<Annotation>> getAnnotationByVerticalIndex(int verticalIndex);

    /**
     * 获取指定类型的聚合注解
     *
     * @param annotationType 注解类型
     * @param <A> 注解类型
     * @return 聚合注解
     */
    <A extends Annotation> Collection<AggregatedAnnotation<A>> getAnnotationsByType(Class<A> annotationType);

    /**
     * 获取指定类型的聚合注解
     *
     * @param annotationType 注解类型
     * @param selector 选择器
     * @param <A> 注解类型
     * @return 聚合注解
     */
    default <A extends Annotation> AggregatedAnnotation<A> getAnnotation(
        Class<A> annotationType, HierarchySelector<AggregatedAnnotation<A>> selector) {
        return getAnnotationsByType(annotationType).stream()
            .reduce(selector::choose)
            .orElse(null);
    }

}
