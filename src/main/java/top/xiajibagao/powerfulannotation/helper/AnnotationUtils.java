package top.xiajibagao.powerfulannotation.helper;

import cn.hutool.core.collection.CollUtil;

import java.lang.annotation.*;
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

}
