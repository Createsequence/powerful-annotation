package top.xiajibagao.powerfulannotation.synthesis.proxy;

import top.xiajibagao.powerfulannotation.synthesis.SynthesizedAnnotation;

import java.lang.annotation.Annotation;

/**
 * 通过代理类生成的合成注解
 *
 * @author huangchengxing
 * @see SynthesizedAnnotationInvocationHandler
 */
public interface ProxiedSynthesizedAnnotation extends Annotation {

    /**
     * 获取该代理注解对应的已合成注解
     *
     * @return 理注解对应的已合成注解
     */
    SynthesizedAnnotation getSynthesizedAnnotation();

}
