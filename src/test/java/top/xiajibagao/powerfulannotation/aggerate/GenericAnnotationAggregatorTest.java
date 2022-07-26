package top.xiajibagao.powerfulannotation.aggerate;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.helper.CollUtils;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableBy;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingRegistry;
import top.xiajibagao.powerfulannotation.scanner.AnnotationFilter;
import top.xiajibagao.powerfulannotation.scanner.AnnotationSearchMode;

import java.lang.annotation.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * test for {@link GenericAnnotationAggregator}
 *
 * @author huangchengxing
 */
public class GenericAnnotationAggregatorTest {

    @Test
    public void testBaseInfo() {
        GenericAnnotationAggregator<Class<ClassForTest>> aggregator = new GenericAnnotationAggregator<>(ClassForTest.class, 0, 0);
        Assert.assertEquals(ClassForTest.class, aggregator.getRoot());
        Assert.assertEquals(0, aggregator.getHorizontalIndex());
        Assert.assertEquals(0, aggregator.getVerticalIndex());
    }

    @Test
    public void testAccept() {
        GenericAnnotationAggregator<Class<ClassForTest>> aggregator = new GenericAnnotationAggregator<>(ClassForTest.class, 0, 0);
        AnnotationForTest1 annotation1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        aggregator.accept(0, 1, annotation1);
        AnnotationForTest2 annotation2 = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
        aggregator.accept(0, 2, annotation2);
        AnnotationForTest3 annotation3 = ClassForTest.class.getAnnotation(AnnotationForTest3.class);
        aggregator.accept(0, 3, annotation3);

        Assert.assertEquals(3, aggregator.getAggregatedAnnotationMap().size());
        Assert.assertEquals(1, aggregator.getAggregatedAnnotationMap().get(AnnotationForTest1.class).size());
        Assert.assertEquals(1, aggregator.getAggregatedAnnotationMap().get(AnnotationForTest2.class).size());
        Assert.assertEquals(1, aggregator.getAggregatedAnnotationMap().get(AnnotationForTest3.class).size());
    }

    @Test
    public void testIsPresent() {
        GenericAnnotationAggregator<Class<ClassForTest>> aggregator = new GenericAnnotationAggregator<>(ClassForTest.class, 0, 0);
        AnnotationSearchMode.SELF_AND_DIRECT.scan(ClassForTest.class, aggregator, AnnotationFilter.FILTER_JAVA);
        Assert.assertTrue(aggregator.isPresent(AnnotationForTest1.class));
        Assert.assertFalse(aggregator.isPresent(AnnotationForTest4.class));
    }

    @Test
    public void testGetAnnotationByVerticalIndex() {
        GenericAnnotationAggregator<Class<ClassForTest>> aggregator = new GenericAnnotationAggregator<>(ClassForTest.class, 0, 0);
        AnnotationSearchMode.TYPE_HIERARCHY_AND_INDIRECT.scan(ClassForTest.class, aggregator, AnnotationFilter.FILTER_JAVA);
        Function<Integer, List<Annotation>> getAnnotationFromAggregator = index -> aggregator
            .getAnnotationByVerticalIndex(index).stream()
            .sorted(Comparator.comparing(annotation -> annotation.getAnnotation().annotationType().getSimpleName()))
            .map(HierarchicalAnnotation::getAnnotation)
            .collect(Collectors.toList());

        // 第一层
        AnnotationForTest1 annotation1 = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        AnnotationForTest2 annotation2 = ClassForTest.class.getAnnotation(AnnotationForTest2.class);
        AnnotationForTest3 annotation3 = ClassForTest.class.getAnnotation(AnnotationForTest3.class);
        Assert.assertEquals(CollUtils.newArrayList(annotation1, annotation2, annotation3), getAnnotationFromAggregator.apply(1));

        // 第二层
        AnnotationForTest1 annotation4 = AnnotationForTest2.class.getAnnotation(AnnotationForTest1.class);
        AnnotationForTest2 annotation5 = AnnotationForTest3.class.getAnnotation(AnnotationForTest2.class);
        AnnotationForTest4 annotation6 = AnnotationForTest3.class.getAnnotation(AnnotationForTest4.class);
        Assert.assertEquals(CollUtils.newArrayList(annotation4, annotation5, annotation6), getAnnotationFromAggregator.apply(2));

        // 第三场
        AnnotationForTest1 annotation7 = AnnotationForTest2.class.getAnnotation(AnnotationForTest1.class);
        Assert.assertEquals(CollUtils.newArrayList(annotation7), getAnnotationFromAggregator.apply(3));
    }

