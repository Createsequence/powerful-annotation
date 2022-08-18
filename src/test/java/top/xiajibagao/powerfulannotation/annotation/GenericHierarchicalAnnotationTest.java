package top.xiajibagao.powerfulannotation.annotation;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.attribute.CacheableAnnotationAttribute;
import top.xiajibagao.powerfulannotation.helper.ReflectUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * test for {@link GenericHierarchicalAnnotation}
 *
 * @author huangchengxing
 */
public class GenericHierarchicalAnnotationTest {

    @Test
    public void testIntrospection() {
        AnnotationForTest source = ClassForTest.class.getAnnotation(AnnotationForTest.class);
        Object root = new Object();
        GenericHierarchicalAnnotation<Object> annotation = new GenericHierarchicalAnnotation<>(
            source, root, root, 0, 0
        );
        GenericHierarchicalAnnotation<Object> annotation2 = new GenericHierarchicalAnnotation<>(
            source, root, root, 0, 0
        );
        Assert.assertSame(root, annotation.getRoot());
        Assert.assertSame(root, annotation.getSource());
        Assert.assertSame(source, annotation.getAnnotation());
        Assert.assertEquals(source.annotationType(), annotation.annotationType());
        Assert.assertEquals(0, annotation.getVerticalIndex());
        Assert.assertEquals(0, annotation.getHorizontalIndex());
        Assert.assertEquals(annotation2, annotation);
        Assert.assertEquals(annotation2.hashCode(), annotation.hashCode());
    }

    @Test
    public void testHasAttribute() {
        AnnotationForTest source = ClassForTest.class.getAnnotation(AnnotationForTest.class);
        Object root = new Object();
        GenericHierarchicalAnnotation<Object> annotation = new GenericHierarchicalAnnotation<>(
            source, root, root, 0, 0
        );

        // hasAttribute
        Assert.assertEquals(2, annotation.getAttributeMap().size());
        Assert.assertTrue(annotation.hasAttribute("value", String.class));
        Assert.assertTrue(annotation.hasAttribute("code", Integer.class));
        Assert.assertTrue(annotation.hasAttribute("code", int.class));
        Assert.assertFalse(annotation.hasAttribute("value", Integer.class));

        // getAttribute
        Assert.assertNotNull(annotation.getAttribute("value"));
        Assert.assertNotNull(annotation.getAttribute("code"));
        Assert.assertNull(annotation.getAttribute("name"));

        // putAttribute & getAllAttribute
        annotation.putAttribute("foo", new CacheableAnnotationAttribute(source, ReflectUtils.getDeclaredMethod(AnnotationForTest.class, "code")));
        Assert.assertNotNull(annotation.getAttribute("foo"));
        Assert.assertEquals(3, annotation.getAllAttribute().size());
    }

    @AnnotationForTest
    private static class ClassForTest {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    private @interface AnnotationForTest {
        String value() default "";
        int code() default 0;
    }

}
