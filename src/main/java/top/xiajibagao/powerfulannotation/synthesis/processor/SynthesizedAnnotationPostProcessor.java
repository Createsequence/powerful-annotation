package top.xiajibagao.powerfulannotation.synthesis.processor;

import cn.hutool.core.comparator.CompareUtil;
import top.xiajibagao.powerfulannotation.annotation.Link;
import top.xiajibagao.powerfulannotation.synthesis.AnnotationSynthesizer;
import top.xiajibagao.powerfulannotation.synthesis.MirrorLinkAnnotationPostProcessor;
import top.xiajibagao.powerfulannotation.synthesis.RelationType;
import top.xiajibagao.powerfulannotation.synthesis.SynthesizedAnnotation;

import java.util.Comparator;

/**
 * <p>合成注解后置处理器，在{@link AnnotationSynthesizer}加载完所有{@link SynthesizedAnnotation}后执行，
 * 用于对合成器中的合成注解进行后处理，以支持一些特殊的功能。
 * 当存在多个处理器时，将按{@link #order()}排序，越小越优先执行。
 *
 * <p>默认提供了以下实现，用于支持合成注解基于{@link Link}的别名机制：
 * <ul>
 *     <li>
 *         {@link MirrorLinkAnnotationPostProcessor}：用于处理合成注解中代理带有{@link Link},
 *         且{@link Link#type()}为{@link RelationType#MIRROR_FOR}的属性；
 *     </li>
 *     <li>
 *         {@link AliasLinkAnnotationPostProcessor}：用于处理合成注解中代理带有{@link Link},
 *  *         且{@link Link#type()}为{@link RelationType#FORCE_ALIAS_FOR}或{@link RelationType#ALIAS_FOR}的属性；
 *     </li>
 * </ul>
 *
 * @author huangchengxing
 * @see AnnotationSynthesizer
 * @see MirrorLinkAnnotationPostProcessor
 * @see AliasLinkAnnotationPostProcessor
 */
public interface SynthesizedAnnotationPostProcessor extends Comparable<SynthesizedAnnotationPostProcessor> {

	/**
	 * 属性上带有{@link Link}，且与其他注解的属性存在镜像关系的注解对象的后置处理器
	 */
	MirrorLinkAnnotationPostProcessor MIRROR_LINK_ANNOTATION_POST_PROCESSOR = new MirrorLinkAnnotationPostProcessor();

	/**
	 * 属性上带有{@link Link}，且与其他注解的属性存在别名关系的注解对象的后置处理器
	 */
	AliasLinkAnnotationPostProcessor ALIAS_LINK_ANNOTATION_POST_PROCESSOR = new AliasLinkAnnotationPostProcessor();

	/**
	 * 在一组后置处理器中被调用的顺序，越小越靠前
	 *
	 * @return 排序值
	 */
	default int order() {
		return Integer.MAX_VALUE;
	}

	/**
	 * 比较两个后置处理器的{@link #order()}返回值
	 *
	 * @param o 比较对象
	 * @return 大小
	 */
	@Override
	default int compareTo(SynthesizedAnnotationPostProcessor o) {
		return CompareUtil.compare(this, o, Comparator.comparing(SynthesizedAnnotationPostProcessor::order));
	}

	/**
	 * 给定指定被合成注解与其所属的合成注解聚合器实例，经过处理后返回最终
	 *
	 * @param synthesizedAnnotation 合成的注解
	 * @param synthesizer           注解合成器
	 */
	void process(SynthesizedAnnotation synthesizedAnnotation, AnnotationSynthesizer synthesizer);

}
