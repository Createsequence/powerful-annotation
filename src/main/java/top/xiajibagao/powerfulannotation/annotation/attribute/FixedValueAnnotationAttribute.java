package top.xiajibagao.powerfulannotation.annotation.attribute;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.ToString;

/**
 * 总是返回固定值的注解属性
 *
 * @author huangchengxing
 */
@ToString(callSuper = true)
public class FixedValueAnnotationAttribute extends AbstractWrappedAnnotationAttribute  {

    private final Object value;

    public FixedValueAnnotationAttribute(AnnotationAttribute original, Object value) {
        super(original, null);
        this.value = value;
        Class<?> targetClass = ClassUtil.getClass(value);
        Assert.isAssignable(
            original.getAttributeType(), targetClass,
            "value type [{}] must be consistent with the original attribute type [{}]",
            targetClass, original.getAttributeType()
        );
    }

    @Override
    public boolean isValueEquivalentToDefaultValue() {
        return ObjectUtil.equals(original.getValue(), value);
    }

    @Override
    public Object getValue() {
        return value;
    }

}