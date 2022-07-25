package top.xiajibagao.powerfulannotation.repeatable;

import top.xiajibagao.powerfulannotation.aggerate.AnnotationAggregator;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * 表明实现类是一个可重复注解容器，可以从该对象中批量获得指定类型的可重复注解
 *
 * @author huangchengxing
 * @see AnnotationAggregator
 */
public interface RepeatableContainer {

    /**
     * 从当前对象中获得可重复注解，包括该对象中全部直接以及间接声明的注解对象
     *
     * @param annotationType 注解类型
     * @return java.util.Collection<A>
     */
    <A extends Annotation> Collection<A> getRepeatableAnnotations(Class<A> annotationType);

}
