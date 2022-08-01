package top.xiajibagao.powerfulannotation.scanner;

import top.xiajibagao.powerfulannotation.helper.Function3;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationCollector;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationFinder;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * <p>用于从{@link AnnotatedElement}及其层级结构扫描注解的扫描模式封装
 *
 * <h3>层级结构扫描支持</h3>
 * <p>扫描模式按照对层级结构的扫描范围分为四类，每一类分别对应一个关键词：
 * <ul>
 *     <li><code>SELF</code>：只扫描元素本身；</li>
 *     <li><code>SUPERCLASS</code>：扫描元素本身以及层级结构中的父类；</li>
 *     <li><code>INTERFACE</code>：扫描元素本身以及层级结构中的接口；</li>
 *     <li><code>TYPE_HIERARCHY</code>：扫描元素本身以及层级结构中的父类与父接口；</li>
 * </ul>
 *
 * <h3>元注解扫描支持</h3>
 * <p>扫描模式按照对注解的元注解结构的扫描范围分为两类，每一类分别对应一个关键词：
 * <ul>
 *     <li>
 *         <code>INDIRECT</code>：从层级结构中扫描到注解后，还会继续扫描这些注解的元注解。<br />
 *         eg：<em>X</em>上存在注解<em>A</em>，<em>A</em>上存在元注解<em>B</em>，则扫描<em>X</em>，将获得<em>A</em>和<em>B</em>；
 *     </li>
 *     <li>
 *         <code>DIRECT</code>：从层级结构中扫描到注解后，不会继续扫描它们的元注解；<br />
 *         eg：<em>X</em>上存在注解<em>A</em>，<em>A</em>上存在元注解<em>B</em>，则扫描<em>X</em>，将只获得<em>A</em>；
 *     </li>
 * </ul>
 *
 * <h3>注意</h3>
 * <ul>
 *     <li>提供的策略皆不会扫描{@link com.sun}，{@link java.lang}及{@link javax}包下的类的注解；</li>
 *     <li>提供的策略皆不会重复扫描一个已经扫描过的普通类，并且在扫描注解类时，该类层级结构中的一种类型的元注解只会被扫描一次；</li>
 *     <li>
 *         当查找的元素为注解类，即元素类型为{@link Class}、且{@link Class#isAnnotation()}返回{@code true}时，
 *         必须选择<code>INDIRECT</code>类型的策略才会完整的扫描其层级结构中的元注解，
 *         其余策略皆仅会扫描该注解类直接声明的元注解；
 *     </li>
 * </ul>
 *
 * @author huangchengxing
 * @see AbstractAnnotationScanner
 */
public enum AnnotationSearchMode {

	/**
	 * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解
	 */
	SELF_AND_DIRECT(new GenericAnnotationScanner(false, false, false)),

	/**
	 * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解，以及这些注解的元注解
	 */
	SELF_AND_INDIRECT(new GenericAnnotationScanner(false, false, true)),

	/**
	 * 扫描元素本身以及层级结构中的父类声明的注解
	 */
	SUPERCLASS_AND_DIRECT(new GenericAnnotationScanner(true, false, false)),

	/**
	 * 扫描元素本身以及层级结构中的父类声明的注解，以及这些注解的元注解
	 */
	SUPERCLASS_AND_INDIRECT(new GenericAnnotationScanner(true, false, true)),

	/**
	 * 扫描元素本身以及层级结构中的父接口声明的注解
	 */
	INTERFACE_AND_DIRECT(new GenericAnnotationScanner(false, true, false)),

	/**
	 * 扫描元素本身以及层级结构中的父接口声明的注解，以及这些注解的元注解
	 */
	INTERFACE_AND_INDIRECT(new GenericAnnotationScanner(false, true, true)),

	/**
	 * 扫描元素本身以及层级结构中的父类及父接口声明的注解
	 */
	TYPE_HIERARCHY_AND_DIRECT(new GenericAnnotationScanner(true, true, false)),

	/**
	 * 扫描元素本身以及层级结构中的父类及父接口声明的注解，以及这些注解的元注解
	 */
	TYPE_HIERARCHY_AND_INDIRECT(new GenericAnnotationScanner(true, true, true));

	/**
	 * 注解扫描器
	 */
	protected final AbstractAnnotationScanner scanner;

	/**
	 * 构造
	 *
	 * @param scanner 扫描器
	 */
	AnnotationSearchMode(AbstractAnnotationScanner scanner) {
		this.scanner = scanner;
	}

	/**
	 * 获得与该策略一致的扫描器配置对象
	 *
	 * @return 扫描器配置对象
	 */
	public ScanOptions getOptions() {
		return scanner.copyOptions();
	}

	/**
	 * 从元素获取全部注解并将其转为指定类型
	 *
	 * @param element   要扫描的元素
	 * @param filter    注解过滤器
	 * @param converter 转换器
	 * @param <T>       转换类型
	 * @return 注解对象
	 */
	public <T> List<T> getAnnotations(
		AnnotatedElement element, AnnotationFilter filter,
		Function3<Integer, Integer, Annotation, T> converter) {
		if (Objects.isNull(element)) {
			return Collections.emptyList();
		}
		AnnotationCollector<T> collector = new AnnotationCollector<>(converter);
		scan(element, collector, filter);
		return collector.getTargets();
	}

	/**
	 * 从元素获取全部注解
	 *
	 * @param element 要扫描的元素
	 * @param filter  注解过滤器
	 * @return 注解对象
	 */
	public List<Annotation> getAnnotations(AnnotatedElement element, AnnotationFilter filter) {
		return getAnnotations(element, filter, (vi, hi, annotation) -> annotation);
	}

	/**
	 * 从元素获取注解并将其转为指定类型对象，若符合条件则返回该指定类型对象
	 *
	 * @param element   要扫描的元素
	 * @param filter    注解过滤器
	 * @param predicate 目标的判断条件
	 * @param converter 转换器
	 * @param <T>       转换类型
	 * @return 注解对象
	 */
	public <T> T getAnnotation(
		AnnotatedElement element, AnnotationFilter filter, Predicate<T> predicate,
		Function3<Integer, Integer, Annotation, T> converter) {
		if (Objects.isNull(element)) {
			return null;
		}
		AnnotationFinder<T> finder = new AnnotationFinder<>(converter, predicate);
		scan(element, finder, filter);
		return finder.getTarget();
	}

	/**
	 * 从元素获取类型注解
	 *
	 * @param element        要扫描的元素
	 * @param annotationType 注解类型
	 * @param <T>            注解类型
	 * @return 注解对象
	 */
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(AnnotatedElement element, Class<T> annotationType) {
		return (T) getAnnotation(
			element, AnnotationFilter.FILTER_NOTHING,
			annotation -> Objects.equals(annotation.annotationType(), annotationType),
			(vi, hi, annotation) -> annotation
		);
	}

	/**
	 * 元素是否存在该类型的注解
	 *
	 * @param element        要扫描的元素
	 * @param annotationType 注解类型
	 * @return 是否
	 */
	public boolean isAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotationType) {
		return Objects.nonNull(getAnnotation(element, annotationType));
	}

	/**
	 * 扫描与指定元素具有关联的注解，并对其进行处理
	 *
	 * @param element   要扫描的元素
	 * @param processor 注解处理器
	 * @param filter    过滤器
	 */
	public void scan(AnnotatedElement element, AnnotationProcessor processor, AnnotationFilter filter) {
		scanner.scan(element, processor, filter);
	}

}
