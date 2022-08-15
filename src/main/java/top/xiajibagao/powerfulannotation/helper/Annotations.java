package top.xiajibagao.powerfulannotation.helper;

import top.xiajibagao.powerfulannotation.scanner.AnnotationFilter;
import top.xiajibagao.powerfulannotation.scanner.AnnotationProcessor;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationCollector;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationFinder;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 通用注解工具类
 *
 * @author huangchengxing
 */
public class Annotations {

    private Annotations() {
    }

    /**
     * 空数组
     */
    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    // ========================== default ==========================
    
    /**
     * 获取空注解数组
     *
     * @return 空数组
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T[] emptyArray() {
        return (T[])EMPTY_ANNOTATIONS;
    }

    /**
     * 获取直接声明的注解
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @return 注解对象
     */
    public static <T extends Annotation> T getDeclaredAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return Objects.isNull(element) ? null : element.getDeclaredAnnotation(annotationType);
    }

    /**
     * 获取直接声明的注解
     *
     * @param element 注解元素
     * @return 注解对象
     */
    public static Annotation[] getDeclaredAnnotations(AnnotatedElement element) {
        return Objects.isNull(element) ?
            emptyArray() : element.getDeclaredAnnotations();
    }

    // ========================== scanner ==========================

    /**
     * 通过扫描器从指定元素获取指定类型注解。注意，默认不支持搜索{@link java}包下的注解
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @param scanner 扫描器
     * @return 注解对象
     */
    public static <T extends Annotation> T find(AnnotatedElement element, Class<T> annotationType, AnnotationScanner scanner) {
        return scan(element, scanner, new AnnotationFinder(a -> Objects.equals(a.annotationType(), annotationType)))
            .map(AnnotationFinder::getTarget)
            .map(annotationType::cast)
            .orElse(null);
    }

    /**
     * 通过扫描器从指定元素获取指定类型注解。注意，默认不支持搜索{@link java}包下的注解
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @param scanner 扫描器
     * @return 注解对象
     */
    public static <T extends Annotation> List<T> collectByType(AnnotatedElement element, Class<T> annotationType, AnnotationScanner scanner) {
        return collect(element, scanner).stream()
            .filter(a -> Objects.equals(a.annotationType(), annotationType))
            .map(annotationType::cast)
            .collect(Collectors.toList());
    }

    /**
     * 通过扫描器从指定元素获取全部关联注解。注意，默认不支持搜索{@link java}包下的注解
     *
     * @param element 注解元素
     * @param scanner 扫描器
     * @return 注解对象
     */
    public static List<Annotation> collect(AnnotatedElement element, AnnotationScanner scanner) {
        return scan(element, scanner, new AnnotationCollector())
            .map(AnnotationCollector::getTargets)
            .orElseGet(Collections::emptyList);
    }

    // ========================== private ==========================

    /**
     * 使用指定注解处理器扫描元素，并默认过滤{@link java}包下的注解
     */
    private static <P extends AnnotationProcessor> Optional<P> scan(AnnotatedElement element, AnnotationScanner scanner, P processor) {
        if (Objects.isNull(element) || Objects.isNull(scanner)) {
            return Optional.empty();
        }
        scanner.scan(element, processor, AnnotationFilter.FILTER_JAVA);
        return Optional.of(processor);
    }

}
