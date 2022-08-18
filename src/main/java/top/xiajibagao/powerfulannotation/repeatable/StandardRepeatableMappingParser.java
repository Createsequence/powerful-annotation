package top.xiajibagao.powerfulannotation.repeatable;

import top.xiajibagao.powerfulannotation.helper.ReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * 用于处理由{@link Repeatable}注解对应的元素注解与容器注解层级
 *
 * @author huangchengxing
 * @see SimpleRepeatableMappingRegistry
 */
public class StandardRepeatableMappingParser implements RepeatableMappingParser {

	/**
	 * 默认的容器属性
	 */
	private static final String VALUE = "value";

	/**
	 * 获取注解类上的{@link Repeatable}，若存在则获取其指定的容器注解类，并为当前注解类型与其建立映射关系。 <br>
	 * 要求{@link Repeatable#value()}指定的注解类中，必须有名为{@link #VALUE}的属性，
	 * 且该属性的返回值类型必须为{@code annotationType}。 <br>
	 * 当不存在该注解，或注解指定的注解类不符合条件时返回null。
	 *
	 * @param annotationType 枚举类
	 * @param registry       注册表
	 * @return 映射关系
	 */
	@Override
	public RepeatableMapping parse(Class<? extends Annotation> annotationType, RepeatableMappingRegistry registry) {
		final Class<? extends Annotation> containerType = getContainerType(annotationType);
		if (Objects.isNull(containerType)) {
			return null;
		}
		final Method containedAttribute = ReflectUtils.getDeclaredMethod(containerType, VALUE);
		RepeatableMappingParser.checkContainedAttribute(annotationType, containerType, containedAttribute);
		return new RepeatableAnnotationMapping(annotationType, containerType, containedAttribute);
	}

	private Class<? extends Annotation> getContainerType(Class<?> annotationType) {
		final Class<? extends Annotation> containerType =  Optional.ofNullable(annotationType)
			.map(t -> t.getAnnotation(Repeatable.class))
			.map(Repeatable::value)
			.orElse(null);
		if (Objects.isNull(containerType)) {
			return null;
		}
		// 检验注解
		final boolean isContainerAnnotation = Optional.ofNullable(containerType)
			.map(t -> ReflectUtils.getDeclaredMethod(t, VALUE))
			.map(m -> ReflectUtils.isAssignable(annotationType, m.getReturnType().getComponentType()))
			.orElse(false);
		return isContainerAnnotation ? containerType : null;
	}

}
