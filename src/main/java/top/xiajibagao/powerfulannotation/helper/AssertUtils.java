package top.xiajibagao.powerfulannotation.helper;

import java.util.Objects;

/**
 * 断言工具类
 *
 * @author huangchengxing
 */
public class AssertUtils {

    private AssertUtils() {
    }

    public static void isTrue(boolean condition, String temp, Object... args) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(temp, args));
        }
    }

    public static void isTrue(boolean condition, String msg) {
        if (!condition) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void isFalse(boolean condition, String temp, Object... args) {
        isTrue(!condition, temp, args);
    }

    public static void isFalse(boolean condition, String msg) {
        isTrue(!condition, msg);
    }

    public static void notNull(Object target, String temp, Object... args) {
        isTrue(Objects.nonNull(target), temp, args);
    }

    public static void notNull(Object target, String msg) {
        isTrue(Objects.nonNull(target), msg);
    }

    public static void isAssignable(Class<?> superClass, Class<?> sourceClass, String temp, Object... args) {
        isTrue(superClass.isAssignableFrom(sourceClass), temp, args);
    }

    public static void equals(Object t1, Object t2, String temp, Object... args) {
        isTrue(Objects.equals(t1, t2), temp, args);
    }

    public static void equals(Object t1, Object t2, String msg) {
        isTrue(Objects.equals(t1, t2), msg);
    }

    public static void isNotEquals(Object t1, Object t2, String temp, Object... args) {
        isFalse(Objects.equals(t1, t2), temp, args);
    }

}
