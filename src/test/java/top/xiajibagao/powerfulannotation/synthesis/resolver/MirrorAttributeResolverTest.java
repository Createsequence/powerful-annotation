package top.xiajibagao.powerfulannotation.synthesis.resolver;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.GenericHierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.attribute.AnnotationAttribute;
import top.xiajibagao.powerfulannotation.annotation.attribute.MirroredAnnotationAttribute;
import top.xiajibagao.powerfulannotation.annotation.attribute.WrappedAnnotationAttribute;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.helper.ReflectUtils;
import top.xiajibagao.powerfulannotation.synthesis.GenericAnnotationSynthesizer;
import top.xiajibagao.powerfulannotation.synthesis.MirrorFor;

import java.lang.annotation.*;
import java.util.Collections;

/**
 * test for {@link MirrorAttributeResolver}
 *
 * @author huangchengxing
 */
public class MirrorAttributeResolverTest {

	@Test
	public void testOrder() {
		MirrorAttributeResolver processor = new MirrorAttributeResolver();
		Assert.assertEquals(SyntheticAnnotationResolver.MIRROR_ATTRIBUTE_RESOLVER_ORDER, processor.order());
	}

	@Test
	public void testResolve() {
		MirrorAttributeResolver processor = new MirrorAttributeResolver();
		HierarchicalAnnotation<Annotation> annotation = new GenericHierarchicalAnnotation<>(ClassForTest.class.getAnnotation(AnnotationForTest.class));
		GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(Collections.singletonList(processor), HierarchySelector.nearestAndOldestPriority());
		synthesizer.accept(annotation);
		synthesizer.resolve();

		AnnotationAttribute valueAttribute = annotation.getAttribute("value");
		Assert.assertEquals(ReflectUtils.getDeclaredMethod(AnnotationForTest.class, "value"), valueAttribute.getAttribute());
		Assert.assertTrue(valueAttribute.isWrapped());
		Assert.assertEquals(MirroredAnnotationAttribute.class, valueAttribute.getClass());

		AnnotationAttribute nameAttribute = annotation.getAttribute("name");
		Assert.assertEquals(ReflectUtils.getDeclaredMethod(AnnotationForTest.class, "name"), nameAttribute.getAttribute());
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
		//@Link(attribute = "name", type = RelationType.MIRROR_FOR)
		@MirrorFor(attribute = "name")
		String value() default "";
		//@Link(type = RelationType.MIRROR_FOR)
		@MirrorFor
		String name() default "";
	}

}
