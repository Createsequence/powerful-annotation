package top.xiajibagao.powerfulannotation.synthesis.resolver;

import lombok.RequiredArgsConstructor;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.attribute.AliasedAnnotationAttribute;
import top.xiajibagao.powerfulannotation.annotation.attribute.AnnotationAttribute;
import top.xiajibagao.powerfulannotation.annotation.attribute.ForceAliasedAnnotationAttribute;
import top.xiajibagao.powerfulannotation.synthesis.AnnotationSynthesizer;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * 用于根据指定排序，令排序靠前的注解属性，覆盖排序靠后的注解中类型、名称均一致的属性
 *
 * @author huangchengxing
 * @see ForceAliasedAnnotationAttribute
 */
@RequiredArgsConstructor
public class CoveredAttributeResolver implements SynthesizedAnnotationResolver {

    /**
     * 合成注解排序器，靠前的注解属性将覆盖靠后的注解属性
     */
    private final Comparator<HierarchicalAnnotation<Annotation>> comparator;

    /**
     * 是否强制覆盖，若为否则仅当排序靠前的注解属性不为默认值时才覆盖
     */
    private final boolean isForceConverted;

    /**
     * 筛选出合成器中排序小于当前注解的注解，若其中存在注解属性与当前注解名称类型皆一致，则使用该属性覆盖当前注解中的同名属性
     *
     * @param annotation 待处理的注解
     * @param synthesizer 合成器
     */
    @Override
    public void resolve(HierarchicalAnnotation<Annotation> annotation, AnnotationSynthesizer synthesizer) {
        // 筛选出比当前注解靠前的注解
        Collection<HierarchicalAnnotation<Annotation>> sortedAnnotation = synthesizer.getAllAnnotation()
            .stream()
            .filter(t -> comparator.compare(t, annotation) < 0)
            .sorted(comparator)
            .collect(Collectors.toList());
        // 若排序靠前的注解存在于当前注解名称与类型皆相同的属性，则使用该属性覆盖当前注解的属性
        for (AnnotationAttribute attribute : annotation.getAllAttribute()) {
            sortedAnnotation.stream()
                .filter(t -> t.hasAttribute(attribute.getAttributeName(), attribute.getAttributeType()))
                .findFirst()
                .map(t -> t.getAttribute(attribute.getAttributeName()))
                .ifPresent(t -> annotation.replaceAttribute(
                    t.getAttributeName(),
                    a -> isForceConverted ? new ForceAliasedAnnotationAttribute(a, t) : new AliasedAnnotationAttribute(a, t)
                ));
        }
    }

}
