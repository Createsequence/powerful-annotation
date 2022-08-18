package top.xiajibagao.powerfulannotation.annotation;

import top.xiajibagao.powerfulannotation.helper.Annotations;
import top.xiajibagao.powerfulannotation.helper.AssertUtils;
import top.xiajibagao.powerfulannotation.helper.CollUtils;
import top.xiajibagao.powerfulannotation.helper.HierarchySelector;
import top.xiajibagao.powerfulannotation.repeatable.RepeatableMappingRegistry;
import top.xiajibagao.powerfulannotation.scanner.AnnotationFilter;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>表示一组处于聚合状态，且彼此之间具有层级联系的注解形成的聚合。
 * 聚合中的注解将以{@link AnnotationTypeMapping}的形式存在，
 * 并维持彼此之间的父子/先后关系。
 *
 * <p>该对象可由{@link Class}上的注解与元注解解析而来，或通过具备顺序的注解对象合成:
 * <ul>
 *     <li>当基于注解对象构建时：实例表示一颗以注解为根节点，该注解的各层元注解为子节点的多叉树；</li>
 *     <li>当基于类构建时：实例表示以类上直接声明的注解为根节点，注解各自用于的各层元注解为子节点的树构成的森林；</li>
 *     <li>当基于注解数组构建时：实例按注解中注解对象的顺序形成的链表；</li>
 * </ul>
 *
 * @author huangchengxing
 */
public class AnnotationTypeMappings implements AnnotationAggregation<AnnotationTypeMapping, AnnotationTypeMapping> {

    private final Map<Class<? extends Annotation>, AnnotationTypeMapping> mappings;
    private final AnnotationFilter annotationFilter;
    private int total;
    private boolean inited;

    // ======================== factory ========================

    /**
     * 根据类上的注解及元注解创建一个{@link AnnotationTypeMappings}
     * <pre>
     *                            |-> annotation...
     *           |-> annotation-> |
     *           |   (root)       |-> annotation...
     *           |
     * type ===> |
     *           |                |-> annotation...
     *           |-> annotation-> |
     *               (root)       |-> annotation...
     * </pre>
     *
     * @param type 类型
     * @param annotationFilter 注解过滤器
     * @return 注解聚合
     */
    // TODO 添加元注解缓存，避免重复解析
    public static AnnotationTypeMappings from(
        Class<?> type, AnnotationFilter annotationFilter) {
        AssertUtils.notNull(type, "type must not null");
        AssertUtils.notNull(annotationFilter, "annotationFilter must not null");
        AnnotationTypeMappings mappings = new AnnotationTypeMappings(annotationFilter);
        mappings.initFromTypeAnnotationMeta(type);
        return mappings;
    }

    /**
     * 根据注解对象以及注解上的元注解创建一个{@link AnnotationTypeMappings}
     * <pre>
     *                                 |-> annotation...
     *               |-> annotation -> |
     *               |                 |-> annotation...
     *               |
     * annotation -> |
     *  (root)       |                 |-> annotation...
     *               |-> annotation -> |
     *                                 |-> annotation...
     * </pre>
     *
     * @param annotation 注解
     * @param annotationFilter 注解过滤器
     * @return 注解聚合
     */
    public static AnnotationTypeMappings from(Annotation annotation, AnnotationFilter annotationFilter) {
        AssertUtils.notNull(annotation, "type must not null");
        AssertUtils.notNull(annotationFilter, "annotationFilter must not null");
        AnnotationTypeMappings mappings = new AnnotationTypeMappings(annotationFilter);
        mappings.initFromAnnotationMeta(annotation);
        return mappings;
    }

    /**
     * 根据注解对象创建一个{@link AnnotationTypeMappings}
     * <pre>
     *     annotation -> annotation -> annotation...
     *       (root)
     * </pre>
     *
     * @param annotations 注解
     * @return 注解聚合
     */
    public static AnnotationTypeMappings from(Annotation... annotations) {
        AnnotationTypeMappings mappings = new AnnotationTypeMappings(AnnotationFilter.FILTER_NOTHING);
        if (CollUtils.isNotEmpty(annotations)) {
            mappings.initFromAnnotationArray(annotations);
        }
        return mappings;
    }

    // ======================== init ========================

    /**
     * 创建一个注解聚合，不过并不进行初始化
     *
     * @param annotationFilter 注解过滤器
     */
    protected AnnotationTypeMappings(AnnotationFilter annotationFilter) {
        this.mappings = new LinkedHashMap<>();
        this.annotationFilter = annotationFilter;
        this.total = AnnotationScanner.HORIZONTAL_INDEX_START_POINT;
        this.inited = false;
    }

