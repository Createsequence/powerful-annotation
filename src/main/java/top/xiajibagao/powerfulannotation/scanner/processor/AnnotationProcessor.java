package top.xiajibagao.powerfulannotation.scanner.processor;

import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;

/**
 * <p>注解处理器，用于在{@link AnnotationScanner}扫描得到注解进行一些回调处理。
 * {@link AnnotationScanner}每次获取到一个新注解时，
 * 都会调用{@link #process(int, int, Annotation)}对其进行处理，
 * 然后再对应调用一次{@link #interruptScanning()}判断处理该注解后是否需要中断扫描。
 *
 * @author huangchengxing
 * @see AnnotationScanner
 */
@FunctionalInterface
public interface AnnotationProcessor {

    /**
     * 处理注解
     *
     * @param verticalIndex 与被扫描对象的垂直距离
     * @param horizontalIndex 与被扫描对象的水平距离
     * @param annotation 注解对象
     */
    void process(int verticalIndex, int horizontalIndex, Annotation annotation);

    /**
     * 是否中断扫描。该方法默认总是返回{@code false}
     *
     * @return 是否中断扫描
     */
    default boolean interruptScanning() {
        return false;
    }

}
