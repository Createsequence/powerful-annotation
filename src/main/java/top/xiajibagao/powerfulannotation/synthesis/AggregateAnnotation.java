package top.xiajibagao.powerfulannotation.synthesis;

import java.lang.annotation.Annotation;

/**
 * 表示一组被聚合在一起的，具有一定关联的注解对象
 *
 * @author huangchengxing
 * @see SynthesizedAggregateAnnotation
 */
public interface AggregateAnnotation extends Annotation {

	/**
	 * 在聚合中是否存在的指定类型注解对象
	 *
	 * @param annotationType 注解类型
	 * @return 是否
	 */
	boolean isAnnotationPresent(Class<? extends Annotation> annotationType);

	/**
	 * 获取聚合中的全部注解对象
	 *
	 * @return 注解对象
	 */
	Annotation[] getAnnotations();

}
