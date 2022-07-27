package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.powerfulannotation.helper.Hierarchical;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

/**
 * <p>用于为支持从具备层级结构中的元素扫描注解的扫描器提供基本实现。<br />
 * 该抽象类定义了针对对象层级结构中注解进行扫描的基本流程，
 * 并支持根据{@link ScanOptions}对各项参数进行配置。<br />
 * 实现类可以重写所需的非私有方法从而调整部分环节的执行逻辑。
 *
 * @author huangchengxing
 * @see ScanOptions
 */
@RequiredArgsConstructor
public abstract class AbstractTypeHierarchyScanner implements AnnotationScanner {

    /**
     * 扫描配置
     */
    protected final ScanOptions scanOptions;

    /**
     * 扫描指定元素上的注解
     *
     * @param element 待扫描的元素
     * @param processor 注解处理器
     * @param filter 过滤器，若为空则不过滤任何注解
     */
    @Override
    public void scan(AnnotatedElement element, AnnotationProcessor processor, AnnotationFilter filter) {
        if (ObjectUtil.isNull(element)) {
            return;
        }
        filter = ObjectUtil.defaultIfNull(filter, AnnotationFilter.FILTER_NOTHING);
        Context context = new Context(
            element, Hierarchical.VERTICAL_INDEX_START_POINT, Hierarchical.HORIZONTAL_INDEX_START_POINT, false
        );
        scanForElement(context, element, processor, filter);
    }

    /**
     * 扫描元素上的注解，若存在类型层级结构则还会递归扫描其层级结构
     */
    private void scanForElement(
        Context context, AnnotatedElement element, AnnotationProcessor processor, AnnotationFilter filter) {
        final Class<?> sourceClass = getClassFormElement(element);
        // 若没有待扫描的注解元素没有可以遍历的类型层级结构
        if (ObjectUtil.isNull(sourceClass)) {
            processAnnotation(context, processor, filter, element.getAnnotations());
            return;
        }
        // 若待扫描的注解有可用遍历的层级结构
        scanForElementHierarchy(context, processor, filter, sourceClass);
    }

    /**
     * 按广度优先从类的层级结构中扫描注解
     *
     * @param context 上下文
     * @param processor 注解处理器
     * @param filter 过滤器
     */
    private void scanForElementHierarchy(
        Context context, AnnotationProcessor processor, AnnotationFilter filter, Class<?> sourceClass) {
        // 若本次扫描已经中断，则直接返回
        if (context.isInterrupted()) {
            return;
        }

        // 初始化层级队列与索引
        final Deque<List<Class<?>>> typeHierarchyDeque = CollUtil.newLinkedList(CollUtil.newArrayList(sourceClass));
        final Set<Class<?>> accessedTypes = new LinkedHashSet<>();

        // 递归扫描目标元素的层级结构
        while (!typeHierarchyDeque.isEmpty()) {
            final List<Class<?>> currTypeHierarchies = CollUtil.filter(
                typeHierarchyDeque.removeFirst(), t -> isNeedProcess(t, accessedTypes)
            );
            final List<Class<?>> nextTypeHierarchies = new ArrayList<>();
            for (final Class<?> type : currTypeHierarchies) {

                // 处理当前层待处理的类型
                final Annotation[] annotationArray = getAnnotationsFromType(context, type);
                processAnnotation(context, processor, filter, annotationArray);
                // 若本次扫描已经中断，则直接返回
                if (context.isInterrupted()) {
                    return;
                }

                // 搜集下一层需要处理的类型
                accessedTypes.add(type);
                collectToQueue(nextTypeHierarchies, type);
            }

            // 进入下一层
            if (CollUtil.isNotEmpty(nextTypeHierarchies)) {
                typeHierarchyDeque.addLast(nextTypeHierarchies);
            }
            context.incrementVerticalIndexAndGet();
        }
    }

    /**
     * <p>使用处理器处理扫描到的注解，并令上下文的水平索引递增。<br />
     * 若处理完注解对象后，{@link AnnotationProcessor#interrupted()}返回{@code true}，
     * 则也会将{@link Context#interrupted}也标记为{@code true}，
     * 扫描器将终止后续的扫描行为
     *
     * @param context 上下文
     * @param processor 处理器
     * @param filter 过滤器
     * @param annotations 待处理的注解
     */
    protected void processAnnotation(
        Context context, AnnotationProcessor processor, AnnotationFilter filter, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (processor.interrupted()) {
                context.interrupt();
                return;
            }
            if (!filter.test(annotation)) {
                continue;
            }
            processor.accept(context.getVerticalIndex(), context.getHorizontalIndexAndIncrement(), annotation);
        }
    }

    /**
     * 是否处理当前类 <br />
     * 当在下述情况下时，将不处理指定的类对象：
     * <ul>
     *     <li>类对象为空；</li>
     *     <li>类对象未通过{@link ScanOptions#getTypeFilter()}校验；</li>
     *     <li>类对象是已被访问过注解类，且{@link ScanOptions#isEnableScanAccessedAnnotationType()}为{@code false}；</li>
     *     <li>类对象是已被访问过非注解类，且{@link ScanOptions#isEnableScanAccessedType()}为{@code false}；</li>
     * </ul>
     *
     * @param type 目标类型
     * @param accessedTypes 已经访问过的类型
     * @return 是否不需要处理
     */
    protected boolean isNeedProcess(Class<?> type, Set<Class<?>> accessedTypes) {
        if (ObjectUtil.isNull(type) || !scanOptions.getTypeFilter().test(type)) {
            return false;
        }
        if (!accessedTypes.contains(type)) {
            return true;
        }
        return type.isAnnotation() ?
            scanOptions.isEnableScanAccessedAnnotationType() : scanOptions.isEnableScanAccessedType();
    }

    /**
     * 从目标类型上获取下一层级需要处理的类，并添加到队列
     *
     * @param nextTypeHierarchies 队列
     * @param type 目标类型
     */
    protected abstract void collectToQueue(List<Class<?>> nextTypeHierarchies, Class<?> type);

    /**
     * 从要搜索的注解元素上获得要递归的类型，当返回null时，
     * 则认为该元素不存在层级结构
     *
     * @param element 注解元素
     * @return 要递归的类型
     */
    protected abstract Class<?> getClassFormElement(AnnotatedElement element);

    /**
     * 从类上获取所需的目标注解
     *
     * @param context 上下文
     * @param type 类
     * @return 最终所需的目标注解
     */
    protected abstract Annotation[] getAnnotationsFromType(Context context, Class<?> type);

    /**
     * 扫描上下文，用于存储一次扫描动作中的一些共享信息
     */
    @AllArgsConstructor
    @Getter(AccessLevel.PROTECTED)
    protected static class Context {

        /**
         * 本次扫描的元素
         */
        private final AnnotatedElement source;

        /**
         * 当前扫描的层级对应的索引
         */
        private int verticalIndex;

        /**
         * 当前扫描的注解个数
         */
        private int horizontalIndex;

        /**
         * 本次扫描是否已经中断
         */
        private boolean interrupted;

        /**
         * 中断本次扫描
         */
        protected void interrupt() {
            interrupted = true;
        }

        /**
         * 获取水平索引并递增
         */
        protected int getHorizontalIndexAndIncrement() {
            return horizontalIndex++;
        }

        /**
         * 递增并获取垂直索引
         */
        protected int incrementVerticalIndexAndGet() {
            return ++verticalIndex;
        }

    }

}
