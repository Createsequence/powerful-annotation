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
import java.util.HashMap;
import java.util.Map;

/**
 * 用于根据指定排序，令排序靠前的注解属性，覆盖排序靠后的注解中类型、名称均一致的属性
 *
 * @author huangchengxing
 * @see ForceAliasedAnnotationAttribute
 */
@RequiredArgsConstructor
public class CoveredAttributeResolver implements SyntheticAnnotationResolver {

    /**
     * 合成注解排序器，靠前的注解属性将覆盖靠后的注解属性
     */
    private final Comparator<HierarchicalAnnotation<Annotation>> comparator;

    /**
     * 是否强制覆盖，若为否则仅当排序靠前的注解属性不为默认值时才覆盖
     */
    private final boolean isForceConverted;
    
    /**
     * 创建一个覆盖属性解析器，默认只允许低层级的注解属性覆盖高层级的注解属性
     *
     * @param isForceConverted 是否强制覆盖，若为否则仅当排序靠前的注解属性不为默认值时才覆盖
     */
    public CoveredAttributeResolver(boolean isForceConverted) {
        this.comparator = Comparator.comparing(HierarchicalAnnotation<Annotation>::getVerticalIndex)
            .thenComparing(HierarchicalAnnotation<Annotation>::getHorizontalIndex);
        this.isForceConverted = isForceConverted;
    }

    /**
     * 排序值，固定返回{@link SyntheticAnnotationResolver#COVERED_ATTRIBUTE_RESOLVER_ORDER}
     *
     * @return 排序值
     */
    @Override
    public int order() {
        return SyntheticAnnotationResolver.COVERED_ATTRIBUTE_RESOLVER_ORDER;
    }

    /**
     * 筛选出合成器中排序小于当前注解的注解，若其中存在注解属性与当前注解名称类型皆一致，则使用该属性覆盖当前注解中的同名属性
     *
     * @param annotations 待处理的注解
     * @param synthesizer 合成器
     */
    @Override
    public void resolve(Collection<HierarchicalAnnotation<Annotation>> annotations, AnnotationSynthesizer synthesizer) {
        Map<String, Map<Class<?>, AnnotationAttribute>> convertedAttributes = new HashMap<>(16);
        for (HierarchicalAnnotation<Annotation> annotation : annotations) {
            for (AnnotationAttribute attribute : annotation.getAllAttribute()) {
                AnnotationAttribute convertedAttribute = convertedAttributes
                    .computeIfAbsent(attribute.getAttributeName(), t -> new HashMap<>(8))
                    .computeIfAbsent(attribute.getAttributeType(), t -> attribute);
                annotation.replaceAttribute(
                    convertedAttribute.getAttributeName(),
                    t -> isForceConverted ?
                        new ForceAliasedAnnotationAttribute(t, convertedAttribute) : new AliasedAnnotationAttribute(t, convertedAttribute)
                );
            }
        }
    }

}
