package top.xiajibagao.powerfulannotation.repeatable;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Opt;
import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.RepeatableBy;
import top.xiajibagao.powerfulannotation.helper.ForestMap;
import top.xiajibagao.powerfulannotation.helper.TreeEntry;

import java.lang.annotation.*;
import java.util.Collection;
import java.util.Set;

public class SimpleRepeatableMappingRegistryTest {

	@Test
	public void registerTest() {
		SimpleRepeatableMappingRegistry registry = new SimpleRepeatableMappingRegistry(
			new StandardRepeatableMappingParser(), new RepeatableByMappingParser()
		);
		registry.register(AnnotationForTest1.class);

		RepeatableMapping mapping1 = getNodeValue(registry.mappingForestMap, AnnotationForTest1.class);
		Assert.assertNull(mapping1);

		RepeatableMapping mapping2 = getNodeValue(registry.mappingForestMap, AnnotationForTest2.class);
		Assert.assertNotNull(mapping2);
		Assert.assertEquals(AnnotationForTest2.class, mapping2.getContainerType());
		Assert.assertEquals(AnnotationForTest1.class, mapping2.getElementType());

		RepeatableMapping mapping3 = getNodeValue(registry.mappingForestMap, AnnotationForTest3.class);
		Assert.assertNotNull(mapping3);
		Assert.assertEquals(AnnotationForTest3.class, mapping3.getContainerType());
		Assert.assertEquals(AnnotationForTest2.class, mapping3.getElementType());
	}

	@Test
	public void isContainerTest() {
		SimpleRepeatableMappingRegistry registry = new SimpleRepeatableMappingRegistry(
			new StandardRepeatableMappingParser(), new RepeatableByMappingParser()
		);
		registry.register(AnnotationForTest1.class);

		Assert.assertFalse(registry.isContainer(AnnotationForTest1.class));
		Assert.assertTrue(registry.isContainer(AnnotationForTest2.class));
		Assert.assertTrue(registry.isContainer(AnnotationForTest3.class));
		Assert.assertFalse(registry.isContainer(AnnotationForTest4.class));
	}

	@Test
	public void hasContainerTest() {
		SimpleRepeatableMappingRegistry registry = new SimpleRepeatableMappingRegistry(
			new StandardRepeatableMappingParser(), new RepeatableByMappingParser()
		);
		registry.register(AnnotationForTest1.class);

		Assert.assertTrue(registry.hasContainer(AnnotationForTest1.class));
		Assert.assertTrue(registry.hasContainer(AnnotationForTest2.class));
		Assert.assertFalse(registry.hasContainer(AnnotationForTest3.class));
		Assert.assertFalse(registry.hasContainer(AnnotationForTest4.class));
	}

	@Test
	public void getContainersTest() {
		SimpleRepeatableMappingRegistry registry = new SimpleRepeatableMappingRegistry(
			new StandardRepeatableMappingParser(), new RepeatableByMappingParser()
		);
		registry.register(AnnotationForTest1.class);

		Set<Class<? extends Annotation>> set1 = CollUtil.newLinkedHashSet(AnnotationForTest2.class, AnnotationForTest3.class);
		Assert.assertEquals(set1, CollStreamUtil.toSet(registry.getContainers(AnnotationForTest1.class), RepeatableMapping::getContainerType));
		Set<Class<? extends Annotation>> set2 = CollUtil.newLinkedHashSet(AnnotationForTest3.class);
		Assert.assertEquals(set2, CollStreamUtil.toSet(registry.getContainers(AnnotationForTest2.class), RepeatableMapping::getContainerType));
		Assert.assertTrue(registry.getContainers(AnnotationForTest3.class).isEmpty());
		Assert.assertTrue(registry.getContainers(AnnotationForTest4.class).isEmpty());
	}

	@Test
	public void getAllElementsFromContainerTest() {
		SimpleRepeatableMappingRegistry registry = new SimpleRepeatableMappingRegistry(
			new StandardRepeatableMappingParser(), new RepeatableByMappingParser()
		);
		registry.register(AnnotationForTest1.class);

		AnnotationForTest3 annotation =  ClassForTest.class.getAnnotation(AnnotationForTest3.class);
		Collection<Annotation> annotations = registry.getAllElementsFromContainer(annotation);
		Assert.assertEquals(7, annotations.size());
	}

	@Test
	public void getElementsFromContainerTest() {
		SimpleRepeatableMappingRegistry registry = new SimpleRepeatableMappingRegistry(
			new StandardRepeatableMappingParser(), new RepeatableByMappingParser()
		);
		registry.register(AnnotationForTest1.class);

		AnnotationForTest3 annotation = ClassForTest.class.getAnnotation(AnnotationForTest3.class);
		Assert.assertTrue(registry.getElementsFromContainer(annotation, AnnotationForTest4.class).isEmpty());
		Assert.assertEquals(CollUtil.newArrayList(annotation), registry.getElementsFromContainer(annotation, AnnotationForTest3.class));
		Assert.assertEquals(2, registry.getElementsFromContainer(annotation, AnnotationForTest2.class).size());
		Assert.assertEquals(4, registry.getElementsFromContainer(annotation, AnnotationForTest1.class).size());

		AnnotationForTest4 annotation2 =  ClassForTest.class.getAnnotation(AnnotationForTest4.class);
		Assert.assertEquals(CollUtil.newArrayList(annotation2), registry.getElementsFromContainer(annotation2, AnnotationForTest4.class));
		Assert.assertTrue(registry.getElementsFromContainer(annotation2, AnnotationForTest3.class).isEmpty());
	}

	private <K, V> V getNodeValue(ForestMap<K, V> map, K key) {
		return Opt.ofNullable(key)
			.map(map::get)
			.map(TreeEntry::getValue)
			.get();
	}

	@Repeatable(AnnotationForTest2.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest1 {
		String value() default "";
		String name() default "";
	}

	@RepeatableBy(annotation = AnnotationForTest3.class, attribute = "annotations")
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest2 {
		AnnotationForTest1[] value() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest3 {
		AnnotationForTest2[] annotations() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest4 {
		String value() default "";
	}

	@AnnotationForTest4
	@AnnotationForTest3(annotations = {
		@AnnotationForTest2({
			@AnnotationForTest1("1"),
			@AnnotationForTest1("2")
		}),
		@AnnotationForTest2({
			@AnnotationForTest1("3"),
			@AnnotationForTest1("4")
		})
	})
	private static class ClassForTest{}

}
