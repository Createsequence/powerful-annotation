package top.xiajibagao.powerfulannotation.helper;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * test for {@link AnnotationUtils}
 *
 * @author huangchengxing
 */
public class AnnotationUtilsTest {

    @Test
    public void testGetDeclaredAnnotations() {
        Assert.assertArrayEquals(
            ClassForTest.class.getDeclaredAnnotations(),
            AnnotationUtils.getDeclaredAnnotations(ClassForTest.class)
        );
    }

    @Test
    public void testGetDeclaredAnnotation() {
        Assert.assertEquals(
            ClassForTest.class.getDeclaredAnnotation(AnnotationForTest.class),
            AnnotationUtils.getDeclaredAnnotation(ClassForTest.class, AnnotationForTest.class)
        );
    }

    @Test
    public void testEmptyAnnotations() {
        Assert.assertEquals(0, AnnotationUtils.emptyAnnotations().length);
    }

    @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface MetaAnnotationForTest { }

    @MetaAnnotationForTest
    @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest { }

    @AnnotationForTest
    private static class ClassForTest extends SupperForTest implements InterfaceForTest { }

    @AnnotationForTest
    private static class SupperForTest { }

    @AnnotationForTest
    private interface InterfaceForTest { }

}