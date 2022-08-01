package top.xiajibagao.powerfulannotation.scanner;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

public class AnnotationSearchModeTest {

	@Test
	public void annotationTypeTest() {
		AnnotationSearchMode typeHierarchyMode = AnnotationSearchMode.TYPE_HIERARCHY_AND_DIRECT;
		AnnotationSearchMode directMode = AnnotationSearchMode.SELF_AND_DIRECT;
		Assert.assertEquals(
			typeHierarchyMode.getAnnotations(AnnotationForTest2.class, AnnotationFilter.FILTER_NOTHING),
			directMode.getAnnotations(AnnotationForTest2.class, AnnotationFilter.FILTER_NOTHING)
		);

		AnnotationSearchMode typeHierarchyIndirectMode = AnnotationSearchMode.TYPE_HIERARCHY_AND_INDIRECT;
		AnnotationSearchMode directIndirectMode = AnnotationSearchMode.SUPERCLASS_AND_INDIRECT;
		Assert.assertEquals(
			typeHierarchyIndirectMode.getAnnotations(AnnotationForTest2.class, AnnotationFilter.FILTER_NOTHING),
			directIndirectMode.getAnnotations(AnnotationForTest2.class, AnnotationFilter.FILTER_NOTHING)
		);
	}

	@Test
	public void nullParamsTest() {
		AnnotatedElement element = null;
		List<Annotation> annotations = AnnotationSearchMode.SELF_AND_DIRECT.getAnnotations(element, AnnotationFilter.FILTER_NOTHING);
		Assert.assertNotNull(annotations);
		Assert.assertTrue(annotations.isEmpty());
		Assert.assertNull(AnnotationSearchMode.SELF_AND_DIRECT.getAnnotation(element, AnnotationForTest1.class));
		Assert.assertFalse(AnnotationSearchMode.SELF_AND_DIRECT.isAnnotationPresent(element, AnnotationForTest1.class));
	}

	@Test
	public void copyOptionsTest() {
		ScanOptions source = AnnotationSearchMode.SELF_AND_DIRECT.scanner.options;
		ScanOptions copy = AnnotationSearchMode.SELF_AND_DIRECT.getOptions();
		Assert.assertNotSame(source, copy);
		Assert.assertEquals(source.isEnableScanAccessedType(), copy.isEnableScanAccessedType());
		Assert.assertEquals(source.isEnableScanMetaAnnotation(), copy.isEnableScanMetaAnnotation());
		Assert.assertEquals(source.isEnableScanInterface(), copy.isEnableScanInterface());
		Assert.assertEquals(source.isEnableScanSuperClass(), copy.isEnableScanSuperClass());
	}

