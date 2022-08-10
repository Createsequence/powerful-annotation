package top.xiajibagao.powerfulannotation.repeatable;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;
import java.util.List;

/**
 * test for {@link StandardRepeatableMappingParser}
 *
 * @author huangchengxing
 */
public class StandardRepeatableMappingParserTest {

	@Test
	public void testParse() {
		StandardRepeatableMappingParser parser = new StandardRepeatableMappingParser();
		List<RepeatableMapping> mappings;

		mappings = parser.parse(AnnotationForTest1.class, null);
		Assert.assertEquals(1, mappings.size());
		RepeatableMapping mapping1 = mappings.get(0);
		Assert.assertNotNull(mapping1);
		Assert.assertEquals(AnnotationForTest2.class, mapping1.getContainerType());
		Assert.assertEquals(AnnotationForTest1.class, mapping1.getElementType());

		mappings = parser.parse(AnnotationForTest2.class, null);
		Assert.assertEquals(1, mappings.size());
		RepeatableMapping mapping2 = mappings.get(0);
		Assert.assertNotNull(mapping2);
		Assert.assertEquals(AnnotationForTest3.class, mapping2.getContainerType());
		Assert.assertEquals(AnnotationForTest2.class, mapping2.getElementType());

		mappings = parser.parse(AnnotationForTest3.class, null);
		Assert.assertTrue(mappings.isEmpty());
	}

	@Repeatable(AnnotationForTest2.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest1 {
		String value() default "";
		String name() default "";
	}

	@Repeatable(AnnotationForTest3.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest2 {
		AnnotationForTest1[] value() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest3 {
		AnnotationForTest2[] value() default {};
	}

}
