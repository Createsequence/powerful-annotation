package top.xiajibagao.powerfulannotation.synthesis;

import lombok.AccessLevel;
import lombok.Getter;
import top.xiajibagao.powerfulannotation.annotation.GenericHierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.proxy.AnnotationProxyFactory;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.synthesis.resolver.SyntheticAnnotationResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>{@link AnnotationSynthesizer}的基本实现，用于根据注册到实例中的注解，
 * 根据一些规则“合成”具有与原始属性不一样的值的合成注解。
 *
 * <p>一个可用的注解处理器通常需要经过下述过程完成初始化：
 * <ol>
 *     <li>创建一个实例，并为其指定选择器{@link HierarchySelector}与注解解析器{@link SyntheticAnnotationResolver}；</li>
 *     <li>使用方法{@link #accept(int, int, Annotation)}或{@link #accept(HierarchicalAnnotation)}方法向实例注册注解对象；</li>
 *     <li>
 *         若存在多个类型相同的注解对象，则会经过{@link HierarchySelector}的筛选，保留唯一一个有效的注解，
 *         并注册到{@link #synthesizedAnnotationMap}中，该集合一种注解类型有且仅有一个对应的注解对象；
 *     </li>
 *     <li>使用{@link SyntheticAnnotationResolver}对注册了的注解对象进行解析，这个过程通常用于完成注解的各种别名属性的处理；</li>
 * </ol>
 *
 * <p>完成上述初始化后，即可通过{@link #synthesize(Class)}获取指定类型的合成注解。<br />
 * 该操作将以被注册且被处理后的对应类型{@link HierarchicalAnnotation}为原料，通过{@link AnnotationProxyFactory}生成一个代理注解。
 * 生成的代理注解类型与原始类型一致，但是获取属性值时，会获取到经过处理后的合成注解中的属性值，
 * 它们可能会根据指定的规则返回与原始属性不一样的值。
 *
 * @author huangchengxing
 * @see SyntheticAnnotationResolver
 */
public class GenericAnnotationSynthesizer implements AnnotationSynthesizer {

    /**
     * 注解解析器
     */
    private final List<SyntheticAnnotationResolver> resolvers;

    /**
     * 是否已解析
     */
    private boolean resolved;

    /**
     * 待合成的注解
     */
    @Getter(AccessLevel.PROTECTED)
    private final Map<Class<? extends Annotation>, HierarchicalAnnotation<Annotation>> synthesizedAnnotationMap;
    
    /**
     * 注解选择器
     */
    private final HierarchySelector<HierarchicalAnnotation<Annotation>> selector;

    /**
     * 创建一个注解合成器
     *
     * @param resolvers 注解解析器
     * @param selector 注解选择器
     */
    public GenericAnnotationSynthesizer(
        Collection<SyntheticAnnotationResolver> resolvers,
        HierarchySelector<HierarchicalAnnotation<Annotation>> selector) {
        this.resolvers = resolvers.stream()
            .sorted(Comparator.comparing(SyntheticAnnotationResolver::order))
            .collect(Collectors.toList());
        this.synthesizedAnnotationMap = new LinkedHashMap<>();
        this.selector = selector;
        this.resolved = false;
    }

    /**
     * 向当前实例注册注解，若该类型的注解已经在{@link #synthesizedAnnotationMap}中存在，
     * 则使用{@link #selector}两注解进行选择，并仅保留最终有效的注解 <br />
     * <b>注意：注解注册的先后顺序将会影响到属性的“合并”</b>
     *
     * @param verticalIndex 垂直索引。一般表示与扫描器扫描的{@link AnnotatedElement}相隔的层级层次。默认从1开始
     * @param horizontalIndex 水平索引，一般用于衡量两个注解对象之间被扫描到的先后顺序。默认从1开始
     * @param annotation 被扫描到的注解对象
     */
    @Override
    public void accept(int verticalIndex, int horizontalIndex, Annotation annotation) {
        HierarchicalAnnotation<Annotation> hierarchicalAnnotation = new GenericHierarchicalAnnotation<>(
            annotation, this, verticalIndex, horizontalIndex
        );
        accept(hierarchicalAnnotation);
    }

    /**
     * 向当前实例注册注解，若该类型的注解已经在{@link #synthesizedAnnotationMap}中存在，
     * 则使用{@link #selector}两注解进行选择，并仅保留最终有效的注解 <br />
     * <b>注意：注解注册的先后顺序将会影响到属性的“合并”</b>
     *
     * @param hierarchicalAnnotation 注解
     */
    @Override
    public void accept(HierarchicalAnnotation<Annotation> hierarchicalAnnotation) {
        registerAnnotation(hierarchicalAnnotation);
        this.resolved = false;
    }

    /**
     * 向当前实例注册注解，若该类型的注解已经在{@link #synthesizedAnnotationMap}中存在，
     * 则使用{@link #selector}两注解进行选择，并仅保留最终有效的注解
     */
    private void registerAnnotation(HierarchicalAnnotation<Annotation> annotation) {
        Class<? extends Annotation> type = annotation.annotationType();
        HierarchicalAnnotation<Annotation> old = synthesizedAnnotationMap.get(type);
        if (Objects.isNull(old)) {
            synthesizedAnnotationMap.put(type, annotation);
            return;
        }
        synthesizedAnnotationMap.put(type, selector.choose(old, annotation));
    }

    /**
     * 完成解析
     */
    public synchronized void resolve() {
        if (this.resolved) {
            return;
        }
        List<HierarchicalAnnotation<Annotation>> annotations = getAllAnnotation().stream()
            .sorted(Comparator.comparing(HierarchicalAnnotation<Annotation>::getVerticalIndex)
                .thenComparing(HierarchicalAnnotation<Annotation>::getHorizontalIndex)
            ).collect(Collectors.toList());
        resolvers.forEach(resolver -> resolver.resolve(annotations, this));
        this.resolved = true;
    }

    /**
     * 获取待合成的注解
     *
     * @param annotationType 注解类型
     * @return 待合成的注解
     */
    @Override
    public HierarchicalAnnotation<Annotation> getAnnotation(Class<?> annotationType) {
        return synthesizedAnnotationMap.get(annotationType);
    }

    /**
     * 是否支持合成指定注解
     *
     * @param annotationType 注解类型
     * @return 是否
     */
    @Override
    public boolean support(Class<? extends Annotation> annotationType) {
        return synthesizedAnnotationMap.containsKey(annotationType);
    }

    /**
     * 获取全部待合成的注解
     *
     * @return 全部待合成的注解
     */
    @Override
    public Collection<HierarchicalAnnotation<Annotation>> getAllAnnotation() {
        return synthesizedAnnotationMap.values();
    }

    /**
     * 基于一组具有一定关系的注解，对其进行“合成”，并最终返回一个指定类型的合成注解 <br />
     * <b>调用该方法前，需要保证至少调用过一次{@link #resolve()}</b>
     *
     * @param annotationType 注解类型
     * @param <T> 注解类型
     * @return 合成注解
     */
    @Override
    public <T extends Annotation> T synthesize(Class<T> annotationType) {
        if (!resolved) {
            resolve();
        }
        HierarchicalAnnotation<Annotation> annotation = synthesizedAnnotationMap.get(annotationType);
        return Objects.isNull(annotation) ?
            null : AnnotationProxyFactory.get(annotationType, annotation);
    }

}
