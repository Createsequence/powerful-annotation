package top.xiajibagao.powerfulannotation.repeatable;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * test for {@link RepeatableByMappingParser}
 *
 * @author huangchengxing
 */
public class RepeatableByMappingParserTest {

	@Test
	public void testParse() {
		RepeatableByMappingParser parser = new RepeatableByMappingParser();
		List<RepeatableMapping> mappings;

		mappings = parser.parse(AnnotationForTest1.class, null);
		Assert.assertEquals(2, mappings.size());
		RepeatableMapping mapping11 = mappings.get(1);
		Assert.assertNotNull(mapping11);
		Assert.assertEquals(AnnotationForTest1.class, mapping11.getElementType());
		Assert.assertEquals(AnnotationForTest2.class, mapping11.getContainerType());
		RepeatableMapping mapping12 = mappings.get(0);
		Assert.assertNotNull(mapping12);
		Assert.assertEquals(AnnotationForTest1.class, mapping12.getElementType());
		Assert.assertEquals(AnnotationForTest3.class, mapping12.getContainerType());

		mappings = parser.parse(AnnotationForTest2.class, null);
		Assert.assertEquals(1, mappings.size());
		RepeatableMapping mapping2 = mappings.get(0);
		Assert.assertNotNull(mapping2);
		Assert.assertEquals(AnnotationForTest2.class, mapping2.getElementType());
		Assert.assertEquals(AnnotationForTest3.class, mapping2.getContainerType());

		mappings = parser.parse(AnnotationForTest3.class, null);
		Assert.assertTrue(mappings.isEmpty());
	}

	@RepeatableBy(annotation = AnnotationForTest3.class, attribute = "annotation1")
	@RepeatableBy(annotation = AnnotationForTest2.class, attribute = "annotations")
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest1 {
		String value() default "";
	}

	@RepeatableBy(annotation = AnnotationForTest3.class, attribute = "annotation2")
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest2 {
		AnnotationForTest1[] annotations() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest3 {
		AnnotationForTest1[] annotation1() default {};
		AnnotationForTest2[] annotation2() default {};
	}

}
