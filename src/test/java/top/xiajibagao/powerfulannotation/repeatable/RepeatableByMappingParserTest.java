package top.xiajibagao.powerfulannotation.repeatable;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.RepeatableBy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class RepeatableByMappingParserTest {

	@Test
	public void parseTest() {
		RepeatableByMappingParser parser = new RepeatableByMappingParser();

		RepeatableMapping mapping1 = parser.parse(AnnotationForTest1.class, null);
		Assert.assertNotNull(mapping1);
		Assert.assertEquals(AnnotationForTest2.class, mapping1.getContainerType());
		Assert.assertEquals(AnnotationForTest1.class, mapping1.getElementType());

		RepeatableMapping mapping2 = parser.parse(AnnotationForTest2.class, null);
		Assert.assertNotNull(mapping2);
		Assert.assertEquals(AnnotationForTest3.class, mapping2.getContainerType());
		Assert.assertEquals(AnnotationForTest2.class, mapping2.getElementType());

		RepeatableMapping mapping3 = parser.parse(AnnotationForTest3.class, null);
		Assert.assertNull(mapping3);
	}

	//@Link(annotation = AnnotationForTest2.class)
	@RepeatableBy(annotation = AnnotationForTest2.class, attribute = "annotations")
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest1 {
		String value() default "";
	}

	@RepeatableBy(annotation = AnnotationForTest3.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest2 {
		AnnotationForTest1[] annotations() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest3 {
		AnnotationForTest2[] value() default {};
	}

}
