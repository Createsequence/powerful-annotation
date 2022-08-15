package top.xiajibagao.powerfulannotation.scanner.processor;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;
import java.util.Arrays;

/**
 * test for {@link AnnotationCollector}
 *
 * @author huangchengxing
 */
public class AnnotationCollectorTest {

    @Test
    public void processTest() {
        AnnotationCollector collector = new AnnotationCollector();
        AnnotationForTest1 annotationForTest1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        AnnotationForTest2 annotationForTest2 = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
        AnnotationForTest3 annotationForTest3 = ClassForTest.class.getAnnotation(AnnotationForTest3.class);
        Annotation[] annotations = new Annotation[]{ annotationForTest1, annotationForTest2, annotationForTest3 };
        collector.accept(0, 0, ClassForTest.class, annotations);
        Assert.assertEquals(Arrays.asList(annotations), collector.getTargets());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    private @interface AnnotationForTest1 {}

    @AnnotationForTest1
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    private @interface AnnotationForTest2 {}

    @AnnotationForTest2
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    private @interface AnnotationForTest3 {}

    @AnnotationForTest1
    @AnnotationForTest2
    @AnnotationForTest3
    private static class ClassForTest{}

}
