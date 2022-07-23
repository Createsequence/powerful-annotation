package top.xiajibagao.powerfulannotation.repeatable;

import java.lang.annotation.Annotation;

/**
 * <p>假设可以通过某个注解的某个属性获得另一批注解，则称后者为元素注解，而前者为容器注解。
 * {@link RepeatableMapping}则用于描述一对元素注解与容器注解之间的映射关系。
 *
 * @author huangchengxing
 */
public interface RepeatableMapping {

	/**
	 * 获取元素注解类型
	 *
	 * @return 元素注解类型
	 */
	Class<? extends Annotation> getElementType();

	/**
	 * 获取容器注解类型
	 *
	 * @return 元素容器类型
	 */
	Class<? extends Annotation> getContainerType();

	/**
	 * 从容器注解中获取元素注解
	 *
	 * @param container 容器注解对象
	 * @return 元素注解
	 */
	Annotation[] getElementsFromContainer(Annotation container);

	/**
	 * 当元素注解是否存在容器注解
	 *
	 * @return 是否
	 */
	boolean hasContainer();

}
