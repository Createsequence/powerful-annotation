package top.xiajibagao.powerfulannotation.repeatable;

import top.xiajibagao.powerfulannotation.helper.Assert;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Method;

/**
 * 映射关系解析器，用于在{@link SimpleRepeatableMappingRegistry}
 * 中即系不同规则下的容器注解与元素注解间的映射关系
 *
 * @author huangchengxing
 * @see RepeatableByMappingParser
 * @see StandardRepeatableMappingParser
 */
public interface RepeatableMappingParser {

	/**
	 * 用于处理由{@link RepeatableBy}注解对应的元素注解与容器注解层级
	 */
	RepeatableByMappingParser REPEATABLE_BY_MAPPING_PARSER = new RepeatableByMappingParser();

	/**
	 * 用于处理由{@link Repeatable}注解对应的元素注解与容器注解层级
	 */
	StandardRepeatableMappingParser STANDARD_REPEATABLE_MAPPING_PARSER = new StandardRepeatableMappingParser();

	/**
	 * 解析注解类型，获得需要向注册表中注册的{@link RepeatableMapping}
	 *
	 * @param annotationType 注解类型
	 * @param registry       注册表
	 * @return 映射关系
	 */
	RepeatableMapping parse(Class<? extends Annotation> annotationType, RepeatableMappingRegistry registry);

	/**
	 * 检查{@code containerAttribute}方法是否存在，且返回值类型为{@code elementType}
	 *
	 * @param elementType        元素注解类型
	 * @param containerType      容器注解类型
	 * @param containerAttribute 容器注解中用于获取元素注解的方法
	 */
	static void checkContainedAttribute(Class<? extends Annotation> elementType, Class<? extends Annotation> containerType, Method containerAttribute) {
		// 容器注解的元素属性不能为空
		Assert.notNull(containerAttribute, "container annotation [%s] of element annotation [%s] must have contained attribute", containerType, elementType);
		// 获取属性的返回值类型，校验该属性是否返回指定的元素注解
		Class<?> containerAttributeType = containerAttribute.getReturnType();
		containerAttributeType = containerAttributeType.isArray() ?
			containerAttributeType.getComponentType() : containerAttributeType;
		Assert.isAssignable(
			elementType, containerAttributeType, "the element attribute [%s] of the container annotation [%s] must return the element annotation [%s]",
			containerAttribute.getName(), containerType, elementType
		);
	}

}
