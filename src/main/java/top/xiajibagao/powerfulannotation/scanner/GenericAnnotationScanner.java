package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.powerfulannotation.helper.FuncUtils;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;
import top.xiajibagao.powerfulannotation.scanner.processor.CombinedAnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * <p>通用注解扫描器，支持按不同的层级结构扫描{@link AnnotatedElement}上的注解。
 *
 * <p>当{@link AnnotatedElement}类型不同时，“层级结构”指向的对象将有所区别：
 * <ul>
 *     <li>
 *         当元素为{@link Method}时，此处层级结构指声明方法的类的层级结构，
 *         扫描器将从层级结构中寻找与该方法签名相同的方法，并对其进行扫描；
 *     </li>
 *     <li>
 *         当元素为{@link Class}时，此处层级结构即指类本身与其父类、父接口共同构成的层级结构，
 *         扫描器将扫描层级结构中类、接口声明的注解；
 *     </li>
 *     <li>当元素不为{@link Method}或{@link Class}时，则其层级结构仅有其本身一层；</li>
 * </ul>
 * 此外，扫描器支持在获取到层级结构中的注解对象后，再对注解对象的元注解进行扫描。
 *
 * @author huangchengxing
 * @see TypeHierarchyScanner
 * @see TypeMethodHierarchyScanner
 * @see AnnotationHierarchyScanner
 * @see FlatElementAnnotationScanner
 */
public class GenericAnnotationScanner implements AnnotationScanner {

	/**
	 * 类型扫描器
	 */
	private final AnnotationScanner typeHierarchyScanner;

	/**
	 * 方法扫描器
	 */
	private final AnnotationScanner typeMethodHierarchyScanner;

	/**
	 * 元注解扫描器
	 */
	private final AnnotationScanner annotationHierarchyScanner;

	/**
	 * 普通元素扫描器
	 */
	private final AnnotationScanner flatElementScanner;

	/**
	 * 通用注解扫描器支持扫描所有类型的{@link AnnotatedElement}
	 *
	 * @param element 被注解的元素
	 * @return 是否支持扫描该注解元素
	 */
	@Override
	public boolean support(AnnotatedElement element) {
		return true;
	}

	/**
	 * 构造一个通用注解扫描器
	 *
	 * @param enableScanMetaAnnotation  是否扫描注解上的元注解
	 * @param enableScanSupperClass     是否扫描父类
	 * @param enableScanSupperInterface 是否扫描父接口
	 */
	public GenericAnnotationScanner(
		boolean enableScanMetaAnnotation,
		boolean enableScanSupperClass,
		boolean enableScanSupperInterface) {

		this.annotationHierarchyScanner = enableScanMetaAnnotation ? new AnnotationHierarchyScanner() : new NothingScanner();
		this.typeHierarchyScanner = new TypeHierarchyScanner(
			enableScanSupperClass, enableScanSupperInterface, a -> true, Collections.emptySet()
		);
		this.typeMethodHierarchyScanner = new TypeMethodHierarchyScanner(
			enableScanSupperClass, enableScanSupperInterface, a -> true, Collections.emptySet()
		);
		this.flatElementScanner = new FlatElementAnnotationScanner();
	}

	/**
	 * 扫描注解元素的层级结构（若存在），然后对获取到的注解和注解对应的层级索引进行处理
	 *
	 * @param processor     对获取到的注解和注解对应的层级索引的处理
	 * @param element 被注解的元素
	 * @param filter       注解过滤器，无法通过过滤器的注解不会被处理。该参数允许为空。
	 */
	@Override
	public void scan(AnnotationProcessor processor, AnnotatedElement element, Predicate<Annotation> filter) {
		filter = ObjectUtil.defaultIfNull(filter, FuncUtils.alwaysTrue());
		if (ObjectUtil.isNull(element)) {
			return;
		}
		// 注解元素是类
		if (element instanceof Class) {
			scanElement(typeHierarchyScanner, processor, element, filter);
		}
		// 注解元素是方法
		else if (element instanceof Method) {
			scanElement(typeMethodHierarchyScanner, processor, element, filter);
		}
		// 注解元素是其他类型
		else {
			scanElement(flatElementScanner, processor, element, filter);
		}
	}

	/**
	 * 扫描注解类的层级结构（若存在），然后对获取到的注解和注解对应的层级索引进行处理
	 */
	private void scanElement(
		AnnotationScanner scanner,
		AnnotationProcessor processor,
		AnnotatedElement element,
		Predicate<Annotation> filter) {

		CombinedAnnotationProcessor combinedAnnotationProcessor = new CombinedAnnotationProcessor(processor, annotationHierarchyScanner, processor, filter);
		scanner.scan(combinedAnnotationProcessor, element, filter);
	}



	@RequiredArgsConstructor
	private class SharedContextProcessor implements AnnotationProcessor {

		private final int verticalIndex;
		private final AtomicInteger horizontalIndex;
		private final AnnotationProcessor processor;

		public SharedContextProcessor(int verticalIndex, AnnotationProcessor processor) {
			this.verticalIndex = verticalIndex + 1;
			this.processor = processor;
			this.horizontalIndex = new AtomicInteger(0);
		}

		@Override
		public void process(int verticalIndex, int horizontalIndex, Annotation annotation) {
			processor.process(this.verticalIndex + verticalIndex, this.horizontalIndex.getAndIncrement(), annotation);
		}

		@Override
		public boolean interruptScanning() {
			return processor.interruptScanning();
		}
	}

}
