package top.xiajibagao.powerfulannotation.synthesis.proxy;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import top.xiajibagao.powerfulannotation.helper.AnnotationUtils;
import top.xiajibagao.powerfulannotation.synthesis.SynthesizedAnnotation;
import top.xiajibagao.powerfulannotation.synthesis.attribute.AnnotationAttributeValueProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 合成注解代理类，用于为{@link SynthesizedAnnotation}生成对应的合成注解代理对象
 *
 * @author huangchengxing
 * @see SynthesizedAnnotation
 * @see AnnotationAttributeValueProvider
 */
public class SynthesizedAnnotationInvocationHandler implements InvocationHandler {

	private final AnnotationAttributeValueProvider annotationAttributeValueProvider;
	private final SynthesizedAnnotation annotation;
	private final Map<String, BiFunction<Method, Object[], Object>> methods;

	/**
	 * 创建一个注解代理对象
	 *
	 * @param annotationType                   注解类型
	 * @param annotationAttributeValueProvider 注解属性值获取器
	 * @param annotation                       合成注解
	 * @param <T>                              注解类型
	 * @return 代理注解
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T createProxy(
			Class<T> annotationType,
			AnnotationAttributeValueProvider annotationAttributeValueProvider,
			SynthesizedAnnotation annotation) {
		if (ObjectUtil.isNull(annotation)) {
			return null;
		}
		final SynthesizedAnnotationInvocationHandler proxyHandler = new SynthesizedAnnotationInvocationHandler(annotationAttributeValueProvider, annotation);
		if (ObjectUtil.isNull(annotation)) {
			return null;
		}
		return (T) Proxy.newProxyInstance(
				annotationType.getClassLoader(),
				new Class[]{annotationType, ProxiedSynthesizedAnnotation.class},
				proxyHandler
		);
	}

	/**
	 * 创建一个代理注解，生成的代理对象将是{@link ProxiedSynthesizedAnnotation}与指定的注解类的子类。
	 *
	 * @param annotationType 注解类型
	 * @param annotation     合成注解
	 * @param <T>            注解类型
	 * @return 代理注解
	 */
	public static <T extends Annotation> T createProxy(
			Class<T> annotationType, SynthesizedAnnotation annotation) {
		return createProxy(annotationType, annotation, annotation);
	}

	/**
	 * 该类是否为通过{@code SynthesizedAnnotationInvocationHandler}生成的代理类
	 *
	 * @param annotationType 注解类型
	 * @return 是否
	 */
	public static boolean isProxyAnnotation(Class<?> annotationType) {
		return ClassUtil.isAssignable(ProxiedSynthesizedAnnotation.class, annotationType);
	}

	/**
	 * 创建一个代理对象
	 *
	 * @param annotationAttributeValueProvider 属性值提供者
	 * @param annotation 代理的合成注解
	 */
	SynthesizedAnnotationInvocationHandler(
		AnnotationAttributeValueProvider annotationAttributeValueProvider, SynthesizedAnnotation annotation) {
		Assert.notNull(annotationAttributeValueProvider, "annotationAttributeValueProvider must not null");
		Assert.notNull(annotation, "annotation must not null");
		this.annotationAttributeValueProvider = annotationAttributeValueProvider;
		this.annotation = annotation;
		this.methods = new HashMap<>(9);
		loadMethods();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		return Opt.ofNullable(methods.get(method.getName()))
				.map(m -> m.apply(method, args))
				.orElseGet(() -> ReflectUtil.invoke(this, method, args));
	}

	// ========================= 代理方法 =========================

	void loadMethods() {
		methods.put("toString", (method, args) -> proxyToString());
		methods.put("hashCode", (method, args) -> proxyHashCode());
		methods.put("getSynthesizedAnnotation", (method, args) -> proxyGetSynthesizedAnnotation());
		methods.put("getRoot", (method, args) -> annotation.getRoot());
		methods.put("getVerticalDistance", (method, args) -> annotation.getVerticalDistance());
		methods.put("getHorizontalDistance", (method, args) -> annotation.getHorizontalDistance());
		methods.put("hasAttribute", (method, args) -> annotation.hasAttribute((String) args[0], (Class<?>) args[1]));
		methods.put("getAttributes", (method, args) -> annotation.getAttributes());
		methods.put("setAttribute", (method, args) -> {
			throw new UnsupportedOperationException("proxied annotation can not reset attributes");
		});
		methods.put("getAttributeValue", (method, args) -> annotation.getAttributeValue((String) args[0]));
		methods.put("annotationType", (method, args) -> annotation.annotationType());
		for (final Method declaredMethod : ClassUtil.getDeclaredMethods(annotation.getAnnotation().annotationType())) {
			methods.put(declaredMethod.getName(), (method, args) -> proxyAttributeValue(method));
		}
	}

	/**
	 * 代理toString方法
	 */
	private String proxyToString() {
		final String attributes = Stream.of(ClassUtil.getDeclaredMethods(annotation.getAnnotation().annotationType()))
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
		return Objects.hash(annotationAttributeValueProvider, annotation);
	}

	/**
	 * 代理获取合成注解对象的方法
	 */
	private Object proxyGetSynthesizedAnnotation() {
		return annotation;
	}

	/**
	 * 获取代理获取属性的方法
	 */
	private Object proxyAttributeValue(Method attributeMethod) {
		return annotationAttributeValueProvider.getAttributeValue(attributeMethod.getName(), attributeMethod.getReturnType());
	}

}
