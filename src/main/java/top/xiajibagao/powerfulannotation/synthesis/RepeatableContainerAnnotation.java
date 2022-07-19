package top.xiajibagao.powerfulannotation.synthesis;

import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingRegistry;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 允许合成可重复注解的{@link AnnotationSynthesizer}
 *
 * @author huangchengxing
 */
public interface RepeatableContainerAnnotation {

    /**
     * 获取可重复注解映射关系注册表
     *
     * @return 可重复注解映射关系注册表
     */
    RepeatableMappingRegistry getRepeatableMappingRegistry();

    /**
     * 获取可重复注解
     *
     * @param annotationType 注解类型
     * @param <T>            注解类型
     * @return 类型
     */
    <T extends Annotation> List<T> getRepeatableAnnotations(Class<T> annotationType);

}
