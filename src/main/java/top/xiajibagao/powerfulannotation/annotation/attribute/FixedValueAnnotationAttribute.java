package top.xiajibagao.powerfulannotation.annotation.attribute;

import top.xiajibagao.powerfulannotation.helper.Assert;

import java.util.Objects;

/**
 * 总是返回固定值的注解属性
 *
 * @author huangchengxing
 */
public class FixedValueAnnotationAttribute extends AbstractWrappedAnnotationAttribute  {

    private final Object value;

    public FixedValueAnnotationAttribute(AnnotationAttribute original, Object value) {
        super(original, null);
        this.value = value;
        Class<?> targetClass = value.getClass();
        Assert.isAssignable(
            original.getAttributeType(), targetClass,
            "value type [%s] must be consistent with the original attribute type [%s]",
            targetClass, original.getAttributeType()
        );
    }

    @Override
    public boolean isValueEquivalentToDefaultValue() {
        return Objects.equals(original.getValue(), value);
    }

    @Override
    public Object getValue() {
        return value;
    }

}
