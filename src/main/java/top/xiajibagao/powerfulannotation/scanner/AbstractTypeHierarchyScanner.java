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
import java.util.function.Predicate;

/**
 * <p>用于为支持从具备层级结构中的元素扫描注解的扫描器提供基本实现。
 * 该抽象类定义了针对对象层级结构中注解进行扫描的基本流程，
 * 实现类允许重写所有的非私有方法从而调整部分环节的执行逻辑。
 *
 * @author huangchengxing
 */
@RequiredArgsConstructor
public abstract class AbstractTypeHierarchyScanner implements AnnotationScanner {

    /**
     * 类型过滤器，若该类型无法通过过滤器，则不会被扫描器扫描
     */
    private final Predicate<Class<?>> typeFilter;

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
     * <p>是否处理当前类。
     *
     * <p>不处理为空，或未通过{@link #typeFilter}校验的类型对象。
     * 并且也不处理已经访问过的普通类对象，
     * 但是若类对象为注解类则不再磁力
     *
     * 默认仅限制针对普通类的重复访问，
     * 但是不并限制对注解类的重复访问，因此若注解出现循环引用，
     * 则有可能引起{@link StackOverflowError}。
     * 若有必要，可针对该方法进行重写。
     *
     * @param type 目标类型
     * @param accessedTypes 已经访问过的类型
     * @return 是否不需要处理
     */
    protected boolean isNeedProcess(Class<?> type, Set<Class<?>> accessedTypes) {
        if (ObjectUtil.isNull(type) || !typeFilter.test(type)) {
            return false;
        }
        return type.isAnnotation() || !accessedTypes.contains(type);
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
         * 获取垂直索引并递增
         */
        protected int getVerticalIndexAndIncrement() {
            return verticalIndex++;
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

        /**
         * 递增并获取水平索引
         */
        protected int incrementHorizontalIndexAndGet() {
            return ++horizontalIndex;
        }

    }

}
