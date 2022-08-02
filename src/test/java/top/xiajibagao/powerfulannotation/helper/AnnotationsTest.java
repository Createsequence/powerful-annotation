package top.xiajibagao.powerfulannotation.helper;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableBy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * test for {@link Annotations}
 *
 * @author huangchengxing
 */
public class AnnotationsTest {

    @Test
    public void testGetDeclaredAnnotations() {
        Assert.assertArrayEquals(
            ClassForTest.class.getDeclaredAnnotations(),
            Annotations.getDeclaredAnnotations(ClassForTest.class)
        );
    }

    @Test
    public void testGetDeclaredAnnotation() {
        Assert.assertEquals(
            ClassForTest.class.getDeclaredAnnotation(AnnotationForTest2.class),
            Annotations.getDeclaredAnnotation(ClassForTest.class, AnnotationForTest2.class)
        );
    }

    @Test
    public void testEmptyAnnotations() {
        Assert.assertEquals(0, Annotations.emptyAnnotations().length);
    }

    @Test
    public void testIsAttributeMethod() {
        List<Method> attributes = Stream.of(AnnotationForTest1.class.getDeclaredMethods())
            .filter(Annotations::isAttributeMethod)
            .collect(Collectors.toList());
        Assert.assertEquals(1, attributes.size());
        Assert.assertEquals(ReflectUtils.getDeclaredMethod(AnnotationForTest1.class, "value"), attributes.get(0));
    }

    // =========================== get & direct ===========================

