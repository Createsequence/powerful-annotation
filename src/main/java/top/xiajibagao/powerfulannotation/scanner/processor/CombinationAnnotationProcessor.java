package top.xiajibagao.powerfulannotation.scanner.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * <p>用于将一组{@link AnnotationProcessor}适配为单个注解处理器的包装类。<br />
 * 传入一组具有顺序的处理器对象，当组合处理器被调用{@link #accept(int, int, Annotation)}方法时，
 * 将按顺序依次调用每一个内部处理器对象的{@link #accept(int, int, Annotation)}方法。<br />
 * 当调用{@link #interrupted()}方法时，任意一个内部处理器的{@link #interrupted()}方法返回{@code true}，
 * 都会使得该方法返回{@code true}。
 *
 * @author huangchengxing
 */
public class CombinationAnnotationProcessor implements AnnotationProcessor {

    /**
     * 内部处理器
     */
    private final List<AnnotationProcessor> processors;

    /**
     * 是否已中断
     */
    private boolean interrupted;

    /**
     * 创建一个组合处理器
     *
     * @param processors 内部处理器
     */
    public CombinationAnnotationProcessor(AnnotationProcessor... processors) {
        Assert.notNull(processors, "processors must not null");
        this.processors = CollUtil.newArrayList(processors);
        this.interrupted = false;
    }

    /**
     * 按顺序依次调用{@link #processors}中每一个处理器的{@link AnnotationProcessor#accept(int, int, Annotation)}方法
     *
     * @param verticalIndex 垂直索引。一般表示与扫描器扫描的{@link AnnotatedElement}相隔的层级层次。默认从1开始
     * @param horizontalIndex 水平索引，一般用于衡量两个注解对象之间被扫描到的先后顺序。默认从1开始
     * @param annotation 被扫描到的注解对象
     */
    @Override
    public void accept(int verticalIndex, int horizontalIndex, Annotation annotation) {
        if (interrupted()) {
            return;
        }
        for (AnnotationProcessor processor : processors) {
            processor.accept(verticalIndex, horizontalIndex, annotation);
            if (processor.interrupted()) {
                interrupted = true;
            }
        }
    }
    
    /**
     * {@link #processors}中任意一个处理器的{@link AnnotationProcessor#interrupted()}方法返回{@code true}，则该方法返回{@code true}
     *
     * @return 是否中断
     */
    @Override
    public boolean interrupted() {
        return interrupted;
    }
}
