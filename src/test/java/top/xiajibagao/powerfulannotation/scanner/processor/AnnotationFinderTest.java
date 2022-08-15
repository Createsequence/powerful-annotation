package top.xiajibagao.powerfulannotation.scanner.processor;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;
import java.util.Objects;

/**
 * test for {@link AnnotationFinder}
 *
 * @author huangchengxing
 */
public class AnnotationFinderTest {

    @Test
    public void finderTest() {
        AnnotationFinder finder = new AnnotationFinder(
            annotation -> Objects.equals(annotation.annotationType(), AnnotationForTest2.class)
        );

        AnnotationForTest1 annotationForTest1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        boolean isContinue = finder.accept(0, 0, ClassForTest.class, new Annotation[]{ annotationForTest1 });
        Assert.assertTrue(isContinue);
        Assert.assertFalse(finder.isFound());
        Assert.assertNull(finder.getTarget());

        AnnotationForTest2 annotationForTest2 = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
        isContinue = finder.accept(0, 0, ClassForTest.class, new Annotation[]{ annotationForTest2 });
        Assert.assertFalse(isContinue);
        Assert.assertTrue(finder.isFound());
        Assert.assertEquals(annotationForTest2, finder.getTarget());

        AnnotationForTest3 annotationForTest3 = ClassForTest.class.getAnnotation(AnnotationForTest3.class);
        isContinue = finder.accept(0, 0, ClassForTest.class, new Annotation[]{ annotationForTest3 });
        Assert.assertFalse(isContinue);
        Assert.assertTrue(finder.isFound());
        Assert.assertEquals(annotationForTest2, finder.getTarget());
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
