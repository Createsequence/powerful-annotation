package top.xiajibagao.powerfulannotation.synthesis.resolver;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.GenericHierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.synthesis.AnnotationSynthesizer;
import top.xiajibagao.powerfulannotation.synthesis.GenericAnnotationSynthesizer;

import java.lang.annotation.*;
import java.util.Collections;
import java.util.Comparator;

/**
 * test for {@link CoveredAttributeResolver}
 *
 * @author huangchengxing
 */
public class CoveredAttributeResolverTest {

    @Test
    public void testResolve() {
        CoveredAttributeResolver processor = new CoveredAttributeResolver(
            Comparator.comparing(HierarchicalAnnotation<Annotation>::getVerticalIndex)
                .thenComparing(HierarchicalAnnotation<Annotation>::getHorizontalIndex),
            true
        );

        AnnotationSynthesizer synthesizer = new GenericAnnotationSynthesizer(Collections.singletonList(processor), HierarchySelector.nearestAndOldestPriority());
        HierarchicalAnnotation<Annotation> annotation1 = new GenericHierarchicalAnnotation<>(
            ClassForTest.class.getAnnotation(AnnotationForTest1.class), ClassForTest.class, 0, 1
        );
        synthesizer.accept(annotation1);
        HierarchicalAnnotation<Annotation> annotation2 = new GenericHierarchicalAnnotation<>(
            ClassForTest.class.getAnnotation(AnnotationForTest2.class), ClassForTest.class, 0, 2
        );
        synthesizer.accept(annotation2);
        HierarchicalAnnotation<Annotation> annotation3 = new GenericHierarchicalAnnotation<>(
            ClassForTest.class.getAnnotation(AnnotationForTest3.class), ClassForTest.class, 0, 3
        );
        synthesizer.accept(annotation3);

        Assert.assertEquals("value1", annotation1.getAttribute("value").getValue());
        Assert.assertEquals("value1", annotation2.getAttribute("value").getValue());
        Assert.assertEquals(3, annotation3.getAttribute("value").getValue());
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
        int value() default 0;
    }

    @AnnotationForTest1("value1")
    @AnnotationForTest2("value2")
    @AnnotationForTest3(3)
    private static class ClassForTest {}

}
