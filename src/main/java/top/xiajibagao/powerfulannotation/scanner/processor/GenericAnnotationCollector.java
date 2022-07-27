package top.xiajibagao.powerfulannotation.scanner.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.powerfulannotation.helper.Function3;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>用于在{@link AnnotationScanner}扫描过程中收集注解的注解处理器，
 * 完成扫描后，用户可以通过{@link #getTargets()}获得按照被扫描的顺序排序的注解对象
 *
 * @author huangchengxing
 */
@RequiredArgsConstructor
@Getter
public class GenericAnnotationCollector<T> implements AnnotationProcessor {

    public static <T> GenericAnnotationCollector<T> create(Function3<Integer, Integer, Annotation, T> function) {
        return new GenericAnnotationCollector<>(function);
    }

    public static GenericAnnotationCollector<Annotation> create() {
        return new GenericAnnotationCollector<>((vi, hi, a) -> a);
    }

    /**
     * 目标对象
     */
    private final List<T> targets = new ArrayList<>();

    /**
     * 转换操作
     */
    private final Function3<Integer, Integer, Annotation, T> function;

    /**
     * 处理注解，将其添加到{@link #targets}中
     *
     * @param verticalIndex 垂直索引。一般表示与扫描器扫描的{@link AnnotatedElement}相隔的层级层次。默认从1开始
     * @param horizontalIndex 水平索引，一般用于衡量两个注解对象之间被扫描到的先后顺序。默认从1开始
     * @param annotation 被扫描到的注解对象
     */
    @Override
    public void accept(int verticalIndex, int horizontalIndex, Annotation annotation) {
        targets.add(function.accept(verticalIndex, horizontalIndex, annotation));
    }

}
