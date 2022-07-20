package top.xiajibagao.powerfulannotation.synthesis;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.annotation.RepeatableBy;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huangchengxing
 */
public class RepeatableSynthesizedAggregateAnnotationTest {

    @Test
    public void testGetRepeatableAnnotations() {
        RepeatableContainerAnnotation repeatableContainerAnnotation = new RepeatableSynthesizedAggregateAnnotation(
            Arrays.asList(Class1.class.getAnnotations()), AnnotationScanner.DIRECTLY_AND_META_ANNOTATION
        );
        List<Annotation1> annotation1s = repeatableContainerAnnotation.getRepeatableAnnotations(Annotation1.class);
        Assert.assertEquals(7, annotation1s.size());
        Assert.assertEquals(
            CollUtil.newArrayList("1", "2", "3", "4", "5", "6", "7"),
            CollStreamUtil.toList(annotation1s, Annotation1::value)
        );

        List<Annotation2> annotation2s = repeatableContainerAnnotation.getRepeatableAnnotations(Annotation2.class);
        Assert.assertEquals(3, annotation2s.size());
        Assert.assertEquals(
            CollUtil.newArrayList("2", "3", "4", "5", "6", "7"),
            annotation2s.stream()
                .map(Annotation2::value)
                .flatMap(Stream::of)
                .map(Annotation1::value)
                .collect(Collectors.toList())
        );

        List<Annotation3> annotation3s = repeatableContainerAnnotation.getRepeatableAnnotations(Annotation3.class);
        Assert.assertEquals(1, annotation3s.size());
        Assert.assertEquals(
            CollUtil.newArrayList("4", "5", "6", "7"),
            annotation3s.stream()
                .map(Annotation3::annotations)
                .flatMap(Stream::of)
                .map(Annotation2::value)
                .flatMap(Stream::of)
                .map(Annotation1::value)
                .collect(Collectors.toList())
        );
    }

    @Repeatable(Annotation2.class)
    @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation1 {
        String value() default "";
    }

    @RepeatableBy(annotation = Annotation3.class, attribute = "annotations")
    @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation2 {
        Annotation1[] value() default {};
    }

    @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation3 {
        Annotation2[] annotations() default {};
    }

    @Annotation1("1")
    @Annotation2({
        @Annotation1("2"),
        @Annotation1("3")
    })
    @Annotation3(annotations = {
        @Annotation2({
            @Annotation1("4"),
            @Annotation1("5")
        }),
        @Annotation2({
            @Annotation1("6"),
            @Annotation1("7")
        })
    })
    public static class Class1 {}


}
