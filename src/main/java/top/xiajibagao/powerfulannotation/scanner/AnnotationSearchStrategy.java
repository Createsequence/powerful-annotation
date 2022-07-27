package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.powerfulannotation.helper.Function3;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationCollector;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationFinder;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>用于从{@link AnnotatedElement}上搜索注解的策略，用于提供一些常用扫描操作的封装。<br />
 * 可以作为一个{@link AnnotationScanner}使用，此外还提供了一些额外的方法：
 * <ul>
 *     <li>{@code getAnnotationsXXX}: 用于批量获取注解；</li>
 *     <li>{@code getAnnotationXXX}: 用于获取单个注解，当扫描并获取到第一个符合条件的注解后，将直接返回并停止扫描；</li>
 * </ul>
 *
 * <p><strong>可选的扫描策略：</strong>
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
 *     <li>提供扫描策略皆不会处理{@link com.sun}，{@link java.lang}及{@link javax}包下的注解；</li>
 *     <li>提供扫描策略皆不会重复访问一个已经访问过的非注解类，即使它们在出现在不同层级；</li>
 *     <li>
 *         若扫描对象的层级结构中存在多个类型一致的注解对象，则扫描策略会重复处理该对象。<br />
 *         该设置是为了保证即使相同的注解被重复注解在元素的不同层级结构中时，它们依然可以被正确的扫描到。 <br />
 *         <strong>但是若注解与其元注解若存在直接或间接的循环引用，将有可能引发{@link StackOverflowError}</strong>；
 *     </li>
 * </ul>
 *
 * @author huangchengxing
 * @see GenericTypeHierarchyScanner
 * @see AnnotationCollector
 * @see AnnotationFinder
 */
@Getter
@RequiredArgsConstructor
public enum AnnotationSearchStrategy implements AnnotationScanner {

    /**
     * 不扫描任何注解
     */
    NOTHING((element, processor, filter) -> {}),

    /**
     * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解
     */
    DIRECTLY(new GenericTypeHierarchyScanner(false, false, false)),

    /**
     * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解，以及这些注解的元注解
     */
    DIRECTLY_AND_META_ANNOTATION(new GenericTypeHierarchyScanner(false, false, true)),

    /**
     * 扫描元素本身以及父类的层级结构中声明的注解
     */
    SUPERCLASS(new GenericTypeHierarchyScanner(true, false, false)),

    /**
     * 扫描元素本身以及父类的层级结构中声明的注解，以及这些注解的元注解
     */
    SUPERCLASS_AND_META_ANNOTATION(new GenericTypeHierarchyScanner(true, false, true)),

    /**
     * 扫描元素本身以及父接口的层级结构中声明的注解
     */
    INTERFACE(new GenericTypeHierarchyScanner(false, true, false)),

    /**
     * 扫描元素本身以及父接口的层级结构中声明的注解，以及这些注解的元注解
     */
    INTERFACE_AND_META_ANNOTATION(new GenericTypeHierarchyScanner(false, true, true)),

    /**
     * 扫描元素本身以及父类、父接口的层级结构中声明的注解
     */
    TYPE_HIERARCHY(new GenericTypeHierarchyScanner(true, true, false)),

    /**
     * 扫描元素本身以及父接口、父接口的层级结构中声明的注解，以及这些注解的元注解
     */
    TYPE_HIERARCHY_AND_META_ANNOTATION(new GenericTypeHierarchyScanner(true, true, true));
    
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
    public <T> List<T> getAnnotations(
        AnnotatedElement element, AnnotationFilter filter,
        Function3<Integer, Integer, Annotation, T> function) {
        if (ObjectUtil.isNull(element)) {
            return Collections.emptyList();
        }
        AnnotationCollector<T> collector = AnnotationCollector.create(function);
        getScanner().scan(element, collector, filter);
        return collector.getTargets();
    }

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
        AnnotationCollector<Annotation> collector = AnnotationCollector.create();
        getScanner().scan(element, collector, filter);
        return collector.getTargets();
    }

    /**
     * 获取元素上全部指定类型的注解，该方法无法获得{@link java.lang}, 与{@link javax}还有{@link com.sun}包下的注解
     *
     * @param element 要扫描的元素
     * @param annotationType 注解过滤器
     * @return 注解对象
     */
    public <T extends Annotation> List<T> getAnnotationsByType(AnnotatedElement element, Class<T> annotationType) {
        return getAnnotations(element, AnnotationFilter.NOT_JDK_ANNOTATION).stream()
            .filter(annotation -> ObjectUtil.equals(annotation.annotationType(), annotationType))
            .map(annotationType::cast)
            .collect(Collectors.toList());
    }

    /**
     * 扫描元素并获得指定注解
     *
     * @param element   要扫描的元素
     * @param filter    注解过滤器
     * @param predicate 目标的判断条件
     * @return 注解对象
     */
    public <T> T getAnnotation(
        AnnotatedElement element, AnnotationFilter filter, Predicate<T> predicate,
        Function3<Integer, Integer, Annotation, T> function) {
        if (ObjectUtil.isNull(element)) {
            return null;
        }
        AnnotationFinder<T> finder = AnnotationFinder.create(function, predicate);
        getScanner().scan(element, finder, filter);
        return finder.getTarget();
    }

    /**
     * 扫描元素并获得指定注解，该方法无法获得{@link java.lang}, 与{@link javax}还有{@link com.sun}包下的注解
     *
     * @param element 要扫描的元素
     * @param annotationType 注解类型
     * @return 注解对象
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotationByType(AnnotatedElement element, Class<T> annotationType) {
        if (ObjectUtil.isNull(element)) {
            return null;
        }
        AnnotationFinder<Annotation> finder = AnnotationFinder.create(annotation -> ObjectUtil.equals(annotation.annotationType(), annotationType));
        getScanner().scan(element, finder, AnnotationFilter.NOT_JDK_ANNOTATION);
        return (T)finder.getTarget();
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
