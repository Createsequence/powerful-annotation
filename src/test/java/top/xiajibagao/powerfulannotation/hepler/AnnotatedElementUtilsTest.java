package top.xiajibagao.powerfulannotation.hepler;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.helper.AnnotatedElementUtils;

import java.lang.annotation.*;
import java.util.List;

/**
 * @author huangchengxing
 */
public class AnnotatedElementUtilsTest {

    @Test
    public void testScanDirectly() {
        List<Annotation> annotationList = AnnotatedElementUtils.scanDirectly(Class1.class);
        Assert.assertEquals(2, annotationList.size());
    }

    @Test
    public void testScanDirectlyAndMetaAnnotation() {
        List<Annotation> annotationList = AnnotatedElementUtils.scanDirectlyAndMetaAnnotation(Class1.class);
        Assert.assertEquals(3, annotationList.size());
    }

    @Test
    public void testScanSuperclass() {
        List<Annotation> annotationList = AnnotatedElementUtils.scanSuperclass(Class1.class);
        Assert.assertEquals(4, annotationList.size());
    }

    @Test
    public void testScanInterface() {
        List<Annotation> annotationList = AnnotatedElementUtils.scanInterface(Class1.class);
        Assert.assertEquals(3, annotationList.size());
    }

    @Test
    public void testScanInterfaceAndMetaAnnotation() {
        List<Annotation> annotationList = AnnotatedElementUtils.scanInterfaceAndMetaAnnotation(Class1.class);
        Assert.assertEquals(5, annotationList.size());
    }

    @Test
    public void testScanTypeHierarchy() {
        List<Annotation> annotationList = AnnotatedElementUtils.scanTypeHierarchy(Class1.class);
        Assert.assertEquals(5, annotationList.size());
    }

    @Test
    public void testScanTypeHierarchyAndMetaAnnotation() {
        List<Annotation> annotationList = AnnotatedElementUtils.scanTypeHierarchyAndMetaAnnotation(Class1.class);
        Assert.assertEquals(8, annotationList.size());
    }

    @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation2 {
        String value() default "";
    }

    @Annotation2("0")
    @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation1 {
        String value() default "";
    }

    @Annotation2("1")
    @Annotation1("2")
    public static class Class1 extends SupperClass2 implements Interface3 {}

    @Annotation2("3")
    @Annotation1("4")
    public static class SupperClass2 {}

    @Annotation1("5")
    public interface Interface3 {}

}
