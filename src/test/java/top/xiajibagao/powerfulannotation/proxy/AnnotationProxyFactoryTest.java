package top.xiajibagao.powerfulannotation.proxy;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.aggerate.AggregatedAnnotation;
import top.xiajibagao.powerfulannotation.aggerate.GenericAggregatedAnnotation;

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
        AggregatedAnnotation<AnnotationForTest> aggregatedAnnotation = new GenericAggregatedAnnotation<>(annotation, null, 0, 0);
        aggregatedAnnotation.replaceAttribute("value", attribute -> aggregatedAnnotation.getAttribute("name"));
        AnnotationForTest proxiedAnnotation = AnnotationProxyFactory.get(AnnotationForTest.class, aggregatedAnnotation);

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
        AggregatedAnnotation<AnnotationForTest> aggregatedAnnotation = new GenericAggregatedAnnotation<>(annotation, null, 0, 0);
        AnnotationForTest proxiedAnnotation = AnnotationProxyFactory.get(AnnotationForTest.class, aggregatedAnnotation);

        Assert.assertFalse(AnnotationProxyFactory.isProxied(annotation));
        Assert.assertTrue(AnnotationProxyFactory.isProxied(proxiedAnnotation));
    }

    @Test
    public void testGetOriginal() {
        AnnotationForTest annotation = ClassForTest.class.getAnnotation(AnnotationForTest.class);
        AggregatedAnnotation<AnnotationForTest> aggregatedAnnotation = new GenericAggregatedAnnotation<>(annotation, null, 0, 0);
        AnnotationForTest proxiedAnnotation = AnnotationProxyFactory.get(AnnotationForTest.class, aggregatedAnnotation);

        AggregatedAnnotation<AnnotationForTest> aggregatedAnnotation2 = new GenericAggregatedAnnotation<>(proxiedAnnotation, null, 0, 0);
        AnnotationForTest proxiedAnnotation2 = AnnotationProxyFactory.get(AnnotationForTest.class, aggregatedAnnotation);

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
