package top.xiajibagao.powerfulannotation.repeatable;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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
		this.hasContainer = ObjectUtil.isNotNull(containerType);
		this.containedAttribute = containedAttribute;
	}

	@Override
	public Annotation[] getElementsFromContainer(Annotation containerAnnotation) {
		if (!hasContainer()) {
			return new Annotation[0];
		}
		return (Annotation[])Opt.ofNullable(containerAnnotation)
			.map(m -> ReflectUtil.invoke(containerAnnotation, containedAttribute))
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
