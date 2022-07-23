package top.xiajibagao.powerfulannotation.helper;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 函数式接口工具类
 *
 * @author huangchengxing
 */
public class FuncUtils {

    private FuncUtils() {
    }

    /**
     * 什么都不做
     *
     * @return java.util.function.Consumer<T>
     */
    public static <T> Consumer<T> doNothing() {
        return t -> {};
    }

    /**
     * 永远返回true
     *
     * @return java.util.function.Predicate<T>
     */
    public static <T> Predicate<T> alwaysTrue() {
        return t -> true;
    }

    /**
     * 永远返回false
     *
     * @return java.util.function.Predicate<T>
     */
    public static <T> Predicate<T> alwaysFalse() {
        return t -> false;
    }
    
}
