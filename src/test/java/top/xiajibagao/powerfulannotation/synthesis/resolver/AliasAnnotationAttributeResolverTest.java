package top.xiajibagao.powerfulannotation.synthesis.resolver;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.GenericHierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.attribute.*;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.helper.ReflectUtils;
import top.xiajibagao.powerfulannotation.synthesis.AliasFor;
import top.xiajibagao.powerfulannotation.synthesis.ForceAliasFor;
import top.xiajibagao.powerfulannotation.synthesis.GenericAnnotationSynthesizer;

import java.lang.annotation.*;
import java.util.Collections;

/**
 * test for {@link AliasAttributeResolver}
 *
 * @author huangchengxing
 */
public class AliasAnnotationAttributeResolverTest {

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
		Assert.assertTrue(valueAttribute.isWrapped());
		Assert.assertEquals(AliasAnnotationAttribute.class, valueAttribute.getClass());

		AnnotationAttribute nameAttribute = annotation.getAttribute("name");
		Assert.assertEquals(ReflectUtils.getDeclaredMethod(AnnotationForTest1.class, "name"), nameAttribute.getAttribute());
		Assert.assertTrue(nameAttribute.isWrapped());
		Assert.assertEquals(ForceAliasedAnnotationAttribute.class, nameAttribute.getClass());

		Assert.assertEquals(((AliasAnnotationAttribute)valueAttribute).getOriginal(), ((WrappedAnnotationAttribute)nameAttribute).getLinked());
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
		Assert.assertTrue(valueAttribute.isWrapped());
		Assert.assertEquals(AliasAnnotationAttribute.class, valueAttribute.getClass());

		AnnotationAttribute nameAttribute = annotation.getAttribute("name2");
		Assert.assertEquals(ReflectUtils.getDeclaredMethod(AnnotationForTest1.class, "name2"), nameAttribute.getAttribute());
		Assert.assertTrue(nameAttribute.isWrapped());
		Assert.assertEquals(AliasedAnnotationAttribute.class, nameAttribute.getClass());

		Assert.assertEquals(((AliasAnnotationAttribute)valueAttribute).getOriginal(), ((WrappedAnnotationAttribute)nameAttribute).getLinked());
	}

	@AnnotationForTest3("value3")
	@AnnotationForTest1
	private static class ClassForTest {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest1 {
		//@Link(attribute = "name", type = RelationType.FORCE_ALIAS_FOR)
		@ForceAliasFor(attribute = "name")
		String value() default "";
		String name() default "";

		//@Link(attribute = "name2", type = RelationType.ALIAS_FOR)
		@AliasFor(attribute = "name2")
		String value2() default "";
		String name2() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest2 {
		//@Link(type = RelationType.FORCE_ALIAS_FOR)
		@ForceAliasFor
		String value() default "";
	}

	@AnnotationForTest2("value2")
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest3 {
		String value() default "";
	}

	@Test
	public void testMultiAlias() {
		AliasAttributeResolver processor = new AliasAttributeResolver();
		GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(Collections.singletonList(processor), HierarchySelector.nearestAndOldestPriority());
		synthesizer.accept(0, 1, ClassForTest2.class.getAnnotation(AnnotationForTest6.class));
		synthesizer.accept(1, 1, AnnotationForTest6.class.getAnnotation(AnnotationForTest5.class));
		synthesizer.accept(2, 1, AnnotationForTest5.class.getAnnotation(AnnotationForTest4.class));
		synthesizer.resolve();

		HierarchicalAnnotation<Annotation> annotation6 = synthesizer.getAnnotation(AnnotationForTest6.class);
		Assert.assertNotNull(annotation6);
		Assert.assertEquals("foo", annotation6.getAttribute("value6").getValue());

		HierarchicalAnnotation<Annotation> annotation5 = synthesizer.getAnnotation(AnnotationForTest5.class);
		Assert.assertNotNull(annotation5);
		Assert.assertEquals("foo", annotation5.getAttribute("value5").getValue());

		HierarchicalAnnotation<Annotation> annotation4 = synthesizer.getAnnotation(AnnotationForTest4.class);
		Assert.assertNotNull(annotation4);
		Assert.assertEquals("foo", annotation4.getAttribute("value4").getValue());
		Assert.assertEquals("foo", annotation4.getAttribute("name4").getValue());
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest4 {
		@AliasFor(attribute = "name4")
		String value4() default "";
		String name4() default "";
	}

	@AnnotationForTest4
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest5 {
		@AliasFor(annotation = AnnotationForTest4.class, attribute = "value4")
		String value5() default "";
	}

	@AnnotationForTest5
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest6 {
		@ForceAliasFor(annotation = AnnotationForTest5.class, attribute = "value5")
		String value6() default "";
	}

	@AnnotationForTest6(value6 = "foo")
	private static class ClassForTest2 {}

}
