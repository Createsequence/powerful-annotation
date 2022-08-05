package top.xiajibagao.powerfulannotation.helper;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableBy;
import top.xiajibagao.powerfulannotation.synthesis.AliasFor;
import top.xiajibagao.powerfulannotation.synthesis.MirrorFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
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
        Assert.assertEquals(3, attributes.size());
        Assert.assertEquals(ReflectUtils.getDeclaredMethod(AnnotationForTest1.class, "name"), attributes.get(0));
        Assert.assertEquals(ReflectUtils.getDeclaredMethod(AnnotationForTest1.class, "value"), attributes.get(1));
        Assert.assertEquals(ReflectUtils.getDeclaredMethod(AnnotationForTest1.class, "forCover"), attributes.get(2));
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
        List<AnnotationForTest1> annotations = Annotations.getAllIndirectRepeatableAnnotations(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(4, annotations.size());
        Assert.assertEquals(
            CollUtils.newHashSet("class1", "annotation1", "class3", "class4"),
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
        List<AnnotationForTest1> annotations = Annotations.findAllIndirectRepeatableAnnotations(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(9, annotations.size());
        Assert.assertEquals(
            CollUtils.newHashSet("class1", "class3", "class4", "super1", "super3", "super4", "interface1", "interface3", "interface4"),
            CollUtils.toSet(annotations, AnnotationForTest1::value)
        );
    }

    @Test
    public void testIsIndirectAnnotationFind() {
        Assert.assertTrue(Annotations.isIndirectAnnotationFound(ClassForTest.class, AnnotationForTest1.class));
        Assert.assertTrue(Annotations.isIndirectAnnotationFound(ClassForTest.class, AnnotationForTest2.class));
        Assert.assertTrue(Annotations.isIndirectAnnotationFound(ClassForTest.class, AnnotationForTest3.class));
    }

    // =========================== synthesize ===========================

    @Test
    public void testIsSynthesizedAnnotation() {
        AnnotationForTest1 annotation = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        Assert.assertFalse(Annotations.isSynthesizedAnnotation(annotation));
        AnnotationForTest1 synthesized = Annotations.synthesize(annotation, AnnotationForTest1.class, false);
        Assert.assertNotNull(synthesized);
        Assert.assertTrue(Annotations.isSynthesizedAnnotation(synthesized));
    }

    @Test
    public void testGetNotSynthesizedAnnotation() {
        AnnotationForTest1 annotation = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        AnnotationForTest1 synthesized = Annotations.synthesize(annotation, AnnotationForTest1.class, false);
        synthesized = Annotations.synthesize(synthesized, AnnotationForTest1.class, false);
        Assert.assertEquals(annotation, Annotations.getNotSynthesizedAnnotation(synthesized));
    }

    @Test
    public void testSynthesize1() {
        AnnotationForTest1 annotation = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        AnnotationForTest1 synthesized1 = Annotations.synthesize(null, AnnotationForTest1.class, false);
        Assert.assertNull(synthesized1);

        synthesized1 = Annotations.synthesize(annotation, AnnotationForTest1.class, false);
        Assert.assertNotNull(synthesized1);
        Assert.assertNull(Annotations.synthesize(annotation, AnnotationForTest4.class, false));
        Assert.assertEquals("class1", synthesized1.value());
        Assert.assertEquals("class1", synthesized1.name());

        AnnotationForTest4 synthesized2 = Annotations.synthesize(annotation, AnnotationForTest4.class, true);
        Assert.assertNotNull(synthesized2);
        Assert.assertEquals("class1", synthesized2.text());
    }

    @Test
    public void testSynthesize2() {
        AnnotationForTest1 annotation1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        Assert.assertNull(Annotations.synthesize(AnnotationForTest1.class, null));

        AnnotationForTest1 synthesized1 = Annotations.synthesize(AnnotationForTest1.class, annotation1);
        Assert.assertNull(Annotations.synthesize(AnnotationForTest4.class, annotation1));
        Assert.assertNotNull(synthesized1);
        Assert.assertEquals("class1", synthesized1.value());
        Assert.assertEquals("class1", synthesized1.name());

        AnnotationForTest4 annotation2 = AnnotationForTest1.class.getAnnotation(AnnotationForTest4.class);
        AnnotationForTest4 synthesized2 = Annotations.synthesize(AnnotationForTest4.class, annotation1, annotation2);
        Assert.assertNotNull(synthesized2);
        Assert.assertEquals("class1", synthesized2.text());
    }

    @Test
    public void testGetSynthesizedAnnotation() {
        AnnotationForTest1 annotation = Annotations.getSynthesizedAnnotation(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertNotNull(annotation);
        Assert.assertEquals("class1", annotation.value());
        Assert.assertEquals("class1", annotation.name());
    }

    @Test
    public void testGetAllSynthesizedAnnotations() {
        List<AnnotationForTest1> annotations = Annotations.getAllSynthesizedAnnotations(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(2, annotations.size());

        AnnotationForTest1 annotation1 = annotations.get(0);
        Assert.assertNotNull(annotation1);
        Assert.assertEquals("class1", annotation1.value());
        Assert.assertEquals("class1", annotation1.name());

        AnnotationForTest1 annotation2 = annotations.get(1);
        Assert.assertNotNull(annotation2);
        Assert.assertEquals("class2", annotation2.value());
        Assert.assertEquals("class2", annotation2.name());
    }

    @Test
    public void testFindSynthesizedAnnotation() {
        AnnotationForTest1 annotation = Annotations.findSynthesizedAnnotation(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertNotNull(annotation);
        Assert.assertEquals("class1", annotation.value());
        Assert.assertEquals("class1", annotation.name());
    }

    @Test
    public void testFindAllSynthesizedAnnotations() {
        List<AnnotationForTest1> annotations = Annotations.findAllSynthesizedAnnotations(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(6, annotations.size());

        // self

        AnnotationForTest1 annotation1 = annotations.get(0);
        Assert.assertNotNull(annotation1);
        Assert.assertEquals("class1", annotation1.value());
        Assert.assertEquals("class1", annotation1.name());

        AnnotationForTest1 annotation2 = annotations.get(1);
        Assert.assertNotNull(annotation2);
        Assert.assertEquals("class2", annotation2.value());
        Assert.assertEquals("class2", annotation2.name());

        // super

        AnnotationForTest1 annotation3 = annotations.get(2);
        Assert.assertNotNull(annotation3);
        Assert.assertEquals("super1", annotation3.value());
        Assert.assertEquals("super1", annotation3.name());

        AnnotationForTest1 annotation4 = annotations.get(3);
        Assert.assertNotNull(annotation4);
        Assert.assertEquals("super2", annotation4.value());
        Assert.assertEquals("super2", annotation4.name());

        // interface

        AnnotationForTest1 annotation5 = annotations.get(4);
        Assert.assertNotNull(annotation5);
        Assert.assertEquals("interface1", annotation5.value());
        Assert.assertEquals("interface1", annotation5.name());

        AnnotationForTest1 annotation6 = annotations.get(5);
        Assert.assertNotNull(annotation6);
        Assert.assertEquals("interface2", annotation6.value());
        Assert.assertEquals("interface2", annotation6.name());
    }

    @Test
    public void testGetRepeatableFrom() {
        AnnotationForTest1 annotation1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        Assert.assertEquals(
            Collections.singletonList(annotation1), Annotations.getRepeatableFrom(AnnotationForTest1.class, annotation1)
        );

        AnnotationForTest2 annotation2 = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
        List<AnnotationForTest1> annotations = Annotations.getRepeatableFrom(AnnotationForTest1.class, annotation1, annotation2);
        Assert.assertEquals(
            Arrays.asList(annotation1, annotation2.annotations()[0], annotation2.annotations()[1]),
            annotations
        );
    }

    @AnnotationForTest4(text = "covered")
    @RepeatableBy(annotation = AnnotationForTest2.class, attribute = "annotations")
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest1 {
        //@Link(attribute = "name", type = RelationType.MIRROR_FOR)
        @MirrorFor(attribute = "name")
        String value() default "";
        //@Link(type = RelationType.MIRROR_FOR)
        @MirrorFor
        String name() default "";
        //@Link(annotation = AnnotationForTest4.class, attribute = "text", type = RelationType.ALIAS_FOR)
        @AliasFor(annotation = AnnotationForTest4.class, attribute = "text")
        String forCover() default "";
    }

    @AnnotationForTest1("annotation1")
    @AnnotationForTest3("annotation2")
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest2 {
        //@Link(annotation = AnnotationForTest1.class, type = RelationType.ALIAS_FOR)
        @AliasFor(annotation = AnnotationForTest1.class)
        String value() default "";
        AnnotationForTest1[] annotations() default {};
    }

    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest3 {
        String value() default "";
    }

    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest4 {
        String text() default "";
    }

    @AnnotationForTest1(value = "class1", forCover = "class1")
    @AnnotationForTest2(value = "class2", annotations = { @AnnotationForTest1("class3"), @AnnotationForTest1("class4") })
    private static class ClassForTest extends SuperForTest implements InterfaceForTest { }

    @AnnotationForTest1("interface1")
    @AnnotationForTest2(value = "interface2", annotations = { @AnnotationForTest1("interface3"), @AnnotationForTest1("interface4") })
    private interface InterfaceForTest { }

    @AnnotationForTest1("super1")
    @AnnotationForTest2(value = "super2", annotations = { @AnnotationForTest1("super3"), @AnnotationForTest1("super4") })
    private static class SuperForTest implements InterfaceForTest { }

}
