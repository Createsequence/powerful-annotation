package top.xiajibagao.powerfulannotation.helper;

import cn.hutool.core.util.ObjectUtil;
import top.xiajibagao.powerfulannotation.annotation.Link;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;
import top.xiajibagao.powerfulannotation.synthesis.GenericSynthesizedAggregateAnnotation;
import top.xiajibagao.powerfulannotation.synthesis.SynthesizedAggregateAnnotation;
import top.xiajibagao.powerfulannotation.synthesis.proxy.SynthesizedAnnotationInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 支持从层级结构中获取注解或合成注解的工具类
 *
 * @author huangchengxing
 */
public class SynthesizedAnnotationUtils {

    private SynthesizedAnnotationUtils() {

    }

    // ============================ 注解扫描 ============================

    /**
     * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解
     *
     * @param element 被注解的元素
     * @return java.util.List<java.lang.annotation.Annotation>
     */
    public static List<Annotation> scanDirectly(AnnotatedElement element) {
        return scanByScanner(element, AnnotationScanner.DIRECTLY);
    }

    /**
     * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解，以及这些注解的元注解
     *
     * @param element 被注解的元素
     * @return java.util.List<java.lang.annotation.Annotation>
     */
    public static List<Annotation> scanDirectlyAndMetaAnnotation(AnnotatedElement element) {
        return scanByScanner(element, AnnotationScanner.DIRECTLY_AND_META_ANNOTATION);
    }

    /**
     * 扫描元素本身以及父类的层级结构中声明的注解
     *
     * @param element 被注解的元素
     * @return java.util.List<java.lang.annotation.Annotation>
     */
    public static List<Annotation> scanSuperclass(AnnotatedElement element) {
        return scanByScanner(element, AnnotationScanner.SUPERCLASS);
    }

    /**
     * 扫描元素本身以及父类的层级结构中声明的注解
     *
     * @param element 被注解的元素
     * @return java.util.List<java.lang.annotation.Annotation>
     */
    public static List<Annotation> scanSuperclassAndMetaAnnotation(AnnotatedElement element) {
        return scanByScanner(element, AnnotationScanner.SUPERCLASS_AND_META_ANNOTATION);
    }

    /**
     * 扫描元素本身以及父接口的层级结构中声明的注解
     *
     * @param element 被注解的元素
     * @return java.util.List<java.lang.annotation.Annotation>
     */
    public static List<Annotation> scanInterface(AnnotatedElement element) {
        return scanByScanner(element, AnnotationScanner.INTERFACE);
    }

    /**
     * 扫描元素本身以及父接口的层级结构中声明的注解，以及这些注解的元注解
     *
     * @param element 被注解的元素
     * @return java.util.List<java.lang.annotation.Annotation>
     */
    public static List<Annotation> scanInterfaceAndMetaAnnotation(AnnotatedElement element) {
        return scanByScanner(element, AnnotationScanner.INTERFACE_AND_META_ANNOTATION);
    }

    /**
     * 扫描元素本身以及父类、父接口的层级结构中声明的注解
     *
     * @param element 被注解的元素
     * @return java.util.List<java.lang.annotation.Annotation>
     */
    public static List<Annotation> scanTypeHierarchy(AnnotatedElement element) {
        return scanByScanner(element, AnnotationScanner.TYPE_HIERARCHY);
    }

    /**
     * 扫描元素本身以及父接口、父接口的层级结构中声明的注解，以及这些注解的元注解
     *
     * @param element 被注解的元素
     * @return java.util.List<java.lang.annotation.Annotation>
     */
    public static List<Annotation> scanTypeHierarchyAndMetaAnnotation(AnnotatedElement element) {
        return scanByScanner(element, AnnotationScanner.TYPE_HIERARCHY_AND_META_ANNOTATION);
    }

    // ============================ get方法 ============================

