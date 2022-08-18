package top.xiajibagao.powerfulannotation.repeatable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 表示一个空的{@link RepeatableMappingRegistry}
 *
 * @author huangchengxing
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoneRepeatableMappingRegistry implements RepeatableMappingRegistry {

    public static final NoneRepeatableMappingRegistry INSTANCE = new NoneRepeatableMappingRegistry();

    @Override
    public void registerMappingParser(RepeatableMappingParser mappingParser) {
        // do nothing
    }

    @Override
    public void register(Class<? extends Annotation> annotationType) {
        // do nothing
    }

    @Override
    public boolean isContainer(Class<? extends Annotation> annotationType) {
        return false;
    }

    @Override
    public boolean hasContainer(Class<? extends Annotation> annotationType) {
        return false;
    }

    @Override
    public boolean isContainerOf(Class<? extends Annotation> elementType, Class<? extends Annotation> containerType) {
        return false;
    }

    @Override
    public List<RepeatableMapping> getContainers(Class<? extends Annotation> annotationType) {
        return Collections.emptyList();
    }

    @Override
    public List<Annotation> getAllElementsFromContainer(Annotation container) {
        return Collections.singletonList(container);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> List<T> getElementsFromContainer(Annotation container, Class<T> elementType) {
        return (Objects.nonNull(container) && Objects.equals(container.annotationType(), elementType)) ?
            (List<T>)Collections.singletonList(container) : Collections.emptyList();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
