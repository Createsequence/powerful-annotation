package top.xiajibagao.powerfulannotation.synthesis;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.GenericHierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;

import java.lang.annotation.*;
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
            CollUtil.newLinkedHashSet(AnnotationForTest1.class, AnnotationForTest2.class, AnnotationForTest3.class),
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
            CollUtil.newLinkedHashSet(AnnotationForTest1.class, AnnotationForTest2.class, AnnotationForTest3.class),
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
            CollUtil.newArrayList(annotation1, annotation2, annotation3),
            CollStreamUtil.toList(synthesizer.getAllAnnotation(), HierarchicalAnnotation::getAnnotation)
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

}
