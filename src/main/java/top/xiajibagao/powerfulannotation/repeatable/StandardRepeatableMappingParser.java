package top.xiajibagao.powerfulannotation.repeatable;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Method;

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
	 * 默认实例
	 */
	public static final StandardRepeatableMappingParser INSTANCE = new StandardRepeatableMappingParser();

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
		if (ObjectUtil.isNull(containerType)) {
			return null;
		}
		final Method containedAttribute = ReflectUtil.getMethod(containerType, VALUE);
		RepeatableMappingParser.checkContainedAttribute(annotationType, containerType, containedAttribute);
		return new RepeatableAnnotationMapping(annotationType, containerType, containedAttribute);
	}

	private Class<? extends Annotation> getContainerType(Class<?> annotationType) {
		final Class<? extends Annotation> containerType =  Opt.ofNullable(annotationType)
			.map(t -> t.getAnnotation(Repeatable.class))
			.map(Repeatable::value)
			.orElse(null);
		if (ObjectUtil.isNull(containerType)) {
			return null;
		}
		// 检验注解
		final boolean isContainerAnnotation = Opt.ofNullable(containerType)
			.map(t -> ReflectUtil.getMethod(t, VALUE))
			.map(m -> ClassUtil.isAssignable(annotationType, m.getReturnType().getComponentType()))
			.orElse(false);
		return isContainerAnnotation ? containerType : null;
	}

}
