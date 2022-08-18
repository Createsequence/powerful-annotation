package top.xiajibagao.powerfulannotation.annotation;

import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * 表示一个具有层级结构的注解对象
 *
 * @author huangchengxing
 */
@Getter
public class AnnotationTypeMapping extends GenericHierarchicalAnnotation<AnnotationTypeMapping> {

    /**
     * 创建一个通用注解
     *
     * @param annotation 注解对象
     * @param source 源注解
     * @param verticalIndex   垂直索引
     * @param horizontalIndex 水平索引
     */
    AnnotationTypeMapping(
        Annotation annotation, AnnotationTypeMapping source,
        int verticalIndex, int horizontalIndex) {
        super(annotation, null, source, verticalIndex, horizontalIndex);
        this.root = Objects.isNull(source) ? this : source.getRoot();
    }

    /**
     * 当前对象是否为根对象
     *
     * @return 是否
     */
    public boolean isRoot() {
        return getRoot() == this;
    }

}
