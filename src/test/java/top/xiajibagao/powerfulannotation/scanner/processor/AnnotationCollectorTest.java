package top.xiajibagao.powerfulannotation.scanner.processor;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;
import java.util.Arrays;

public class AnnotationCollectorTest {

	@Test
	public void processTest() {
		AnnotationCollector<Annotation> collector = new AnnotationCollector<>((vi, hi, annotation) -> annotation);

		AnnotationForTest1 annotationForTest1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
		collector.accept(0, 0, annotationForTest1);
		AnnotationForTest2 annotationForTest2 = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
		collector.accept(0, 0, annotationForTest2);
		AnnotationForTest3 annotationForTest3 = ClassForTest.class.getAnnotation(AnnotationForTest3.class);
		collector.accept(0, 0, annotationForTest3);

		Assert.assertEquals(Arrays.asList(annotationForTest1, annotationForTest2, annotationForTest3), collector.getTargets());
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	private @interface AnnotationForTest1 {}

	@AnnotationForTest1
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	private @interface AnnotationForTest2 {}

	@AnnotationForTest2
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	private @interface AnnotationForTest3 {}

	@AnnotationForTest1
	@AnnotationForTest2
	@AnnotationForTest3
	private static class ClassForTest{}

}
