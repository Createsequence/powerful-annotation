package top.xiajibagao.powerfulannotation.helper;

import cn.hutool.core.util.ObjectUtil;
import top.xiajibagao.powerfulannotation.annotation.Link;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;
import top.xiajibagao.powerfulannotation.synthesis.*;
import top.xiajibagao.powerfulannotation.synthesis.attribute.FixedValueAnnotationAttribute;
import top.xiajibagao.powerfulannotation.synthesis.proxy.ProxiedSynthesizedAnnotation;
import top.xiajibagao.powerfulannotation.synthesis.proxy.SynthesizedAnnotationInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>支持从层级结构中获取注解或合成注解的工具类
 *
 * <p>类中以<em>find</em>和<em>get</em>作为前缀的方法将查找{@link AnnotatedElement}的不同层级结构：
 * <ul>
 *     <li>find：表示从{@link AnnotatedElement}及其类层结构中寻找注解；</li>
 *     <li>get：表示从{@link AnnotatedElement}本身寻找直接存在的注解；</li>
 * </ul>
 * 此外，本类中的所有的方法都支持在获取到{@link AnnotatedElement}上的注解后，
 * 继续查找获得的注解的元注解层级结构，若不需要查找元注解，则应当使用{@link AnnotationUtils}中的方法。<br />
 *
 * <p>通过本类中的<em>findXXX</em>/<em>getXXX</em>方法获取到的合成注解，
 * 都支持基于{@link Link}的属性别名机制，并且遵循元注解同名同类型属性会被子注解覆盖的原则。<br />
 * 当使用合成注解对从层级结构中查找到的注解及其元注解进行合成时，
 * 若存在多个同类的注解，则将仅保留第一个被查找到的注解，其余的同类型注解将被忽略。<br />
 * 默认的查询顺序为：
 * <ol>
 *     <li>{@link AnnotatedElement}上直接声明的注解；</li>
 *     <li>{@link AnnotatedElement}上直接声明的注解的元注解；</li>
 *     <li>{@link AnnotatedElement}的上层结构中对应{@link AnnotatedElement}直接声明的注解；</li>
 *     <li>{@link AnnotatedElement}的上层结构中对应{@link AnnotatedElement}直接声明的注解的元注解；</li>
 * </ol>
 *
 * <p>当使用<em>getAllXXX</em>或<em>findAllXXX</em>方法时，
 * {@link AnnotatedElement}的层级结构间的注解
 *
 * @author huangchengxing
 * @see Link
 * @see SynthesizedAggregateAnnotation
 * @see SynthesizedAnnotation
 * @see SynthesizedAnnotationInvocationHandler
 */
public class AnnotatedElementUtils {

    private AnnotatedElementUtils() {

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
     * <p>将指定元素上的直接存在的注解聚合为合成注解，并从聚合后的注解中查找指定的注解对象，
     * 该注解对象的属性值将会根据被低层级注解中类型与名称完全一致的属性值覆盖。<br />
     * 合成注解支持处理被{@link Link}注解的属性。
     *
     * @param annotationType 注解类型
     * @param element 注解对象
     * @return T
     */
    public static <T extends Annotation> T getSynthesizedAnnotation(AnnotatedElement element, Class<T> annotationType) {
        List<Annotation> annotations = scanDirectly(element);
        return aggregatingFromAnnotationWithMeta(annotations)
            .synthesize(annotationType);
    }

    /**
     * 获取可重复的合成注解
     *
     * @param annotatedEle   注解元素
     * @param annotationType 注解类型
     * @return java.util.List<T>
     */
    public static <T extends Annotation> List<T> getRepeatableSynthesizedAnnotations(AnnotatedElement annotatedEle, Class<T> annotationType) {
        List<Annotation> annotations = scanDirectly(annotatedEle);
        return aggregatingRepeatableFromAnnotationWithMeta(annotations)
            .getRepeatableAnnotations(annotationType);
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
     * 获取可重复的合成注解
     *
     * @param annotatedEle   注解元素
     * @param annotationType 注解类型
     * @return java.util.List<T>
     */
    public static <T extends Annotation> List<T> findRepeatableSynthesizedAnnotations(AnnotatedElement annotatedEle, Class<T> annotationType) {
        List<Annotation> annotations = scanTypeHierarchy(annotatedEle);
        return aggregatingRepeatableFromAnnotationWithMeta(annotations)
            .getRepeatableAnnotations(annotationType);
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
     * 获取未被代理前的注解类
     *
     * @param annotation 注解类
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getSourceAnnotation(T annotation) {
        while (!isSynthesizedAnnotation(annotation)) {
            annotation = (T)((ProxiedSynthesizedAnnotation)annotation).getSynthesizedAnnotation().getAnnotation();
        }
        return annotation;
    }

    /**
     * 更新注解对象的属性值
     *
     * @param annotation 注解
     * @param attributes 要更新的属性与新属性值
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T updateAttributeValue(T annotation, Map<String, Object> attributes) {
        annotation = getSourceAnnotation(annotation);
        SynthesizedAnnotation synthesizedAnnotation = new GenericSynthesizedAnnotation<>(
            annotation.annotationType(), annotation, 0, 0
        );
        attributes.forEach((a, v) -> synthesizedAnnotation.replaceAttribute(a, attribute -> new FixedValueAnnotationAttribute(attribute, v)));
        return (T)SynthesizedAnnotationInvocationHandler.createProxy(annotation.annotationType(), synthesizedAnnotation);
    }

    // ============================ 私有方法 ============================

    /**
     * 使用扫描器扫描器注解
     */
    private static List<Annotation> scanByScanner(AnnotatedElement annotatedElement, AnnotationScanner scanner) {
        return ObjectUtil.isNull(annotatedElement) ? Collections.emptyList() : scanner.getAnnotations(annotatedElement);
    }

    /**
     * 对指定注解对象及其元注解进行聚合
     *
     * @param annotations 注解对象
     * @return 聚合注解
     */
    private static RepeatableContainerAnnotation aggregatingRepeatableFromAnnotationWithMeta(List<Annotation> annotations) {
        return new RepeatableSynthesizedAggregateAnnotation(annotations, AnnotationScanner.DIRECTLY_AND_META_ANNOTATION);
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
     * 批量聚合注解，然后从每一个聚合的注解中获取合成注解
     */
    private static <T extends Annotation> List<T> synthesizeForEachAggregateAnnotation(Collection<Annotation> annotations, Class<T> annotationType) {
        return annotations.stream()
            .map(Collections::singletonList)
            .map(AnnotatedElementUtils::aggregatingFromAnnotationWithMeta)
            .map(a -> a.synthesize(annotationType))
            .filter(ObjectUtil::isNotNull)
            .collect(Collectors.toList());
    }

}
