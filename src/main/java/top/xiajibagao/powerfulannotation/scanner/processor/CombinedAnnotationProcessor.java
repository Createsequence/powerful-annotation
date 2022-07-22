package top.xiajibagao.powerfulannotation.scanner.processor;

import lombok.RequiredArgsConstructor;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * <p>表示处理一个注解，并扫描然后再处理该注解的元注解的组合注解处理器
 *
 * <p>给定一个源注解处理器{@link #annotationProcessor}，
 * 与该注解的元注解扫描器{@link #metaAnnotationScanner}及元注解处理器{@link #metaAnnotationProcessor}，
 * 当源注解处理完毕，且{@link AnnotationProcessor#process(int, int, Annotation)}返回值不为{@code false}时，
 * 将使用元注解扫描器对原注解的类进行扫描，并使用元注解处理器对获取到的元注解进行处理。
 *
 * @author huangchengxing
 */
@RequiredArgsConstructor
public class CombinedAnnotationProcessor implements AnnotationProcessor {

    /**
     * 源注解处理器
     */
    private final AnnotationProcessor annotationProcessor;

    /**
     * 元注解扫描器
     */
    private final AnnotationScanner metaAnnotationScanner;

    /**
     * 元注解处理器
     */
    private final AnnotationProcessor metaAnnotationProcessor;

    /**
     * 元注解过滤器
     */
    private final Predicate<Annotation> metaAnnotationFilter;

    /**
     * 是否已经中断扫描器
     */
    private boolean interrupted = false;

    /**
     * 处理器源注解，然后扫描器源注解上的元注解并使用指定处理器进行处理
     *
     * @param verticalIndex 与被扫描对象的垂直距离
     * @param horizontalIndex 与被扫描对象的水平距离
     * @param annotation 注解对象
     */
    @Override
    public void process(int verticalIndex, int horizontalIndex, Annotation annotation) {
        if (annotationProcessor.interruptScanning()) {
            return;
        }
        // 若源注解处理器中断，则当前处理器中断
        annotationProcessor.process(verticalIndex, horizontalIndex, annotation);
        if (annotationProcessor.interruptScanning()) {
            interrupted = true;
            return;
        }
        // 若源注解处理器未中断，但是源注解的元注解处理器中断，则当前处理器中断
        metaAnnotationScanner.scan(metaAnnotationProcessor, annotation.annotationType(), metaAnnotationFilter);
        if (metaAnnotationProcessor.interruptScanning()) {
            interrupted = true;
        }
    }

    @Override
    public boolean interruptScanning() {
        return interrupted;
    }

}
