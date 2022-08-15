package top.xiajibagao.powerfulannotation.scanner.processor;

import lombok.Getter;
import top.xiajibagao.powerfulannotation.helper.AssertUtils;
import top.xiajibagao.powerfulannotation.scanner.AnnotationProcessor;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * <p>用于在{@link AnnotationScanner}扫描过程中查找指定注解的注解处理器。
 * 当查找到第一个符合给定条件的注解后，扫描器将中断扫描，不再继续向后查找
 *
 * @author huangchengxing
 */
public class AnnotationFinder implements AnnotationProcessor {

    /**
     * 判断条件
     */
    private final Predicate<Annotation> predicate;

    /**
     * 目标注解
     */
    @Getter
    private Annotation target;

    /**
     * 是否已经找到目标注解
     */
    @Getter
    private boolean found;

    /**
     * 创建一个注解查找器
     *
     * @param predicate 判断条件
     */
    public AnnotationFinder(Predicate<Annotation> predicate) {
        AssertUtils.notNull(predicate, "predicate must not null");
        this.predicate = predicate;
    }

    @Override
    public boolean accept(int verticalIndex, int horizontalIndex, AnnotatedElement source, Annotation[] annotations) {
        if (this.isFound()) {
            return false;
        }
        Optional<Annotation> annotation = Stream.of(annotations)
            .filter(predicate)
            .findFirst();
        if (annotation.isPresent()) {
            this.found = true;
            this.target = annotation.get();
            return false;
        }
        return true;
    }

}
