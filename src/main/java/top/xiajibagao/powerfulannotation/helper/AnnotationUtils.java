package top.xiajibagao.powerfulannotation.helper;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * 注解工具类
 *
 * @author huangchengxing
 */
public class AnnotationUtils {

    private AnnotationUtils() {
    }

    /**
     * 获取直接声明的注解
     *
     * @param element 注解元素
     * @return 直接声明的注解
     */
    public static Annotation[] getDeclaredAnnotations(AnnotatedElement element) {
        return ObjectUtil.isNotNull(element) ? element.getDeclaredAnnotations() : emptyAnnotations();
    }
    
    /**
     * 获取直接声明的注解
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @return 获取直接声明的注解
     */
    public static <T extends Annotation> T getDeclaredAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return ObjectUtil.isNull(element) ? null : element.getDeclaredAnnotation(annotationType);
    }

    /**
     * 获取一个空注解数组
     *
     * @return 空注解数组
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T[] emptyAnnotations() {
        return (T[])new Annotation[0];
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
