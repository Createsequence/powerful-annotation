package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationCollector;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationFinder;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * <p>对{@link AnnotatedElement}上注解的搜索策略
 *
 * <p>默认提供了以下策略：
 * <ul>
 *     <li>{@link #NOTHING}：什么都不做，什么注解都不扫描；</li>
 *     <li>{@link #DIRECTLY}：扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解；</li>
 *     <li>
 *         {@link #DIRECTLY_AND_META_ANNOTATION}：扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解，
 *         以及这些注解的元注解；
 *     </li>
 *     <li>{@link #SUPERCLASS}：扫描元素本身以及父类的层级结构中声明的注解；</li>
 *     <li>{@link #SUPERCLASS_AND_META_ANNOTATION}：扫描元素本身以及父类的层级结构中声明的注解，以及这些注解的元注解；</li>
 *     <li>{@link #INTERFACE}：扫描元素本身以及父接口的层级结构中声明的注解；</li>
 *     <li>{@link #INTERFACE_AND_META_ANNOTATION}：扫描元素本身以及父接口的层级结构中声明的注解，以及这些注解的元注解；</li>
 *     <li>{@link #TYPE_HIERARCHY}：扫描元素本身以及父类、父接口的层级结构中声明的注解；</li>
 *     <li>{@link #TYPE_HIERARCHY_AND_META_ANNOTATION}：扫描元素本身以及父接口、父接口的层级结构中声明的注解，以及这些注解的元注解；</li>
 * </ul>
 *
 * <p><strong>注意：</strong>
 * <ul>
 *     <li>上述扫描器皆不处理{@link com.sun}，{@link java.lang}及{@link javax}包下注解的元注解；</li>
 *     <li>扫描器可能无法正确处理注解与其元注解之间潜在的循环依赖关系，比如：<code>{@code a -> b -> a}</code>；</li>
 * </ul>
 *
 * @author huangchengxing
 * @see AnnotationScanner
 */
@Getter
@RequiredArgsConstructor
public enum AnnotatedElementScanStrategy implements AnnotationScanner {

    /**
     * 不扫描任何注解
     */
    NOTHING((element, processor, filter) -> {}),

    /**
     * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解
     */
    DIRECTLY(new GenericTypeHierarchyAnnotationScanner(false, false, false)),

    /**
     * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解，以及这些注解的元注解
     */
    DIRECTLY_AND_META_ANNOTATION(new GenericTypeHierarchyAnnotationScanner(false, false, true)),

    /**
     * 扫描元素本身以及父类的层级结构中声明的注解
     */
    SUPERCLASS(new GenericTypeHierarchyAnnotationScanner(true, false, false)),

    /**
     * 扫描元素本身以及父类的层级结构中声明的注解，以及这些注解的元注解
     */
    SUPERCLASS_AND_META_ANNOTATION(new GenericTypeHierarchyAnnotationScanner(true, false, true)),

    /**
     * 扫描元素本身以及父接口的层级结构中声明的注解
     */
    INTERFACE(new GenericTypeHierarchyAnnotationScanner(false, true, false)),

    /**
     * 扫描元素本身以及父接口的层级结构中声明的注解，以及这些注解的元注解
     */
    INTERFACE_AND_META_ANNOTATION(new GenericTypeHierarchyAnnotationScanner(false, true, true)),

    /**
     * 扫描元素本身以及父类、父接口的层级结构中声明的注解
     */
    TYPE_HIERARCHY(new GenericTypeHierarchyAnnotationScanner(true, true, false)),

    /**
     * 扫描元素本身以及父接口、父接口的层级结构中声明的注解，以及这些注解的元注解
     */
    TYPE_HIERARCHY_AND_META_ANNOTATION(new GenericTypeHierarchyAnnotationScanner(true, true, true));
    
    /**
     * 注解扫描器
     */
    private final AnnotationScanner scanner;

    /**
     * 扫描元素并获得相关注解对象
     *
     * @param element 要扫描的元素
     * @param filter 注解过滤器
     * @return 注解对象
     */
    public List<Annotation> getAnnotations(AnnotatedElement element, AnnotationFilter filter) {
        if (ObjectUtil.isNull(element)) {
            return Collections.emptyList();
        }
        AnnotationCollector collector = new AnnotationCollector();
        getScanner().scan(element, collector, filter);
        return collector.getAnnotations();
    }

    /**
     * 扫描元素并获得指定注解
     *
     * @param element 要扫描的元素
     * @param filter 注解过滤器
     * @param predicate 目标的判断条件
     * @return 注解对象
     */
    public Annotation findAnnotation(AnnotatedElement element, AnnotationFilter filter, Predicate<Annotation> predicate) {
        if (ObjectUtil.isNull(element)) {
            return null;
        }
        AnnotationFinder finder = new AnnotationFinder(predicate);
        getScanner().scan(element, finder, filter);
        return finder.getTarget();
    }

    /**
     * 扫描元素并获得指定注解
     *
     * @param element 要扫描的元素
     * @param filter 注解过滤器
     * @param annotationType 注解类型
     * @return 注解对象
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T findAnnotation(AnnotatedElement element, AnnotationFilter filter, Class<T> annotationType) {
        return (T)findAnnotation(element, filter, annotation -> ObjectUtil.equals(annotation.annotationType(), annotationType));
    }

    /**
     * 扫描与指定具有关联的注解，并对其进行处理
     *
     * @param element 元素
     * @param processor 注解处理器
     * @param filter 过滤器
     */
    @Override
    public void scan(AnnotatedElement element, AnnotationProcessor processor, AnnotationFilter filter) {
        scanner.scan(element, processor, filter);
    }

}