    @Test
    public void testGetAnnotationsByType() {
        GenericAnnotationAggregator<Class<ClassForTest>> aggregator = new GenericAnnotationAggregator<>(ClassForTest.class, 0, 0);
        AnnotationSearchMode.TYPE_HIERARCHY_AND_INDIRECT.scan(ClassForTest.class, aggregator, AnnotationFilter.FILTER_JAVA);

        Collection<HierarchicalAnnotation<AnnotationForTest1>> annotation1s = aggregator.getAnnotationsByType(AnnotationForTest1.class);
        Assert.assertEquals(3, annotation1s.size());
        Collection<HierarchicalAnnotation<AnnotationForTest2>> annotation2s = aggregator.getAnnotationsByType(AnnotationForTest2.class);
        Assert.assertEquals(2, annotation2s.size());
        Collection<HierarchicalAnnotation<AnnotationForTest3>> annotation3s = aggregator.getAnnotationsByType(AnnotationForTest3.class);
        Assert.assertEquals(1, annotation3s.size());
        Collection<HierarchicalAnnotation<AnnotationForTest4>> annotation4s = aggregator.getAnnotationsByType(AnnotationForTest4.class);
        Assert.assertEquals(1, annotation4s.size());
    }

    @Test
    public void testGetAllAnnotations() {
        GenericAnnotationAggregator<Class<ClassForTest>> aggregator = new GenericAnnotationAggregator<>(ClassForTest.class, 0, 0);
        AnnotationSearchMode.TYPE_HIERARCHY_AND_INDIRECT.scan(ClassForTest.class, aggregator, AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(7, aggregator.getAllAnnotations().size());
    }

    @Test
    public void testGetAnnotation() {
        GenericAnnotationAggregator<Class<ClassForTest>> aggregator = new GenericAnnotationAggregator<>(ClassForTest.class, 0, 0);
        AnnotationSearchMode.TYPE_HIERARCHY_AND_INDIRECT.scan(ClassForTest.class, aggregator, AnnotationFilter.FILTER_JAVA);

        HierarchicalAnnotation<AnnotationForTest1> annotation = aggregator.getAnnotation(AnnotationForTest1.class, HierarchySelector.nearestAndOldestPriority());
        AnnotationForTest1 original = ClassForTest.class.getAnnotation(AnnotationForTest1.class);
        Assert.assertEquals(original, annotation.getAnnotation());

        annotation = aggregator.getAnnotation(AnnotationForTest1.class, HierarchySelector.farthestAndNewestPriority());
        original = AnnotationForTest2.class.getAnnotation(AnnotationForTest1.class);
        Assert.assertEquals(original, annotation.getAnnotation());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
    private @interface AnnotationForTest1 {}

    @AnnotationForTest1
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
    private @interface AnnotationForTest2 {}

    @AnnotationForTest4
    @AnnotationForTest2
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
    private @interface AnnotationForTest3 {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
    private @interface AnnotationForTest4 {}

    @AnnotationForTest1
    @AnnotationForTest2
    @AnnotationForTest3
    private static class ClassForTest{}



    // ================================= repeatable =================================

    @Test
    public void testGetRepeatableAnnotation() {
        GenericAnnotationAggregator<Class<RepeatableClassForTest>> aggregator = new GenericAnnotationAggregator<>(
            RepeatableClassForTest.class, 0, 0, RepeatableMappingRegistry.create()
        );
        AnnotationSearchMode.TYPE_HIERARCHY_AND_INDIRECT.scan(RepeatableClassForTest.class, aggregator, AnnotationFilter.FILTER_JAVA);
        Assert.assertEquals(2, aggregator.getAllAnnotations().size());

        Collection<RepeatableAnnotationForTest1> annotation1s = aggregator.getRepeatableAnnotations(RepeatableAnnotationForTest1.class);
        Assert.assertEquals(4, annotation1s.size());
        Collection<RepeatableAnnotationForTest2> annotation2s = aggregator.getRepeatableAnnotations(RepeatableAnnotationForTest2.class);
        Assert.assertEquals(2, annotation2s.size());
        Collection<RepeatableAnnotationForTest3> annotation3s = aggregator.getRepeatableAnnotations(RepeatableAnnotationForTest3.class);
        Assert.assertEquals(1, annotation3s.size());
        Collection<RepeatableAnnotationForTest4> annotation4s = aggregator.getRepeatableAnnotations(RepeatableAnnotationForTest4.class);
        Assert.assertEquals(1, annotation4s.size());
    }

    @Repeatable(RepeatableAnnotationForTest2.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface RepeatableAnnotationForTest1 {
        String value() default "";
        String name() default "";
    }

    @RepeatableBy(annotation = RepeatableAnnotationForTest3.class, attribute = "annotations")
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface RepeatableAnnotationForTest2 {
        RepeatableAnnotationForTest1[] value() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface RepeatableAnnotationForTest3 {
        RepeatableAnnotationForTest2[] annotations() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface RepeatableAnnotationForTest4 {
        String value() default "";
    }

    @RepeatableAnnotationForTest4
    @RepeatableAnnotationForTest3(annotations = {
        @RepeatableAnnotationForTest2({
            @RepeatableAnnotationForTest1("1"),
            @RepeatableAnnotationForTest1("2")
        }),
        @RepeatableAnnotationForTest2({
            @RepeatableAnnotationForTest1("3"),
            @RepeatableAnnotationForTest1("4")
        })
    })
    private static class RepeatableClassForTest {}

}
