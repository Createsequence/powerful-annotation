package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import top.xiajibagao.powerfulannotation.helper.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * 为需要从类的层级结构中获取注解的{@link AnnotationScanner}提供基本实现
 *
 * @author huangchengxing
 * @see TypeHierarchyScanner
 * @see AnnotationHierarchyScanner
 */
public abstract class AbstractTypeHierarchyScanner<T extends AbstractTypeHierarchyScanner<T>> implements AnnotationScanner {

	/**
	 * 是否允许扫描父类
	 */
	private boolean includeSuperClass;

	/**
	 * 是否允许扫描父接口
	 */
	private boolean includeInterfaces;

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
	 * @param includeSuperClass 是否允许扫描父类
	 * @param includeInterfaces 是否允许扫描父接口
	 * @param filter            过滤器
	 * @param excludeTypes      不包含的类型
	 */
	@SuppressWarnings("unchecked")
	protected AbstractTypeHierarchyScanner(boolean includeSuperClass, boolean includeInterfaces, Predicate<Class<?>> filter, Set<Class<?>> excludeTypes) {
		Assert.notNull(filter, "filter must not null");
		Assert.notNull(excludeTypes, "excludeTypes must not null");
		this.includeSuperClass = includeSuperClass;
		this.includeInterfaces = includeInterfaces;
		this.filter = filter;
		this.excludeTypes = excludeTypes;
		this.typedThis = (T) this;
	}

	/**
	 * 是否允许扫描父类
	 *
	 * @return 是否允许扫描父类
	 */
	public boolean isIncludeSuperClass() {
		return includeSuperClass;
	}

	/**
	 * 是否允许扫描父接口
	 *
	 * @return 是否允许扫描父接口
	 */
	public boolean isIncludeInterfaces() {
		return includeInterfaces;
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
	 * 是否允许扫描父类
	 *
	 * @param includeSuperClass 是否
	 * @return 当前实例
	 */
	protected T setIncludeSuperClass(boolean includeSuperClass) {
		this.includeSuperClass = includeSuperClass;
		return typedThis;
	}

	/**
	 * 是否允许扫描父接口
	 *
	 * @param includeInterfaces 是否
	 * @return 当前实例
	 */
	protected T setIncludeInterfaces(boolean includeInterfaces) {
		this.includeInterfaces = includeInterfaces;
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
		filter = ObjectUtil.defaultIfNull(filter, annotation -> true);
		final Class<?> sourceClass = getClassFormAnnotatedElement(annotatedEle);
		final Deque<List<Class<?>>> classDeque = CollUtil.newLinkedList(CollUtil.newArrayList(sourceClass));
		final Set<Class<?>> accessedTypes = new LinkedHashSet<>();
		int index = 0;
		while (!classDeque.isEmpty()) {
			final List<Class<?>> currClassQueue = classDeque.removeFirst();
			final List<Class<?>> nextClassQueue = new ArrayList<>();
			for (final Class<?> targetClass : currClassQueue) {
				// 过滤不需要处理的类
				if (isNotNeedProcess(accessedTypes, targetClass)) {
					continue;
				}
				accessedTypes.add(targetClass);
				// 扫描父类
				scanSuperClassIfNecessary(nextClassQueue, targetClass);
				// 扫描接口
				scanInterfaceIfNecessary(nextClassQueue, targetClass);
				// 处理层级索引和注解
				final Annotation[] targetAnnotations = getAnnotationsFromTargetClass(annotatedEle, index, targetClass);
				for (final Annotation annotation : targetAnnotations) {
					if (AnnotationUtils.isNotJdkAnnotation(annotation.annotationType()) && filter.test(annotation)) {
						consumer.accept(index, annotation);
					}
				}
				index++;
			}
			if (CollUtil.isNotEmpty(nextClassQueue)) {
				classDeque.addLast(nextClassQueue);
			}
		}
	}

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

	/**
	 * 若{@link #includeInterfaces}为{@code true}，则将目标类的父接口也添加到nextClasses
	 *
	 * @param nextClasses 下一个类集合
	 * @param targetClass 目标类型
	 */
	protected void scanInterfaceIfNecessary(List<Class<?>> nextClasses, Class<?> targetClass) {
		if (includeInterfaces) {
			final Class<?>[] interfaces = targetClass.getInterfaces();
			if (ArrayUtil.isNotEmpty(interfaces)) {
				CollUtil.addAll(nextClasses, interfaces);
			}
		}
	}

	/**
	 * 若{@link #includeSuperClass}为{@code true}，则将目标类的父类也添加到nextClasses
	 *
	 * @param nextClassQueue 下一个类队列
	 * @param targetClass    目标类型
	 */
	protected void scanSuperClassIfNecessary(List<Class<?>> nextClassQueue, Class<?> targetClass) {
		if (includeSuperClass) {
			final Class<?> superClass = targetClass.getSuperclass();
			if (!ObjectUtil.equals(superClass, Object.class) && ObjectUtil.isNotNull(superClass)) {
				nextClassQueue.add(superClass);
			}
		}
	}

}
