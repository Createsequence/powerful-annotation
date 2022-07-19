package top.xiajibagao.powerfulannotation.annotation;

import cn.hutool.core.annotation.Alias;
import top.xiajibagao.powerfulannotation.synthesis.RelationType;

import java.lang.annotation.*;

/**
 * <p>{@link Link}的子注解。表示“原始属性”将强制作为“关联属性”的别名。效果等同于在“原始属性”上添加{@link Alias}注解，
 * 任何情况下，获取“关联属性”的值都将直接返回“原始属性”的值
 *
 * @author huangchengxing
 * @see Link
 * @see RelationType#FORCE_ALIAS_FOR
 */
@Link(type = RelationType.FORCE_ALIAS_FOR)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface ForceAliasFor {

	/**
	 * 产生关联的注解类型，当不指定时，默认指注释的属性所在的类
	 *
	 * @return 关联注解类型
	 */
	@Link(annotation = Link.class, attribute = "annotation", type = RelationType.FORCE_ALIAS_FOR)
	Class<? extends Annotation> annotation() default Annotation.class;

	/**
	 * {@link #annotation()}指定注解中关联的属性
	 *
	 * @return 关联的属性
	 */
	@Link(annotation = Link.class, attribute = "attribute", type = RelationType.FORCE_ALIAS_FOR)
	String attribute() default "value";
}
