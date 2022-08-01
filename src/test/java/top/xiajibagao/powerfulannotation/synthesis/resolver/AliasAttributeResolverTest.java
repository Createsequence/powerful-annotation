package top.xiajibagao.powerfulannotation.synthesis.resolver;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.GenericHierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.attribute.*;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.helper.ReflectUtils;
import top.xiajibagao.powerfulannotation.synthesis.GenericAnnotationSynthesizer;
import top.xiajibagao.powerfulannotation.synthesis.Link;
import top.xiajibagao.powerfulannotation.synthesis.RelationType;

import java.lang.annotation.*;
import java.util.Collections;

/**
 * test for {@link AliasAttributeResolver}
 *
 * @author huangchengxing
 */
public class AliasAttributeResolverTest {

	@Test
	public void testOrder() {
		AliasAttributeResolver processor = new AliasAttributeResolver();
		Assert.assertEquals(SyntheticAnnotationResolver.ALIAS_ATTRIBUTE_RESOLVER_ORDER, processor.order());
	}

	@Test
	public void testResolveForceAliasFor() {
		AliasAttributeResolver processor = new AliasAttributeResolver();

		HierarchicalAnnotation<Annotation> annotation = new GenericHierarchicalAnnotation<>(ClassForTest.class.getAnnotation(AnnotationForTest1.class));
		GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(Collections.singletonList(processor), HierarchySelector.nearestAndOldestPriority());
		synthesizer.accept(annotation);
		synthesizer.resolve();

		AnnotationAttribute valueAttribute = annotation.getAttribute("value");
		Assert.assertEquals(ReflectUtils.getDeclaredMethod(AnnotationForTest1.class, "value"), valueAttribute.getAttribute());
		Assert.assertFalse(valueAttribute.isWrapped());
		Assert.assertEquals(CacheableAnnotationAttribute.class, valueAttribute.getClass());

		AnnotationAttribute nameAttribute = annotation.getAttribute("name");
		Assert.assertEquals(ReflectUtils.getDeclaredMethod(AnnotationForTest1.class, "name"), nameAttribute.getAttribute());
		Assert.assertTrue(nameAttribute.isWrapped());
		Assert.assertEquals(ForceAliasedAnnotationAttribute.class, nameAttribute.getClass());

		Assert.assertEquals(valueAttribute, ((WrappedAnnotationAttribute)nameAttribute).getLinked());
	}

	@Test
	public void testResolveAliasFor() {
		AliasAttributeResolver processor = new AliasAttributeResolver();
		HierarchicalAnnotation<Annotation> annotation = new GenericHierarchicalAnnotation<>(ClassForTest.class.getAnnotation(AnnotationForTest1.class));
		GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(Collections.singletonList(processor), HierarchySelector.nearestAndOldestPriority());
		synthesizer.accept(annotation);
		synthesizer.resolve();

		AnnotationAttribute valueAttribute = annotation.getAttribute("value2");
		Assert.assertEquals(ReflectUtils.getDeclaredMethod(AnnotationForTest1.class, "value2"), valueAttribute.getAttribute());
		Assert.assertFalse(valueAttribute.isWrapped());
		Assert.assertEquals(CacheableAnnotationAttribute.class, valueAttribute.getClass());

		AnnotationAttribute nameAttribute = annotation.getAttribute("name2");
		Assert.assertEquals(ReflectUtils.getDeclaredMethod(AnnotationForTest1.class, "name2"), nameAttribute.getAttribute());
		Assert.assertTrue(nameAttribute.isWrapped());
		Assert.assertEquals(AliasedAnnotationAttribute.class, nameAttribute.getClass());

		Assert.assertEquals(valueAttribute, ((WrappedAnnotationAttribute)nameAttribute).getLinked());
	}

	@AnnotationForTest3("value3")
	@AnnotationForTest1
	private static class ClassForTest {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest1 {
		@Link(attribute = "name", type = RelationType.FORCE_ALIAS_FOR)
		String value() default "";
		String name() default "";

		@Link(attribute = "name2", type = RelationType.ALIAS_FOR)
		String value2() default "";
		String name2() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest2 {
		@Link(type = RelationType.FORCE_ALIAS_FOR)
		String value() default "";
	}

	@AnnotationForTest2("value2")
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest3 {
		String value() default "";
	}

}
