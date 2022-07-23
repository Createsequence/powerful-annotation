package top.xiajibagao.powerfulannotation.aggerate;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * 可重复注解容器
 *
 * @author huangchengxing
 */
public interface RepeatableContainer {

    /**
     * 获取可重复注解
     *
     * @param annotationType 注解类型
     * @return java.util.Collection<A>
     */
    <A extends Annotation> Collection<A> getRepeatableAnnotations(Class<A> annotationType);

}
