package top.xiajibagao.powerfulannotation.annotation.attribute;

import cn.hutool.core.util.ReflectUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * test for {@link CacheableAnnotationAttribute}
 *
 * @author huangchengxing
 */
public class CacheableAnnotationAttributeTest {

    @Test
    public void testBaseInfo() {
        final Annotation annotation = ClassForTest1.class.getAnnotation(AnnotationForTest.class);
        final Method attribute = ReflectUtil.getMethod(AnnotationForTest.class, "value");
        final CacheableAnnotationAttribute annotationAttribute = new CacheableAnnotationAttribute(annotation, attribute);
        // 注解属性
        Assert.assertEquals(annotation, annotationAttribute.getAnnotation());
        Assert.assertEquals(annotation.annotationType(), annotationAttribute.getAnnotationType());
        // 方法属性
        Assert.assertEquals(attribute.getName(), annotationAttribute.getAttributeName());
        Assert.assertEquals(attribute.getReturnType(), annotationAttribute.getAttributeType());
    }

    @Test
    public void testWorkWhenValueDefault() {
        final Annotation annotation = ClassForTest1.class.getAnnotation(AnnotationForTest.class);
        final Method attribute = ReflectUtil.getMethod(AnnotationForTest.class, "value");
        final CacheableAnnotationAttribute annotationAttribute = new CacheableAnnotationAttribute(annotation, attribute);

        // 值处理
        Assert.assertEquals("", annotationAttribute.getValue());
        Assert.assertTrue(annotationAttribute.isValueEquivalentToDefaultValue());
        Assert.assertFalse(annotationAttribute.isWrapped());
    }

    @Test
    public void TestWorkWhenValueNonDefault() {
        final Annotation annotation = ClassForTest2.class.getAnnotation(AnnotationForTest.class);
        final Method attribute = ReflectUtil.getMethod(AnnotationForTest.class, "value");
        final CacheableAnnotationAttribute annotationAttribute = new CacheableAnnotationAttribute(annotation, attribute);

        // 值处理
        Assert.assertEquals("test", annotationAttribute.getValue());
        Assert.assertFalse(annotationAttribute.isValueEquivalentToDefaultValue());
        Assert.assertFalse(annotationAttribute.isWrapped());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface AnnotationForTest {
        String value() default "";
    }

    @AnnotationForTest("")
    private static class ClassForTest1 {}

    @AnnotationForTest("test")
    private static class ClassForTest2 {}

}
