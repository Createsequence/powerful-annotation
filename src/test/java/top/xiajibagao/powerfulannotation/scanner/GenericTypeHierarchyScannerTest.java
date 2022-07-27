package top.xiajibagao.powerfulannotation.scanner;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.scanner.processor.GenericAnnotationCollector;

import java.lang.annotation.*;
import java.util.List;

/**
 * test for {@link GenericTypeHierarchyScanner}
 *
 * @author huangchengxing
 */
public class GenericTypeHierarchyScannerTest {

	@Test
	public void testScanDirectly() {
		final GenericTypeHierarchyScanner scanner = new GenericTypeHierarchyScanner(false, false, false);
		GenericAnnotationCollector<Annotation> processor = GenericAnnotationCollector.create();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getTargets();
		Assert.assertEquals(1, annotations.size());
	}

	@Test
	public void testScanDirectlyAndMetaAnnotation() {
		final GenericTypeHierarchyScanner scanner = new GenericTypeHierarchyScanner(false, false, true);
		GenericAnnotationCollector<Annotation> processor = GenericAnnotationCollector.create();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getTargets();
		Assert.assertEquals(2, annotations.size());
	}

	@Test
	public void testScanSuperclass() {
		final GenericTypeHierarchyScanner scanner = new GenericTypeHierarchyScanner(true, false, false);
		GenericAnnotationCollector<Annotation> processor = GenericAnnotationCollector.create();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getTargets();
		Assert.assertEquals(2, annotations.size());
	}

	@Test
	public void testScanSuperclassAndMetaAnnotation() {
		final GenericTypeHierarchyScanner scanner = new GenericTypeHierarchyScanner(true, false, true);
		GenericAnnotationCollector<Annotation> processor = GenericAnnotationCollector.create();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getTargets();
		Assert.assertEquals(4, annotations.size());
	}

	@Test
	public void testScanInterface() {
		final GenericTypeHierarchyScanner scanner = new GenericTypeHierarchyScanner(false, true, false);
		GenericAnnotationCollector<Annotation> processor = GenericAnnotationCollector.create();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getTargets();
		Assert.assertEquals(2, annotations.size());
	}

	@Test
	public void testScanInterfaceAndMetaAnnotation() {
		final GenericTypeHierarchyScanner scanner = new GenericTypeHierarchyScanner(true, true, true);
		GenericAnnotationCollector<Annotation> processor = GenericAnnotationCollector.create();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getTargets();
		Assert.assertEquals(6, annotations.size());
	}

	@Test
	public void testScanTypeHierarchy() {
		final GenericTypeHierarchyScanner scanner = new GenericTypeHierarchyScanner(true, true, false);
		GenericAnnotationCollector<Annotation> processor = GenericAnnotationCollector.create();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getTargets();
		Assert.assertEquals(3, annotations.size());
	}

	@Test
	public void testScanTypeHierarchyAndMetaAnnotation() {
		final GenericTypeHierarchyScanner scanner = new GenericTypeHierarchyScanner(true, true, true);
		GenericAnnotationCollector<Annotation> processor = GenericAnnotationCollector.create();
		scanner.scan(ClassForTest.class, processor, AnnotationFilter.NOT_JDK_ANNOTATION);
		final List<Annotation> annotations = processor.getTargets();
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
