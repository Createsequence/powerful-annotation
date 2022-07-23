package top.xiajibagao.powerfulannotation.repeatable;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import top.xiajibagao.powerfulannotation.helper.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Method;

/**
 * 用于处理由{@link RepeatableBy}注解对应的元素注解与容器注解层级
 *
 * @author huangchengxing
 * @see SimpleRepeatableMappingRegistry
 */
public class RepeatableByMappingParser implements RepeatableMappingParser {

	@Override
	public RepeatableMapping parse(Class<? extends Annotation> annotationType, RepeatableMappingRegistry registry) {
		final RepeatableBy annotation = AnnotationUtils.getDeclaredAnnotation(annotationType, RepeatableBy.class);
		if (ObjectUtil.isNull(annotation)) {
			return null;
		}
		Assert.isFalse(
			annotationType.isAnnotationPresent(Repeatable.class),
			"cannot parse @Link(type = RelationType.CONTAINED_BY) on [{}] , the annotation already annotated by @Repeatable",
			annotationType, annotationType
		);
		final Class<? extends Annotation> containerType = annotation.annotation();
		final Method containedAttribute = ReflectUtil.getMethod(containerType, annotation.attribute());
		RepeatableMappingParser.checkContainedAttribute(annotationType, containerType, containedAttribute);
		return new RepeatableAnnotationMapping(annotationType, containerType, containedAttribute);
	}

}
