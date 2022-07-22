package top.xiajibagao.powerfulannotation.scanner;

import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationCollector;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.function.Predicate;

/**
 * <p>注解扫描器，用于从支持的可注解元素上获取所需注解
 *
 * <p>默认提供了以下扫描方式：
 * <ul>
 *     <li>{@link #NOTHING}：什么都不做，什么注解都不扫描；</li>
 *     <li>{@link #DIRECTLY}：扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解；</li>
 *     <li>
 *         {@link #DIRECTLY_AND_META_ANNOTATION}：扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解，
 *         以及这些注解的元注解；
 *     </li>
 *     <li>{@link #SUPERCLASS}：扫描元素本身以及父类的层级结构中声明的注解；</li>
 *     <li>{@link #SUPERCLASS_AND_META_ANNOTATION}：扫描元素本身以及父类的层级结构中声明的注解，以及这些注解的元注解；</li>
 *     <li>{@link #INTERFACE}：扫描元素本身以及父接口的层级结构中声明的注解；</li>
 *     <li>{@link #INTERFACE_AND_META_ANNOTATION}：扫描元素本身以及父接口的层级结构中声明的注解，以及这些注解的元注解；</li>
 *     <li>{@link #TYPE_HIERARCHY}：扫描元素本身以及父类、父接口的层级结构中声明的注解；</li>
 *     <li>{@link #TYPE_HIERARCHY_AND_META_ANNOTATION}：扫描元素本身以及父接口、父接口的层级结构中声明的注解，以及这些注解的元注解；</li>
 * </ul>
 *
 * @author huangchengxing
 * @see TypeHierarchyScanner
 * @see TypeMethodHierarchyScanner
 * @see AnnotationHierarchyScanner
 * @see FlatElementAnnotationScanner
 * @see GenericAnnotationScanner
 */
public interface AnnotationScanner {

	// ============================ 预置的扫描器实例 ============================

	/**
	 * 不扫描任何注解
	 */
	AnnotationScanner NOTHING = new NothingScanner();

	/**
	 * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解的扫描器
	 */
	AnnotationScanner DIRECTLY = new GenericAnnotationScanner(false, false, false);

	/**
	 * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解，以及这些注解的元注解的扫描器
	 */
	AnnotationScanner DIRECTLY_AND_META_ANNOTATION = new GenericAnnotationScanner(true, false, false);

	/**
	 * 扫描元素本身以及父类的层级结构中声明的注解的扫描器
	 */
	AnnotationScanner SUPERCLASS = new GenericAnnotationScanner(false, true, false);

	/**
	 * 扫描元素本身以及父类的层级结构中声明的注解，以及这些注解的元注解的扫描器
	 */
	AnnotationScanner SUPERCLASS_AND_META_ANNOTATION = new GenericAnnotationScanner(true, true, false);

	/**
	 * 扫描元素本身以及父接口的层级结构中声明的注解的扫描器
	 */
	AnnotationScanner INTERFACE = new GenericAnnotationScanner(false, false, true);

	/**
	 * 扫描元素本身以及父接口的层级结构中声明的注解，以及这些注解的元注解的扫描器
	 */
	AnnotationScanner INTERFACE_AND_META_ANNOTATION = new GenericAnnotationScanner(true, false, true);

	/**
	 * 扫描元素本身以及父类、父接口的层级结构中声明的注解的扫描器
	 */
	AnnotationScanner TYPE_HIERARCHY = new GenericAnnotationScanner(false, true, true);

	/**
	 * 扫描元素本身以及父接口、父接口的层级结构中声明的注解，以及这些注解的元注解的扫描器
	 */
	AnnotationScanner TYPE_HIERARCHY_AND_META_ANNOTATION = new GenericAnnotationScanner(true, true, true);

	// ============================ 抽象方法 ============================

	/**
	 * 判断是否支持扫描该注解元素
	 *
	 * @param element 被注解的元素
	 * @return 是否支持扫描该注解元素
	 */
	default boolean support(AnnotatedElement element) {
		return false;
	}

	/**
	 * 获取注解元素上的全部注解。调用该方法前，需要确保调用{@link #support(AnnotatedElement)}返回为true
	 *
	 * @param element 被注解的元素
	 * @return 注解
	 */
	default List<Annotation> getAnnotations(AnnotatedElement element) {
		final AnnotationCollector collector = new AnnotationCollector();
		scan(collector, element, null);
		return collector.getCollectedAnnotations();
	}

	/**
	 * 扫描注解元素的层级结构（若存在），然后对获取到的注解和注解对应的层级索引进行处理。
	 * 调用该方法前，需要确保调用{@link #support(AnnotatedElement)}返回为true
	 *
	 * @param processor 注解处理器
	 * @param element 被注解的元素
	 * @param filter 注解过滤器，无法通过过滤器的注解不会被处理。该参数允许为空。
	 */
	void scan(AnnotationProcessor processor, AnnotatedElement element, Predicate<Annotation> filter);

}
