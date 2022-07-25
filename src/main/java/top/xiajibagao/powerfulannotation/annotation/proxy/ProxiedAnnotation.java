package top.xiajibagao.powerfulannotation.annotation.proxy;

import java.lang.annotation.Annotation;

/**
 * 已经被代理过的注解对象
 *
 * @author huangchengxing
 */
public interface ProxiedAnnotation {

    /**
     * 获取被代理的注解对象
     *
     * @return 被代理的注解对象
     */
    Annotation getOriginal();

}
