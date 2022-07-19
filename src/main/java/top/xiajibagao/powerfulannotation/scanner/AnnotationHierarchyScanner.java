package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import top.xiajibagao.powerfulannotation.helper.AnnotationUtils;
import top.xiajibagao.powerfulannotation.helper.FuncUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 支持扫描注解类与其元注解的层级结构中的注解的扫描器
 *
 * @author huangchengxing
 */
public class AnnotationHierarchyScanner extends AbstractHierarchyScanner<AnnotationHierarchyScanner> {

	/**
	 * 构造一个类注解扫描器
	 */
	public AnnotationHierarchyScanner() {
		super(FuncUtils.alwaysTrue(), Collections.emptySet());
	}

	/**
	 * 构造一个类注解扫描器
	 *
	 * @param filter       过滤器
	 * @param excludeTypes 不包含的类型
	 */
	protected AnnotationHierarchyScanner(Predicate<Class<?>> filter, Set<Class<?>> excludeTypes) {
		super(filter, excludeTypes);
	}

	/**
	 * 判断是否支持扫描该注解元素，仅当注解元素是{@link Annotation}接口的子类{@link Class}时返回{@code true}
	 *
	 * @param element 被注解的元素
	 * @return 是否支持扫描该注解元素
	 */
	@Override
	public boolean support(AnnotatedElement element) {
		return (element instanceof Class && ClassUtil.isAssignable(Annotation.class, (Class<?>)element));
	}

	/**
	 * 按广度优先扫描指定注解上的元注解，对扫描到的注解与层级索引进行操作
	 *
	 * @param consumer     当前层级索引与操作
	 * @param element 被注解的元素
	 * @param filter       过滤器
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void scan(BiConsumer<Integer, Annotation> consumer, AnnotatedElement element, Predicate<Annotation> filter) {
		filter = ObjectUtil.defaultIfNull(filter, t -> true);
		Set<Class<? extends Annotation>> accessed = new HashSet<>();
		final Deque<List<Class<? extends Annotation>>> deque = CollUtil.newLinkedList(CollUtil.newArrayList((Class<? extends Annotation>)element));
		int distance = 0;
		do {
			final List<Class<? extends Annotation>> annotationTypes = deque.removeFirst();
			for (final Class<? extends Annotation> type : annotationTypes) {
				final List<Annotation> metaAnnotations = Stream.of(type.getAnnotations())
					.filter(a -> AnnotationUtils.isNotJdkAnnotation(a.annotationType()))
					.filter(filter)
					.collect(Collectors.toList());
				for (final Annotation metaAnnotation : metaAnnotations) {
					consumer.accept(distance, metaAnnotation);
				}
				accessed.add(type);
				List<Class<? extends Annotation>> next = metaAnnotations.stream()
					.map(Annotation::annotationType)
					.filter(t -> !accessed.contains(t))
					.collect(Collectors.toList());
				if (CollUtil.isNotEmpty(next)) {
					deque.addLast(next);
				}
			}
			distance++;
		} while (!deque.isEmpty());
	}

	@Override
	protected void collectToQueue(List<Class<?>> nextClassQueue, Class<?> targetClass) {
		Stream.of(targetClass.getDeclaredAnnotations())
			.map(Annotation::annotationType)
			.forEach(nextClassQueue::add);
	}

	@Override
	protected Class<?> getClassFormAnnotatedElement(AnnotatedElement annotatedElement) {
		return (Class<?>)annotatedElement;
	}

	@Override
	protected Annotation[] getAnnotationsFromTargetClass(AnnotatedElement source, int index, Class<?> targetClass) {
		return targetClass.getDeclaredAnnotations();
	}

}

