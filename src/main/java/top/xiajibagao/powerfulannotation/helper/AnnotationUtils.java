package top.xiajibagao.powerfulannotation.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import top.xiajibagao.powerfulannotation.scanner.AnnotationScanner;
import top.xiajibagao.powerfulannotation.synthesis.GenericSynthesizedAggregateAnnotation;
import top.xiajibagao.powerfulannotation.synthesis.SynthesizedAggregateAnnotation;
import top.xiajibagao.powerfulannotation.synthesis.proxy.SynthesizedAnnotationInvocationHandler;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 注解工具类
 *
 * @author huangchengxing
 */
public class AnnotationUtils {

    private AnnotationUtils() {
    }

    private static final Set<Class<? extends Annotation>> JDK_ANNOTATIONS = Collections.unmodifiableSet(CollUtil.newHashSet(
        Target.class, Retention.class, Inherited.class, Documented.class,
        SuppressWarnings.class, Override.class, Deprecated.class
    ));

    /**
     * 是否为Jdk自带的元注解
     *
     * @param annotationType 注解类型
     * @return boolean
     */
    public static boolean isJdkAnnotation(Class<? extends Annotation> annotationType) {
        return JDK_ANNOTATIONS.contains(annotationType);
    }

    /**
     * 是否不为Jdk自带的元注解
     *
     * @param annotationType 注解类型
     * @return boolean
     */
    public static boolean isNotJdkAnnotation(Class<? extends Annotation> annotationType) {
        return !isJdkAnnotation(annotationType);
    }

    /**
     * 方法是否为注解属性方法。 <br>
     * 方法无参数，且有返回值的方法认为是注解属性的方法。
     *
     * @param method 方法
     */
    public static boolean isAttributeMethod(Method method) {
        return method.getParameterCount() == 0 && method.getReturnType() != void.class;
    }

    /**
     * 该注解对象是否为通过代理类生成的合成注解
     *
     * @param annotation 注解对象
     * @return 是否
     * @see SynthesizedAnnotationInvocationHandler#isProxyAnnotation(Class)
     */
    public static boolean isSynthesizedAnnotation(Annotation annotation) {
        return SynthesizedAnnotationInvocationHandler.isProxyAnnotation(annotation.getClass());
    }

    /**
     * 将指定注解实例与其元注解转为合成注解
     *
     * @param annotationType 注解类
     * @param annotations    注解对象
     * @param <T>            注解类型
     * @return 合成注解
     * @see SynthesizedAggregateAnnotation
     */
    public static <T extends Annotation> T getSynthesizedAnnotation(Class<T> annotationType, Annotation... annotations) {
        // TODO 缓存合成注解信息，避免重复解析
        return Opt.ofNullable(annotations)
            .filter(ArrayUtil::isNotEmpty)
            .map(AnnotationUtils::aggregatingFromAnnotationWithMeta)
            .map(a -> a.synthesize(annotationType))
            .get();
    }

    /**
     * <p>获取元素上距离指定元素最接近的合成注解
     * <ul>
     *     <li>若元素是类，则递归解析全部父类和全部父接口上的注解;</li>
     *     <li>若元素是方法、属性或注解，则只解析其直接声明的注解;</li>
     * </ul>
     *
     * <p>注解合成规则如下：
     * 若{@code AnnotatedEle}按顺序从上到下声明了A，B，C三个注解，且三注解存在元注解如下：
     * <pre>
     *    A -&gt; M3
     *    B -&gt; M1 -&gt; M2 -&gt; M3
     *    C -&gt; M2 -&gt; M3
     * </pre>
     * 此时入参{@code annotationType}类型为{@code M2}，则最终将优先返回基于根注解B合成的合成注解
     *
     * @param annotatedEle   {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param annotationType 注解类
     * @param <T>            注解类型
     * @return 合成注解
     * @see SynthesizedAggregateAnnotation
     */
    public static <T extends Annotation> T getSynthesizedAnnotation(AnnotatedElement annotatedEle, Class<T> annotationType) {
        T target = annotatedEle.getAnnotation(annotationType);
        if (ObjectUtil.isNotNull(target)) {
            return target;
        }
        return AnnotationScanner.DIRECTLY
            .getAnnotationsIfSupport(annotatedEle).stream()
            .map(annotation -> getSynthesizedAnnotation(annotationType, annotation))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    /**
     * 获取元素上所有指定注解
     * <ul>
     *     <li>若元素是类，则递归解析全部父类和全部父接口上的注解;</li>
     *     <li>若元素是方法、属性或注解，则只解析其直接声明的注解;</li>
     * </ul>
     *
     * <p>注解合成规则如下：
     * 若{@code AnnotatedEle}按顺序从上到下声明了A，B，C三个注解，且三注解存在元注解如下：
     * <pre>
     *    A -&gt; M1 -&gt; M2
     *    B -&gt; M3 -&gt; M1 -&gt; M2
     *    C -&gt; M2
     * </pre>
     * 此时入参{@code annotationType}类型为{@code M1}，则最终将返回基于根注解A与根注解B合成的合成注解。
     *
     * @param annotatedEle   {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param annotationType 注解类
     * @param <T>            注解类型
     * @return 合成注解
     * @see SynthesizedAggregateAnnotation
     */
    public static <T extends Annotation> List<T> getAllSynthesizedAnnotations(AnnotatedElement annotatedEle, Class<T> annotationType) {
        return AnnotationScanner.DIRECTLY
            .getAnnotationsIfSupport(annotatedEle).stream()
            .map(annotation -> getSynthesizedAnnotation(annotationType, annotation))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * 对指定注解对象进行聚合
     *
     * @param annotations 注解对象
     * @return 聚合注解
     */
    public static SynthesizedAggregateAnnotation aggregatingFromAnnotation(Annotation... annotations) {
        return new GenericSynthesizedAggregateAnnotation(Arrays.asList(annotations), AnnotationScanner.NOTHING);
    }

    /**
     * 对指定注解对象及其元注解进行聚合
     *
     * @param annotations 注解对象
     * @return 聚合注解
     */
    public static SynthesizedAggregateAnnotation aggregatingFromAnnotationWithMeta(Annotation... annotations) {
        return new GenericSynthesizedAggregateAnnotation(Arrays.asList(annotations), AnnotationScanner.DIRECTLY_AND_META_ANNOTATION);
    }

}
