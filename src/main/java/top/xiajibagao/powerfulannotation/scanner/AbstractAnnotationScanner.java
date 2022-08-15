package top.xiajibagao.powerfulannotation.scanner;

import top.xiajibagao.powerfulannotation.helper.Annotations;
import top.xiajibagao.powerfulannotation.helper.CollUtils;
import top.xiajibagao.powerfulannotation.helper.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>注解扫描器抽象类，用于从指定的{@link AnnotatedElement}及其可能存在的层级结构中获取注解对象，
 * 包括：{@link Class}、{@link Method}以及{@link Field}。<br />
 * 扫描器支持通过{@link ScanOptions}对一些参数进行配置。
 *
 * <p>{@link AbstractAnnotationScanner}定义了根据配置项从待扫描元素的层级结构中获取注解的基本逻辑，
 * 调用者需要在实现类中实现下述方法，以补全注解扫描相关功能的逻辑：<br />
 * 类的层级结构递归相关：
 * <ul>
 *     <li>{@link #collectSuperTypeIfNecessary(List, Class)}：递归注解接口类的层级结构；</li>
 *     <li>{@link #collectInterfaceTypeIfNecessary(List, Class)}：递归父类的层级结构；</li>
 *     <li>{@link #collectMetaAnnotationTypeIfNecessary(List, List)}：递归元注解的层级结构；</li>
 * </ul>
 * 从层级结构中的类获取注解相关：
 * <ul>
 *     <li>{@link #getElementFromType(Class, Class)}：从类上获取注解元素；</li>
 *     <li>{@link #getElementFromTypeDeclaredMethod(Class, Method)}：从类声明的方法上获取注解元素；</li>
 *     <li>{@link #getElementFromTypeDeclaredField(Class, Field)}：从类声明的属性上获取注解元素；</li>
 * </ul>
 *
 * @author huangchengxing
 * @see AnnotationFilter
 * @see AnnotationProcessor
 */
public abstract class AbstractAnnotationScanner implements AnnotationScanner {

	/**
	 * 水平索引起始点
	 */
	public static final int VERTICAL_INDEX_START_POINT = 0;

	/**
	 * 垂直索引起始点
	 */
	public static final int HORIZONTAL_INDEX_START_POINT = 0;

	/**
	 * 扫描配置
	 */
	private final ScanOptions options;

    /**
     * 构造一个通用注解扫描器
     *
     * @param options 扫描配置
     */
    protected AbstractAnnotationScanner(ScanOptions options) {
        this.options = options;
        options.lockOptions();
    }

    /**
     * 获取与当前扫描器相同的配置类
     *
     * @return 配置类
     */
    public ScanOptions copyOptions() {
		return new ScanOptions(options);
	}

	/**
	 * 扫描指定元素上的注解
	 *
	 * @param element 待扫描的元素
	 * @param processor 注解处理器
	 * @param filter 过滤器，若为空则不过滤任何注解。若支持扫描元注解，则不通过过滤器的注解对象的元注解不会被扫描
	 */
	@Override
	public void scan(AnnotatedElement element, AnnotationProcessor processor, AnnotationFilter filter) {
		if (Objects.isNull(element) || Objects.isNull(processor)) {
			return;
		}
		filter = ObjectUtils.defaultIfNull(filter, AnnotationFilter.FILTER_NOTHING);
		Class<?> typeHierarchy = getTypeHierarchyFromElement(element);
		Context context = new Context(element, VERTICAL_INDEX_START_POINT, HORIZONTAL_INDEX_START_POINT);
		scanForElementHierarchy(context, processor, filter, typeHierarchy);
	}

	/**
	 * <ul>
	 *     <li>若元素是{@link Class}，则直接返回；</li>
	 *     <li>若元素是{@link Member}，则返回{@link Member#getDeclaringClass()}；</li>
	 *     <li>若元素是不为上述两者，则返回{@code null};</li>
	 * </ul>
	 */
	private Class<?> getTypeHierarchyFromElement(AnnotatedElement element) {
		if (element instanceof Class) {
			return (Class<?>)element;
		}
		if (element instanceof Member) {
			return ((Member)element).getDeclaringClass();
		}
		return null;
	}

	/**
	 * 按广度优先递归类的层级结构，并从中获取注解
	 */
	private void scanForElementHierarchy(
		Context context, AnnotationProcessor processor, AnnotationFilter filter, Class<?> sourceClass) {

		// 初始化层级队列与索引
		final Deque<List<Class<?>>> typeHierarchyDeque = new LinkedList<>();
		typeHierarchyDeque.addLast(new ArrayList<>(Collections.singletonList(sourceClass)));
		List<Annotation> processedAnnotationsCache = new ArrayList<>();
		boolean hasTypeHierarchy = Objects.nonNull(sourceClass);

		// 递归扫描目标元素的层级结构
		while (CollUtils.isNotEmpty(typeHierarchyDeque)) {
			++context.verticalIndex;

			final List<Class<?>> currTypeHierarchies = typeHierarchyDeque.removeFirst();
			final List<Class<?>> nextTypeHierarchies = new ArrayList<>();
			for (final Class<?> type : currTypeHierarchies) {
				if (hasTypeHierarchy && !isNeedProcessType(type, context)) {
					continue;
				}
				// 处理当前层待处理的类型
				AnnotatedElement source;
				if (hasTypeHierarchy) {
					source = getElementFromType(context, type);
				} else {
					source = context.source;
					hasTypeHierarchy = true;
				}
				// 处理注解
				boolean continueScanning = processAnnotationsFromSource(
					context, processor, filter,
					processedAnnotationsCache, source
				);
				if (!continueScanning) {
					return;
				}
				// 搜集下一层需要处理的类型
				collectNextTypes(context, processedAnnotationsCache, nextTypeHierarchies, type);
			}
			// 进入下一层
			collectNextTypesToQueue(typeHierarchyDeque, nextTypeHierarchies);
		}
	}

	/**
	 * 收集下一层待处理的类型，将其加入下一层待处理队列
	 */
	private void collectNextTypes(Context context, List<Annotation> processedAnnotationsCache, List<Class<?>> nextTypeHierarchies, Class<?> type) {
		context.accessedTypes.add(type);
		collectTypeToQueue(nextTypeHierarchies, type, processedAnnotationsCache);
		processedAnnotationsCache.clear();
	}

	/**
	 * 若下一层待处理的类型不为空，则将其加入处理队列
	 */
	private void collectNextTypesToQueue(Deque<List<Class<?>>> typeHierarchyDeque, List<Class<?>> nextTypeHierarchies) {
		if (CollUtils.isNotEmpty(nextTypeHierarchies)) {
			typeHierarchyDeque.addLast(nextTypeHierarchies);
		}
	}

	/**
	 * 处理{@code source}上直接声明的注解
	 */
	private boolean processAnnotationsFromSource(Context context, AnnotationProcessor processor, AnnotationFilter filter, List<Annotation> processedAnnotationsCache, AnnotatedElement source) {
		Annotation[] annotations = Objects.isNull(source) ?
			Annotations.emptyArray() : source.getDeclaredAnnotations();
		return processAnnotation(context, processedAnnotationsCache, processor, filter, source, annotations);
	}

	/**
	 * 在注解过滤器过滤后使用处理器处理剩余的注解，并令上下文中水平索引+1
	 */
	private boolean processAnnotation(
		Context context, List<Annotation> processedAnnotations,
		AnnotationProcessor processor, AnnotationFilter filter,
		AnnotatedElement source, Annotation[] annotations) {
		Annotation[] filtered = Stream.of(annotations)
			.filter(filter)
			.collect(Collectors.toList())
			.toArray(Annotations.emptyArray());
		CollUtils.addAll(processedAnnotations, filtered);
		return processor.accept(context.verticalIndex, ++context.horizontalIndex, source, filtered);
	}

    // ======================== 获取需要扫描的类对象 ========================

	/**
	 * 当类对象未通过{@link ScanOptions#getTypeFilter()}校验，或已经被访问过，则不处理指定的类对象：
	 */
	private boolean isNeedProcessType(Class<?> type, Context context) {
		return options.getTypeFilter().test(type)
			&& !context.accessedTypes.contains(type);
	}

    /**
     * 收集当前类父接口、父类或者元注解类，将其加入队列用于下一次递归获取注解
     */
    private void collectTypeToQueue(List<Class<?>> nextTypeHierarchies, Class<?> type, List<Annotation> annotations) {
    	// 扫描元注解类
		if (options.isEnableScanMetaAnnotation() && CollUtils.isNotEmpty(annotations)) {
			collectMetaAnnotationTypeIfNecessary(nextTypeHierarchies, annotations);
		}
		// 扫描普通类
		if (Objects.nonNull(type) && !type.isAnnotation()) {
			if (options.isEnableScanSuperClass()) {
				collectSuperTypeIfNecessary(nextTypeHierarchies, type);
			}
			if (options.isEnableScanInterface()) {
				collectInterfaceTypeIfNecessary(nextTypeHierarchies, type);
			}
		}
    }

    /**
     * 若{@link ScanOptions#isEnableScanInterface}为{@code true}，则将目标类的父接口也添加到队列
     *
     * @param nextTypeHierarchies  下一层级待处理的类队列
	 * @param type 当前正在处理的类对象
     */
    protected abstract void collectInterfaceTypeIfNecessary(List<Class<?>> nextTypeHierarchies, Class<?> type);

    /**
     * 若{@link ScanOptions#isEnableScanSuperClass}为{@code true}，则将目标类的父类也添加到队列
     *
     * @param nextTypeHierarchies 下一层级待处理的类队列
     * @param type 当前正在处理的类对象
     */
    protected abstract void collectSuperTypeIfNecessary(List<Class<?>> nextTypeHierarchies, Class<?> type);

	/**
	 * 若{@link ScanOptions#isEnableScanMetaAnnotation()}为{@code true}，则将处理的注解的元注解也添加到队列
	 *
	 * @param nextTypeHierarchies 下一层级待处理的类队列
	 * @param annotations 需要收集的注解对象
	 */
	protected abstract void collectMetaAnnotationTypeIfNecessary(List<Class<?>> nextTypeHierarchies, List<Annotation> annotations);

	// ======================== 从类对象中获取所需注解 ========================

    /**
	 * 从{@code type}中获取所需的{@link AnnotatedElement}
     */
    private AnnotatedElement getElementFromType(Context context, Class<?> type) {
        AnnotatedElement element = context.source;
        // 搜索对象是类
        if (element instanceof Class) {
			Class<?> sourceClass = (Class<?>)element;
            return getElementFromType(type, sourceClass);
        }
        // 搜索对象的元注解类
		if (options.isEnableScanMetaAnnotation() && type.isAnnotation()) {
			return type;
		}
		// 搜索对象是方法
        if (element instanceof Method) {
            Method sourceMethod = (Method)element;
            return getElementFromTypeDeclaredMethod(type, sourceMethod);
        }
        // 搜索对象的属性
        if (element instanceof Field) {
            Field sourceField = (Field)element;
            return getElementFromTypeDeclaredField(type, sourceField);
        }
        // 理论上不会进入该分支
        throw new IllegalArgumentException(String.format(
			"cannot get element from [%s]", element
		));
    }

	/**
	 * 从类中的指定属性获取注解对象
	 *
	 * @param type    当前正在处理的类对象
	 * @param element 最开始扫描的对象
	 * @return 注解对象
	 */
	protected abstract AnnotatedElement getElementFromTypeDeclaredField(Class<?> type, Field element);

	/**
     * 从类中的指定方法获取注解对象
     *
     * @param type    当前正在处理的类对象
	 * @param element 最开始扫描的对象
     * @return 注解对象
     */
	protected abstract AnnotatedElement getElementFromTypeDeclaredMethod(Class<?> type, Method element);

	/**
	 * 从类获取注解对象
	 *
	 * @param type    当前正在处理的类对象
	 * @param element 最开始扫描的对象
	 * @return 注解对象
	 */
	protected abstract AnnotatedElement getElementFromType(Class<?> type, Class<?> element);

	/**
	 * 扫描上下文，用于存储一次扫描动作中的一些共享信息
	 *
	 * @author huangchengxing
	 */
	protected static class Context {

		/**
		 * 本次扫描的元素
		 */
		private final AnnotatedElement source;

		/**
		 * 已经访问过的类
		 */
		private final Set<Class<?>> accessedTypes;

		/**
		 * 当前扫描的层级对应的索引
		 */
		private int verticalIndex;

		/**
		 * 当前扫描的注解个数
		 */
		private int horizontalIndex;

		/**
		 * 创建一个扫描上下文
		 *
		 * @param source          数据源
		 * @param verticalIndex   垂直索引
		 * @param horizontalIndex 水平索引
		 */
		public Context(AnnotatedElement source, int verticalIndex, int horizontalIndex) {
			this.source = source;
			this.verticalIndex = verticalIndex;
			this.horizontalIndex = horizontalIndex;
			this.accessedTypes = new LinkedHashSet<>();
		}
	}

}
