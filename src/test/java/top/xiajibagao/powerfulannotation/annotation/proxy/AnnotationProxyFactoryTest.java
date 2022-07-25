package top.xiajibagao.powerfulannotation.annotation.proxy;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.GenericHierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * test for {@link AnnotationProxyFactory}
 *
 * @author huangchengxing
 */
public class AnnotationProxyFactoryTest {

    @Test
    public void testGet() {
        AnnotationForTest annotation = ClassForTest.class.getAnnotation(AnnotationForTest.class);
        HierarchicalAnnotation<AnnotationForTest> hierarchicalAnnotation = new GenericHierarchicalAnnotation<>(annotation, null, 0, 0);
        hierarchicalAnnotation.replaceAttribute("value", attribute -> hierarchicalAnnotation.getAttribute("name"));
        AnnotationForTest proxiedAnnotation = AnnotationProxyFactory.get(AnnotationForTest.class, hierarchicalAnnotation);

        Assert.assertNotNull(proxiedAnnotation);
        Assert.assertEquals("name", proxiedAnnotation.value());
        Assert.assertEquals("name", proxiedAnnotation.name());
        Assert.assertNotEquals(annotation.hashCode(), proxiedAnnotation.hashCode());
        Assert.assertNotEquals(annotation.toString(), proxiedAnnotation.toString());
        Assert.assertNotEquals(proxiedAnnotation, annotation);
    }

    @Test
    public void testIsProxied() {
        AnnotationForTest annotation = ClassForTest.class.getAnnotation(AnnotationForTest.class);
        HierarchicalAnnotation<AnnotationForTest> hierarchicalAnnotation = new GenericHierarchicalAnnotation<>(annotation, null, 0, 0);
        AnnotationForTest proxiedAnnotation = AnnotationProxyFactory.get(AnnotationForTest.class, hierarchicalAnnotation);

        Assert.assertFalse(AnnotationProxyFactory.isProxied(annotation));
        Assert.assertTrue(AnnotationProxyFactory.isProxied(proxiedAnnotation));
    }

    @Test
    public void testGetOriginal() {
        AnnotationForTest annotation = ClassForTest.class.getAnnotation(AnnotationForTest.class);
        HierarchicalAnnotation<AnnotationForTest> hierarchicalAnnotation = new GenericHierarchicalAnnotation<>(annotation, null, 0, 0);
        AnnotationForTest proxiedAnnotation = AnnotationProxyFactory.get(AnnotationForTest.class, hierarchicalAnnotation);

        HierarchicalAnnotation<AnnotationForTest> hierarchicalAnnotation2 = new GenericHierarchicalAnnotation<>(proxiedAnnotation, null, 0, 0);
        AnnotationForTest proxiedAnnotation2 = AnnotationProxyFactory.get(AnnotationForTest.class, hierarchicalAnnotation);

        Assert.assertEquals(annotation, AnnotationProxyFactory.getOriginal(proxiedAnnotation2));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
    private @interface AnnotationForTest {
        String value() default "";
        String name() default "";
    }

    @AnnotationForTest(value = "value", name = "name")
    private class ClassForTest {}

}
