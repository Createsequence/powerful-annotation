package top.xiajibagao.powerfulannotation.repeatable;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import top.xiajibagao.powerfulannotation.annotation.Link;
import top.xiajibagao.powerfulannotation.helper.SynthesizedAnnotationUtils;
import top.xiajibagao.powerfulannotation.synthesis.RelationType;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Method;

/**
 * 用于处理由{@link Link}注解对应的元素注解与容器注解层级
 *
 * @author huangchengxing
 * @see SimpleRepeatableMappingRegistry
 */
public class LinkRepeatableMappingParser implements RepeatableMappingParser {

	@Override
	public RepeatableMapping parse(Class<? extends Annotation> annotationType, RepeatableMappingRegistry registry) {
		final Link link = SynthesizedAnnotationUtils.getSynthesizedAnnotation(annotationType, Link.class);
		if (ObjectUtil.isNull(link) || ObjectUtil.notEqual(RelationType.REPEATABLE_BY, link.type())) {
			return null;
		}
		Assert.isFalse(
			annotationType.isAnnotationPresent(Repeatable.class),
			"cannot parse @Link(type = RelationType.CONTAINED_BY) on [{}] , the annotation already annotated by @Repeatable",
			annotationType, annotationType
		);
		final Class<? extends Annotation> containerType = link.annotation();
		final Method containedAttribute = ReflectUtil.getMethod(containerType, link.attribute());
		RepeatableMappingParser.checkContainedAttribute(annotationType, containerType, containedAttribute);
		return new RepeatableAnnotationMapping(annotationType, containerType, containedAttribute);
	}

}
