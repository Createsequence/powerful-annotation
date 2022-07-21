package top.xiajibagao.powerfulannotation.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

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
     * 不包括{@code toString}, {@code hashCode}与{@code annotationType}
     *
     * @param method 方法
     */
    public static boolean isAttributeMethod(Method method) {
        return method.getParameterCount() == 0
            && method.getReturnType() != void.class
            && !ReflectUtil.isHashCodeMethod(method)
            && !ReflectUtil.isToStringMethod(method)
            && !CharSequenceUtil.equals("annotationType", method.getName());
    }

}
