package top.xiajibagao.powerfulannotation.synthesis;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.GenericHierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.helper.CollUtils;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.synthesis.resolver.AliasAttributeResolver;
import top.xiajibagao.powerfulannotation.synthesis.resolver.CoveredAttributeResolver;
import top.xiajibagao.powerfulannotation.synthesis.resolver.MirrorAttributeResolver;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.Collections;

/**
 * test for {@link GenericAnnotationSynthesizer}
 *
 * @author huangchengxing
 */
public class GenericAnnotationSynthesizerTest {

    @Test
    public void testAccept() {
        GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(
            Collections.emptyList(), HierarchySelector.nearestAndOldestPriority()
        );

        AnnotationForTest1 annotation1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        synthesizer.accept(0, 1, annotation1);
        AnnotationForTest2 annotation2 = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
        synthesizer.accept(0, 2, annotation2);
        AnnotationForTest3 annotation3 = ClassForTest.class.getAnnotation(AnnotationForTest3.class);
        synthesizer.accept(0, 3, annotation3);
        Assert.assertEquals(
            CollUtils.newLinkedHashSet(AnnotationForTest1.class, AnnotationForTest2.class, AnnotationForTest3.class),
            synthesizer.getSynthesizedAnnotationMap().keySet()
        );

        synthesizer.getSynthesizedAnnotationMap().clear();
        HierarchicalAnnotation<Annotation> hierarchicalAnnotation1 = new GenericHierarchicalAnnotation<>(ClassForTest.class.getAnnotation(AnnotationForTest1.class), synthesizer, 0, 1);
        synthesizer.accept(hierarchicalAnnotation1);
        HierarchicalAnnotation<Annotation> hierarchicalAnnotation2 = new GenericHierarchicalAnnotation<>(ClassForTest.class.getAnnotation(AnnotationForTest2.class), synthesizer, 0, 2);
        synthesizer.accept(hierarchicalAnnotation2);
        HierarchicalAnnotation<Annotation> hierarchicalAnnotation3 = new GenericHierarchicalAnnotation<>(ClassForTest.class.getAnnotation(AnnotationForTest3.class), synthesizer, 0, 3);
        synthesizer.accept(hierarchicalAnnotation3);
        Assert.assertEquals(
            CollUtils.newLinkedHashSet(AnnotationForTest1.class, AnnotationForTest2.class, AnnotationForTest3.class),
            synthesizer.getSynthesizedAnnotationMap().keySet()
        );
    }

    @Test
    public void testGetAnnotation() {
        GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(
            Collections.emptyList(), HierarchySelector.nearestAndOldestPriority()
        );
        AnnotationForTest1 annotation1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        synthesizer.accept(0, 1, annotation1);

        Assert.assertNotNull(synthesizer.getAnnotation(AnnotationForTest1.class));
        Assert.assertEquals(annotation1, synthesizer.getAnnotation(AnnotationForTest1.class).getAnnotation());
        Assert.assertNull(synthesizer.getAnnotation(AnnotationForTest2.class));
    }

    @Test
    public void testGetAllAnnotation() {
        GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(
            Collections.emptyList(), HierarchySelector.nearestAndOldestPriority()
        );
        AnnotationForTest1 annotation1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        synthesizer.accept(0, 1, annotation1);
        AnnotationForTest2 annotation2 = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
        synthesizer.accept(0, 2, annotation2);
        AnnotationForTest3 annotation3 = ClassForTest.class.getAnnotation(AnnotationForTest3.class);
        synthesizer.accept(0, 3, annotation3);

        Assert.assertEquals(3, synthesizer.getAllAnnotation().size());
        Assert.assertEquals(
            CollUtils.newArrayList(annotation1, annotation2, annotation3),
            CollUtils.toList(synthesizer.getAllAnnotation(), HierarchicalAnnotation::getAnnotation)
        );
    }

    @Test
    public void testSupport() {
        GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(
            Collections.emptyList(), HierarchySelector.nearestAndOldestPriority()
        );
        AnnotationForTest1 annotation1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        synthesizer.accept(0, 1, annotation1);

        Assert.assertTrue(synthesizer.support(AnnotationForTest1.class));
        Assert.assertFalse(synthesizer.support(AnnotationForTest2.class));
    }