    @Test
    public void testGetDirectAnnotation() {
        AnnotationForTest1 annotation = Annotations.getDirectAnnotation(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertNotNull(annotation);
        Assert.assertEquals("class1", annotation.value());
    }

    @Test
    public void testGetAllDirectAnnotations() {
        List<AnnotationForTest1> annotations = Annotations.getAllDirectAnnotations(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(1, annotations.size());
        Assert.assertEquals("class1", annotations.get(0).value());
    }

    @Test
    public void testGetAllDirectRepeatableAnnotations() {
        List<AnnotationForTest1> annotations = Annotations.getAllDirectRepeatableAnnotations(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(3, annotations.size());
        Assert.assertEquals(
            CollUtils.newHashSet("class1", "class3", "class4"),
            CollUtils.toSet(annotations, AnnotationForTest1::value)
        );
    }

    @Test
    public void testIsDirectAnnotationPresent() {
        Assert.assertTrue(Annotations.isDirectAnnotationPresent(ClassForTest.class, AnnotationForTest1.class));
        Assert.assertTrue(Annotations.isDirectAnnotationPresent(ClassForTest.class, AnnotationForTest2.class));
        Assert.assertFalse(Annotations.isDirectAnnotationPresent(ClassForTest.class, AnnotationForTest3.class));
    }

    // =========================== get & indirect ===========================

    @Test
    public void testGetIndirectAnnotation() {
        AnnotationForTest1 annotation1 = Annotations.getIndirectAnnotation(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertNotNull(annotation1);
        Assert.assertEquals("class1", annotation1.value());
        AnnotationForTest3 annotation2 = Annotations.getIndirectAnnotation(ClassForTest.class, AnnotationForTest3.class);
        Assert.assertNotNull(annotation2);
        Assert.assertEquals("annotation2", annotation2.value());
    }

    @Test
    public void testGetAllIndirectAnnotations() {
        List<AnnotationForTest1> annotations = Annotations.getAllIndirectAnnotations(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(2, annotations.size());
        Assert.assertEquals(
            CollUtils.newHashSet("annotation1", "class1"),
            CollUtils.toSet(annotations, AnnotationForTest1::value)
        );
    }

    @Test
    public void testGetAllIndirectRepeatableAnnotations() {
        List<AnnotationForTest1> annotations = Annotations.getAllDirectRepeatableAnnotations(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(3, annotations.size());
        Assert.assertEquals(
            CollUtils.newHashSet("class1", "class3", "class4"),
            CollUtils.toSet(annotations, AnnotationForTest1::value)
        );
    }

    @Test
    public void testIsIndirectAnnotationPresent() {
        Assert.assertTrue(Annotations.isIndirectAnnotationPresent(ClassForTest.class, AnnotationForTest1.class));
        Assert.assertTrue(Annotations.isIndirectAnnotationPresent(ClassForTest.class, AnnotationForTest2.class));
        Assert.assertTrue(Annotations.isIndirectAnnotationPresent(ClassForTest.class, AnnotationForTest3.class));
    }

    // =========================== find & direct ===========================

    @Test
    public void testFindDirectAnnotation() {
        AnnotationForTest1 annotation1 = Annotations.findDirectAnnotation(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertNotNull(annotation1);
        Assert.assertEquals("class1", annotation1.value());

        AnnotationForTest2 annotation2 = Annotations.findDirectAnnotation(ClassForTest.class, AnnotationForTest2.class);
        Assert.assertNotNull(annotation2);
        Assert.assertEquals("class2", annotation2.value());
    }

    @Test
    public void testFindAllDirectAnnotations() {
        List<AnnotationForTest1> annotations = Annotations.findAllDirectAnnotations(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(3, annotations.size());
        Assert.assertEquals("class1", annotations.get(0).value());
        Assert.assertEquals("super1", annotations.get(1).value());
        Assert.assertEquals("interface1", annotations.get(2).value());
    }

    @Test
    public void testFindAllDirectRepeatableAnnotations() {
        List<AnnotationForTest1> annotations = Annotations.findAllDirectRepeatableAnnotations(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(9, annotations.size());
        Assert.assertEquals(
            CollUtils.newHashSet("class1", "class3", "class4", "super1", "super3", "super4", "interface1", "interface3", "interface4"),
            CollUtils.toSet(annotations, AnnotationForTest1::value)
        );
    }

    @Test
    public void testIsDirectAnnotationFound() {
        Assert.assertTrue(Annotations.isDirectAnnotationFound(ClassForTest.class, AnnotationForTest1.class));
        Assert.assertTrue(Annotations.isDirectAnnotationPresent(ClassForTest.class, AnnotationForTest2.class));
        Assert.assertFalse(Annotations.isDirectAnnotationPresent(ClassForTest.class, AnnotationForTest3.class));
    }

    // =========================== find & indirect ===========================

    @Test
    public void testFindIndirectAnnotation() {
        AnnotationForTest1 annotation1 = Annotations.findIndirectAnnotation(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertNotNull(annotation1);
        Assert.assertEquals("class1", annotation1.value());
        AnnotationForTest3 annotation2 = Annotations.findIndirectAnnotation(ClassForTest.class, AnnotationForTest3.class);
        Assert.assertNotNull(annotation2);
        Assert.assertEquals("annotation2", annotation2.value());
    }

    @Test
    public void testFindAllIndirectAnnotations() {
        List<AnnotationForTest1> annotations = Annotations.findAllIndirectAnnotations(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(6, annotations.size());
        Assert.assertEquals(
            CollUtils.newHashSet("annotation1", "class1", "annotation1", "super1", "annotation1", "interface1"),
            CollUtils.toSet(annotations, AnnotationForTest1::value)
        );
    }

    @Test
    public void testFindAllIndirectRepeatableAnnotations() {
        List<AnnotationForTest1> annotations = Annotations.findAllDirectRepeatableAnnotations(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(9, annotations.size());
        Assert.assertEquals(
            CollUtils.newHashSet("class1", "class3", "class4", "super1", "super3", "super4", "interface1", "interface3", "interface4"),
            CollUtils.toSet(annotations, AnnotationForTest1::value)
        );
    }

    @Test
    public void testIsIndirectAnnotationFind() {
        Assert.assertTrue(Annotations.isIndirectAnnotationPresent(ClassForTest.class, AnnotationForTest1.class));
        Assert.assertTrue(Annotations.isIndirectAnnotationPresent(ClassForTest.class, AnnotationForTest2.class));
        Assert.assertTrue(Annotations.isIndirectAnnotationPresent(ClassForTest.class, AnnotationForTest3.class));
    }

    @RepeatableBy(annotation = AnnotationForTest2.class, attribute = "annotations")
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest1 {
        String value() default "";
    }

    @AnnotationForTest1("annotation1")
    @AnnotationForTest3("annotation2")
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest2 {
        String value() default "";
        AnnotationForTest1[] annotations() default {};
    }

    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest3 {
        String value() default "";
    }

    @AnnotationForTest1("class1")
    @AnnotationForTest2(value = "class2", annotations = { @AnnotationForTest1("class3"), @AnnotationForTest1("class4") })
    private static class ClassForTest extends SuperForTest implements InterfaceForTest { }

    @AnnotationForTest1("interface1")
    @AnnotationForTest2(value = "interface2", annotations = { @AnnotationForTest1("interface3"), @AnnotationForTest1("interface4") })
    private interface InterfaceForTest { }

    @AnnotationForTest1("super1")
    @AnnotationForTest2(value = "super2", annotations = { @AnnotationForTest1("super3"), @AnnotationForTest1("super4") })
    private static class SuperForTest implements InterfaceForTest { }

}
