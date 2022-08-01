package top.xiajibagao.powerfulannotation.helper;

/**
 * 字符串工具类
 *
 * @author huangchengxing
 */
public class StrUtils {

    private StrUtils() {
    }

    public static boolean isNotStartWithAny(String target, String... charSequences) {
        return !isStartWithAny(target, charSequences);
    }

    public static boolean isStartWithAny(String target, String... charSequences) {
        for (String charSequence : charSequences) {
            if (target.startsWith(charSequence)) {
                return true;
            }
        }
        return false;
    }

}
