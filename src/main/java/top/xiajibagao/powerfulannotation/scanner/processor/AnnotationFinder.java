package top.xiajibagao.powerfulannotation.scanner.processor;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * 用于在{@link AnnotationScanner}扫描过程中查找指定注解的注解处理器。
 * 当查找到第一个符合给定条件的注解后，扫描器将中断扫描，不再继续向后查找
 *
 * @author huangchengxing
 */
@Getter
public class AnnotationFinder implements AnnotationProcessor {

    /**
     * 判断条件
     */
    private final Predicate<Annotation> predicate;

    /**
     * 目标注解
     */
    private Annotation target;

    /**
     * 是否已经找到目标注解
     */
    private boolean found;
    
    /**
     * 创建一个注解查找器
     *
     * @param predicate 目标注解的判断条件
     */
    public AnnotationFinder(Predicate<Annotation> predicate) {
        Assert.notNull(predicate, "annotationFilter must not null");
        this.predicate = predicate.negate();
        this.found = false;
    }
    
    /**
     * 判断注解是否符合给定的{@link #predicate}，若符合条件则将会记录该注解对象，并返回{@code false}
     *
     * @param verticalIndex 与被扫描对象的垂直距离
     * @param horizontalIndex 与被扫描对象的水平距离
     * @param annotation 注解对象
     */
    @Override
    public void process(int verticalIndex, int horizontalIndex, Annotation annotation) {
        if (found) {
            return;
        }
        if (predicate.test(annotation)) {
            target = annotation;
            found = true;
        }
    }

    /**
     * 是否已经找到符合{@link #predicate}条件的注解
     *
     * @return 是否
     */
    public boolean isFound() {
        return ObjectUtil.isNotNull(target);
    }
    
    /**
     * 当{@link #isFound()}返回{@code false}时中断扫描
     *
     * @return 是否中断扫描
     */
    @Override
    public boolean interruptScanning() {
        return isFound();
    }
}
