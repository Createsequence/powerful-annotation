package top.xiajibagao.powerfulannotation.synthesis;

import java.lang.annotation.*;

/**
 * 令两个字段互为镜像，等同于类型为{@link RelationType#MIRROR_FOR}的{@link Link}注解
 *
 * @author huangchengxing
 */
@Link(type = RelationType.MIRROR_FOR)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface MirrorFor {

    @Link(attribute = "attribute", type = RelationType.MIRROR_FOR)
    String value() default "value";

    @Link(type = RelationType.MIRROR_FOR)
    String attribute() default "value";

}
