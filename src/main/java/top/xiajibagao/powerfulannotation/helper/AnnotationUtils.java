package top.xiajibagao.powerfulannotation.helper;

import cn.hutool.core.util.ObjectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

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

}
