package top.xiajibagao.powerfulannotation.annotation.attribute;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * test for {@link FixedValueAnnotationAttribute}
 *
 * @author huangchengxing
 */
public class FixedValueAnnotationAttributeTest {

    @Test
    public void testBaseInfo() {
        Annotation annotation = ClassForTest.class.getAnnotation(AnnotationForTest.class);
        Method method = ReflectUtil.getMethod(AnnotationForTest.class, "value");
        Assert.assertNotNull(method);
        AnnotationAttribute originalAttribute = new CacheableAnnotationAttribute(annotation, method);
        Assert.assertThrows(IllegalArgumentException.class, () -> new FixedValueAnnotationAttribute(originalAttribute, 123));
        FixedValueAnnotationAttribute attribute = new FixedValueAnnotationAttribute(originalAttribute, "name");

        // 注解属性
        Assert.assertEquals(annotation, attribute.getAnnotation());
        Assert.assertEquals(annotation.annotationType(), attribute.getAnnotationType());
        Assert.assertTrue(attribute.isWrapped());
        Assert.assertEquals(originalAttribute, attribute.getNonWrappedOriginal());
        // 方法属性
        Assert.assertEquals(method.getName(), attribute.getAttributeName());
        Assert.assertEquals(method.getReturnType(), attribute.getAttributeType());
        // 获取包装对象
        Assert.assertEquals(originalAttribute, attribute.getOriginal());
        Assert.assertEquals(CollUtil.newArrayList(originalAttribute), attribute.getAllLinkedNonWrappedAttributes());
        // 获取值
        Assert.assertEquals("name", attribute.getValue());
        Assert.assertFalse(attribute.isValueEquivalentToDefaultValue());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface AnnotationForTest {
        String value();
    }

    @AnnotationForTest("value")
    private static class ClassForTest {}

}
