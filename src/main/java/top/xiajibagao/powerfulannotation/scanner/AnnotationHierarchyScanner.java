package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.util.ClassUtil;
import top.xiajibagao.powerfulannotation.helper.FuncUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
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

