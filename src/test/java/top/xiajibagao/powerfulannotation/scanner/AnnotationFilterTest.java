package top.xiajibagao.powerfulannotation.scanner;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationFilterTest {

	@Test
	public void JavaAnnotationFilterTest() {
		Annotation[] annotations = AnnotationForTest2.class.getAnnotations();
		List<Annotation> annotationList = Stream.of(annotations)
			.filter(AnnotationFilter.FILTER_JAVA)
			.collect(Collectors.toList());
		Assert.assertEquals(1, annotationList.size());
		Assert.assertEquals(AnnotationForTest2.class.getAnnotation(AnnotationForTest1.class), annotationList.get(0));
	}

	@Test
	public void AnythingFilterTest() {
		Annotation[] annotations = AnnotationForTest2.class.getAnnotations();
		List<Annotation> annotationList = Stream.of(annotations)
			.filter(AnnotationFilter.FILTER_ANYTHING)
			.collect(Collectors.toList());
		Assert.assertEquals(0, annotationList.size());
	}

	@Test
	public void NothingFilterTest() {
		Annotation[] annotations = AnnotationForTest2.class.getAnnotations();
		List<Annotation> annotationList = Stream.of(annotations)
			.filter(AnnotationFilter.FILTER_NOTHING)
			.collect(Collectors.toList());
		Assert.assertEquals(3, annotationList.size());
	}

	@Test
	public void combineTest() {
		Annotation[] annotations = AnnotationForTest2.class.getAnnotations();
		AnnotationFilter filter = AnnotationFilter.combine(AnnotationFilter.FILTER_ANYTHING, AnnotationFilter.FILTER_JAVA);
		List<Annotation> annotationList = Stream.of(annotations)
			.filter(filter)
			.collect(Collectors.toList());
		Assert.assertEquals(0, annotationList.size());
	}

	@Target(ElementType.ANNOTATION_TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface AnnotationForTest1 {}

	@AnnotationForTest1
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface AnnotationForTest2 {}

}
