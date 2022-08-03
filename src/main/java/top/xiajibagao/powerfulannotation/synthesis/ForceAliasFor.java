package top.xiajibagao.powerfulannotation.synthesis;

import java.lang.annotation.*;

/**
 * 为字段强制指定别名，等同于类型为{@link RelationType#FORCE_ALIAS_FOR}的{@link Link}注解
 *
 * @author huangchengxing
 */
@Link(type = RelationType.FORCE_ALIAS_FOR)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface ForceAliasFor {

    /**
     * 产生关联的注解类型，当不指定时，默认指注释的属性所在的类
     *
     * @return 关联的注解类型
     */
    Class<? extends Annotation> annotation() default Annotation.class;

    /**
     * {@link #annotation()}指定注解中关联的属性
     *
     * @return 属性名
     */
    String attribute() default "value";

}
