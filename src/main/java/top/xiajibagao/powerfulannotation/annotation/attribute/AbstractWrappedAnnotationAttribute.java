package top.xiajibagao.powerfulannotation.annotation.attribute;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link WrappedAnnotationAttribute}的基本实现
 *
 * @author huangchengxing
 */
@RequiredArgsConstructor
@Getter
public abstract class AbstractWrappedAnnotationAttribute implements WrappedAnnotationAttribute {

    @NonNull
    protected final AnnotationAttribute original;

    protected final AnnotationAttribute linked;

    @Override
    public AnnotationAttribute getNonWrappedOriginal() {
        AnnotationAttribute curr = null;
        AnnotationAttribute next = original;
        while (next != null) {
            curr = next;
            next = next.isWrapped() ? ((WrappedAnnotationAttribute)curr).getOriginal() : null;
        }
        return curr;
    }

    @Override
    public Collection<AnnotationAttribute> getAllLinkedNonWrappedAttributes() {
        List<AnnotationAttribute> leafAttributes = new ArrayList<>();
        collectLeafAttribute(this, leafAttributes);
        return leafAttributes;
    }

    private void collectLeafAttribute(AnnotationAttribute curr, List<AnnotationAttribute> leafAttributes) {
        if (ObjectUtil.isNull(curr)) {
            return;
        }
        if (!curr.isWrapped()) {
            leafAttributes.add(curr);
            return;
        }
        WrappedAnnotationAttribute wrappedAttribute = (WrappedAnnotationAttribute)curr;
        collectLeafAttribute(wrappedAttribute.getOriginal(), leafAttributes);
        collectLeafAttribute(wrappedAttribute.getLinked(), leafAttributes);
    }

}
