package top.xiajibagao.powerfulannotation.scanner;

import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;

/**
 * 用于从{@link AnnotatedElement}上获取相关注解扫描器
 *
 * <h3>注解索引</h3>
 * <p>在{@link AbstractAnnotationScanner}以及{@link AnnotationProcessor}中提到的<em>垂直索引</em>与<em>水平索引</em>将遵循如下定义：<br />
 * 若以扫描的{@link AnnotatedElement}为原点建立坐标系，
 * 以被扫描到的注解所在的类层级作为y轴，同一类层级中注解被扫描到的顺序作为x轴，则有：
 * <ul>
 *     <li>
 *         <em>垂直索引</em>：即被扫描到的注解在y轴上的坐标，
 *         用于反映注解与根节点所在层级结构的相对位置 <br />
 *         该索引总是从{@link #VERTICAL_INDEX_START_POINT}开始递增；
 *     </li>
 *     <li>
 *         <em>水平索引</em>：即被扫描到的注解在x轴上的坐标，
 *         用于反映被扫描到的注解之间以根节点为参照物的相对位置关系。
 *         若两个注解垂直索引相同，则通过水平索引可以区分两注解被扫描的先后顺序。<br />
 *         该索引总是从{@link #HORIZONTAL_INDEX_START_POINT}开始递增。
 *     </li>
 * </ul>
 * 扫描器将可以通过两个坐标轴对应的索引该描述注解与被扫描的{@link AnnotatedElement}之间的位置关系。
 *
 * <h3>扫描顺序</h3>
 * <p>默认情况下，扫描器将按照广度优先遍历被扫描的{@link AnnotatedElement}的层级结构中的被注解元素。<br />
 * 同一层级中的注解，按照其所属{@link AnnotatedElement}的被扫描顺序(元素本身 -> 父类 -> 父接口)、
 * 以及其在{@link AnnotatedElement#getDeclaredAnnotations()}返回的注解数组中的顺序决定。<br />
 *
 * <h3>默认实现</h3>
 * <p>该接口提供了一些预设了条件的扫描器实例，可通过本接口的静态成员变量引用。<br />
 * 根据层级结构的扫描方式，分为四类：
 * <ul>
 *     <li><code>SELF</code>：只扫描元素本身；</li>
 *     <li><code>SUPERCLASS</code>：扫描元素本身以及层级结构中的父类；</li>
 *     <li><code>INTERFACE</code>：扫描元素本身以及层级结构中的接口；</li>
 *     <li><code>TYPE_HIERARCHY</code>：扫描元素本身以及层级结构中的父类与父接口；</li>
 * </ul>
 * 根据是否扫描元注解，又分为两类：
 * <ul>
 *     <li>
 *         <code>INDIRECT</code>：从层级结构中扫描到注解后，还会继续扫描这些注解的元注解。<br />
 *         eg：<em>X</em>上存在注解<em>A</em>，<em>A</em>上存在元注解<em>B</em>，则扫描<em>X</em>，将获得<em>A</em>和<em>B</em>；
 *     </li>
 *     <li>
 *         <code>DIRECT</code>：从层级结构中扫描到注解后，不会继续扫描它们的元注解；<br />
 *         eg：<em>X</em>上存在注解<em>A</em>，<em>A</em>上存在元注解<em>B</em>，则扫描<em>X</em>，将只获得<em>A</em>；
 *     </li>
 * </ul>
 *
 * @author huangchengxing
 */
public interface AnnotationScanner {

    /**
     * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解
     */
    AnnotationScanner SELF_AND_DIRECT = new GenericAnnotationScanner(false, false, false);

    /**
     * 扫描元素本身直接声明的注解，包括父类带有{@link Inherited}、被传递到元素上的注解，以及这些注解的元注解
     */
    AnnotationScanner SELF_AND_INDIRECT = new GenericAnnotationScanner(false, false, true);

    /**
     * 扫描元素本身以及层级结构中的父类声明的注解
     */
    AnnotationScanner SUPERCLASS_AND_DIRECT = new GenericAnnotationScanner(true, false, false);

    /**
     * 扫描元素本身以及层级结构中的父类声明的注解，以及这些注解的元注解
     */
    AnnotationScanner SUPERCLASS_AND_INDIRECT = new GenericAnnotationScanner(true, false, true);

    /**
     * 扫描元素本身以及层级结构中的父接口声明的注解
     */
    AnnotationScanner INTERFACE_AND_DIRECT = new GenericAnnotationScanner(false, true, false);

    /**
     * 扫描元素本身以及层级结构中的父接口声明的注解，以及这些注解的元注解
     */
    AnnotationScanner INTERFACE_AND_INDIRECT = new GenericAnnotationScanner(false, true, true);

    /**
     * 扫描元素本身以及层级结构中的父类及父接口声明的注解
     */
    AnnotationScanner TYPE_HIERARCHY_AND_DIRECT = new GenericAnnotationScanner(true, true, false);

    /**
     * 扫描元素本身以及层级结构中的父类及父接口声明的注解，以及这些注解的元注解
     */
    AnnotationScanner TYPE_HIERARCHY_AND_INDIRECT = new GenericAnnotationScanner(true, true, true);

    /**
     * 水平索引起始点
     */
    int VERTICAL_INDEX_START_POINT = 0;

    /**
     * 垂直索引起始点
     */
    int HORIZONTAL_INDEX_START_POINT = 0;

    /**
     * 扫描指定元素上的注解
     *
     * @param element   待扫描的元素
     * @param processor 注解处理器
     * @param filter    过滤器，若为空则不过滤任何注解
     */
    void scan(AnnotatedElement element, AnnotationProcessor processor, AnnotationFilter filter);

}
