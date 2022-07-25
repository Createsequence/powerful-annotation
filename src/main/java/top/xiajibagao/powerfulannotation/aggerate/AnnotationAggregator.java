package top.xiajibagao.powerfulannotation.aggerate;

import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.helper.Hierarchical;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableContainer;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * <p>{@link HierarchicalAnnotation}的聚合器，用于聚合具一组相关的注解，并提供一些批量操作的方法。
 *
 * <p>注解聚合器可以通过{@link #accept(int, int, Annotation)}方法向其注册注解，
 * 或直接作为{@link AnnotationProcessor}通过{@link AnnotationScanner}快速对指定元素的相关注解进行聚合。<br />
 * 聚合器还允许被作为{@link RepeatableContainer}使用，
 * 当从聚合器中获得可重复的注解时，聚合器将遍历已聚合的注解中所有直接或间接与指定注解相关的注解，
 * 并最终提取出所需的可重复注解对象。该操作支持处理多集嵌套的注解容器。
 *
 * @param <T> 数据源类型
 * @author huangchengxing
 */
public interface AnnotationAggregator<T>
    extends Hierarchical, AnnotationProcessor, RepeatableContainer {

    /**
     * 获取注解的来源
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
     * 获取全部注解
     *
     * @return 全部的注解
     */
    Collection<HierarchicalAnnotation<Annotation>> getAllAnnotations();

    /**
     * 获取指定层级中的注解
     *
     * @param verticalIndex 垂直索引
     * @return 注解
     */
    Collection<HierarchicalAnnotation<Annotation>> getAnnotationByVerticalIndex(int verticalIndex);

    /**
     * 获取指定类型的注解
     *
     * @param annotationType 注解类型
     * @param <A> 注解类型
     * @return 注解
     */
    <A extends Annotation> Collection<HierarchicalAnnotation<A>> getAnnotationsByType(Class<A> annotationType);

    /**
     * 获取指定类型的注解
     *
     * @param annotationType 注解类型
     * @param selector 选择器
     * @param <A> 注解类型
     * @return 注解
     */
    default <A extends Annotation> HierarchicalAnnotation<A> getAnnotation(
        Class<A> annotationType, HierarchySelector<HierarchicalAnnotation<A>> selector) {
        return getAnnotationsByType(annotationType).stream()
            .reduce(selector::choose)
            .orElse(null);
    }

}
