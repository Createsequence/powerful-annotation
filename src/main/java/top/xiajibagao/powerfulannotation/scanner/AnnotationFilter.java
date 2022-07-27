package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.text.CharSequenceUtil;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * 注解过滤器，默认提供了三种实现：
 * <ul>
 *     <li>{@link #NOT_JDK_ANNOTATION}：过滤{@link java.lang}, 与{@link javax}还有{@link com.sun}包下的注解；</li>
 *     <li>{@link #FILTER_NOTHING}：不过滤任何注解；</li>
 *     <li>{@link #FILTER_ANYTHING}：过滤任何注解；</li>
 * </ul>
 *
 * @author huangchengxing
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

    /**
     * 过滤JDK注解，包括{@link java.lang}, 与{@link javax}还有{@link com.sun}包下的注解
     */
    AnnotationFilter NOT_JDK_ANNOTATION = annotation -> !CharSequenceUtil.startWithAny(
        annotation.annotationType().getName(), "java.lang", "javax", "com.sum"
    );

    /**
     * 不过滤任何注解
     */
    AnnotationFilter FILTER_NOTHING = annotation -> true;

    /**
     * 过滤任何注解
     */
    AnnotationFilter FILTER_ANYTHING = annotation -> false;

}
