package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.text.CharSequenceUtil;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * <p>注解过滤器，用于在{@link AbstractAnnotationScanner}中过滤不需要的注解。<br />
 * 提供了四种默认实现：
 * <ul>
 *     <li>{@link #FILTER_JAVA}：过滤{@link java.lang}, 与{@link javax}还有{@link com.sun}包下的注解；</li>
 *     <li>{@link #FILTER_NOTHING}：不过滤任何注解；</li>
 *     <li>{@link #FILTER_ANYTHING}：过滤任何注解；</li>
 * </ul>
 *
 * @author huangchengxing
 * @see AbstractAnnotationScanner
 */
public interface AnnotationFilter extends Predicate<Annotation> {

	/**
	 * 是否保留该注解对象
	 *
	 * @param annotation 注解对象
	 * @return 是否
	 */
	@Override
	boolean test(Annotation annotation);

	// ===================== 默认实现 =====================

	/**
	 * 过滤JDK注解，包括{@link java.lang}, 与{@link javax}还有{@link com.sun}包下的注解
	 */
	AnnotationFilter FILTER_JAVA = new JavaAnnotationFilter();

	/**
	 * 不过滤任何注解
	 */
	AnnotationFilter FILTER_NOTHING = new NothingFilter();

	/**
	 * 过滤任何注解
	 */
	AnnotationFilter FILTER_ANYTHING = new AnythingFilter();

	/**
	 * 过滤JDK注解，包括{@link java.lang}, 与{@link javax}还有{@link com.sun}包下的注解
	 *
	 * @author huangchengxing
	 */
	class JavaAnnotationFilter implements AnnotationFilter {
		@Override
		public boolean test(Annotation annotation) {
			return !CharSequenceUtil.startWithAny(
				annotation.annotationType().getName(), "java.lang", "javax", "com.sum"
			);
		}
	}

	/**
	 * 不过滤任何注解
	 *
	 * @author huangchengxing
	 */
	class NothingFilter implements AnnotationFilter {
		@Override
		public boolean test(Annotation annotation) {
			return true;
		}
	}

	/**
	 * 过滤所有注解
	 *
	 * @author huangchengxing
	 */
	class AnythingFilter implements AnnotationFilter {
		@Override
		public boolean test(Annotation annotation) {
			return false;
		}
	}

	/**
	 * 组合过滤器
	 *
	 * @param filters 过滤器
	 * @return 组合的过滤器
	 */
	static AnnotationFilter combine(AnnotationFilter... filters) {
		return annotation -> Arrays.stream(filters)
			.allMatch(filter -> filter.test(annotation));
	}

}
