package top.xiajibagao.powerfulannotation.scanner;

import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;

/**
 * <p>注解扫描器，用于从指定的{@link AnnotatedElement}中获取具有直接或间接关联的注解对象
 *
 * <p>默认提供了以下扫描方式：
 * <ul>
 *     <li>{@link #NOTHING}：什么都不做，什么注解都不扫描；</li>
 *     <li>{@link #DIRECTLY}：扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解；</li>
 *     <li>
 *         {@link #DIRECTLY_AND_META_ANNOTATION}：扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解，
 *         以及这些注解的元注解；
 *     </li>
 *     <li>{@link #SUPERCLASS}：扫描元素本身以及父类的层级结构中声明的注解；</li>
 *     <li>{@link #SUPERCLASS_AND_META_ANNOTATION}：扫描元素本身以及父类的层级结构中声明的注解，以及这些注解的元注解；</li>
 *     <li>{@link #INTERFACE}：扫描元素本身以及父接口的层级结构中声明的注解；</li>
 *     <li>{@link #INTERFACE_AND_META_ANNOTATION}：扫描元素本身以及父接口的层级结构中声明的注解，以及这些注解的元注解；</li>
 *     <li>{@link #TYPE_HIERARCHY}：扫描元素本身以及父类、父接口的层级结构中声明的注解；</li>
 *     <li>{@link #TYPE_HIERARCHY_AND_META_ANNOTATION}：扫描元素本身以及父接口、父接口的层级结构中声明的注解，以及这些注解的元注解；</li>
 * </ul>
 *
 * <p><strong>注意：</strong>
 * <ul>
 *     <li>上述扫描器皆不处理{@link com.sun}，{@link java.lang}及{@link javax}包下注解的元注解；</li>
 *     <li>扫描器可能无法正确处理注解与其元注解之间潜在的循环依赖关系，比如：<code>{@code a -> b -> a}</code>；</li>
 * </ul>
 *
 * @author huangchengxing
 * @see AnnotationProcessor
 * @see AnnotationFilter
 * @see GenericTypeHierarchyAnnotationScanner
 */
public interface AnnotationScanner {

    /**
     * 不扫描任何注解的扫描器
     */
    AnnotationScanner NOTHING = (element, processor, filter) -> {};

    /**
     * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解的扫描器
     */
    AnnotationScanner DIRECTLY = new GenericTypeHierarchyAnnotationScanner(false, false, false);

    /**
     * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解，以及这些注解的元注解的扫描器
     */
    AnnotationScanner DIRECTLY_AND_META_ANNOTATION = new GenericTypeHierarchyAnnotationScanner(false, false, true);

    /**
     * 扫描元素本身以及父类的层级结构中声明的注解的扫描器
     */
    AnnotationScanner SUPERCLASS = new GenericTypeHierarchyAnnotationScanner(true, false, false);

    /**
     * 扫描元素本身以及父类的层级结构中声明的注解，以及这些注解的元注解的扫描器
     */
    AnnotationScanner SUPERCLASS_AND_META_ANNOTATION = new GenericTypeHierarchyAnnotationScanner(true, false, true);

    /**
     * 扫描元素本身以及父接口的层级结构中声明的注解的扫描器
     */
    AnnotationScanner INTERFACE = new GenericTypeHierarchyAnnotationScanner(false, true, false);

    /**
     * 扫描元素本身以及父接口的层级结构中声明的注解，以及这些注解的元注解的扫描器
     */
    AnnotationScanner INTERFACE_AND_META_ANNOTATION = new GenericTypeHierarchyAnnotationScanner(false, true, true);

    /**
     * 扫描元素本身以及父类、父接口的层级结构中声明的注解的扫描器
     */
    AnnotationScanner TYPE_HIERARCHY = new GenericTypeHierarchyAnnotationScanner(true, true, false);

    /**
     * 扫描元素本身以及父接口、父接口的层级结构中声明的注解，以及这些注解的元注解的扫描器
     */
    AnnotationScanner TYPE_HIERARCHY_AND_META_ANNOTATION = new GenericTypeHierarchyAnnotationScanner(true, true, true);

    /**
     * 扫描与指定具有关联的注解，并对其进行处理
     *
     * @param element 元素
     * @param processor 注解处理器
     * @param filter 过滤器
     */
    void scan(AnnotatedElement element, AnnotationProcessor processor, AnnotationFilter filter);

}
