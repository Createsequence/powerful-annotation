package top.xiajibagao.powerfulannotation.scanner.processor;

import lombok.Getter;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于在{@link AnnotationScanner}扫描过程中收集注解的注解处理器，
 * 完成扫描后，用户可以通过{@link #getCollectedAnnotations()}
 * 获得按照被扫描的顺序排序的注解对象
 *
 * @author huangchengxing
 */
@Getter
public class AnnotationCollector implements AnnotationProcessor {

    /**
     * 已收集的注解
     */
    private final List<Annotation> collectedAnnotations =  new ArrayList<>();

    /**
     * 收集被扫描到的注解
     *
     * @param verticalIndex 与被扫描对象的垂直距离
     * @param horizontalIndex 与被扫描对象的水平距离
     * @param annotation 注解对象
     */
    @Override
    public void process(int verticalIndex, int horizontalIndex, Annotation annotation) {
        collectedAnnotations.add(annotation);
    }

}