    @Test
    public void testSynthesize() {
        GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(
            Collections.emptyList(), HierarchySelector.nearestAndOldestPriority()
        );
        AnnotationForTest1 annotation1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        synthesizer.accept(0, 1, annotation1);
        
        AnnotationForTest1 synthesizeAnnotation1 = synthesizer.synthesize(AnnotationForTest1.class);
        Assert.assertEquals("value1", synthesizeAnnotation1.value());
        Assert.assertNull(synthesizer.synthesize(AnnotationForTest2.class));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface AnnotationForTest1 {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface AnnotationForTest2 {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface AnnotationForTest3 {
        String value() default "";
    }

    @AnnotationForTest1("value1")
    @AnnotationForTest2("value2")
    @AnnotationForTest3("value3")
    private static class ClassForTest {}

    @Test
    public void testSynthesizeMirrorForAttribute() {
        GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(
            Collections.singletonList(new MirrorAttributeResolver()),
            HierarchySelector.nearestAndOldestPriority()
        );

        Annotation annotation = ClassForTest2.class.getAnnotation(AnnotationForTest4.class);
        synthesizer.accept(1, 1, annotation);
        
        AnnotationForTest4 synthesize = synthesizer.synthesize(AnnotationForTest4.class);
        Assert.assertEquals("value1", synthesize.name());
        Assert.assertEquals("value1", synthesize.value());
    }

    @Test
    public void testSynthesizeAliasForAttribute() {
        GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(
            Collections.singletonList(new AliasAttributeResolver()),
            HierarchySelector.farthestAndNewestPriority()
        );

        Annotation annotation = ClassForTest2.class.getAnnotation(AnnotationForTest5.class);
        synthesizer.accept(0, 0, annotation);
        
        AnnotationForTest5 synthesize = synthesizer.synthesize(AnnotationForTest5.class);
        Assert.assertEquals("name", synthesize.name());
        Assert.assertEquals("default", synthesize.value());

        annotation = ClassForTest3.class.getAnnotation(AnnotationForTest5.class);
        synthesizer.accept(0, 0, annotation);
        
        synthesize = synthesizer.synthesize(AnnotationForTest5.class);
        Assert.assertEquals("value", synthesize.name());
        Assert.assertEquals("value", synthesize.value());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface AnnotationForTest4 {
        @Link(attribute = "name", type = RelationType.MIRROR_FOR)
        String value() default "";
        @Link(type = RelationType.MIRROR_FOR)
        String name() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface AnnotationForTest5 {
        @Link(attribute = "name", type = RelationType.ALIAS_FOR)
        String value() default "default";
        String name() default "";
    }

    @AnnotationForTest4("value1")
    @AnnotationForTest5(name = "name")
    private static class ClassForTest2 {}

    @AnnotationForTest5(value = "value")
    public static class ClassForTest3 {}

    @Test
    public void testHierarchySynthesize() {
        GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(
            Arrays.asList(new MirrorAttributeResolver(), new AliasAttributeResolver()),
            HierarchySelector.farthestAndNewestPriority()
        );

        synthesizer.accept(0, 2, AnnotationForTest7.class.getAnnotation(AnnotationForTest6.class));
        synthesizer.accept(0, 1, ClassForTest4.class.getAnnotation(AnnotationForTest7.class));
        Assert.assertTrue(synthesizer.support(AnnotationForTest7.class));
        Assert.assertTrue(synthesizer.support(AnnotationForTest6.class));
        
        AnnotationForTest6 annotationForTest6 = synthesizer.synthesize(AnnotationForTest6.class);
        Assert.assertEquals("default_name", annotationForTest6.name());
        Assert.assertEquals("default_name", annotationForTest6.value());
        AnnotationForTest7 annotationForTest7 = synthesizer.synthesize(AnnotationForTest7.class);
        Assert.assertEquals("default_name", annotationForTest7.alias());

        synthesizer.getSynthesizedAnnotationMap().clear();
        synthesizer.accept(0, 2, AnnotationForTest7.class.getAnnotation(AnnotationForTest6.class));
        synthesizer.accept(0, 1, ClassForTest5.class.getAnnotation(AnnotationForTest7.class));

        annotationForTest6 = synthesizer.synthesize(AnnotationForTest6.class);
        Assert.assertEquals("alias_name", annotationForTest6.name());
        Assert.assertEquals("alias_name", annotationForTest6.value());
        annotationForTest7 = synthesizer.synthesize(AnnotationForTest7.class);
        Assert.assertEquals("alias_name", annotationForTest7.alias());
    }

    @Test
    public void testHierarchyConvertedAttributeSynthesize() {
        GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(
            Arrays.asList(
                new MirrorAttributeResolver(),
                new AliasAttributeResolver(),
                new CoveredAttributeResolver(true)
            ),
            HierarchySelector.farthestAndNewestPriority()
        );

        synthesizer.accept(1, 1, AnnotationForTest7.class.getAnnotation(AnnotationForTest6.class));
        synthesizer.accept(0, 1, ClassForTest4.class.getAnnotation(AnnotationForTest7.class));
        Assert.assertTrue(synthesizer.support(AnnotationForTest7.class));
        Assert.assertTrue(synthesizer.support(AnnotationForTest6.class));
        
        AnnotationForTest6 annotationForTest6 = synthesizer.synthesize(AnnotationForTest6.class);
        Assert.assertEquals("converted_name", annotationForTest6.name());
        Assert.assertEquals("default_name", annotationForTest6.value());
        AnnotationForTest7 annotationForTest7 = synthesizer.synthesize(AnnotationForTest7.class);
        Assert.assertEquals("default_name", annotationForTest7.alias());

        synthesizer.getSynthesizedAnnotationMap().clear();
        synthesizer.accept(1, 1, AnnotationForTest7.class.getAnnotation(AnnotationForTest6.class));
        synthesizer.accept(0, 1, ClassForTest5.class.getAnnotation(AnnotationForTest7.class));

        annotationForTest6 = synthesizer.synthesize(AnnotationForTest6.class);
        Assert.assertEquals("converted_name", annotationForTest6.name());
        Assert.assertEquals("alias_name", annotationForTest6.value());
        annotationForTest7 = synthesizer.synthesize(AnnotationForTest7.class);
        Assert.assertEquals("alias_name", annotationForTest7.alias());
        Assert.assertEquals("converted_name", annotationForTest7.name());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface AnnotationForTest6 {
        @Link(attribute = "name", type = RelationType.MIRROR_FOR)
        String value() default "";
        @Link(type = RelationType.MIRROR_FOR)
        String name() default "";
    }

    @AnnotationForTest6("default_name")
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface AnnotationForTest7 {
        @Link(annotation = AnnotationForTest6.class, type = RelationType.ALIAS_FOR)
        String alias() default "default_name";
        String name() default "converted_name";
    }

    @AnnotationForTest7
    public static class ClassForTest4 {}

    @AnnotationForTest7(alias = "alias_name")
    public static class ClassForTest5 {}

    @Test
    public void testForHierarchy() {
        GenericAnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(
            Arrays.asList(
                new MirrorAttributeResolver(),
                new AliasAttributeResolver(),
                new CoveredAttributeResolver(true)
            ),
            HierarchySelector.farthestAndNewestPriority()
        );
        synthesizer.accept(0, 1, ClassForHierarchy.class.getAnnotation(AnnotationForHierarchy3.class));
        synthesizer.accept(1, 1, AnnotationForHierarchy3.class.getAnnotation(AnnotationForHierarchy2.class));
        synthesizer.accept(2, 1, AnnotationForHierarchy2.class.getAnnotation(AnnotationForHierarchy1.class));

        AnnotationForHierarchy3 annotation3 = synthesizer.synthesize(AnnotationForHierarchy3.class);
        Assert.assertEquals("foo", annotation3.value3());
        AnnotationForHierarchy2 annotation2 = synthesizer.synthesize(AnnotationForHierarchy2.class);
        Assert.assertEquals("foo", annotation2.value2());
        Assert.assertEquals("foo", annotation2.name2());
        AnnotationForHierarchy1 annotation1 = synthesizer.synthesize(AnnotationForHierarchy1.class);
        Assert.assertEquals("foo", annotation1.name1());
        Assert.assertEquals("foo", annotation1.value1());
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface AnnotationForHierarchy1 {
        @MirrorFor(attribute = "name1")
        String value1() default "";
        @MirrorFor(attribute = "value1")
        String name1() default "";
    }

    @AnnotationForHierarchy1
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface AnnotationForHierarchy2 {
        @AliasFor(attribute = "name2")
        String value2() default "";
        @AliasFor(annotation = AnnotationForHierarchy1.class, attribute = "value1")
        String name2() default "";
    }

    @AnnotationForHierarchy2
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface AnnotationForHierarchy3 {
        @ForceAliasFor(annotation = AnnotationForHierarchy2.class, attribute = "value2")
        String value3() default "";
    }

    @AnnotationForHierarchy3(value3 = "foo")
    private static class ClassForHierarchy {}

}
