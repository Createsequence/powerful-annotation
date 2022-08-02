package top.xiajibagao.powerfulannotation.helper;

import top.xiajibagao.powerfulannotation.aggerate.AnnotationAggregator;
import top.xiajibagao.powerfulannotation.aggerate.GenericAnnotationAggregator;
import top.xiajibagao.powerfulannotation.annotation.proxy.AnnotationProxyFactory;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableBy;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingParser;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingRegistry;
import top.xiajibagao.powerfulannotation.repeatable.SimpleRepeatableMappingRegistry;
import top.xiajibagao.powerfulannotation.scanner.AnnotationFilter;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;
import top.xiajibagao.powerfulannotation.scanner.AnnotationSearchMode;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationCollector;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationFinder;
import top.xiajibagao.powerfulannotation.synthesis.AnnotationSynthesizer;
import top.xiajibagao.powerfulannotation.synthesis.GenericAnnotationSynthesizer;
import top.xiajibagao.powerfulannotation.synthesis.Link;
import top.xiajibagao.powerfulannotation.synthesis.RelationType;
import top.xiajibagao.powerfulannotation.synthesis.resolver.AliasAttributeResolver;
import top.xiajibagao.powerfulannotation.synthesis.resolver.CoveredAttributeResolver;
import top.xiajibagao.powerfulannotation.synthesis.resolver.MirrorAttributeResolver;
import top.xiajibagao.powerfulannotation.synthesis.resolver.SyntheticAnnotationResolver;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.ref.WeakReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>注解工具类，提供从{@link AnnotatedElement}及其层级结构中获取注解的方法
 *
 * <h3>注解获取支持</h3>
 * <p>工具类支持按<em>get</em>语义或<em>find</em>语义对{@link AnnotatedElement}上的元素进行查找：<br />
 * <p><strong>get</strong>：指仅从{@link AnnotatedElement#getDeclaredAnnotations()}的范围中进行查找。
 * <p><strong>find</strong>：指从{@link AnnotatedElement}及其<em>层级结构</em>中进行查找，
 * 当查找的{@link AnnotatedElement}不同时，查找对应的层级结构的定义也将有所不同：
 * <ul>
 *     <li>
 *         当元素为非注解类，即为{@link Class}、且{@link Class#isAnnotation()}返回{@code false}时，
 *         此处层级结构即指<strong>类本身与其父类、父接口共同构成的层级结构</strong>，
 *         <em>find</em>将查找层级结构中类、接口声明的注解；
 *     </li>
 *     <li>
 *         当元素为注解类，即为{@link Class}、且{@link Class#isAnnotation()}返回{@code true}时，
 *         此处层级结构指<strong>注解类及其元注解构成的层级结构</strong>，
 *          <em>find</em>将查找该注解层级结构中的元注解，并对其进行处理；
 *     </li>
 *     <li>
 *         当元素为{@link Method}时，此处层级结构指<strong>声明该方法的类的层级结构</strong>，
 *         <em>find</em>将从层级结构中寻找与该方法签名相同的非桥接方法，并对其进行扫描；
 *     </li>
 *     <li>
 *         当元素为{@link Field}时，此处层级结构指<strong>声明该属性的类的层级结构</strong>，
 *         <em>find</em>将从层级结构中寻找该属性，并对其注解进行扫描；
 *     </li>
 *     <li>当元素不为上述四者时，则认为其层级结构仅有其本身一层，将只直接扫描器该元素上的注解；</li>
 * </ul>
 * 若有更细粒度的方法需求，可以参考{@link AnnotationSearchMode}或{@link AnnotationScanner}相关方法。
 *
 * <h3>元注解支持</h3>
 * 注解工具类支持对元注解进行处理。用户可根据可根据<em>indirect</em>与<em>direct</em>区分方法是否支持处理元注解。
 * <ul>
 *     <li>indirect：指方法处理获取到的注解，并还会处理元注解；</li>
 *     <li>direct：指方法仅处理获取到的注解，不会再处理其元注解；</li>
 * </ul>
 * 需要注意的是，此处对<em>indirect</em>与<em>direct</em>的定义与{@link AnnotatedElement}中规定的并不相符，
 * 因此{@link AnnotatedElement}中对注解作用域的定义在当前工具类中无效。<br />
 * 若有更细粒度的方法需求，可以参考{@link AnnotationSearchMode}或{@link AnnotationScanner}相关方法。
 *
 * <h3>可重复注解注解</h3>
 * 注解工具类支持从层级结构中获取可重复的注解。通过名称带有<em>AllRepeatable</em>关键字的方法，
 * 用户可以从元素中获取全部直接或间接存在的、基于{@link Repeatable}或{@link RepeatableBy}实现的可重复注解。<br />
 * 若有更细粒度的方法需求，可以参考{@link AnnotationAggregator}相关方法。
 *
 * <h3>合成注解</h3>
 * <p>工具类支持将一批具有相关的注解对象“合并”为一个具有特殊属性的合成注解，
 * 此类方法包括<em>synthesize</em>以及所有方法名以<em>SynthesizedAnnotation</em>结尾的方法。<br />
 * 合成注解一般用于合成一个指定的注解对象以及其元注解，
 * 合成注解的属性将根据属性上的{@link Link}及其扩展注解而于源注解的属性有所不同，
 * 根据{@link Link#type()}指定的{@link RelationType}，将会实现诸如属性互为镜像、属性互为别名或者属性互相覆盖的情况。<br />
 * 若有更细粒度的方法需求，可以参考{@link AnnotationSynthesizer}相关方法。
 *
 * @author huangchengxing
 * @see AnnotationSearchMode
 * @see AnnotationSynthesizer
 * @see SyntheticAnnotationResolver
 * @see AnnotationAggregator
 * @see RepeatableMappingRegistry
 */
public class Annotations {

    private Annotations() {
    }

    /**
     * 默认的{@link MirrorAttributeResolver}
     */
    private static final SyntheticAnnotationResolver DEFAULT_MIRROR_ATTRIBUTE_RESOLVER = new MirrorAttributeResolver();

    /**
     * 默认的{@link AliasAttributeResolver}
     */
    private static final SyntheticAnnotationResolver DEFAULT_ALIAS_ATTRIBUTE_RESOLVER = new AliasAttributeResolver();

    /**
     * 默认的{@link CoveredAttributeResolver}
     */
    private static final SyntheticAnnotationResolver COVERED_ATTRIBUTE_RESOLVER = new CoveredAttributeResolver(false);

    /**
     * 注解缓存
     */
    private static final Map<AnnotatedElement, WeakReference<Annotation[]>> ANNOTATED_ELEMENT_MAP = new ConcurrentHashMap<>(36);

    /**
     * 获取直接声明的注解
     *
     * @param element 注解元素
     * @return 直接声明的注解
     */
    public static Annotation[] getDeclaredAnnotations(AnnotatedElement element) {
        return Optional.ofNullable(ANNOTATED_ELEMENT_MAP.get(element))
            .map(WeakReference::get)
            .orElseGet(() -> {
                Annotation[] annotations = Objects.isNull(element) ?
                    emptyAnnotations() : element.getAnnotations();
                ANNOTATED_ELEMENT_MAP.put(element, new WeakReference<>(annotations));
                return annotations;
            });
    }
    
    /**
     * 获取直接声明的注解
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @return 获取直接声明的注解
     */
    public static <T extends Annotation> T getDeclaredAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return Objects.isNull(element) ? null : element.getDeclaredAnnotation(annotationType);
    }

    /**
     * 获取一个空注解数组
     *
     * @return 空注解数组
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T[] emptyAnnotations() {
        return (T[])new Annotation[0];
    }

    /**
     * 方法是否为注解属性方法。 <br>
     * 方法无参数，且有返回值的方法认为是注解属性的方法。
     * 不包括{@code toString}, {@code hashCode}与{@code annotationType}
     *
     * @param method 方法
     */
    public static boolean isAttributeMethod(Method method) {
        return method.getParameterCount() == 0
            && method.getReturnType() != void.class
            && !"hashCode".equals(method.getName())
            && !"toString".equals(method.getName())
            && !"annotationType".equals(method.getName());
    }

    // =========================== get & direct ===========================

    /**
     * 从元素直接声明的注解中获取指定注解
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 注解
     * @see AnnotationSearchMode#SELF_AND_DIRECT
     */
    public static <T extends Annotation> T getDirectAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return AnnotationSearchMode.SELF_AND_DIRECT
            .getAnnotation(element, annotationType);
    }

    /**
     * 从元素直接声明的注解中获取指定注解
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 注解
     * @see AnnotationSearchMode#SELF_AND_DIRECT
     */
    public static <T extends Annotation> List<T> getAllDirectAnnotations(AnnotatedElement element, Class<T> annotationType) {
        return AnnotationSearchMode.SELF_AND_DIRECT
            .getAnnotations(element, annotationType);
    }

    /**
     * 从元素直接声明的注解中获取指定可重复注解
     *
     * @param element 查找的元素
     * @param annotationType 可重复注解类型
     * @return 注解
     * @see AnnotationSearchMode#SELF_AND_DIRECT
     */
    public static <T extends Annotation> List<T> getAllDirectRepeatableAnnotations(
        AnnotatedElement element, Class<T> annotationType) {
        AnnotationAggregator<AnnotatedElement> aggregator = getAnnotationAggregator(element);
        AnnotationSearchMode.SELF_AND_DIRECT.scan(element, aggregator, AnnotationFilter.FILTER_JAVA);
        return aggregator.getRepeatableAnnotations(annotationType);
    }

    /**
     * 判断注解是否在元素直接声明的注解中存在
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 是否存在
     * @see AnnotationSearchMode#SELF_AND_DIRECT
     */
    public static boolean isDirectAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return AnnotationSearchMode.SELF_AND_DIRECT
            .isAnnotationPresent(element, annotationType);
    }

    // =========================== get & indirect ===========================

    /**
     * 从元素直接声明的注解及元注解中获取指定注解
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 注解
     * @see AnnotationSearchMode#SELF_AND_INDIRECT
     */
    public static <T extends Annotation> T getIndirectAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return AnnotationSearchMode.SELF_AND_INDIRECT
            .getAnnotation(element, annotationType);
    }

    /**
     * 从元素直接声明的注解及元注解中获取指定注解
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 注解
     * @see AnnotationSearchMode#SELF_AND_INDIRECT
     */
    public static <T extends Annotation> List<T> getAllIndirectAnnotations(AnnotatedElement element, Class<T> annotationType) {
        return AnnotationSearchMode.SELF_AND_INDIRECT
            .getAnnotations(element, annotationType);
    }

    /**
     * 从元素直接声明的注解及元注解中获取指定可重复注解
     *
     * @param element 查找的元素
     * @param annotationType 可重复注解类型
     * @return 注解
     * @see AnnotationSearchMode#SELF_AND_INDIRECT
     */
    public static <T extends Annotation> List<T> getAllIndirectRepeatableAnnotations(
        AnnotatedElement element, Class<T> annotationType) {
        AnnotationAggregator<AnnotatedElement> aggregator = getAnnotationAggregator(element);
        AnnotationSearchMode.SELF_AND_INDIRECT.scan(element, aggregator, AnnotationFilter.FILTER_JAVA);
        return aggregator.getRepeatableAnnotations(annotationType);
    }

    /**
     * 判断注解是否在元素直接声明的注解及元注解中存在
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 是否存在
     * @see AnnotationSearchMode#SELF_AND_INDIRECT
     */
    public static boolean isIndirectAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return AnnotationSearchMode.SELF_AND_INDIRECT
            .isAnnotationPresent(element, annotationType);
    }

    // =========================== find & direct ===========================

    /**
     * 从元素的层级结构中获取指定注解
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 注解
     * @see AnnotationSearchMode#TYPE_HIERARCHY_AND_DIRECT
     */
    public static <T extends Annotation> T findDirectAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return AnnotationSearchMode.TYPE_HIERARCHY_AND_DIRECT
            .getAnnotation(element, annotationType);
    }

    /**
     * 从元素的层级结构中获取指定注解
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 注解
     * @see AnnotationSearchMode#TYPE_HIERARCHY_AND_DIRECT
     */
    public static <T extends Annotation> List<T> findAllDirectAnnotations(AnnotatedElement element, Class<T> annotationType) {
        return AnnotationSearchMode.TYPE_HIERARCHY_AND_DIRECT
            .getAnnotations(element, annotationType);
    }

    /**
     * 从元素的层级结构中获取指定可重复注解
     *
     * @param element 查找的元素
     * @param annotationType 可重复注解类型
     * @return 注解
     * @see AnnotationSearchMode#TYPE_HIERARCHY_AND_DIRECT
     */
    public static <T extends Annotation> List<T> findAllDirectRepeatableAnnotations(
        AnnotatedElement element, Class<T> annotationType) {
        AnnotationAggregator<AnnotatedElement> aggregator = getAnnotationAggregator(element);
        AnnotationSearchMode.TYPE_HIERARCHY_AND_DIRECT.scan(element, aggregator, AnnotationFilter.FILTER_JAVA);
        return aggregator.getRepeatableAnnotations(annotationType);
    }

    /**
     * 判断注解是否在元素的层级结构中存在
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 是否存在
     * @see AnnotationSearchMode#TYPE_HIERARCHY_AND_DIRECT
     */
    public static boolean isDirectAnnotationFound(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return AnnotationSearchMode.TYPE_HIERARCHY_AND_INDIRECT
            .isAnnotationPresent(element, annotationType);
    }

    // =========================== find & indirect ===========================

    /**
     * 从元素的层级结构中及其元注解中获取指定注解
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 注解
     * @see AnnotationSearchMode#TYPE_HIERARCHY_AND_INDIRECT
     */
    public static <T extends Annotation> T findIndirectAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return AnnotationSearchMode.TYPE_HIERARCHY_AND_INDIRECT
            .getAnnotation(element, annotationType);
    }

    /**
     * 从元素的层级结构中获取指定可重复注解
     *
     * @param element 查找的元素
     * @param annotationType 可重复注解类型
     * @return 注解
     * @see AnnotationSearchMode#TYPE_HIERARCHY_AND_DIRECT
     */
    public static <T extends Annotation> List<T> findAllIndirectRepeatableAnnotations(
        AnnotatedElement element, Class<T> annotationType) {
        AnnotationAggregator<AnnotatedElement> aggregator = getAnnotationAggregator(element);
        AnnotationSearchMode.TYPE_HIERARCHY_AND_DIRECT.scan(element, aggregator, AnnotationFilter.FILTER_JAVA);
        return aggregator.getRepeatableAnnotations(annotationType);
    }

    /**
     * 从元素的层级结构及其元注解中获取指定注解
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 注解
     * @see AnnotationSearchMode#TYPE_HIERARCHY_AND_INDIRECT
     */
    public static <T extends Annotation> List<T> findAllIndirectAnnotations(AnnotatedElement element, Class<T> annotationType) {
        return AnnotationSearchMode.TYPE_HIERARCHY_AND_INDIRECT
            .getAnnotations(element, annotationType);
    }

    /**
     * 判断注解是否在元素的层级结构及其元注解中存在
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 是否存在
     * @see AnnotationSearchMode#TYPE_HIERARCHY_AND_INDIRECT
     */
    public static boolean isIndirectAnnotationFound(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return AnnotationSearchMode.TYPE_HIERARCHY_AND_INDIRECT
            .isAnnotationPresent(element, annotationType);
    }

    // =========================== synthesize ===========================

    /**
     * 该注解是否为一个合成注解
     *
     * @param annotation 注解对象
     * @return 是否
     */
    public static boolean isSynthesized(Annotation annotation) {
        return AnnotationProxyFactory.isProxied(annotation);
    }
    
    /**
     * 若注解为一个合成注解，则返回该注解未被合成前的原始注解
     *
     * @param annotation 注解对象
     * @return 原始注解
     */
    public static <T extends Annotation> T getNotSynthesized(T annotation) {
        return AnnotationProxyFactory.getOriginal(annotation);
    }

    /**
     * 将一个注解及其元注解合成为指定类型的合成注解
     *
     * @param annotation     待合成的注解
     * @param annotationType 注解类型
     * @return 合成注解
     */
    public static <T extends Annotation> T synthesize(Annotation annotation, Class<T> annotationType) {
        AnnotationSynthesizer synthesizer = getAnnotationSynthesizer();
        synthesizer.accept(0, 0, annotation);
        AnnotationSearchMode.SELF_AND_INDIRECT.scan(annotation.annotationType(), synthesizer, AnnotationFilter.FILTER_JAVA);
        return synthesizer.support(annotationType) ?
            synthesizer.synthesize(annotationType) : null;
    }

    /**
     * 将一批注解合成为指定类型的合成注解
     *
     * @param annotationType 注解类型
     * @param annotations 待合成的注解
     * @return 合成注解
     */
    public static <T extends Annotation> T synthesize(Class<T> annotationType, Annotation... annotations) {
        AnnotationSynthesizer synthesizer = getAnnotationSynthesizer();
        for (int i = 0; i < annotations.length; i++) {
            Annotation annotation = annotations[i];
            if (Objects.isNull(annotation)) {
                continue;
            }
            synthesizer.accept(0, i, annotation);
        }
        return synthesizer.support(annotationType) ? synthesizer.synthesize(annotationType) : null;
    }

    /**
     * 遍历元素的直接声明的注解，并将扫描到的注解与其元注解“合并”，
     * 若“合并”后的注解支持合成指定类型的合成注解，则返回该合成注解
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 合成注解
     * @see AnnotationSearchMode#SELF_AND_DIRECT
     */
    public static <T extends Annotation> T getSynthesizedAnnotation(AnnotatedElement element, Class<T> annotationType) {
        AnnotationFinder<AnnotationSynthesizer> finder = getSynthesizedAnnotationFinder(annotationType);
        AnnotationSearchMode.SELF_AND_DIRECT.scan(element, finder, AnnotationFilter.FILTER_JAVA);
        return finder.isFound() ?
            finder.getTarget().synthesize(annotationType) : null;
    }

    /**
     * 遍历元素的直接声明的注解，并将扫描到的注解与其元注解“合并”，然后返回所有指定类型的合成注解
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 合成注解
     * @see AnnotationSearchMode#SELF_AND_DIRECT
     */
    public static <T extends Annotation> List<T> getAllSynthesizedAnnotations(AnnotatedElement element, Class<T> annotationType) {
        AnnotationCollector<AnnotationSynthesizer> collector = getSynthesizedAnnotationCollector();
        AnnotationSearchMode.SELF_AND_DIRECT.scan(element, collector, AnnotationFilter.FILTER_JAVA);
        return collector.getTargets().stream()
            .filter(synthesizer -> synthesizer.support(annotationType))
            .map(synthesizer -> synthesizer.synthesize(annotationType))
            .collect(Collectors.toList());
    }

    /**
     * 遍历元素的层级结构，并将扫描到的注解与其元注解“合并”，
     * 若“合并”后的注解支持合成指定类型的合成注解，则返回该合成注解
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 合成注解
     * @see AnnotationSearchMode#TYPE_HIERARCHY_AND_DIRECT
     */
    public static <T extends Annotation> T findSynthesizedAnnotation(AnnotatedElement element, Class<T> annotationType) {
        AnnotationFinder<AnnotationSynthesizer> finder = getSynthesizedAnnotationFinder(annotationType);
        AnnotationSearchMode.TYPE_HIERARCHY_AND_DIRECT.scan(element, finder, AnnotationFilter.FILTER_JAVA);
        return finder.isFound() ?
            finder.getTarget().synthesize(annotationType) : null;
    }

    /**
     * 遍历元素的层级结构，并将扫描到的注解与其元注解“合并”，然后返回所有指定类型的合成注解
     *
     * @param element 查找的元素
     * @param annotationType 注解类型
     * @return 合成注解
     * @see AnnotationSearchMode#TYPE_HIERARCHY_AND_DIRECT
     */
    public static <T extends Annotation> List<T> findAllSynthesizedAnnotations(AnnotatedElement element, Class<T> annotationType) {
        AnnotationCollector<AnnotationSynthesizer> collector = getSynthesizedAnnotationCollector();
        AnnotationSearchMode.TYPE_HIERARCHY_AND_DIRECT.scan(element, collector, AnnotationFilter.FILTER_JAVA);
        return collector.getTargets().stream()
            .filter(synthesizer -> synthesizer.support(annotationType))
            .map(synthesizer -> synthesizer.synthesize(annotationType))
            .collect(Collectors.toList());
    }

    // =========================== private ===========================

    /**
     * 获取一个标准的注解聚合器
     */
    private static <T> AnnotationAggregator<T> getAnnotationAggregator(T root) {
        RepeatableMappingRegistry repeatableMappingRegistry = new SimpleRepeatableMappingRegistry(
            RepeatableMappingParser.STANDARD_REPEATABLE_MAPPING_PARSER,
            RepeatableMappingParser.REPEATABLE_BY_MAPPING_PARSER
        );
        return new GenericAnnotationAggregator<>(root, 0, 0, repeatableMappingRegistry);
    }

    /**
     * 获取一个默认配置的注解合成器
     */
    private static AnnotationSynthesizer getAnnotationSynthesizer() {
        return new GenericAnnotationSynthesizer(
            Arrays.asList(DEFAULT_MIRROR_ATTRIBUTE_RESOLVER, DEFAULT_ALIAS_ATTRIBUTE_RESOLVER, COVERED_ATTRIBUTE_RESOLVER),
            HierarchySelector.nearestAndOldestPriority()
        );
    }

    /**
     * 获取用于寻找合成注解的{@link AnnotationCollector}
     */
    private static AnnotationCollector<AnnotationSynthesizer> getSynthesizedAnnotationCollector() {
        return new AnnotationCollector<>((vi, hi, a) -> {
            AnnotationSynthesizer synthesizer = getAnnotationSynthesizer();
            synthesizer.accept(0, 0, a);
            AnnotationSearchMode.SELF_AND_INDIRECT.scan(a.annotationType(), synthesizer, AnnotationFilter.FILTER_JAVA);
            return synthesizer;
        });
    }

    /**
     * 获取用于寻找合成注解的{@link AnnotationFinder}
     */
    private static AnnotationFinder<AnnotationSynthesizer> getSynthesizedAnnotationFinder(Class<? extends Annotation> annotationType) {
        return new AnnotationFinder<>(
            (vi, hi, a) -> {
                AnnotationSynthesizer synthesizer = getAnnotationSynthesizer();
                synthesizer.accept(0, 0, a);
                AnnotationSearchMode.SELF_AND_INDIRECT.scan(a.annotationType(), synthesizer, AnnotationFilter.FILTER_JAVA);
                return synthesizer;
            },
            synthesizer -> synthesizer.support(annotationType)
        );
    }

}
