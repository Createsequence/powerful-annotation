package top.xiajibagao.powerfulannotation.annotation.attribute;

import top.xiajibagao.powerfulannotation.helper.Assert;
import top.xiajibagao.powerfulannotation.helper.ReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * {@link AnnotationAttribute}的基本实现，调用{@link #getValue()}后将会缓存返回值
 *
 * @author huangchengxing
 */
public class CacheableAnnotationAttribute implements AnnotationAttribute {

    private boolean valueInvoked;
    private Object value;

    private boolean defaultValueInvoked;
    private Object defaultValue;

    private final Annotation annotation;
    private final Method attribute;

    public CacheableAnnotationAttribute(Annotation annotation, Method attribute) {
        Assert.notNull(annotation, "annotation must not null");
        Assert.notNull(attribute, "attribute must not null");
        this.annotation = annotation;
        this.attribute = attribute;
        this.valueInvoked = false;
        this.defaultValueInvoked = false;
    }

    @Override
    public Annotation getAnnotation() {
        return this.annotation;
    }

    @Override
    public Method getAttribute() {
        return this.attribute;
    }

    @Override
    public Object getValue() {
        if (!valueInvoked) {
            valueInvoked = true;
            value = ReflectUtils.invoke(annotation, attribute);
        }
        return value;
    }

    @Override
    public boolean isValueEquivalentToDefaultValue() {
        if (!defaultValueInvoked) {
            defaultValue = attribute.getDefaultValue();
            defaultValueInvoked = true;
        }
        return Objects.equals(getValue(), defaultValue);
    }

}
