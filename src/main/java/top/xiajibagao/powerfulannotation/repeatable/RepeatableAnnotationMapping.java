package top.xiajibagao.powerfulannotation.repeatable;

import top.xiajibagao.powerfulannotation.helper.ReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link RepeatableMapping}的基本实现
 *
 * @author huangchengxing
 */
public class RepeatableAnnotationMapping implements RepeatableMapping {

	final Class<? extends Annotation> elementType;
	final Class<? extends Annotation> containerType;
	final Method containedAttribute;
	final boolean hasContainer;

	RepeatableAnnotationMapping(Class<? extends Annotation> elementType, Class<? extends Annotation> containerType, Method containedAttribute) {
		this.elementType = elementType;
		this.containerType = containerType;
		this.hasContainer = Objects.nonNull(containerType);
		this.containedAttribute = containedAttribute;
	}

	@Override
	public Annotation[] getElementsFromContainer(Annotation containerAnnotation) {
		if (!hasContainer()) {
			return new Annotation[0];
		}
		return (Annotation[])Optional.ofNullable(containerAnnotation)
			.map(m -> ReflectUtils.invoke(containerAnnotation, containedAttribute))
			.orElse(new Annotation[0]);
	}

	@Override
	public Class<? extends Annotation> getElementType() {
		return this.elementType;
	}

	@Override
	public Class<? extends Annotation> getContainerType() {
		return this.containerType;
	}

	@Override
	public boolean hasContainer() {
		return this.hasContainer;
	}

}
