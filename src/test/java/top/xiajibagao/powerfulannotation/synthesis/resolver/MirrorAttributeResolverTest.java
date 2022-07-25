package top.xiajibagao.powerfulannotation.synthesis.resolver;

import cn.hutool.core.util.ReflectUtil;
import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.GenericHierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.attribute.AnnotationAttribute;
import top.xiajibagao.powerfulannotation.annotation.attribute.MirroredAnnotationAttribute;
import top.xiajibagao.powerfulannotation.annotation.attribute.WrappedAnnotationAttribute;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.synthesis.AnnotationSynthesizer;
import top.xiajibagao.powerfulannotation.synthesis.GenericAnnotationSynthesizer;
import top.xiajibagao.powerfulannotation.synthesis.Link;
import top.xiajibagao.powerfulannotation.synthesis.RelationType;

import java.lang.annotation.*;
import java.util.Collections;

/**
 * test for {@link MirrorAttributeResolver}
 *
 * @author huangchengxing
 */
public class MirrorAttributeResolverTest {

	@Test
	public void processTest() {
		MirrorAttributeResolver processor = new MirrorAttributeResolver();
		HierarchicalAnnotation<Annotation> annotation = new GenericHierarchicalAnnotation<>(ClassForTest.class.getAnnotation(AnnotationForTest.class));
		AnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(Collections.singletonList(processor), HierarchySelector.nearestAndOldestPriority());
		synthesizer.accept(annotation);

		AnnotationAttribute valueAttribute = annotation.getAttribute("value");
		Assert.assertEquals(ReflectUtil.getMethod(AnnotationForTest.class, "value"), valueAttribute.getAttribute());
		Assert.assertTrue(valueAttribute.isWrapped());
		Assert.assertEquals(MirroredAnnotationAttribute.class, valueAttribute.getClass());

		AnnotationAttribute nameAttribute = annotation.getAttribute("name");
		Assert.assertEquals(ReflectUtil.getMethod(AnnotationForTest.class, "name"), nameAttribute.getAttribute());
		Assert.assertTrue(nameAttribute.isWrapped());
		Assert.assertEquals(MirroredAnnotationAttribute.class, nameAttribute.getClass());

		Assert.assertEquals(((WrappedAnnotationAttribute)nameAttribute).getLinked(), ((WrappedAnnotationAttribute)valueAttribute).getOriginal());
		Assert.assertEquals(((WrappedAnnotationAttribute)nameAttribute).getOriginal(), ((WrappedAnnotationAttribute)valueAttribute).getLinked());
	}

	@AnnotationForTest
	private static class ClassForTest {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest {
		@Link(attribute = "name", type = RelationType.MIRROR_FOR)
		String value() default "";
		@Link(type = RelationType.MIRROR_FOR)
		String name() default "";
	}

}
