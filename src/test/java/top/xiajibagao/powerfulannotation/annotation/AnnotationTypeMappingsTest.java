package top.xiajibagao.powerfulannotation.annotation;

import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingRegistry;
import top.xiajibagao.powerfulannotation.scanner.AnnotationFilter;

import java.lang.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * test for {@link AnnotationTypeMappings} and {@link AnnotationTypeMapping}
 *
 * @author huangchengxing
 */
public class AnnotationTypeMappingsTest {

    @Test
    public void testFromAnnotationArray() {
        Annotation1 annotation1 = Annotation3.class.getAnnotation(Annotation1.class);
        Annotation2 annotation2 = Annotation3.class.getAnnotation(Annotation2.class);
        Annotation3 annotation3 = Annotation4.class.getAnnotation(Annotation3.class);
        AnnotationTypeMappings mappings = AnnotationTypeMappings.from(annotation3, annotation2, annotation1);
        Assert.assertEquals(3, mappings.getAnnotations().size());
        Assert.assertEquals(3, mappings.getAnnotations().size());
    }

    @Test
    public void testMappingRelationship() {
        AnnotationTypeMappings mappings = AnnotationTypeMappings.from(Annotation5.class, AnnotationFilter.FILTER_JAVA);
        Optional<AnnotationTypeMapping> optional1 = mappings.getAnnotation(Annotation1.class, HierarchySelector.nearestAndOldestPriority(), AnnotationFilter.FILTER_NOTHING);
        Assert.assertTrue(optional1.isPresent());
        AnnotationTypeMapping mapping1 = optional1.get();
        Optional<AnnotationTypeMapping> optional2 = mappings.getAnnotation(Annotation2.class, HierarchySelector.nearestAndOldestPriority(), AnnotationFilter.FILTER_NOTHING);
        Assert.assertTrue(optional2.isPresent());
        AnnotationTypeMapping mapping2 = optional2.get();
        Optional<AnnotationTypeMapping> optional3 = mappings.getAnnotation(Annotation3.class, HierarchySelector.nearestAndOldestPriority(), AnnotationFilter.FILTER_NOTHING);
        Assert.assertTrue(optional3.isPresent());
        AnnotationTypeMapping mapping3 = optional3.get();
        Optional<AnnotationTypeMapping> optional4 = mappings.getAnnotation(Annotation4.class, HierarchySelector.nearestAndOldestPriority(), AnnotationFilter.FILTER_NOTHING);
        Assert.assertTrue(optional4.isPresent());
        AnnotationTypeMapping mapping4 = optional4.get();

        // 1 -> 3
        Assert.assertEquals(mapping4, mapping1.getRoot());
        Assert.assertEquals(mapping3, mapping1.getSource());
        Assert.assertFalse(mapping1.isRoot());

        // 2 -> 3
        Assert.assertEquals(mapping4, mapping2.getRoot());
        Assert.assertEquals(mapping3, mapping2.getSource());
        Assert.assertFalse(mapping2.isRoot());

        // 3 -> 4
        Assert.assertEquals(mapping4, mapping3.getRoot());
        Assert.assertEquals(mapping4, mapping3.getSource());
        Assert.assertFalse(mapping3.isRoot());

        // 4 -> 4
        Assert.assertEquals(mapping4, mapping4.getRoot());
        Assert.assertNull(mapping4.getSource());
        Assert.assertTrue(mapping4.isRoot());
    }

    @Test
    public void testGetAnnotation() {
        AnnotationTypeMappings mappings = AnnotationTypeMappings.from(Annotation4.class, AnnotationFilter.FILTER_JAVA);

        Annotation1 annotation1 = Annotation3.class.getAnnotation(Annotation1.class);
        Optional<Annotation> mapping = mappings.getAnnotation(Annotation1.class, HierarchySelector.nearestAndOldestPriority(), AnnotationFilter.FILTER_NOTHING)
            .map(HierarchicalAnnotation::getAnnotation);
        Assert.assertTrue(mapping.isPresent());
        Assert.assertEquals(annotation1, mapping.get());

        Annotation2 annotation2 = Annotation3.class.getAnnotation(Annotation2.class);
        mapping = mappings.getAnnotation(Annotation2.class, HierarchySelector.nearestAndOldestPriority(), AnnotationFilter.FILTER_NOTHING)
            .map(HierarchicalAnnotation::getAnnotation);
        Assert.assertTrue(mapping.isPresent());
        Assert.assertEquals(annotation2, mapping.get());

        Annotation3 annotation3 = Annotation4.class.getAnnotation(Annotation3.class);
        mapping = mappings
            .getAnnotation(Annotation3.class, HierarchySelector.nearestAndOldestPriority(), AnnotationFilter.FILTER_NOTHING)
            .map(HierarchicalAnnotation::getAnnotation);
        Assert.assertTrue(mapping.isPresent());
        Assert.assertEquals(annotation3, mapping.get());
    }

    @Test
    public void testGetAnnotations() {
        AnnotationTypeMappings mappings = AnnotationTypeMappings.from(Annotation4.class, AnnotationFilter.FILTER_JAVA);
        List<AnnotationTypeMapping> annotationTypeMappings = mappings.getAnnotations();
        Assert.assertEquals(3, annotationTypeMappings.size());

        Annotation1 annotation1 = Annotation3.class.getAnnotation(Annotation1.class);
        Assert.assertEquals(annotation1, annotationTypeMappings.get(1).getAnnotation());

        Annotation2 annotation2 = Annotation3.class.getAnnotation(Annotation2.class);
        Assert.assertEquals(annotation2, annotationTypeMappings.get(2).getAnnotation());

        Annotation3 annotation3 = Annotation4.class.getAnnotation(Annotation3.class);
        Assert.assertEquals(annotation3, annotationTypeMappings.get(0).getAnnotation());
    }

    @Test
    public void testGetRepeatableAnnotations() {
        AnnotationTypeMappings mappings = AnnotationTypeMappings.from(Annotation4.class, AnnotationFilter.FILTER_JAVA);
        Annotation1 annotation = Annotation3.class.getAnnotation(Annotation1.class);
        List<Annotation1> repeatables =  mappings.getRepeatableAnnotations(Annotation1.class, AnnotationFilter.FILTER_NOTHING, RepeatableMappingRegistry.none());
        Assert.assertEquals(1, repeatables.size());
        Assert.assertEquals(annotation, repeatables.get(0));
    }

    @Test
    public void testIsPresent() {
        AnnotationTypeMappings mappings = AnnotationTypeMappings.from(Annotation3.class, AnnotationFilter.FILTER_JAVA);
        Assert.assertTrue(mappings.isPresent(Annotation2.class));
        Assert.assertTrue(mappings.isPresent(Annotation1.class));
        Assert.assertFalse(mappings.isPresent(Annotation4.class));
    }

    @Test
    public void testIsEmpty() {
        AnnotationTypeMappings mappings = AnnotationTypeMappings.from(Annotation1.class, AnnotationFilter.FILTER_JAVA);
        Assert.assertTrue(mappings.isEmpty());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Annotation1 {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Annotation2 {}

    @Annotation1
    @Annotation2
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Annotation3 {}

    @Annotation3
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Annotation4 {}

    @Annotation4
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Annotation5 {}

}
