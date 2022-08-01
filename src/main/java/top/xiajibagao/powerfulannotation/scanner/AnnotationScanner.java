package top.xiajibagao.powerfulannotation.scanner;

import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.reflect.AnnotatedElement;

/**
 * 用于从{@link AnnotatedElement}上获取相关注解扫描器
 *
 * @author huangchengxing
 */
public interface AnnotationScanner {

    /**
     * 扫描指定元素上的注解
     *
     * @param element   待扫描的元素
     * @param processor 注解处理器
     * @param filter    过滤器，若为空则不过滤任何注解
     */
    void scan(AnnotatedElement element, AnnotationProcessor processor, AnnotationFilter filter);

}
