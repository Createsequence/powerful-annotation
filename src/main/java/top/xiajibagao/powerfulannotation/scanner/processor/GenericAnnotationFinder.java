package top.xiajibagao.powerfulannotation.scanner.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.powerfulannotation.helper.Function3;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Predicate;

/**
 * <p>用于在{@link AnnotationScanner}扫描过程中查找指定注解的注解处理器。
 * 当查找到第一个符合给定条件的注解后，扫描器将中断扫描，不再继续向后查找
 *
 * @author huangchengxing
 */
@RequiredArgsConstructor
@Getter
public class GenericAnnotationFinder<T> implements AnnotationProcessor {

    public static <T> GenericAnnotationFinder<T> create(
        Function3<Integer, Integer, Annotation, T> function, Predicate<T> predicate) {
        return new GenericAnnotationFinder<>(predicate, function);
    }

    public static GenericAnnotationFinder<Annotation> create(Predicate<Annotation> predicate) {
        return new GenericAnnotationFinder<>(predicate, (vi, hi, a) -> a);
    }

    /**
     * 判断条件
     */
    private final Predicate<T> predicate;

    /**
     * 转换操作
     */
    private final Function3<Integer, Integer, Annotation, T> function;

    /**
     * 目标注解
     */
    private T target;

    /**
     * 是否已经找到目标注解
     */
    private boolean found;

    /**
     * 使用{@link #predicate}对注解其进行校验，若其通过校验，
     * 则将直接赋值给{@link #target}，并标记本次扫描已经中断
     *
     * @param verticalIndex 垂直索引。一般表示与扫描器扫描的{@link AnnotatedElement}相隔的层级层次。默认从1开始
     * @param horizontalIndex 水平索引，一般用于衡量两个注解对象之间被扫描到的先后顺序。默认从1开始
     * @param annotation 被扫描到的注解对象
     */
    @Override
    public void accept(int verticalIndex, int horizontalIndex, Annotation annotation) {
        if (found) {
            return;
        }
        T converted = function.accept(verticalIndex, horizontalIndex, annotation);
        if (predicate.test(converted)) {
            target = converted;
            found = true;
        }
    }

    /**
     * 当{@link #isFound()}返回{@code false}时中断扫描
     *
     * @return 是否中断扫描
     */
    @Override
    public boolean interrupted() {
        return isFound();
    }
}
