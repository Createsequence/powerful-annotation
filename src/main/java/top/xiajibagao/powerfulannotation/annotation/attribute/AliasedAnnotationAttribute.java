package top.xiajibagao.powerfulannotation.annotation.attribute;

import top.xiajibagao.powerfulannotation.synthesis.RelationType;

/**
 * <p>表示一个具有别名的属性。
 * 当别名属性值为默认值时，优先返回原属性的值，当别名属性不为默认值时，优先返回别名属性的值
 *
 * @author huangchengxing
 * @see RelationType#ALIAS_FOR
 */
public class AliasedAnnotationAttribute extends AbstractWrappedAnnotationAttribute {

	public AliasedAnnotationAttribute(AnnotationAttribute origin, AnnotationAttribute linked) {
		super(origin, linked);
	}

	/**
	 * 若{@link #linked}为默认值，则返回{@link #original}的值，否则返回{@link #linked}的值
	 *
	 * @return 属性值
	 */
	@Override
	public Object getValue() {
		return linked.isValueEquivalentToDefaultValue() ? original.getValue() : linked.getValue();
	}

	/**
	 * 当{@link #original}与{@link #linked}都为默认值时返回{@code true}
	 *
	 * @return 是否
	 */
	@Override
	public boolean isValueEquivalentToDefaultValue() {
		return linked.isValueEquivalentToDefaultValue() && original.isValueEquivalentToDefaultValue();
	}
}
