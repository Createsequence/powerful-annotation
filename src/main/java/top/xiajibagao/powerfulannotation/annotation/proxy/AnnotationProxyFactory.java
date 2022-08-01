package top.xiajibagao.powerfulannotation.annotation.proxy;

import top.xiajibagao.powerfulannotation.annotation.AnnotationAttributeValueProvider;
import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * 代理注解静态工厂，用于合成一些与原始注解具有不一样属性值的代理注解
 *
 * @author huangchengxing
 */
public class AnnotationProxyFactory {

    private AnnotationProxyFactory() {
    }

    /**
     * 根据一个注解，创建一个注解的代理对象，
     * 该代理对象将实现{@code annotationType}与{@link ProxiedAnnotation}接口。
     * 并且，代理对象的属性值将与的注解的属性值保持一致。
     *
     * @param annotationType 注解类型
     * @param annotation 注解
     * @param <T> 代理注解类型
     * @return T
     */
    public static <T extends Annotation> T get(Class<T> annotationType, HierarchicalAnnotation<? extends Annotation> annotation) {
        return get(annotationType, annotation.getAnnotation(), annotation);
    }

    /**
     * 创建一个注解的代理对象，该代理对象将实现{@code annotationType}与{@link ProxiedAnnotation}接口。
     * 并且，当获取代理对象属性值时，将从{@code valueProvider}中获取。
     *
     * @param annotationType 代理注解类型
     * @param original 被代理的注解
     * @param valueProvider 代理对象属性值提供者
     * @param <T> 代理注解类型
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T get(
        Class<T> annotationType, Annotation original, AnnotationAttributeValueProvider valueProvider) {
        if (Objects.isNull(original) || Objects.isNull(annotationType) || Objects.isNull(valueProvider)) {
            return null;
        }
        AnnotationInvocationHandler invocationHandler = new AnnotationInvocationHandler(valueProvider, original);
        return (T)Proxy.newProxyInstance(
            annotationType.getClassLoader(),
            new Class[]{ annotationType, ProxiedAnnotation.class },
            invocationHandler
        );
    }

    /**
     * 注解是否为由代理工厂生成的代理注解
     *
     * @param annotation 注解
     * @return 是否
     */
    public static boolean isProxied(Annotation annotation) {
        return annotation instanceof ProxiedAnnotation;
    }

    /**
     * 获取被代理前的原始注解
     *
     * @param annotation 被代理的注解对象
     * @param <T> 代理注解类型
     * @return 代理前的原始注解
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getOriginal(T annotation) {
        Annotation original = annotation;
        while (isProxied(original)) {
            original = ((ProxiedAnnotation) annotation).getOriginal();
        }
        return (T)original;
    }

}
