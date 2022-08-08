package top.xiajibagao.powerfulannotation.annotation.attribute;

import lombok.NonNull;

/**
 * 表示一个作为另一注解属性别名的别名属性，一般与{@link ForceAliasedAnnotationAttribute}或{@link AliasedAnnotationAttribute}成对出现
 *
 * @author huangchengxing
 */
public class AliasAnnotationAttribute extends AbstractWrappedAnnotationAttribute {

    public AliasAnnotationAttribute(@NonNull AnnotationAttribute original, AnnotationAttribute linked) {
        super(original, linked);
    }

    @Override
    public Object getValue() {
        return original.getValue();
    }

    @Override
    public boolean isValueEquivalentToDefaultValue() {
        return original.isValueEquivalentToDefaultValue();
    }

}
