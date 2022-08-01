package top.xiajibagao.powerfulannotation.scanner;

import top.xiajibagao.powerfulannotation.helper.CollUtils;
import top.xiajibagao.powerfulannotation.helper.ObjectUtils;
import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>注解扫描器抽象类，用于从指定的{@link AnnotatedElement}及其可能存在的层级结构中获取注解对象。<br />
 * 扫描器支持通过{@link ScanOptions}对一些参数进行配置，
 * 并且默认在{@link AnnotationSearchMode}中提供了一些预设了参数的常用扫描器。
 *
 * <h3>注解索引</h3>
 * <p>在{@link AbstractAnnotationScanner}以及{@link AnnotationProcessor}中提到的<em>垂直索引</em>与<em>水平索引</em>将遵循如下定义：<br />
 * 若以扫描的{@link AnnotatedElement}为原点建立坐标系，
 * 以被扫描到的注解所在的类层级作为y轴，同一类层级中注解被扫描到的顺序作为x轴，则有：
 * <ul>
 *     <li>
 *         <em>垂直索引</em>：即被扫描到的注解在y轴上的坐标，
 *         用于反映注解与根节点所在层级结构的相对位置 <br />
 *         该索引总是从{@link #VERTICAL_INDEX_START_POINT}开始递增；
 *     </li>
 *     <li>
 *         <em>水平索引</em>：即被扫描到的注解在x轴上的坐标，
 *         用于反映被扫描到的注解之间以根节点为参照物的相对位置关系。
 *         若两个注解垂直索引相同，则通过水平索引可以区分两注解被扫描的先后顺序。<br />
 *         该索引总是从{@link #HORIZONTAL_INDEX_START_POINT}开始递增。
 *     </li>
 * </ul>
 * 扫描器将可以通过两个坐标轴对应的索引该描述注解与被扫描的{@link AnnotatedElement}之间的位置关系。
 *
 * <h3>扫描顺序</h3>
 * <p>默认情况下，扫描器将按照广度优先遍历被扫描的{@link AnnotatedElement}的层级结构中的被注解元素。<br />
 * 同一层级中的注解，按照其所属{@link AnnotatedElement}的被扫描顺序、
 * 以及其在{@link AnnotatedElement#getDeclaredAnnotations()}返回的注解数组中的顺序决定。<br />
 * 若允许扫描元注解，则在获得注解对象时，将优先扫描该注解对象的层级结构，
 * 然后再继续扫描该与该注解处于统一层级，但是顺序靠后的注解。
 * <p>比如：现有类<em>X</em>，上有注解<em>A</em>，<em>A</em>又有元注解<em>B</em>；
 * 类<em>X</em>存在父类<em>Y</em>，上有注解<em>C</em>，<em>C</em>又有元注解<em>D</em>，
 * 现对<em>X</em>进行扫描，若不扫描元注解，则依次获得<em>A</em>，<em>C</em>；
 * 若扫描元注解，则依次获得<em>A</em>，<em>B</em>，<em>C</em>，<em>D</em>。
 *
 * <h3>覆盖实现</h3>
 * <p>{@link AbstractAnnotationScanner}定义了根据配置项从待扫描元素的层级结构中获取注解的基本逻辑，
 * 调用者需要在实现类中实现下述方法，以补全注解扫描相关功能的逻辑：<br />
 * 类的层级结构递归相关：
 * <ul>
 *     <li>{@link #collectAnnotationTypeIfNecessary(List, Class)}：递归扫描到的注解的元注解层级结构；</li>
 *     <li>{@link #collectSuperTypeIfNecessary(List, Class)}：递归注解接口类的层级结构；</li>
 *     <li>{@link #collectInterfaceTypeIfNecessary(List, Class)}：递归父类的层级结构；</li>
 * </ul>
 * 从层级结构中的类获取注解相关：
 * <ul>
 *     <li>{@link #getAnnotationFromType(Class, Class)}：从类上获取注解；</li>
 *     <li>{@link #getAnnotationsFromTypeDeclaredMethod(Class, Method)}：从类声明的方法上获取注解；</li>
 *     <li>{@link #getAnnotationsFromTypeDeclaredField(Class, Field)}：从类声明的属性上获取注解；</li>
 * </ul>
 *
 * @author huangchengxing
 * @see AnnotationFilter
 * @see AnnotationProcessor
 * @see AnnotationSearchMode
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
	protected final ScanOptions options;

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
	 * @param element   待扫描的元素
	 * @param processor 注解处理器
	 * @param filter    过滤器，若为空则不过滤任何注解
	 */
	@Override
	public void scan(AnnotatedElement element, AnnotationProcessor processor, AnnotationFilter filter) {
		if (Objects.isNull(element) || Objects.isNull(processor)) {
			return;
		}
		filter = ObjectUtils.defaultIfNull(filter, AnnotationFilter.FILTER_NOTHING);
		final Class<?> typeHierarchy = getTypeHierarchyFromElement(element);
		final Context context = new Context(
			element, VERTICAL_INDEX_START_POINT, HORIZONTAL_INDEX_START_POINT,
			Objects.nonNull(typeHierarchy) && typeHierarchy.isAnnotation()
		);

		// 元素没有可递归的层级结构，或者传入了注解类但是又不允许扫描元注解
		if (Objects.isNull(typeHierarchy) || (context.scanningMetaAnnotations && !options.isEnableScanMetaAnnotation())) {
			processAnnotation(context, processor, filter, element.getAnnotations());
			return;
		}

		// 元素存在可递归的层级结构，尝试递归层级获取注解
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
		final Set<Class<?>> accessedTypes = new LinkedHashSet<>();

		// 递归扫描目标元素的层级结构
		while (CollUtils.isNotEmpty(typeHierarchyDeque)) {
			++context.verticalIndex;

			final List<Class<?>> currTypeHierarchies = typeHierarchyDeque.removeFirst();
			final List<Class<?>> nextTypeHierarchies = new ArrayList<>();
			for (final Class<?> type : currTypeHierarchies) {
				if (!isNeedProcessType(type, accessedTypes)) {
					continue;
				}

				// 处理当前层待处理的类型
				final Annotation[] annotation = getAnnotations(context, type);
				processAnnotation(context, processor, filter, annotation);
				// 若本次扫描已经中断，则直接返回
				if (context.interrupted) {
					return;
				}

				// 搜集下一层需要处理的类型
				accessedTypes.add(type);
				collectTypeToQueue(context, nextTypeHierarchies, type);
			}

			// 进入下一层
			if (CollUtils.isNotEmpty(nextTypeHierarchies)) {
				typeHierarchyDeque.addLast(nextTypeHierarchies);
			}
		}
	}

	/**
	 * <p>使用处理器处理扫描到的注解，并令上下文的水平索引递增。<br />
	 * 若处理完注解对象后，{@link AnnotationProcessor#interrupted()}返回{@code true}，
	 * 则也会将{@link Context#interrupted}也标记为{@code true}，扫描器将终止后续的扫描行为
	 *
	 * <p>若允许扫描元注解，并且当前正在扫描的类不为元注解，则会对获得的注解进行一次扫描，
	 * 获取该注解的元注解。
	 */
	private void processAnnotation(
		Context context, AnnotationProcessor processor, AnnotationFilter filter, Annotation[] annotations) {
		for (final Annotation annotation : annotations) {
			if (processor.interrupted()) {
				context.interrupted = true;
				break;
			}
			if (!filter.test(annotation)) {
				continue;
			}
			processor.accept(context.verticalIndex, ++context.horizontalIndex, annotation);

			// 若允许扫描元注解，并且当前扫描器扫描的对象不为注解类，则以该注解类为对象建立一个嵌套的上下文，并发起一次扫描
			if (!context.scanningMetaAnnotations && options.isEnableScanMetaAnnotation()) {
				Context nested = new Context(annotation.annotationType(), context.verticalIndex, context.horizontalIndex, true);
				scanForElementHierarchy(
					nested,
					((verticalIndex, horizontalIndex, metaAnnotation) -> {
						processor.accept(verticalIndex, horizontalIndex, metaAnnotation);
						// 同步水平索引，保证整体水平索引有序递增
						context.horizontalIndex = nested.horizontalIndex;
					}),
					filter, annotation.annotationType()
				);
			}
		}
	}

    // ======================== 获取需要扫描的类对象 ========================

	/**
	 * 是否处理该类对象。当在下述情况下时，将不处理指定的类对象：
	 * <ul>
	 *     <li>类对象未通过{@link ScanOptions#getTypeFilter()}校验；</li>
	 *     <li>类对象是已被访问过的类，且{@link ScanOptions#isEnableScanAccessedType()}为{@code false}；</li>
	 * </ul>
	 */
	private boolean isNeedProcessType(Class<?> type, Set<Class<?>> accessedTypes) {
		if (!options.getTypeFilter().test(type)) {
			return false;
		}
		if (!accessedTypes.contains(type)) {
			return true;
		}
		return options.isEnableScanAccessedType();
	}

    /**
     * 收集当前类父接口、父类或者元注解类，将其加入队列用于下一次递归获取注解
     */
    private void collectTypeToQueue(Context context, List<Class<?>> nextTypeHierarchies, Class<?> type) {
    	// 正在扫描元注解
		if (context.scanningMetaAnnotations && options.isEnableScanMetaAnnotation()) {
			collectAnnotationTypeIfNecessary(nextTypeHierarchies, type);
			return;
		}
		// 不在扫描元注解
		if (options.isEnableScanSuperClass()) {
			collectSuperTypeIfNecessary(nextTypeHierarchies, type);
		}
		if (options.isEnableScanInterface()) {
			collectInterfaceTypeIfNecessary(nextTypeHierarchies, type);
		}
    }

    /**
     * 若{@link ScanOptions#isEnableScanMetaAnnotation}为{@code true}，则将目标类元注解也添加到队列
     *
     * @param nextTypeHierarchies  下一层级待处理的类队列
	 * @param type                 当前正在处理的类对象
     */
    protected abstract void collectAnnotationTypeIfNecessary(List<Class<?>> nextTypeHierarchies, Class<?> type);

    /**
     * 若{@link ScanOptions#isEnableScanInterface}为{@code true}，则将目标类的父接口也添加到队列
     *
     * @param nextTypeHierarchies  下一层级待处理的类队列
     * @param type                 当前正在处理的类对象
     */
    protected abstract void collectInterfaceTypeIfNecessary(List<Class<?>> nextTypeHierarchies, Class<?> type);

    /**
     * 若{@link ScanOptions#isEnableScanSuperClass}为{@code true}，则将目标类的父类也添加到队列
     *
     * @param nextTypeHierarchies 下一层级待处理的类队列
     * @param type                当前正在处理的类对象
     */
    protected abstract void collectSuperTypeIfNecessary(List<Class<?>> nextTypeHierarchies, Class<?> type);

	// ======================== 从类对象中获取所需注解 ========================

    /**
     * <ul>
     *     <li>若根元素是{@link Class}，则直接返回{@code type}上直接声明的注解；</li>
     *     <li>若根元素是{@link Method}，则返回{@code type}中，与根元素具有完全一致的签名的非桥接方法上直接声明的注解；</li>
     *     <li>若元素是{@link Field}，则返回{@code type}中，与跟原生具有一致的名称与类型的属性上直接声明的注解;</li>
     *     <li>若元素不为上述三者中的任意一种，则抛出异常；</li>
     * </ul>
     */
    private Annotation[] getAnnotations(Context context, Class<?> type) {
        AnnotatedElement element = context.source;
        if (element instanceof Class) {
			Class<?> sourceClass = (Class<?>)element;
            return getAnnotationFromType(type, sourceClass);
        }
        if (element instanceof Method) {
            Method sourceMethod = (Method)element;
            return getAnnotationsFromTypeDeclaredMethod(type, sourceMethod);
        }
        if (element instanceof Field) {
            Field sourceField = (Field)element;
            return getAnnotationsFromTypeDeclaredField(type, sourceField);
        }
        // 理论上不会进入该分支
        throw new IllegalArgumentException(String.format(
			"cannot get annotation from element [%s]", element
		));
    }

	/**
	 * 从类中的指定属性获取注解对象
	 *
	 * @param type    当前正在处理的类对象
	 * @param element 最开始扫描的对象
	 * @return 注解对象
	 */
	protected abstract Annotation[] getAnnotationsFromTypeDeclaredField(Class<?> type, Field element);

	/**
     * 从类中的指定方法获取注解对象
     *
     * @param type    当前正在处理的类对象
	 * @param element 最开始扫描的对象
     * @return 注解对象
     */
	protected abstract Annotation[] getAnnotationsFromTypeDeclaredMethod(Class<?> type, Method element);

	/**
	 * 从类获取注解对象
	 *
	 * @param type    当前正在处理的类对象
	 * @param element 最开始扫描的对象
	 * @return 注解对象
	 */
	protected abstract Annotation[] getAnnotationFromType(Class<?> type, Class<?> element);

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
		 * 当前扫描的层级对应的索引
		 */
		private int verticalIndex;

		/**
		 * 当前扫描的注解个数
		 */
		private int horizontalIndex;

		/**
		 * 本次扫描是否已经中断
		 */
		private boolean interrupted;

		/**
		 * 正在扫描元注解
		 */
		private final boolean scanningMetaAnnotations;

		/**
		 * 创建一个扫描上下文
		 *
		 * @param source          数据源
		 * @param verticalIndex   垂直索引
		 * @param horizontalIndex 水平索引
		 */
		public Context(AnnotatedElement source, int verticalIndex, int horizontalIndex, boolean scanningMetaAnnotations) {
			this.source = source;
			this.verticalIndex = verticalIndex;
			this.horizontalIndex = horizontalIndex;
			this.interrupted = false;
			this.scanningMetaAnnotations = scanningMetaAnnotations;
		}
	}

}
