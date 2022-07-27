package top.xiajibagao.powerfulannotation.scanner.processor;

import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * <p>注解处理器，用于在{@link AnnotationScanner}扫描到注解后，进行一些回调处理逻辑。<br />
 * 当扫描器每次调用{@link #accept(int, int, Annotation)}后，
 * 都会对应的调用一次{@link #interrupted()}方法，若返回值为{@code true}，
 * 则扫描器将会中断正在进行从扫描进程。<br />
 * 若一个处理器已经中断，则再次调用{@link #accept(int, int, Annotation)}方法后，
 * 将不会对传入的注解进行任何处理。
 *
 * @author huangchengxing
 */
@FunctionalInterface
public interface AnnotationProcessor {

    /**
     * 处理注解
     *
     * @param verticalIndex 垂直索引。一般表示与扫描器扫描的{@link AnnotatedElement}相隔的层级层次。默认从0开始
     * @param horizontalIndex 水平索引，一般用于衡量两个注解对象之间被扫描到的先后顺序。默认从1开始
     * @param annotation 被扫描到的注解对象
     */
    void accept(int verticalIndex, int horizontalIndex, Annotation annotation);

    /**
     * 是否中断扫描器的扫描进程
     *
     * @return boolean
     */
    default boolean interrupted() {
        return false;
    }

}
