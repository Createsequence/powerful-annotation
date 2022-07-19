package top.xiajibagao.powerfulannotation.repeatable;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;

public class StandardRepeatableMappingParserTest {

	@Test
	public void parseTest() {
		StandardRepeatableMappingParser parser = new StandardRepeatableMappingParser();

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

	@Repeatable(AnnotationForTest2.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	@interface AnnotationForTest1 {
		String value() default "";
		String name() default "";
	}

	@Repeatable(AnnotationForTest3.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	@interface AnnotationForTest2 {
		AnnotationForTest1[] value() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	@interface AnnotationForTest3 {
		AnnotationForTest2[] value() default {};
	}

}
