package top.xiajibagao.powerfulannotation.helper;

import java.util.Objects;

/**
 * Object工具类
 *
 * @author huangchengxing
 */
public class ObjectUtils {

    private ObjectUtils() {
    }

    public static boolean isNotEquals(Object t1, Object t2) {
        return !Objects.equals(t1, t2);
    }

    public static <T> T defaultIfNull(T t1, T t2) {
        return Objects.isNull(t1) ? t2 : t1;
    }

}
