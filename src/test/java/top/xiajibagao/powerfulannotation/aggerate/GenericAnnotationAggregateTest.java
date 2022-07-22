package top.xiajibagao.powerfulannotation.aggerate;


import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.aggregate.GenericAnnotationAggregate;
import top.xiajibagao.powerfulannotation.helper.FuncUtils;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingParser;
import top.xiajibagao.powerfulannotation.repeatable.SimpleRepeatableMappingRegistry;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class GenericAnnotationAggregateTest {

    @Test
    public void testAnnotationAggregate() {
        GenericAnnotationAggregate<Class<ClassForTest>> annotationAggregate = new GenericAnnotationAggregate<>(
            ClassForTest.class, ClassForTest.class, 0, 0, FuncUtils.alwaysTrue(),
            new SimpleRepeatableMappingRegistry(
                RepeatableMappingParser.STANDARD_REPEATABLE_MAPPING_PARSER, RepeatableMappingParser.REPEATABLE_BY_MAPPING_PARSER
            )
        );
        AnnotationScanner.DIRECTLY_AND_META_ANNOTATION.scan(annotationAggregate, ClassForTest.class, FuncUtils.alwaysTrue());

        Assert.assertTrue(annotationAggregate.isPresent(AnnotationForTest1.class));
        Assert.assertTrue(annotationAggregate.isPresent(AnnotationForTest2.class));
        Assert.assertTrue(annotationAggregate.isPresent(AnnotationForTest3.class));
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
    private class ClassForTest {}

}
