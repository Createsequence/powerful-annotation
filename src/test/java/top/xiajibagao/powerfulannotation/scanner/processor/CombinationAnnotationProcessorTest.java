package top.xiajibagao.powerfulannotation.scanner.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;

/**
 * test for {@link CombinationAnnotationProcessor}
 *
 * @author huangchengxing
 */
public class CombinationAnnotationProcessorTest {

    @Test
    public void testAccept() {
        GenericAnnotationCollector<Annotation> collector = GenericAnnotationCollector.create();
        GenericAnnotationFinder<Annotation> finder = GenericAnnotationFinder.create(annotation -> ObjectUtil.equals(annotation.annotationType(), AnnotationForTest2.class));
        CombinationAnnotationProcessor processor = new CombinationAnnotationProcessor(collector, finder);

        AnnotationForTest1 annotationForTest1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        processor.accept(0, 0, annotationForTest1);
        Assert.assertEquals(CollUtil.newArrayList(annotationForTest1), collector.getTargets());
        Assert.assertFalse(finder.isFound());
        Assert.assertNull(finder.getTarget());

        AnnotationForTest2 annotationForTest2 = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
        processor.accept(0, 0, annotationForTest2);
        Assert.assertEquals(CollUtil.newArrayList(annotationForTest1, annotationForTest2), collector.getTargets());
        Assert.assertTrue(finder.isFound());
        Assert.assertEquals(annotationForTest2, finder.getTarget());

        AnnotationForTest3 annotationForTest3 = ClassForTest.class.getAnnotation(AnnotationForTest3.class);
        processor.accept(0, 0, annotationForTest3);
        Assert.assertEquals(CollUtil.newArrayList(annotationForTest1, annotationForTest2), collector.getTargets());
        Assert.assertTrue(finder.isFound());
        Assert.assertEquals(annotationForTest2, finder.getTarget());
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
