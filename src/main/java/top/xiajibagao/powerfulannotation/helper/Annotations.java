package top.xiajibagao.powerfulannotation.helper;

import java.lang.annotation.Annotation;

/**
 * @author huangchengxing
 */
public class Annotations {

    private Annotations() {
    }

    /**
     * 空数组
     */
    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
    
    /**
     * 获取空注解数组
     *
     * @return 空数组
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T[] emptyArray() {
        return (T[])EMPTY_ANNOTATIONS;
    }

}
