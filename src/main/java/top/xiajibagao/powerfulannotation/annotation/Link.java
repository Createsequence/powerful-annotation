package top.xiajibagao.powerfulannotation.annotation;

import java.lang.annotation.*;

/**
 * <p>用于在同一注解中，或具有一定关联的不同注解的属性中，
 * 表明这些属性之间具有特定的关联关系。
 * 在通过{@link AnnotationSynthesizer}使用原始注解进行合成后，
 * 得到的合成注解在获取属性值时会根据该{@link Link}进行调整。
 *
 * <p>{@link Link}注解允许作为元注解使用，即当{@link Link}注解被注解在自定义注解时，
 * 则该自定义注解则为{@link Link}的扩展注解/子注解，它被{@link AnnotationSynthesizer}处理时也会被认为是一个{@link Link}。<br>
 * 默认情况，当一个属性或类同时被多个{@link Link}或其扩展注解标记时，将只有被声明在最上方的注解会生效。<br>
 * 比如，现有扩展注解{@code @LinkExtend1}与{@code @LinkExtend2}，若某注解的{@code value}字段情况如下：
 * <pre>{@code
 * @LinkExtend2(attribute = "attribute")
 * @LinkExtend1(attribute = "attribute")
 * @Link(attribute = "name")
 * String value() default "value";
 * }</pre>
 * 则该字段被处理时，将只有{@code @LinkExtend2}生效。
 *
 * @author huangchengxing
 * @see RelationType
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Link {

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

	/**
	 * {@link #attribute()}指定属性与当前注解的属性建的关联关系类型
	 *
	 * @return 关系类型
	 */
	RelationType type() default RelationType.MIRROR_FOR;

}