    /**
     * 判断在元素的注解和注解的元注解中是否存指定的注解对象
     *
     * @param element 被注解的元素
     * @param annotationType 注解类型
     * @return boolean
     */
    public static boolean canGetAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        List<Annotation> annotations = scanDirectlyAndMetaAnnotation(element);
        return annotations.stream()
            .anyMatch(annotation -> ObjectUtil.equals(annotation.annotationType(), annotationType));
    }

    /**
     * <p>将指定注解及其元注解聚合为一个合成注解，并从聚合后的注解中查找指定的注解对象，
     * 该注解对象的属性值将会根据被低层级注解中类型与名称完全一致的属性值覆盖。<br />
     * 合成注解支持处理被{@link Link}注解的属性。
     *
     * @param annotationType 注解类型
     * @param annotation 注解对象
     * @return T
     */
    public static <T extends Annotation> T getSynthesizedAnnotation(Annotation annotation, Class<T> annotationType) {
        return aggregatingFromAnnotationWithMeta(Collections.singletonList(annotation))
            .synthesize(annotationType);
    }

    /**
     * <p>将指定的注解聚合为合成注解，并从聚合后的注解中查找指定的注解对象，
     * 该注解对象的属性值将会根据被低层级注解中类型与名称完全一致的属性值覆盖。<br />
     * 合成注解支持处理被{@link Link}注解的属性。
     *
     * @param annotationType 注解类型
     * @param annotations 注解对象
     * @return T
     */
    public static <T extends Annotation> T getSynthesizedAnnotation(Annotation[] annotations, Class<T> annotationType) {
        return aggregatingFromAnnotationWithMeta(Arrays.asList(annotations))
            .synthesize(annotationType);
    }

    /**
     * <p>将指定元素上的直接存在的注解聚合为合成注解，并从聚合后的注解中查找指定的注解对象，
     * 该注解对象的属性值将会根据被低层级注解中类型与名称完全一致的属性值覆盖。<br />
     * 合成注解支持处理被{@link Link}注解的属性。
     *
     * @param annotationType 注解类型
     * @param element 注解对象
     * @return T
     */
    public static <T extends Annotation> T getSynthesizedAnnotation(AnnotatedElement element, Class<T> annotationType) {
        List<Annotation> annotations = scanTypeHierarchy(element);
        return aggregatingFromAnnotationWithMeta(annotations)
            .synthesize(annotationType);
    }

    /**
     * 获取元素上所有指定注解
     * <ul>
     *     <li>若元素是类，则递归解析全部父类和全部父接口上的注解;</li>
     *     <li>若元素是方法、属性或注解，则只解析其直接声明的注解;</li>
     * </ul>
     *
     * <p>注解合成规则如下：
     * 若{@code AnnotatedEle}按顺序从上到下声明了A，B，C三个注解，且三注解存在元注解如下：
     * <pre>
     *    A -&gt; M1 -&gt; M2
     *    B -&gt; M3 -&gt; M1 -&gt; M2
     *    C -&gt; M2
     * </pre>
     * 此时入参{@code annotationType}类型为{@code M1}，则最终将返回基于根注解A与根注解B合成的合成注解。
     *
     * @param annotatedEle   {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param annotationType 注解类
     * @param <T>            注解类型
     * @return 合成注解
     * @see SynthesizedAggregateAnnotation
     */
    public static <T extends Annotation> List<T> getAllSynthesizedAnnotations(AnnotatedElement annotatedEle, Class<T> annotationType) {
        return synthesizeForEachAggregateAnnotation(scanDirectly(annotatedEle), annotationType);
    }

    // ============================ find方法 ============================

    /**
     * 判断在元素以及其层次结构中的注解和元注解中是否存指定的注解对象
     *
     * @param element 被注解的元素
     * @param annotationType 注解类型
     * @return boolean
     */
    public static boolean canFindAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        List<Annotation> annotations = scanTypeHierarchyAndMetaAnnotation(element);
        return annotations.stream()
            .anyMatch(annotation -> ObjectUtil.equals(annotation.annotationType(), annotationType));
    }

    /**
     * <p>将指定元素上的注解聚合为合成注解，并从聚合后的注解中查找指定的注解对象，
     * 该注解对象的属性值将会根据被低层级注解中类型与名称完全一致的属性值覆盖。<br />
     * 合成注解支持处理被{@link Link}注解的属性。
     *
     * @param annotationType 注解类型
     * @param element 注解对象
     * @return T
     */
    public static <T extends Annotation> T findSynthesizedAnnotation(AnnotatedElement element, Class<T> annotationType) {
        List<Annotation> annotations = scanTypeHierarchy(element);
        return aggregatingFromAnnotationWithMeta(annotations)
            .synthesize(annotationType);
    }

    /**
     * 获取元素上所有指定注解
     * <ul>
     *     <li>若元素是类，则递归解析全部父类和全部父接口上的注解;</li>
     *     <li>若元素是方法、属性或注解，则只解析其直接声明的注解;</li>
     * </ul>
     *
     * <p>注解合成规则如下：
     * 若{@code AnnotatedEle}按顺序从上到下声明了A，B，C三个注解，且三注解存在元注解如下：
     * <pre>
     *    A -&gt; M1 -&gt; M2
     *    B -&gt; M3 -&gt; M1 -&gt; M2
     *    C -&gt; M2
     * </pre>
     * 此时入参{@code annotationType}类型为{@code M1}，则最终将返回基于根注解A与根注解B合成的合成注解。
     *
     * @param annotatedEle   {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param annotationType 注解类
     * @param <T>            注解类型
     * @return 合成注解
     * @see SynthesizedAggregateAnnotation
     */
    public static <T extends Annotation> List<T> findAllSynthesizedAnnotations(AnnotatedElement annotatedEle, Class<T> annotationType) {
        return synthesizeForEachAggregateAnnotation(AnnotationScanner.TYPE_HIERARCHY.getAnnotations(annotatedEle), annotationType);
    }

    // ============================ 合成注解 ============================

    /**
     * 该注解对象是否为通过代理类生成的合成注解
     *
     * @param annotation 注解对象
     * @return 是否
     * @see SynthesizedAnnotationInvocationHandler#isProxyAnnotation(Class)
     */
    public static boolean isSynthesizedAnnotation(Annotation annotation) {
        return SynthesizedAnnotationInvocationHandler.isProxyAnnotation(annotation.getClass());
    }

    /**
     * 对指定注解对象及其元注解进行聚合
     *
     * @param annotations 注解对象
     * @return 聚合注解
     */
    private static SynthesizedAggregateAnnotation aggregatingFromAnnotationWithMeta(List<Annotation> annotations) {
        return new GenericSynthesizedAggregateAnnotation(annotations, AnnotationScanner.DIRECTLY_AND_META_ANNOTATION);
    }

    /**
     * 使用扫描器扫描器注解
     */
    private static List<Annotation> scanByScanner(AnnotatedElement annotatedElement, AnnotationScanner scanner) {
        return ObjectUtil.isNull(annotatedElement) ? Collections.emptyList() : scanner.getAnnotations(annotatedElement);
    }

    /**
     * 批量聚合注解，然后从每一个聚合的注解中获取合成注解
     */
    private static <T extends Annotation> List<T> synthesizeForEachAggregateAnnotation(Collection<Annotation> annotations, Class<T> annotationType) {
        return annotations.stream()
            .map(Collections::singletonList)
            .map(SynthesizedAnnotationUtils::aggregatingFromAnnotationWithMeta)
            .map(a -> a.synthesize(annotationType))
            .filter(ObjectUtil::isNotNull)
            .collect(Collectors.toList());
    }

}
