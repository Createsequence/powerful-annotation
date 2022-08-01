package top.xiajibagao.powerfulannotation.annotation.attribute;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.helper.ReflectUtils;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * test for {@link ForceAliasedAnnotationAttribute}
 *
 * @author huangchengxing
 */
public class ForceAliasedAnnotationAttributeTest {

	@Test
	public void testBaseInfo() {
		// 组合属性
		Annotation annotation = ClassForTest1.class.getAnnotation(AnnotationForTest.class);
		Method valueMethod = ReflectUtils.getDeclaredMethod(AnnotationForTest.class, "value");
		CacheableAnnotationAttribute valueAttribute = new CacheableAnnotationAttribute(annotation, valueMethod);
		Method nameMethod = ReflectUtils.getDeclaredMethod(AnnotationForTest.class, "name");
		CacheableAnnotationAttribute nameAttribute = new CacheableAnnotationAttribute(annotation, nameMethod);
		ForceAliasedAnnotationAttribute valueAnnotationAttribute = new ForceAliasedAnnotationAttribute(valueAttribute, nameAttribute);

		// 注解属性
		Assert.assertEquals(annotation, valueAnnotationAttribute.getAnnotation());
		Assert.assertEquals(annotation.annotationType(), valueAnnotationAttribute.getAnnotationType());

		// 方法属性
		Assert.assertEquals(valueMethod.getName(), valueAnnotationAttribute.getAttributeName());
		Assert.assertEquals(valueMethod.getReturnType(), valueAnnotationAttribute.getAttributeType());
	}

	@Test
	public void testWorkWhenValueDefault() {
		// 组合属性
		Annotation annotation = ClassForTest1.class.getAnnotation(AnnotationForTest.class);
		Method valueMethod = ReflectUtils.getDeclaredMethod(AnnotationForTest.class, "value");
		CacheableAnnotationAttribute valueAttribute = new CacheableAnnotationAttribute(annotation, valueMethod);
		Method nameMethod = ReflectUtils.getDeclaredMethod(AnnotationForTest.class, "name");
		CacheableAnnotationAttribute nameAttribute = new CacheableAnnotationAttribute(annotation, nameMethod);
		AliasedAnnotationAttribute valueAnnotationAttribute = new AliasedAnnotationAttribute(valueAttribute, nameAttribute);

		// 值处理
		Assert.assertEquals("name", valueAnnotationAttribute.getValue());
		Assert.assertFalse(valueAnnotationAttribute.isValueEquivalentToDefaultValue());
		Assert.assertTrue(valueAnnotationAttribute.isWrapped());
	}

	@Test
	public void testWorkWhenValueNonDefault() {
		// 组合属性
		Annotation annotation = ClassForTest2.class.getAnnotation(AnnotationForTest.class);
		Method valueMethod = ReflectUtils.getDeclaredMethod(AnnotationForTest.class, "value");
		CacheableAnnotationAttribute valueAttribute = new CacheableAnnotationAttribute(annotation, valueMethod);
		Method nameMethod = ReflectUtils.getDeclaredMethod(AnnotationForTest.class, "name");
		CacheableAnnotationAttribute nameAttribute = new CacheableAnnotationAttribute(annotation, nameMethod);
		ForceAliasedAnnotationAttribute valueAnnotationAttribute = new ForceAliasedAnnotationAttribute(valueAttribute, nameAttribute);

		// 值处理
		Assert.assertEquals("", valueAnnotationAttribute.getValue());
		Assert.assertTrue(valueAnnotationAttribute.isValueEquivalentToDefaultValue());
		Assert.assertTrue(valueAnnotationAttribute.isWrapped());
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	private @interface AnnotationForTest {
		String value() default "";
		String name() default "";
	}

	@AnnotationForTest(name = "name", value = "value")
	private static class ClassForTest1 {}

	@AnnotationForTest(value = "value")
	private static class ClassForTest2 {}

}