	@SneakyThrows
	@Test
	public void directlyAndDirectTest() {
		AnnotationSearchMode strategy = AnnotationSearchMode.SELF_AND_DIRECT;

		// class
		AnnotatedElement element = ClassForTest.class;
		List<Annotation> annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(1, annotations.size());
		AnnotationForTest2 annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// method
		element = ClassForTest.class.getDeclaredMethod("method");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(1, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// field
		element = ClassForTest.class.getDeclaredField("filed");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(1, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

	}

	@SneakyThrows
	@Test
	public void directlyAndIndirectTest() {
		AnnotationSearchMode strategy = AnnotationSearchMode.SELF_AND_INDIRECT;

		// class
		AnnotatedElement element = ClassForTest.class;
		List<Annotation> annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(2, annotations.size());
		AnnotationForTest2 annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// method
		element = ClassForTest.class.getDeclaredMethod("method");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(2, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// field
		element = ClassForTest.class.getDeclaredField("filed");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(2, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);
	}

	@SneakyThrows
	@Test
	public void superClassAndDirectTest() {
		AnnotationSearchMode strategy = AnnotationSearchMode.SUPERCLASS_AND_DIRECT;

		// class
		AnnotatedElement element = ClassForTest.class;
		List<Annotation> annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(2, annotations.size());
		AnnotationForTest2 annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// method
		element = ClassForTest.class.getDeclaredMethod("method");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(2, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// field
		element = ClassForTest.class.getDeclaredField("filed");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(1, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);
	}

	@SneakyThrows
	@Test
	public void superclassAndIndirectTest() {
		AnnotationSearchMode strategy = AnnotationSearchMode.SUPERCLASS_AND_INDIRECT;

		// class
		AnnotatedElement element = ClassForTest.class;
		List<Annotation> annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(4, annotations.size());
		AnnotationForTest2 annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// method
		element = ClassForTest.class.getDeclaredMethod("method");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(4, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// field
		element = ClassForTest.class.getDeclaredField("filed");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(2, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);
	}

	@SneakyThrows
	@Test
	public void interfaceAndDirectTest() {
		AnnotationSearchMode strategy = AnnotationSearchMode.INTERFACE_AND_DIRECT;

		// class
		AnnotatedElement element = ClassForTest.class;
		List<Annotation> annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(2, annotations.size());
		AnnotationForTest2 annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// method
		element = ClassForTest.class.getDeclaredMethod("method");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(2, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// field
		element = ClassForTest.class.getDeclaredField("filed");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(1, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);
	}

	@SneakyThrows
	@Test
	public void interfaceAndIndirectTest() {
		AnnotationSearchMode strategy = AnnotationSearchMode.INTERFACE_AND_INDIRECT;

		// class
		AnnotatedElement element = ClassForTest.class;
		List<Annotation> annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(4, annotations.size());
		AnnotationForTest2 annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// method
		element = ClassForTest.class.getDeclaredMethod("method");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(4, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// field
		element = ClassForTest.class.getDeclaredField("filed");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(2, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);
	}

	@SneakyThrows
	@Test
	public void typeHierarchyAndDirectTest() {
		AnnotationSearchMode strategy = AnnotationSearchMode.TYPE_HIERARCHY_AND_DIRECT;

		// class
		AnnotatedElement element = ClassForTest.class;
		List<Annotation> annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(3, annotations.size());
		AnnotationForTest2 annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// method
		element = ClassForTest.class.getDeclaredMethod("method");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(3, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// field
		element = ClassForTest.class.getDeclaredField("filed");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(1, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);
	}

	@SneakyThrows
	@Test
	public void typeHierarchyAndIndirectAnnotationTest() {
		AnnotationSearchMode strategy = AnnotationSearchMode.TYPE_HIERARCHY_AND_INDIRECT;

		// class
		AnnotatedElement element = ClassForTest.class;
		List<Annotation> annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(6, annotations.size());
		AnnotationForTest2 annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// method
		element = ClassForTest.class.getDeclaredMethod("method");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(6, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);

		// field
		element = ClassForTest.class.getDeclaredField("filed");
		annotations = strategy.getAnnotations(element, AnnotationFilter.FILTER_JAVA);
		Assert.assertEquals(2, annotations.size());
		annotation = strategy.getAnnotation(element, AnnotationForTest2.class);
		Assert.assertEquals(element.getAnnotation(AnnotationForTest2.class), annotation);
	}

	@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	private @interface AnnotationForTest1 {
		String value() default "";
	}

	@AnnotationForTest1("annotation")
	@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	private @interface AnnotationForTest2 {
		String value() default "";
	}

	@AnnotationForTest2("class")
	private static class ClassForTest extends SuperForTest implements
		InterfaceForTest {

		@AnnotationForTest2("classField")
		private String filed;

		@AnnotationForTest2("classMethod")
		@Override
		public void method() {}

	}

	@AnnotationForTest2("interface")
	private interface InterfaceForTest {

		@AnnotationForTest2("interfaceMethod")
		void method();

	}

	@AnnotationForTest2("super")
	private static class SuperForTest implements InterfaceForTest {

		@AnnotationForTest2("superField")
		private String filed;

		@AnnotationForTest2("superMethod")
		@Override
		public void method() {}

	}

}
