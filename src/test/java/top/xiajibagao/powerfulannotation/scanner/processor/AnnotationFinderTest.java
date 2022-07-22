package top.xiajibagao.powerfulannotation.scanner.processor;

import cn.hutool.core.util.ObjectUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huangchengxing
 */
public class AnnotationFinderTest {

    @Test
    public void testProcess() {
        AnnotationFinder finder = new AnnotationFinder(annotation -> ObjectUtil.equals(annotation.annotationType(), AnnotationForTest2.class));

        AnnotationForTest1 annotationForTest1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        finder.process(0, 0, annotationForTest1);
        Assert.assertFalse(finder.isFound());
        Assert.assertFalse(finder.interruptScanning());
        Assert.assertNull(finder.getTarget());

        AnnotationForTest2 annotationForTest2 = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
        finder.process(0, 0, annotationForTest2);
        Assert.assertTrue(finder.isFound());
        Assert.assertTrue(finder.interruptScanning());
        Assert.assertEquals(annotationForTest2, finder.getTarget());

        AnnotationForTest3 annotationForTest3 = ClassForTest.class.getAnnotation(AnnotationForTest3.class);
        finder.process(0, 0, annotationForTest3);
        Assert.assertTrue(finder.isFound());
        Assert.assertTrue(finder.interruptScanning());
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
    private class ClassForTest{}

}
