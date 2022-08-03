package top.xiajibagao.powerfulannotation.helper;

import lombok.SneakyThrows;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射工具类
 *
 * @author huangchengxing
 */
public class ReflectUtils {

    /**
     * 基本数据类型与对应包装类的映射关系
     */
    private static final Map<Class<?>, Class<?>> WRAPPER_PRIMITIVE_MAP = new HashMap<>(8);
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new HashMap<>(8);

    /**
     * 注解缓存
     */
    private static final Map<Class<?>, WeakReference<Method[]>> CLASS_DECLARED_METHOD_MAP = new ConcurrentHashMap<>(36);

    static {
        WRAPPER_PRIMITIVE_MAP.put(Boolean.class, boolean.class);
        WRAPPER_PRIMITIVE_MAP.put(Byte.class, byte.class);
        WRAPPER_PRIMITIVE_MAP.put(Character.class, char.class);
        WRAPPER_PRIMITIVE_MAP.put(Double.class, double.class);
        WRAPPER_PRIMITIVE_MAP.put(Float.class, float.class);
        WRAPPER_PRIMITIVE_MAP.put(Integer.class, int.class);
        WRAPPER_PRIMITIVE_MAP.put(Long.class, long.class);
        WRAPPER_PRIMITIVE_MAP.put(Short.class, short.class);

        for (Map.Entry<Class<?>, Class<?>> entry : WRAPPER_PRIMITIVE_MAP.entrySet()) {
            PRIMITIVE_WRAPPER_MAP.put(entry.getValue(), entry.getKey());
        }
    }

    private ReflectUtils() {
    }

    @SneakyThrows
    public static Object invoke(Object target, Method method, Object... args) {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        return method.invoke(Modifier.isStatic(method.getModifiers()) ? null : target, args);
    }

    public static Method[] getDeclaredMethods(Class<?> targetClass) {
        return Optional.ofNullable(CLASS_DECLARED_METHOD_MAP.get(targetClass))
            .map(WeakReference::get)
            .orElseGet(() -> {
                Method[] methods = targetClass.getDeclaredMethods();
                WeakReference<Method[]> reference = new WeakReference<>(methods);
                CLASS_DECLARED_METHOD_MAP.put(targetClass, reference);
                return methods;
            });
    }

    public static Method getDeclaredMethod(Class<?> targetClass, String name) {
        return Arrays.stream(getDeclaredMethods(targetClass))
            .filter(m -> Objects.equals(m.getName(), name))
            .findFirst()
            .orElse(null);
    }

    public static boolean isAssignable(Class<?> superClass, Class<?> sourceClass) {
        if (superClass.isAssignableFrom(sourceClass)) {
            return true;
        }
        if (superClass.isPrimitive()) {
            // 原始类型
            Class<?> resolvedPrimitive = WRAPPER_PRIMITIVE_MAP.get(sourceClass);
            return superClass.equals(resolvedPrimitive);
        } else {
            // 包装类型
            Class<?> resolvedWrapper = PRIMITIVE_WRAPPER_MAP.get(sourceClass);
            return resolvedWrapper != null && superClass.isAssignableFrom(resolvedWrapper);
        }
    }

}
