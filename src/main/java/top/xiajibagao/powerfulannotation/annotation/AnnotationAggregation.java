package top.xiajibagao.powerfulannotation.annotation;

import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingRegistry;
import top.xiajibagao.powerfulannotation.scanner.AnnotationFilter;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * 表示一组处于聚合状态的注解
 *
 * @author huangchengxing
 */
public interface AnnotationAggregation<S, T extends HierarchicalAnnotation<S>> {

    /**
     * 获取指定类型的注解
     *
     * @param annotationType 注解类型
     * @param selector 选择器，当有多个符合的目标时，使用该选择进行过滤
     * @param filter 过滤器
     * @return 注解对象
     */
    Optional<T> getAnnotation(Class<?> annotationType, HierarchySelector<T> selector, AnnotationFilter filter);

    /**
     * 获取全部注解对象
     *
     * @return 注解对象
     */
    List<T> getAnnotations();

    /**
     * 获取可重复注解
     *
     * @param annotationType 注解类型
     * @param filter 过滤器
     * @param repeatableMappingRegistry 注册表
     * @return 可重复注解
     */
    <A extends Annotation> List<A> getRepeatableAnnotations(Class<A> annotationType, AnnotationFilter filter, RepeatableMappingRegistry repeatableMappingRegistry);

    /**
     * 指定类型注解是否存在
     *
     * @param annotationType 注解类型
     * @return 是否
     */
    boolean isPresent(Class<?> annotationType);

    /**
     * 当前聚合是否为空
     *
     * @return 是否
     */
    boolean isEmpty();
    
}
