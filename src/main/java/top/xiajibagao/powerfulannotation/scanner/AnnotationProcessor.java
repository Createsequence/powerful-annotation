package top.xiajibagao.powerfulannotation.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * 注解处理器，用于在{@link AnnotationScanner}扫描到注解后，完成一些回调处理。
 *
 * @author huangchengxing
 */
@FunctionalInterface
public interface AnnotationProcessor {

    /**
     * 处理注解
     *
     * @param verticalIndex 垂直索引。一般表示与扫描器扫描的{@link AnnotatedElement}相隔的层级层次。默认从{@link AnnotationScanner#VERTICAL_INDEX_START_POINT}开始
     * @param horizontalIndex 水平索引，一般用于衡量两个注解对象之间被扫描到的先后顺序。默认从{@link AnnotationScanner#HORIZONTAL_INDEX_START_POINT}开始
     * @param source 注解的数据源
     * @param annotations 被扫描到的注解对象
     * @return 是否中断当前扫描进程
     */
    boolean accept(int verticalIndex, int horizontalIndex, AnnotatedElement source, Annotation[] annotations);

}
