package top.xiajibagao.powerfulannotation.scanner.processor;

import cn.hutool.core.collection.CollUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;

/**
 * test for {@link AnnotationCollector}
 *
 * @author huangcehngxing
 */
public class AnnotationCollectorTest {

    @Test
    public void testApply() {
        AnnotationCollector<Annotation> collector = AnnotationCollector.create();

        AnnotationForTest1 annotationForTest1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        collector.accept(0, 0, annotationForTest1);
        AnnotationForTest2 annotationForTest2 = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
        collector.accept(0, 0, annotationForTest2);
        AnnotationForTest3 annotationForTest3 = ClassForTest.class.getAnnotation(AnnotationForTest3.class);
        collector.accept(0, 0, annotationForTest3);

        Assert.assertEquals(
            CollUtil.newArrayList(annotationForTest1, annotationForTest2, annotationForTest3),
            collector.getTargets()
        );
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
    private @interface AnnotationForTest1 {}

    @AnnotationForTest1
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
    private @interface AnnotationForTest2 {}

    @AnnotationForTest2
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
    private @interface AnnotationForTest3 {}

    @AnnotationForTest1
    @AnnotationForTest2
    @AnnotationForTest3
    private static class ClassForTest{}

}
