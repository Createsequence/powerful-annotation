package top.xiajibagao.powerfulannotation.helper;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.List;

/**
 * test for {@link Annotations}
 *
 * @author huangchengxing
 */
public class AnnotationsTest {

    // ========================== default ==========================

    @Test
    public void testEmptyAnnotations() {
        Annotation[] annotations = Annotations.emptyArray();
        Assert.assertEquals(0, annotations.length);
        Assert.assertSame(annotations, Annotations.emptyArray());
        Assert.assertArrayEquals(annotations, Annotations.emptyArray());
    }

    @Test
    public void testGetDeclaredAnnotation() {
        AnnotationForTest1 annotation = Annotations.getDeclaredAnnotation(ClassForTest.class, AnnotationForTest1.class);
        Assert.assertEquals(annotation, ClassForTest.class.getAnnotation(AnnotationForTest1.class));
    }

    @Test
    public void testGetDeclaredAnnotations() {
        Annotation[] annotations = Annotations.getDeclaredAnnotations(ClassForTest.class);
        Assert.assertArrayEquals(annotations, ClassForTest.class.getDeclaredAnnotations());
    }

    // ========================== scanner ==========================

    @Test
    public void testFind() {
        AnnotationForTest1 annotation = Annotations.find(
            ClassForTest.class, AnnotationForTest1.class, AnnotationScanner.SELF_AND_DIRECT
        );
        Assert.assertEquals(annotation, ClassForTest.class.getAnnotation(AnnotationForTest1.class));
    }

    @Test
    public void testCollect() {
        List<Annotation> annotations = Annotations.collect(ClassForTest.class, AnnotationScanner.SELF_AND_DIRECT);
        Assert.assertEquals(annotations, Arrays.asList(ClassForTest.class.getDeclaredAnnotations()));
    }

    @Test
    public void testCollectByType() {
        List<AnnotationForTest1> annotations = Annotations.collectByType(
            ClassForTest.class, AnnotationForTest1.class, AnnotationScanner.SELF_AND_DIRECT
        );
        Assert.assertEquals(annotations, Arrays.asList(ClassForTest.class.getAnnotationsByType(AnnotationForTest1.class)));
    }


    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest1 {
    }

    @AnnotationForTest1
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest2 {
    }

    @AnnotationForTest1
    @AnnotationForTest2
    private static class ClassForTest extends SuperForTest implements InterfaceForTest { }

    @AnnotationForTest1
    @AnnotationForTest2
    private interface InterfaceForTest { }

    @AnnotationForTest1
    @AnnotationForTest2
    private static class SuperForTest implements InterfaceForTest { }

}
