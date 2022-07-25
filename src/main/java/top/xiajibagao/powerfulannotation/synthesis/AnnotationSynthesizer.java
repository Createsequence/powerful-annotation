package top.xiajibagao.powerfulannotation.synthesis;

import top.xiajibagao.powerfulannotation.aggerate.AnnotationAggregator;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * 注解合成器，以{@link AnnotationAggregator}为原料，
 * 用于“合成”一些与原始注解具有不同属性值的注解
 *
 * @author huangchengxing
 */
public interface AnnotationSynthesizer extends AnnotationProcessor {

    /**
     * 获取待合成的注解
     *
     * @param annotationType 注解类型
     * @return 待合成的注解
     */
    HierarchicalAnnotation<Annotation> getAnnotation(Class<?> annotationType);

    /**
     * 获取全部待合成的注解
     *
     * @return 待合成的注解
     */
    Collection<HierarchicalAnnotation<Annotation>> getAllAnnotation();

    /**
     * 向当前实例注册注解
     *
     * @param hierarchicalAnnotation 注解
     */
    void accept(HierarchicalAnnotation<Annotation> hierarchicalAnnotation);

    /**
     * 是否支持合成指定注解
     *
     * @param annotationType 注解类型
     * @return 是否
     */
    boolean support(Class<? extends Annotation> annotationType);

    /**
     * 基于一组具有一定关系的注解，对其进行“合成”，并最终返回一个指定类型的合成注解
     *
     * @param annotationType 注解类型
     * @param <T> 注解类型
     * @return 合成注解
     */
    <T extends Annotation> T synthesize(Class<T> annotationType);

}
