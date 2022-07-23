package top.xiajibagao.powerfulannotation.scanner;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationCollector;

import java.lang.annotation.*;
import java.util.List;

/**
 * test for {@link GenericTypeHierarchyAnnotationScanner}
 *
 * @author huangchengxing
 */
public class GenericTypeHierarchyAnnotationScannerTest {

	@Test
	public void testScanDirectly() {
		final GenericTypeHierarchyAnnotationScanner scanner = new GenericTypeHierarchyAnnotationScanner(false, false, false);
		AnnotationCollector processor = new AnnotationCollector();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getAnnotations();
		Assert.assertEquals(1, annotations.size());
	}

	@Test
	public void testScanDirectlyAndMetaAnnotation() {
		final GenericTypeHierarchyAnnotationScanner scanner = new GenericTypeHierarchyAnnotationScanner(false, false, true);
		AnnotationCollector processor = new AnnotationCollector();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getAnnotations();
		Assert.assertEquals(2, annotations.size());
	}

	@Test
	public void testScanSuperclass() {
		final GenericTypeHierarchyAnnotationScanner scanner = new GenericTypeHierarchyAnnotationScanner(true, false, false);
		AnnotationCollector processor = new AnnotationCollector();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getAnnotations();
		Assert.assertEquals(2, annotations.size());
	}

	@Test
	public void testScanSuperclassAndMetaAnnotation() {
		final GenericTypeHierarchyAnnotationScanner scanner = new GenericTypeHierarchyAnnotationScanner(true, false, true);
		AnnotationCollector processor = new AnnotationCollector();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getAnnotations();
		Assert.assertEquals(4, annotations.size());
	}

	@Test
	public void testScanInterface() {
		final GenericTypeHierarchyAnnotationScanner scanner = new GenericTypeHierarchyAnnotationScanner(false, true, false);
		AnnotationCollector processor = new AnnotationCollector();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getAnnotations();
		Assert.assertEquals(2, annotations.size());
	}

	@Test
	public void testScanInterfaceAndMetaAnnotation() {
		final GenericTypeHierarchyAnnotationScanner scanner = new GenericTypeHierarchyAnnotationScanner(true, true, true);
		AnnotationCollector processor = new AnnotationCollector();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getAnnotations();
		Assert.assertEquals(6, annotations.size());
	}

	@Test
	public void testScanTypeHierarchy() {
		final GenericTypeHierarchyAnnotationScanner scanner = new GenericTypeHierarchyAnnotationScanner(true, true, false);
		AnnotationCollector processor = new AnnotationCollector();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getAnnotations();
		Assert.assertEquals(3, annotations.size());
	}

	@Test
	public void testScanTypeHierarchyAndMetaAnnotation() {
		final GenericTypeHierarchyAnnotationScanner scanner = new GenericTypeHierarchyAnnotationScanner(true, true, true);
		AnnotationCollector processor = new AnnotationCollector();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getAnnotations();
		Assert.assertEquals(6, annotations.size());
	}

	@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	private @interface MetaAnnotationForTest { }

	@MetaAnnotationForTest
	@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	private @interface AnnotationForTest { }

	@AnnotationForTest
	private static class ClassForTest extends SupperForTest implements InterfaceForTest { }

	@AnnotationForTest
	private static class SupperForTest { }

	@AnnotationForTest
	private interface InterfaceForTest { }

}
