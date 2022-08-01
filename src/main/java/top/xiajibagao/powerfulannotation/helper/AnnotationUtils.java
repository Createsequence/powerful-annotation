package top.xiajibagao.powerfulannotation.helper;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注解工具类，提供从{@link AnnotatedElement}及其层级结构中获取注解的方法
 *
 * @author huangchengxing
 */
public class AnnotationUtils {

    private AnnotationUtils() {
    }

    /**
     * 空注解对象数组
     */
    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    /**
     * 注解缓存
     */
    private static final Map<AnnotatedElement, WeakReference<Annotation[]>> ANNOTATED_ELEMENT_MAP = new ConcurrentHashMap<>(36);

    /**
     * 获取直接声明的注解
     *
     * @param element 注解元素
     * @return 直接声明的注解
     */
    public static Annotation[] getDeclaredAnnotations(AnnotatedElement element) {
        return Optional.ofNullable(ANNOTATED_ELEMENT_MAP.get(element))
            .map(WeakReference::get)
            .orElseGet(() -> {
                Annotation[] annotations = Objects.isNull(element) ?
                    emptyAnnotations() : element.getAnnotations();
                ANNOTATED_ELEMENT_MAP.put(element, new WeakReference<>(annotations));
                return annotations;
            });
    }
    
    /**
     * 获取直接声明的注解
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @return 获取直接声明的注解
     */
    public static <T extends Annotation> T getDeclaredAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return Objects.isNull(element) ? null : element.getDeclaredAnnotation(annotationType);
    }

    /**
     * 获取一个空注解数组
     *
     * @return 空注解数组
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T[] emptyAnnotations() {
        return (T[])EMPTY_ANNOTATIONS.clone();
    }

    /**
     * 方法是否为注解属性方法。 <br>
     * 方法无参数，且有返回值的方法认为是注解属性的方法。
     * 不包括{@code toString}, {@code hashCode}与{@code annotationType}
     *
     * @param method 方法
     */
    public static boolean isAttributeMethod(Method method) {
        return method.getParameterCount() == 0
            && method.getReturnType() != void.class
            && !"hashCode".equals(method.getName())
            && !"toString".equals(method.getName())
            && !"annotationType".equals(method.getName());
    }

}
