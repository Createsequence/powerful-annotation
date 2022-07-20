package top.xiajibagao.powerfulannotation.synthesis;

import cn.hutool.core.collection.CollUtil;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingParser;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingRegistry;
import top.xiajibagao.powerfulannotation.repeatable.SimpleRepeatableMappingRegistry;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;
import top.xiajibagao.powerfulannotation.synthesis.processor.SynthesizedAnnotationAttributeProcessor;
import top.xiajibagao.powerfulannotation.synthesis.processor.SynthesizedAnnotationPostProcessor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author huangchengxing
 */
public class RepeatableSynthesizedAggregateAnnotation
    extends GenericSynthesizedAggregateAnnotation
    implements RepeatableContainerAnnotation {

    private final RepeatableMappingRegistry repeatableMappingRegistry;

    public RepeatableSynthesizedAggregateAnnotation(List<Annotation> source, AnnotationScanner annotationScanner) {
        super(source, annotationScanner);
        this.repeatableMappingRegistry = new SimpleRepeatableMappingRegistry(
            RepeatableMappingParser.STANDARD_REPEATABLE_MAPPING_PARSER,
            RepeatableMappingParser.REPEATABLE_BY_MAPPING_PARSER
        );
    }

    public RepeatableSynthesizedAggregateAnnotation(List<Annotation> source, AnnotationScanner annotationScanner, RepeatableMappingRegistry repeatableMappingRegistry) {
        super(source, annotationScanner);
        this.repeatableMappingRegistry = repeatableMappingRegistry;
    }

    public RepeatableSynthesizedAggregateAnnotation(
        List<Annotation> source,
        SynthesizedAnnotationSelector annotationSelector,
        SynthesizedAnnotationAttributeProcessor attributeProcessor,
        Collection<SynthesizedAnnotationPostProcessor> annotationPostProcessors,
        AnnotationScanner annotationScanner,
        RepeatableMappingRegistry repeatableMappingRegistry) {
        super(source, annotationSelector, attributeProcessor, annotationPostProcessors, annotationScanner);
        this.repeatableMappingRegistry = repeatableMappingRegistry;
    }

    /**
     * 获取可重复注解映射关系注册表
     *
     * @return 可重复注解映射关系注册表
     */
    @Override
    public RepeatableMappingRegistry getRepeatableMappingRegistry() {
        return repeatableMappingRegistry;
    }

    @Override
    protected Map<Class<? extends Annotation>, SynthesizedAnnotation> loadAnnotations() {
        Map<Class<? extends Annotation>, SynthesizedAnnotation> annotationMap = super.loadAnnotations();
        annotationMap.values().forEach(annotation -> repeatableMappingRegistry.register(annotation.annotationType()));
        return annotationMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> List<T> getRepeatableAnnotations(Class<T> annotationType) {
        init();
        List<T> annotations = isAnnotationPresent(annotationType) ?
            CollUtil.newArrayList((T)getSynthesizedAnnotation(annotationType).getAnnotation()) : new ArrayList<>();
        synthesizedAnnotationMap.values().stream()
            .map(SynthesizedAnnotation::getAnnotation)
            .filter(annotation -> repeatableMappingRegistry.isContainerOf(annotationType, annotation.annotationType()))
            .map(annotation -> repeatableMappingRegistry.getElementsFromContainer(annotation, annotationType))
            .forEach(annotations::addAll);
        return annotations;
    }

}
