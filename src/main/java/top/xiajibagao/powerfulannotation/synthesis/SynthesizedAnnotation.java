package top.xiajibagao.powerfulannotation.synthesis;

import cn.hutool.core.collection.CollUtil;
import top.xiajibagao.powerfulannotation.synthesis.proxy.SynthesizedAnnotationInvocationHandler;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * <p>用于包装一个注解对象，从而表示该注解处于“被合成”的状态。
 *
 * <p>该类主要用于为注解对象提供动态属性的功能，在合成注解中，注解的属性以{@link AnnotationAttribute}形式存在，
 * 并允许被其他{@link AnnotationAttribute}实例所替换，因此一个合成注解的属性值允许与原始的注解对象不同。
 *
 * <p>合成注解允许通过{@link SynthesizedAnnotationInvocationHandler#create(Class, SynthesizedAnnotation)}
 * 生成对应类型的代理对象，该代理对象的属性值将与{@link #getAttributeValue(String, Class)}的返回值保持一致。<br>
 * 此外，合成注解还允许被注册到注解合成器{@link AnnotationSynthesizer}，该合成器支持根据一定的规则
 * 使一组相关联的{@link SynthesizedAnnotation}互相交换属性，从而通过{@link AnnotationSynthesizer#synthesize(Class)}
 * 获得的注解对象具有与原始注解对象不一样的属性。
 *
 * @author huangchengxing
 * @see AnnotationSynthesizer
 * @see SynthesizedAnnotationInvocationHandler
 * @see AnnotationAttribute
 */
public interface SynthesizedAnnotation extends Annotation, Hierarchical, AnnotationAttributeValueProvider {

	/**
	 * 获取被合成的注解对象
	 *
	 * @return 注解对象
	 */
	Annotation getAnnotation();

	/**
	 * 获取该合成注解与根对象的垂直距离。
	 * 默认情况下，该距离即为当前注解与根对象之间相隔的层级数。
	 *
	 * @return 合成注解与根对象的垂直距离
	 */
	@Override
	int getVerticalDistance();

	/**
	 * 获取该合成注解与根对象的水平距离。
	 * 默认情况下，该距离即为当前注解与根对象之间相隔的已经被扫描到的注解数。
	 *
	 * @return 合成注解与根对象的水平距离
	 */
	@Override
	int getHorizontalDistance();

	/**
	 * 注解是否存在该名称相同，且类型一致的属性
	 *
	 * @param attributeName 属性名
	 * @param returnType    返回值类型
	 * @return 是否存在该属性
	 */
	boolean hasAttribute(String attributeName, Class<?> returnType);

	/**
	 * 获取该注解的全部属性
	 *
	 * @return 注解属性
	 */
	Map<String, AnnotationAttribute> getAttributes();

	/**
	 * 设置该注解的全部属性
	 *
	 * @param attributes 注解属性
	 */
	default void setAttributes(Map<String, AnnotationAttribute> attributes) {
		if (CollUtil.isNotEmpty(attributes)) {
			attributes.forEach(this::setAttribute);
		}
	}

	/**
	 * 设置属性值
	 *
	 * @param attributeName 属性名称
	 * @param attribute     注解属性
	 */
	void setAttribute(String attributeName, AnnotationAttribute attribute);

	/**
	 * 替换属性值
	 *
	 * @param attributeName 属性名
	 * @param operator      替换操作
	 */
	void replaceAttribute(String attributeName, UnaryOperator<AnnotationAttribute> operator);

	/**
	 * 获取属性值
	 *
	 * @param attributeName 属性名
	 * @return 属性值
	 */
	Object getAttributeValue(String attributeName);

}
