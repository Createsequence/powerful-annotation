package top.xiajibagao.powerfulannotation.scanner.processor;

import lombok.Getter;
import top.xiajibagao.powerfulannotation.helper.CollUtils;
import top.xiajibagao.powerfulannotation.scanner.AnnotationProcessor;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>用于在{@link AnnotationScanner}扫描过程中收集注解的注解处理器，
 * 完成扫描后，用户可以通过{@link #getTargets()}获得按照被扫描的顺序排序的注解对象
 *
 * @author huangchengxing
 * @see AnnotationScanner
 */
@Getter
public class AnnotationCollector implements AnnotationProcessor {

    private final List<Annotation> targets = new ArrayList<>();

    @Override
    public boolean accept(int verticalIndex, int horizontalIndex, AnnotatedElement source, Annotation[] annotations) {
        CollUtils.addAll(this.targets, annotations);
        return true;
    }
}
