package top.xiajibagao.powerfulannotation.annotation;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class GenericHierarchicalAnnotationTest {

    @Test
    public void testGetAnnotation() {
        AnnotationForTest annotation = ClassForTest.class.getAnnotation(AnnotationForTest.class);
        GenericHierarchicalAnnotation<AnnotationForTest> aggregatedAnnotation = new GenericHierarchicalAnnotation<>(
            annotation, ClassForTest.class, 0, 0
        );

        Assert.assertEquals(annotation, aggregatedAnnotation.getAnnotation());
        Assert.assertEquals(ClassForTest.class, aggregatedAnnotation.getRoot());
        Assert.assertEquals(AnnotationForTest.class, aggregatedAnnotation.annotationType());
        Assert.assertEquals(0, aggregatedAnnotation.getHorizontalIndex());
        Assert.assertEquals(0, aggregatedAnnotation.getVerticalIndex());

        Assert.assertTrue(aggregatedAnnotation.hasAttribute("id", Integer.class));
        Assert.assertFalse(aggregatedAnnotation.hasAttribute("id", String.class));
        Assert.assertEquals(2, aggregatedAnnotation.getAllAttribute().size());

        Assert.assertNotNull(aggregatedAnnotation.getAttribute("id"));
        Assert.assertNotNull(aggregatedAnnotation.getAttribute("value"));

        Assert.assertEquals(46, aggregatedAnnotation.getAttributeValue("id", Integer.class));
        Assert.assertNull(aggregatedAnnotation.getAttributeValue("id", String.class));
        Assert.assertEquals("value", aggregatedAnnotation.getAttributeValue("value", String.class));
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest {
        String value() default "";
        int id() default 0;
    }

    @AnnotationForTest(value = "value", id = 46)
    private static class ClassForTest {}

}
