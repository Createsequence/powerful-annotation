package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import top.xiajibagao.powerfulannotation.helper.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为需要从特定层级结构中获取注解的{@link AnnotationScanner}提供基本实现
 *
 * @author huangchengxing
 * @see TypeHierarchyScanner
 * @see AnnotationHierarchyScanner
 */
public abstract class AbstractHierarchyScanner<T extends AbstractHierarchyScanner<T>> implements AnnotationScanner {

	/**
	 * 过滤器，若类型无法通过该过滤器，则该类型及其树结构将直接不被查找
	 */
	private Predicate<Class<?>> filter;

	/**
	 * 排除的类型，以上类型及其树结构将直接不被查找
	 */
	private final Set<Class<?>> excludeTypes;

	/**
	 * 当前实例
	 */
	private final T typedThis;

	/**
	 * 构造一个类注解扫描器
	 *
	 * @param filter            过滤器
	 * @param excludeTypes      不包含的类型
	 */
	@SuppressWarnings("unchecked")
	protected AbstractHierarchyScanner(Predicate<Class<?>> filter, Set<Class<?>> excludeTypes) {
		Assert.notNull(filter, "filter must not null");
		Assert.notNull(excludeTypes, "excludeTypes must not null");
		this.filter = filter;
		this.excludeTypes = excludeTypes;
		this.typedThis = (T) this;
	}

	/**
	 * 设置过滤器，若类型无法通过该过滤器，则该类型及其树结构将直接不被查找
	 *
	 * @param filter 过滤器
	 * @return 当前实例
	 */
	public T setFilter(Predicate<Class<?>> filter) {
		Assert.notNull(filter, "filter must not null");
		this.filter = filter;
		return typedThis;
	}

	/**
	 * 添加不扫描的类型，该类型及其树结构将直接不被查找
	 *
	 * @param excludeTypes 不扫描的类型
	 * @return 当前实例
	 */
	public T addExcludeTypes(Class<?>... excludeTypes) {
		CollUtil.addAll(this.excludeTypes, excludeTypes);
		return typedThis;
	}

	/**
	 * 则根据广度优先递归扫描类的层级结构，并对层级结构中类/接口声明的层级索引和它们声明的注解对象进行处理
	 *
	 * @param consumer     对获取到的注解和注解对应的层级索引的处理
	 * @param annotatedEle 注解元素
	 * @param filter       注解过滤器，无法通过过滤器的注解不会被处理。该参数允许为空。
	 */
	@Override
	public void scan(BiConsumer<Integer, Annotation> consumer, AnnotatedElement annotatedEle, Predicate<Annotation> filter) {
		scanTypeHierarchy(consumer, annotatedEle, filter);
	}

	/**
	 * 从类的层级结构中扫描注解，若扫描过程中中断，则会返回中断时正在处理注解对象，否则将返回null
	 */
	private void scanTypeHierarchy(
		BiConsumer<Integer, Annotation> consumer, AnnotatedElement annotatedEle, Predicate<Annotation> filter) {
		filter = ObjectUtil.defaultIfNull(filter, annotation -> true);
		final Class<?> sourceClass = getClassFormAnnotatedElement(annotatedEle);
		final Deque<List<Class<?>>> classDeque = CollUtil.newLinkedList(CollUtil.newArrayList(sourceClass));
		final Set<Class<?>> accessedTypes = new LinkedHashSet<>();
		int index = 0;
		while (!classDeque.isEmpty()) {
			final List<Class<?>> currClassQueue = CollUtil.filter(classDeque.removeFirst(), t -> !isNotNeedProcess(accessedTypes, t));
			final List<Class<?>> nextClassQueue = new ArrayList<>();
			for (final Class<?> targetClass : currClassQueue) {
				// 过滤不需要处理的类
				accessedTypes.add(targetClass);
				collectToQueue(nextClassQueue, targetClass);
				// 处理层级索引和注解
				final List<Annotation> targetAnnotations = Stream.of(getAnnotationsFromTargetClass(annotatedEle, index, targetClass))
					.filter(annotation -> AnnotationUtils.isNotJdkAnnotation(annotation.annotationType()))
					.filter(filter)
					.collect(Collectors.toList());
				for (final Annotation annotation : targetAnnotations) {
					consumer.accept(index, annotation);
				}
				index++;
			}
			if (CollUtil.isNotEmpty(nextClassQueue)) {
				classDeque.addLast(nextClassQueue);
			}
		}
	}

	/**
	 * 从目标类型上获取下一层级需要处理的类，并添加到队列
	 *
	 * @param nextClassQueue 队列
	 * @param targetClass 目标类型
	 */
	protected abstract void collectToQueue(List<Class<?>> nextClassQueue, Class<?> targetClass);

	/**
	 * 从要搜索的注解元素上获得要递归的类型
	 *
	 * @param annotatedElement 注解元素
	 * @return 要递归的类型
	 */
	protected abstract Class<?> getClassFormAnnotatedElement(AnnotatedElement annotatedElement);

	/**
	 * 从类上获取最终所需的目标注解
	 *
	 * @param source      最初的注解元素
	 * @param index       类的层级索引
	 * @param targetClass 类
	 * @return 最终所需的目标注解
	 */
	protected abstract Annotation[] getAnnotationsFromTargetClass(AnnotatedElement source, int index, Class<?> targetClass);

	/**
	 * 当前类是否不需要处理
	 *
	 * @param accessedTypes 访问类型
	 * @param targetClass   目标类型
	 * @return 是否不需要处理
	 */
	protected boolean isNotNeedProcess(Set<Class<?>> accessedTypes, Class<?> targetClass) {
		return ObjectUtil.isNull(targetClass)
			|| accessedTypes.contains(targetClass)
			|| excludeTypes.contains(targetClass)
			|| filter.negate().test(targetClass);
	}

}