    /**
     * 按广度优先解析{@code source}上的注解及元注解，
     * 将其转为{@link AnnotationTypeMapping}并完成初始化，
     * 注解与其元注解将会形成树结构。
     *
     * @param source 类对象
     */
    protected void initFromTypeAnnotationMeta(Class<?> source) {
        checkInited();
        Deque<AnnotationTypeMapping> deque = new LinkedList<>();
        addMetaMappingFromType(null, source, deque);
        while (!deque.isEmpty()) {
            AnnotationTypeMapping mapping = deque.removeFirst();
            addMetaMappingFromType(mapping, mapping.annotationType(), deque);
        }
        this.inited = true;
    }

    /**
     * 按广度优先解析{@code source}上的注解及元注解，
     * 将其转为{@link AnnotationTypeMapping}并完成初始化，
     * 注解与其元注解将会形成树结构。
     *
     * @param annotation 注解对象
     */
    protected void initFromAnnotationMeta(Annotation annotation) {
        checkInited();
        // 获取根节点
        Deque<AnnotationTypeMapping> deque = new LinkedList<>();
        addMapping(deque, null, annotation);
        AnnotationTypeMapping root = deque.removeFirst();
        // 获取元注解
        addMetaMappingFromType(root, root.annotationType(), deque);
        while (!deque.isEmpty()) {
            AnnotationTypeMapping mapping = deque.removeFirst();
            addMetaMappingFromType(mapping, mapping.annotationType(), deque);
        }
        this.inited = true;
    }

    /**
     * 将{@code annotations}依次转为{@link AnnotationTypeMapping}并完成初始化，
     * 输入的注解间将根据顺序构成链表结构。
     *
     * @param annotations 注解对象
     */
    protected void initFromAnnotationArray(Annotation... annotations) {
        checkInited();
        Deque<AnnotationTypeMapping> deque = new LinkedList<>();
        for (Annotation annotation : annotations) {
            AnnotationTypeMapping prev = deque.peek();
            addMapping(deque, prev, annotation);
        }
        this.inited = true;
    }

    // ======================== implement ========================

    /**
     * 获取指定类型的注解
     *
     * @param annotationType 注解类型
     * @param selector 选择器，当有多个符合的目标时，使用该选择进行过滤
     * @param filter 过滤器
     * @return 注解对象
     */
    @Override
    public Optional<AnnotationTypeMapping> getAnnotation(
        Class<?> annotationType, HierarchySelector<AnnotationTypeMapping> selector, AnnotationFilter filter) {
        return mappings.values().stream()
            .filter(t -> Objects.equals(t.annotationType(), annotationType))
            .filter(filter)
            .reduce(selector::choose);
    }

    /**
     * 获取全部注解对象
     *
     * @return 注解对象
     */
    @Override
    public List<AnnotationTypeMapping> getAnnotations() {
        return new ArrayList<>(mappings.values());
    }

    /**
     * 获取可重复注解
     *
     * @param annotationType 注解类型
     * @param filter 过滤器
     * @param repeatableMappingRegistry 注册表
     * @return 可重复注解
     */
    @Override
    public <A extends Annotation> List<A> getRepeatableAnnotations(
        Class<A> annotationType, AnnotationFilter filter, RepeatableMappingRegistry repeatableMappingRegistry) {
        mappings.keySet().forEach(repeatableMappingRegistry::register);
        return getAnnotations().stream()
            .map(AnnotationTypeMapping::getAnnotation)
            .map(t -> repeatableMappingRegistry.getElementsFromContainer(t, annotationType))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    /**
     * 指定类型注解是否存在
     *
     * @param annotationType 注解类型
     * @return 是否
     */
    @Override
    public boolean isPresent(Class<?> annotationType) {
        return mappings.containsKey(annotationType);
    }

    /**
     * 当前聚合是否为空
     *
     * @return 是否
     */
    @Override
    public boolean isEmpty() {
        return total == 0;
    }

    // ======================== private ========================

    /**
     * 从注解类型上解析器元注解
     */
    private void addMetaMappingFromType(AnnotationTypeMapping source, Class<?> type, Deque<AnnotationTypeMapping> deque) {
        Annotation[] annotations = Annotations.getDeclaredAnnotations(type);
        for (Annotation annotation : annotations) {
            if (!isNeedMapping(annotation)) {
                continue;
            }
            addMapping(deque, source, annotation);
        }
    }

    /**
     * 将注解转为{@link AnnotationTypeMapping}，并添加到{@link #mappings}与{@code deque}中
     */
    private void addMapping(
        Deque<AnnotationTypeMapping> deque, AnnotationTypeMapping source, Annotation annotation) {
        AnnotationTypeMapping mapping = new AnnotationTypeMapping(
            annotation, source, AnnotationScanner.VERTICAL_INDEX_START_POINT, total++
        );
        deque.addLast(mapping);
        mappings.put(mapping.annotationType(), mapping);
    }
    
    /**
     * 该注解是否需要映射
     */
    private boolean isNeedMapping(Annotation annotation) {
        return annotationFilter.test(annotation)
            && !mappings.containsKey(annotation.annotationType());
    }

    /**
     * 校验是否已经初始化
     */
    private void checkInited() {
        AssertUtils.isFalse(inited, "mapping already initialized");
    }

}
