package top.xiajibagao.powerfulannotation.scanner.processor;

import cn.hutool.core.util.ObjectUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;

public class AnnotationFinderTest {

	@Test
	public void finderTest() {
		AnnotationFinder<Annotation> finder = new AnnotationFinder<>(
			(vi, hi, annotation) -> annotation,
			annotation -> ObjectUtil.equals(annotation.annotationType(), AnnotationForTest2.class)
		);

		AnnotationForTest1 annotationForTest1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
		finder.accept(0, 0, annotationForTest1);
		Assert.assertFalse(finder.isFound());
		Assert.assertFalse(finder.interrupted());
		Assert.assertNull(finder.getTarget());

		AnnotationForTest2 annotationForTest2 = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
		finder.accept(0, 0, annotationForTest2);
		Assert.assertTrue(finder.isFound());
		Assert.assertTrue(finder.interrupted());
		Assert.assertEquals(annotationForTest2, finder.getTarget());

		AnnotationForTest3 annotationForTest3 = ClassForTest.class.getAnnotation(AnnotationForTest3.class);
		finder.accept(0, 0, annotationForTest3);
		Assert.assertTrue(finder.isFound());
		Assert.assertTrue(finder.interrupted());
		Assert.assertEquals(annotationForTest2, finder.getTarget());
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
