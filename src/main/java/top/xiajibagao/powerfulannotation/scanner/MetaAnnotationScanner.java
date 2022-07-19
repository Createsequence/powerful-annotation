package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
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
 * 扫描注解类上存在的注解，支持处理枚举实例或枚举类型
 * 需要注意，当待解析是枚举类时，有可能与{@link TypeAnnotationScanner}冲突
 *
 * @author huangchengxing
 * @see TypeAnnotationScanner
 */
public class MetaAnnotationScanner implements AnnotationScanner {

	/**
	 * 获取当前注解的元注解后，是否继续递归扫描的元注解的元注解
	 */
	private final boolean includeSupperMetaAnnotation;

	/**
	 * 构造一个元注解扫描器
	 *
	 * @param includeSupperMetaAnnotation 获取当前注解的元注解后，是否继续递归扫描的元注解的元注解
	 */
	public MetaAnnotationScanner(boolean includeSupperMetaAnnotation) {
		this.includeSupperMetaAnnotation = includeSupperMetaAnnotation;
	}

	/**
	 * 构造一个元注解扫描器，默认在扫描当前注解上的元注解后，并继续递归扫描元注解
	 */
	public MetaAnnotationScanner() {
		this(true);
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
	 * 获取注解元素上的全部注解。调用该方法前，需要确保调用{@link #support(AnnotatedElement)}返回为true
	 *
	 * @param element 被注解的元素
	 * @return 注解
	 */
	@Override
	public List<Annotation> getAnnotations(AnnotatedElement element) {
		final List<Annotation> annotations = new ArrayList<>();
		scan(
			(index, annotation) -> annotations.add(annotation), element,
			annotation -> ObjectUtil.notEqual(annotation, element)
		);
		return annotations;
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
		} while (includeSupperMetaAnnotation && !deque.isEmpty());
	}

}
