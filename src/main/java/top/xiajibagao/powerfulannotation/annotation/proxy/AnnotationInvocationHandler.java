package top.xiajibagao.powerfulannotation.annotation.proxy;

import lombok.NonNull;
import top.xiajibagao.powerfulannotation.annotation.AnnotationAttributeValueProvider;
import top.xiajibagao.powerfulannotation.helper.Annotations;
import top.xiajibagao.powerfulannotation.helper.ReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 被代理的注解的方法调用拦截器
 *
 * @author huangchengxing
 */
public class AnnotationInvocationHandler implements InvocationHandler {

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
     * @param annotation    被代理的注解
     */
    AnnotationInvocationHandler(@NonNull AnnotationAttributeValueProvider valueProvider, @NonNull Annotation annotation) {
        this.methods = new HashMap<>();
        this.valueProvider = valueProvider;
        this.annotation = Annotations.isSynthesizedAnnotation(annotation)
            ? ((ProxiedAnnotation)annotation).getOriginal() : annotation;
        loadMethods();
    }

    /**
     * 调用被代理的方法
     *
     * @param proxy  代理对象
     * @param method 方法
     * @param args   参数
     * @return 返回值
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return Optional.ofNullable(methods.get(method.getName()))
            .map(m -> m.apply(method, args))
            .orElseGet(() -> ReflectUtils.invoke(this, method, args));
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
        Stream.of(ReflectUtils.getDeclaredMethods(annotation.annotationType()))
            .filter(Annotations::isAttributeMethod)
            .forEach(attribute -> methods.put(attribute.getName(), (method, args) -> proxyAttributeValue(method)));
    }

    /**
     * 代理toString方法
     */
    private String proxyToString() {
        final String attributes = Stream.of(ReflectUtils.getDeclaredMethods(annotation.annotationType()))
            .filter(Annotations::isAttributeMethod)
            .map(method -> String.format("%s=%s", method.getName(), proxyAttributeValue(method)))
            .collect(Collectors.joining(", "));
        return String.format("@%s(%s)", annotation.annotationType()
            .getName(), attributes);
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
        return Objects.equals(this, o);
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
