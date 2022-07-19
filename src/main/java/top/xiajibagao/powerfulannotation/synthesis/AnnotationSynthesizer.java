package top.xiajibagao.powerfulannotation.synthesis;

import top.xiajibagao.powerfulannotation.synthesis.processor.SynthesizedAnnotationPostProcessor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

/**
 * <p>注解合成器，用于接受一组与{@link #getSource()}具有直接或间接联系的注解对象，
 * 然后在根据一定规则处理后，通过{@link #synthesize(Class)}“合成”一个与原始注解对象具有
 * 一些不同属性值的合成注解。
 *
 * <p>当注解合成器被创建时，将经历下述过程以完成初始化：
 * <ol>
 *     <li>加载原始注解，并将全部待处理的注解对象封装为{@link SynthesizedAnnotation}；</li>
 *     <li>
 *         使用{@link SynthesizedAnnotationSelector}对已有的{@link SynthesizedAnnotation}
 *         进行过滤，若相同类型的注解对象存在多个，则经过筛选后将最终只留下一个有效的注解对象；
 *     </li>
 *     <li>
 *         使用{@link SynthesizedAnnotationPostProcessor}按指定的顺序依次处理全部的{@link SynthesizedAnnotation}，
 *         该过程一般用于对合成注解中的{@link AnnotationAttribute}进行调整；
 *     </li>
 * </ol>
 *
 * <p>当完成初始化后，使用{@link #synthesize(Class)}即可“合成”指定类型的注解，
 * 合成器将基于中指定类型{@link SynthesizedAnnotation}，通过动态代理生成对应类型的代理对象。
 *
 * @author huangchengxing
 * @see SynthesizedAnnotationSelector
 * @see SynthesizedAnnotationPostProcessor
 * @see SynthesizedAnnotation
 * @see AbstractAnnotationSynthesizer
 * @see SynthesizedAnnotationProxy
 */
public interface AnnotationSynthesizer {

	/**
	 * 获取合成注解来源最初来源
	 *
	 * @return 合成注解来源最初来源
	 */
	Object getSource();

	/**
	 * 合成注解选择器
	 *
	 * @return 注解选择器
	 */
	SynthesizedAnnotationSelector getAnnotationSelector();

	/**
	 * 获取合成注解后置处理器
	 *
	 * @return 合成注解后置处理器
	 */
	Collection<SynthesizedAnnotationPostProcessor> getAnnotationPostProcessors();

	/**
	 * 获取已合成的注解
	 *
	 * @param annotationType 注解类型
	 * @return 已合成的注解
	 */
	SynthesizedAnnotation getSynthesizedAnnotation(Class<?> annotationType);

	/**
	 * 获取全部的合成注解
	 *
	 * @return 合成注解
	 */
	Map<Class<? extends Annotation>, SynthesizedAnnotation> getAllSynthesizedAnnotation();

	/**
	 * 获取合成注解
	 *
	 * @param annotationType 注解类型
	 * @param <T>            注解类型
	 * @return 类型
	 */
	<T extends Annotation> T synthesize(Class<T> annotationType);

}
