package top.xiajibagao.powerfulannotation.synthesis;

/**
 * 表示一个可以从当前接口的实现类中，获得特定的属性值
 *
 * @author huangchengxing
 */
@FunctionalInterface
public interface AnnotationAttributeValueProvider {

	/**
	 * 获取注解属性值
	 *
	 * @param attributeName  属性名称
	 * @param attributeType  属性类型
	 * @return 注解属性值
	 */
	Object getAttributeValue(String attributeName, Class<?> attributeType);

}
