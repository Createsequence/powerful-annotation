package top.xiajibagao.powerfulannotation.repeatable;

import top.xiajibagao.powerfulannotation.helper.Annotations;
import top.xiajibagao.powerfulannotation.helper.CollUtils;
import top.xiajibagao.powerfulannotation.helper.ReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用于处理由{@link RepeatableBy}注解对应的元素注解与容器注解层级
 *
 * @author huangchengxing
 * @see SimpleRepeatableMappingRegistry
 */
public class RepeatableByMappingParser implements RepeatableMappingParser {

	@Override
	public List<RepeatableMapping> parse(Class<? extends Annotation> annotationType, RepeatableMappingRegistry registry) {
		return Stream.of(annotationType)
			.map(this::getAnnotations)
			.flatMap(Collection::stream)
			.map(annotation -> parseToMapping(annotationType, annotation))
			.collect(Collectors.toList());
	}

	/**
	 * 将{@link RepeatableBy}注解转为{@link RepeatableMapping}
	 */
	private RepeatableAnnotationMapping parseToMapping(Class<? extends Annotation> annotationType, RepeatableBy annotation) {
		final Class<? extends Annotation> containerType = annotation.annotation();
		final Method containedAttribute = ReflectUtils.getDeclaredMethod(containerType, annotation.attribute());
		RepeatableMappingParser.checkContainedAttribute(annotationType, containerType, containedAttribute);
		return new RepeatableAnnotationMapping(annotationType, containerType, containedAttribute);
	}

	/**
	 * 获取{@link RepeatableBy}注解
	 */
	private List<RepeatableBy> getAnnotations(Class<? extends Annotation> annotationType) {
		List<RepeatableBy> annotations = new ArrayList<>();
		Optional.ofNullable(Annotations.getDeclaredAnnotation(annotationType, RepeatableBy.class))
			.ifPresent(annotations::add);
		Optional.ofNullable(Annotations.getDeclaredAnnotation(annotationType, RepeatableBy.List.class))
			.map(RepeatableBy.List::value)
			.filter(CollUtils::isNotEmpty)
			.map(Arrays::asList)
			.ifPresent(annotations::addAll);
		return annotations;
	}

}
