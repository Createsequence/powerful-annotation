package top.xiajibagao.powerfulannotation.repeatable;

import java.lang.annotation.*;

/**
 * <p>表示当前注解存在容器注解，效果类似于{@link Repeatable}
 * 注解在注解类上，使得指向的注解类称为当前注解类的容器注解。 <br>
 * 该注解不能与{@link Repeatable}同时使用，并且在任何情况下，
 * 一个注解最多只能被作为一个注解的容器注解，一个注解也仅能指定一个容器注解
 *
 * @author huangchengxing
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface RepeatableBy {

    /**
     * 产生关联的注解类型，当不指定时，默认指注释的属性所在的类
     */
    Class<? extends Annotation> annotation();

    /**
     * {@link #annotation()}指定注解中关联的属性
     */
    String attribute() default "value";

}
