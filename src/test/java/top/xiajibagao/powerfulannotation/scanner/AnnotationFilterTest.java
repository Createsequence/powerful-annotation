package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.util.ArrayUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;

public class AnnotationFilterTest {

	@Test
	public void JavaAnnotationFilterTest() {
		Annotation[] annotations = AnnotationForTest2.class.getAnnotations();
		annotations = ArrayUtil.filter(annotations, AnnotationFilter.FILTER_JAVA::test);
		Assert.assertEquals(1, annotations.length);
		Assert.assertEquals(AnnotationForTest2.class.getAnnotation(AnnotationForTest1.class), annotations[0]);
	}

	@Test
	public void AnythingFilterTest() {
		Annotation[] annotations = AnnotationForTest2.class.getAnnotations();
		annotations = ArrayUtil.filter(annotations, AnnotationFilter.FILTER_ANYTHING::test);
		Assert.assertEquals(0, annotations.length);
	}

	@Test
	public void NothingFilterTest() {
		Annotation[] annotations = AnnotationForTest2.class.getAnnotations();
		annotations = ArrayUtil.filter(annotations, AnnotationFilter.FILTER_NOTHING::test);
		Assert.assertEquals(3, annotations.length);
	}

	@Test
	public void combineTest() {
		Annotation[] annotations = AnnotationForTest2.class.getAnnotations();
		AnnotationFilter filter = AnnotationFilter.combine(AnnotationFilter.FILTER_ANYTHING, AnnotationFilter.FILTER_JAVA);
		annotations = ArrayUtil.filter(annotations, filter::test);
		Assert.assertEquals(0, annotations.length);
	}

	@Target(ElementType.ANNOTATION_TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface AnnotationForTest1 {}

	@AnnotationForTest1
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface AnnotationForTest2 {}

}
