package top.xiajibagao.powerfulannotation.scanner;

import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.reflect.AnnotatedElement;

/**
 * <p>注解扫描器，用于从指定的{@link AnnotatedElement}中获取具有直接或间接关联的注解对象。
 * 默认提供了{@link AnnotatedElementScanStrategy}用于封装一些常用的预置扫描器。
 *
 * @author huangchengxing
 * @see AnnotationProcessor
 * @see AnnotationFilter
 * @see AnnotatedElementScanStrategy
 */
public interface AnnotationScanner {

    /**
     * 扫描与指定具有关联的注解，并对其进行处理
     *
     * @param element 元素
     * @param processor 注解处理器
     * @param filter 过滤器
     */
    void scan(AnnotatedElement element, AnnotationProcessor processor, AnnotationFilter filter);

}
