package top.xiajibagao.powerfulannotation.scanner;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.helper.CollUtils;
import top.xiajibagao.powerfulannotation.helper.StrUtils;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * test for {@link GenericAnnotationScanner}
 *
 * @author huangchengxing
 */
public class GenericAnnotationScannerTest {

    @Test
    public void copyOptionsTest() {
        ScanOptions source = new ScanOptions();
        AbstractAnnotationScanner scanner = new GenericAnnotationScanner(source);
        ScanOptions copy = scanner.copyOptions();
        Assert.assertNotSame(source, copy);
        Assert.assertEquals(source.isEnableScanMetaAnnotation(), copy.isEnableScanMetaAnnotation());
        Assert.assertEquals(source.isEnableScanInterface(), copy.isEnableScanInterface());
        Assert.assertEquals(source.isEnableScanSuperClass(), copy.isEnableScanSuperClass());

        copy.lockOptions();
        Assert.assertThrows(IllegalArgumentException.class, () -> copy.setEnableScanMetaAnnotation(true));
    }

    @SneakyThrows
    @Test
    public void nonHierarchyTest() {
        Method method = ClassForNonHierarchy.class.getDeclaredMethod("method", Integer.class);
        AnnotatedType type = method.getAnnotatedReturnType();
        Assert.assertNotNull(type);
        AbstractAnnotationScanner scanner = new GenericAnnotationScanner(new ScanOptions());
        List<Annotation> annotations = new ArrayList<>();
        scanner.scan(type, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(1, annotations.size());
        Assert.assertEquals(type.getAnnotation(AnnotationForTest1.class), annotations.get(0));

        scanner.scan(null, null, null);
    }

    @SneakyThrows
    @Test
    public void selfAndDirectTest() {
        AbstractAnnotationScanner scanner = new GenericAnnotationScanner(
            new ScanOptions()
                .setEnableScanInterface(false)
                .setEnableScanSuperClass(false)
                .setEnableScanMetaAnnotation(false)
                .setTypeFilter(t -> StrUtils.isNotStartWithAny(t.getName(), "java."))
        );

        // class
        List<Annotation> annotations = new ArrayList<>();
        scanner.scan(ClassForTest.class, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(1, annotations.size());

        // method
        annotations.clear();
        Method method = ClassForTest.class.getDeclaredMethod("method");
        scanner.scan(method, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(1, annotations.size());

        // field
        annotations.clear();
        Field field = ClassForTest.class.getDeclaredField("filed");
        scanner.scan(field, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(1, annotations.size());

    }

    @SneakyThrows
    @Test
    public void selfAndIndirectTest() {
        AbstractAnnotationScanner scanner = new GenericAnnotationScanner(
            new ScanOptions(false, false, true)
        );

        // class
        List<Annotation> annotations = new ArrayList<>();
        scanner.scan(ClassForTest.class, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(2, annotations.size());

        // method
        annotations.clear();
        Method method = ClassForTest.class.getDeclaredMethod("method");
        scanner.scan(method, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(2, annotations.size());

        // field
        annotations.clear();
        Field field = ClassForTest.class.getDeclaredField("filed");
        scanner.scan(field, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(2, annotations.size());

    }

    @SneakyThrows
    @Test
    public void superClassAndDirectTest() {
        AbstractAnnotationScanner scanner = new GenericAnnotationScanner(
            new ScanOptions(true, false, false)
        );

        // class
        List<Annotation> annotations = new ArrayList<>();
        scanner.scan(ClassForTest.class, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(2, annotations.size());

        // method
        annotations.clear();
        Method method = ClassForTest.class.getDeclaredMethod("method");
        scanner.scan(method, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(2, annotations.size());

        // field
        annotations.clear();
        Field field = ClassForTest.class.getDeclaredField("filed");
        scanner.scan(field, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(1, annotations.size());

    }

    @SneakyThrows
    @Test
    public void superClassAndIndirectTest() {
        AbstractAnnotationScanner scanner = new GenericAnnotationScanner(
            new ScanOptions(true, false, true)
        );

        // class
        List<Annotation> annotations = new ArrayList<>();
        scanner.scan(ClassForTest.class, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(3, annotations.size());

        // method
        annotations.clear();
        Method method = ClassForTest.class.getDeclaredMethod("method");
        scanner.scan(method, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(3, annotations.size());

        // field
        annotations.clear();
        Field field = ClassForTest.class.getDeclaredField("filed");
        scanner.scan(field, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(2, annotations.size());
    }

    @SneakyThrows
    @Test
    public void interfaceAndDirectTest() {
        AbstractAnnotationScanner scanner = new GenericAnnotationScanner(
            new ScanOptions(false, true, false)
        );

        // class
        List<Annotation> annotations = new ArrayList<>();
        scanner.scan(ClassForTest.class, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(2, annotations.size());

        // method
        annotations.clear();
        Method method = ClassForTest.class.getDeclaredMethod("method");
        scanner.scan(method, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(2, annotations.size());

        // field
        annotations.clear();
        Field field = ClassForTest.class.getDeclaredField("filed");
        scanner.scan(field, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(1, annotations.size());

    }

    @SneakyThrows
    @Test
    public void interfaceAndIndirectTest() {
        AbstractAnnotationScanner scanner = new GenericAnnotationScanner(
            new ScanOptions(false, true, true)
        );

        // class
        List<Annotation> annotations = new ArrayList<>();
        scanner.scan(ClassForTest.class, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(3, annotations.size());

        // method
        annotations.clear();
        Method method = ClassForTest.class.getDeclaredMethod("method");
        scanner.scan(method, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(3, annotations.size());

        // field
        annotations.clear();
        Field field = ClassForTest.class.getDeclaredField("filed");
        scanner.scan(field, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(2, annotations.size());

    }

    @SneakyThrows
    @Test
    public void typeHierarchyAndDirectTest() {
        AbstractAnnotationScanner scanner = new GenericAnnotationScanner(
            new ScanOptions(true, true, false)
        );

        // class
        List<Annotation> annotations = new ArrayList<>();
        scanner.scan(ClassForTest.class, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(3, annotations.size());

        // method
        annotations.clear();
        Method method = ClassForTest.class.getDeclaredMethod("method");
        scanner.scan(method, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(3, annotations.size());

        // field
        annotations.clear();
        Field field = ClassForTest.class.getDeclaredField("filed");
        scanner.scan(field, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(1, annotations.size());

    }

    @SneakyThrows
    @Test
    public void typeHierarchyAndIndirectTest() {
        AbstractAnnotationScanner scanner = new GenericAnnotationScanner(
            new ScanOptions(true, true, true)
        );

        // class
        List<Annotation> annotations = new ArrayList<>();
        scanner.scan(ClassForTest.class, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(4, annotations.size());

        // method
        annotations.clear();
        Method method = ClassForTest.class.getDeclaredMethod("method");
        scanner.scan(method, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(4, annotations.size());

        // field
        annotations.clear();
        Field field = ClassForTest.class.getDeclaredField("filed");
        scanner.scan(field, new Processor(as -> CollUtils.addAll(annotations, as)), AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(2, annotations.size());

    }

    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest1 {
        String value() default "";
    }

    @AnnotationForTest1("annotation")
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationForTest2 {
        String value() default "";
    }

    @AnnotationForTest2("class")
    private static class ClassForTest extends SuperForTest implements InterfaceForTest {

        @AnnotationForTest2("classField")
        private String filed;

        @AnnotationForTest2("classMethod")
        @Override
        public void method() {}

    }

    @AnnotationForTest2("interface")
    private interface InterfaceForTest {

        @AnnotationForTest2("interfaceMethod")
        void method();

        @AnnotationForTest2("interfaceMethod2")
        default void method2() { }

    }

    @AnnotationForTest2("super")
    private static class SuperForTest implements InterfaceForTest {

        @AnnotationForTest2("superField")
        private String filed;

        @AnnotationForTest2("superMethod")
        @Override
        public void method() {}

        @AnnotationForTest1("superMethod2")
        public void method(Object o) {};

        @AnnotationForTest1("superMethod2")
        public Object method(Object o1, Object o2) {return null;};

    }

    private static class ClassForNonHierarchy {
        public @AnnotationForTest1("result") SuperForTest method(Integer param) {
            return null;
        }
    }

    @RequiredArgsConstructor
    private static class Processor implements AnnotationProcessor {
        private final Consumer<Annotation[]> consumer;
        @Override
        public boolean accept(int verticalIndex, int horizontalIndex, AnnotatedElement source, Annotation[] annotations) {
            consumer.accept(annotations);
            return true;
        }
    }

}
