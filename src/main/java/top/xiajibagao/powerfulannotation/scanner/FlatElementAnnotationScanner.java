package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.util.ObjectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 支持扫描{@link AnnotatedElement}上的注解，而不处理其层级结构的扫描器
 *
 * @author huangchengxing
 */
public class FlatElementAnnotationScanner implements AnnotationScanner {

	/**
	 * 判断是否支持扫描该注解元素，仅当注解元素不为空时返回{@code true}
	 *
	 * @param element 被注解的元素
	 * @return 是否支持扫描该注解元素
	 */
	@Override
	public boolean support(AnnotatedElement element) {
		return ObjectUtil.isNotNull(element);
	}

	/**
	 * 扫描{@link AnnotatedElement}上直接声明的注解，调用前需要确保调用{@link #support(AnnotatedElement)}返回为true
	 *
	 * @param consumer     对获取到的注解和注解对应的层级索引的处理
	 * @param element 被注解的元素
	 * @param filter       注解过滤器，无法通过过滤器的注解不会被处理。该参数允许为空。
	 */
	@Override
	public void scan(BiConsumer<Integer, Annotation> consumer, AnnotatedElement element, Predicate<Annotation> filter) {
		filter = ObjectUtil.defaultIfNull(filter, t -> true);
		Stream.of(element.getAnnotations())
			.filter(filter)
			.forEach(annotation -> consumer.accept(0, annotation));
	}

}
