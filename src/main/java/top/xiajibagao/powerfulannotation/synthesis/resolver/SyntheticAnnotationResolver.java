package top.xiajibagao.powerfulannotation.synthesis.resolver;

import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.synthesis.AnnotationSynthesizer;

import java.lang.annotation.Annotation;

/**
 * 合成注解解析器
 *
 * @author huangchengxing
 * @see AnnotationSynthesizer
 * @see AliasAttributeResolver
 * @see MirrorAttributeResolver
 * @see CoveredAttributeResolver
 */
public interface SyntheticAnnotationResolver {

    /**
     * 处理注解
     *
     * @param annotation 待处理的注解
     * @param synthesizer 合成器
     */
    void resolve(HierarchicalAnnotation<Annotation> annotation, AnnotationSynthesizer synthesizer);

    /**
     * 排序值，越小越靠前
     *
     * @return 排序值
     */
    default int order() {
        return Integer.MAX_VALUE;
    }

}
