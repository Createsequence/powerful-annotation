package top.xiajibagao.powerfulannotation.proxy;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.NonNull;
import top.xiajibagao.powerfulannotation.aggerate.AggregatedAnnotation;
import top.xiajibagao.powerfulannotation.helper.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 代理注解静态工厂，用于合成一些与原始注解具有不一样属性值的代理注解
 *
 * @author huangchengxing
 */
public class AnnotationProxyFactory {

    private AnnotationProxyFactory() {
    }

    /**
     * 根据一个聚合注解，创建一个注解的代理对象，
     * 该代理对象将实现{@code annotationType}与{@link ProxiedAnnotation}接口。
     * 并且，代理对象的属性值将与的聚合注解的属性值保持一致。
     *
     * @param annotationType 注解类型
     * @param annotation 聚合注解
     * @param <T> 代理注解类型
     * @return T
     */
    public static <T extends Annotation> T get(Class<T> annotationType, AggregatedAnnotation<? extends Annotation> annotation) {
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
        if (ObjectUtil.isNull(original) || ObjectUtil.isNull(annotationType) || ObjectUtil.isNull(valueProvider)) {
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

    /**
     * 被代理的注解的方法调用拦截器
     */
    private static class AnnotationInvocationHandler implements InvocationHandler {

        /**
         * 代理方法
         */
        private final Map<String, BiFunction<Method, Object[], Object>> methods;

        /**
         * 代理注解的属性值提供者
         */
        private final AnnotationAttributeValueProvider valueProvider;

        /**
         * 被代理的注解
         */
        private final Annotation annotation;

        /**
         * 创建一个代理方法处理器
         *
         * @param valueProvider 代理注解的属性值提供者
         * @param annotation 被代理的注解
         */
        AnnotationInvocationHandler(
            @NonNull AnnotationAttributeValueProvider valueProvider, @NonNull Annotation annotation) {
            this.methods = new HashMap<>();
            this.valueProvider = valueProvider;
            this.annotation = annotation;
            loadMethods();
        }

        /**
         * 调用被代理的方法
         *
         * @param proxy 代理对象
         * @param method 方法
         * @param args 参数
         * @return 返回值
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return Opt.ofNullable(methods.get(method.getName()))
                .map(m -> m.apply(method, args))
                .orElseGet(() -> ReflectUtil.invoke(this, method, args));
        }

        // ============================== 代理方法 ==============================

        /**
         * 预加载需要代理的方法
         */
        void loadMethods() {
            methods.put("equals", (method, args) -> proxyEquals(args[0]));
            methods.put("toString", (method, args) -> proxyToString());
            methods.put("hashCode", (method, args) -> proxyHashCode());
            methods.put("annotationType", (method, args) -> proxyAnnotationType());
            methods.put("getOriginal", (method, args) -> proxyGetOriginal());
            Stream.of(ClassUtil.getDeclaredMethods(annotation.annotationType()))
                .filter(AnnotationUtils::isAttributeMethod)
                .forEach(attribute -> methods.put(attribute.getName(), (method, args) -> proxyAttributeValue(method)));
        }

        /**
         * 代理toString方法
         */
        private String proxyToString() {
            final String attributes = Stream.of(ClassUtil.getDeclaredMethods(annotation.annotationType()))
                .filter(AnnotationUtils::isAttributeMethod)
                .map(method -> CharSequenceUtil.format(
                    "{}={}", method.getName(), proxyAttributeValue(method))
                )
                .collect(Collectors.joining(", "));
            return CharSequenceUtil.format("@{}({})", annotation.annotationType().getName(), attributes);
        }

        /**
         * 代理hashCode方法
         */
        private int proxyHashCode() {
            return this.hashCode();
        }

        /**
         * 代理equals方法
         */
        private boolean proxyEquals(Object o) {
            return ObjectUtil.equals(this, o);
        }

        /**
         * 获取代理获取属性的方法
         */
        private Object proxyAttributeValue(Method attributeMethod) {
            return valueProvider.getAttributeValue(attributeMethod.getName(), attributeMethod.getReturnType());
        }

        /**
         * 代理getOriginal方法
         */
        private Annotation proxyGetOriginal() {
            return annotation;
        }

        /**
         * 代理annotationType方法
         */
        private Class<? extends Annotation> proxyAnnotationType() {
            return annotation.annotationType();
        }

    }
    
    /**
     * 已经被代理过的注解对象
     *
     * @author huangchengxing 
     */
    private interface ProxiedAnnotation {

        /**
         * 获取被代理的注解对象
         *
         * @return 被代理的注解对象
         */
        Annotation getOriginal();

    }

}
