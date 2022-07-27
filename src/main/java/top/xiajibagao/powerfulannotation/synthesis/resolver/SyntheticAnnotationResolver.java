package top.xiajibagao.powerfulannotation.synthesis.resolver;

import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.synthesis.AnnotationSynthesizer;

import java.lang.annotation.Annotation;

/**
 * 合成注解解析器
 *
 * @author huangchengxing
 * @see AnnotationSynthesizer
 * @see MirrorAttributeResolver
 * @see AliasAttributeResolver
 * @see CoveredAttributeResolver
 */
public interface SyntheticAnnotationResolver {

    /**
     * {@link MirrorAttributeResolver}的排序值
     */
    int MIRROR_ATTRIBUTE_RESOLVER_ORDER = Integer.MIN_VALUE + 1;

    /**
     * {@link AliasAttributeResolver}的排序值
     */
    int ALIAS_ATTRIBUTE_RESOLVER_ORDER = Integer.MIN_VALUE + 2;

    /**
     * {@link CoveredAttributeResolver}的排序值
     */
    int COVERED_ATTRIBUTE_RESOLVER_ORDER = Integer.MIN_VALUE + 3;

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
    int order();

}
