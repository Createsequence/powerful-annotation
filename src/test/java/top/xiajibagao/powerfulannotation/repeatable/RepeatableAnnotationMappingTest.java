package top.xiajibagao.powerfulannotation.repeatable;

import cn.hutool.core.util.ReflectUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class RepeatableAnnotationMappingTest {

	@Test
	public void repeatableAnnotationMappingTest() {
		RepeatableAnnotationMapping mapping = new RepeatableAnnotationMapping(
			AnnotationForTest2.class, AnnotationForTest1.class,
			ReflectUtil.getMethod(AnnotationForTest2.class, "value")
		);
		Assert.assertEquals(AnnotationForTest1.class, mapping.getContainerType());
		Assert.assertEquals(AnnotationForTest2.class, mapping.getElementType());

		AnnotationForTest2 annotation = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
		Assert.assertEquals(3, annotation.value().length);
		Assert.assertArrayEquals(annotation.value(), mapping.getElementsFromContainer(annotation));
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	@interface AnnotationForTest1 {
		String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	@interface AnnotationForTest2 {
		AnnotationForTest1[] value() default {};
	}

	@AnnotationForTest2({
		@AnnotationForTest1("1"),
		@AnnotationForTest1("2"),
		@AnnotationForTest1("3")
	})
	static class ClassForTest {}


}
