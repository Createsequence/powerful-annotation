package top.xiajibagao.powerfulannotation.helper;


/**
 * <p>描述以一个在以{@link #getRoot()}为原点的二维坐标系中存在的对象，
 * 该对象以{@link #getVerticalIndex()}返回值为{@code x}轴坐标，
 * 以{@link #getHorizontalIndex()}为{@code y}轴坐标，
 * 通过这两个参数描述该对象与{@link #getRoot()}之间的相对位置。
 *
 * <p>该接口主要用于描述实现类与{@link #getRoot()}的“距离”，
 * 当在同时处理一批具有相同{@link #getRoot()}的{@link Hierarchical}实例时，
 * 可以根据“距离”确定它们被处理的先后顺序。
 *
 * <p>规定，任何{@link Hierarchical}的实现类，索引的递增递减都应该遵循下述原则：
 * <ul>
 *     <li>
 *         居于{@link #getRoot()}上方的对象，即该对象的父层级：
 *         水平索引从{@link #VERTICAL_INDEX_START_POINT}开始递增，
 *         垂直索引从{@link #HORIZONTAL_INDEX_START_POINT}开始递增
 *     </li>
 *     <li>
 *         居于{@link #getRoot()}下方的对象，即该对象的子层级：
 *         水平索引从-{@link #VERTICAL_INDEX_START_POINT}开始递减，
 *         垂直索引从{@link #HORIZONTAL_INDEX_START_POINT}开始递减
 *     </li>
 *     <li>
 *         {@link #getRoot()}为本身，即自身即为自己的根节点：
 *         水平索引、垂直索引皆为{@code 0}；
 *     </li>
 * </ul>
 *
 * @author huangchengxing
 * @see HierarchySelector
 */
public interface Hierarchical {

    /**
     * 垂直索引起始点
     */
    int VERTICAL_INDEX_START_POINT = 1;

    /**
     * 水平索引起始点
     */
    int HORIZONTAL_INDEX_START_POINT = 0;

    // ====================== hierarchical  ======================

    /**
     * 参照物，即坐标为{@code (0, 0)}的对象。
     * 当对象本身即为参照物时，该方法应当返回其本身
     *
     * @return 参照物
     */
    Object getRoot();

    /**
     * 获取该对象与参照物的垂直距离。
     * 默认情况下，该距离即为当前对象与参照物之间相隔的层级数。
     *
     * @return 合成注解与根对象的垂直距离
     */
    int getVerticalIndex();

    /**
     * 获取该对象与参照物的水平距离。
     * 默认情况下，该距离即为当前对象在与参照物{@link #getVerticalIndex()}相同的情况下条，
     * 该对象被扫描到的顺序。
     *
     * @return 合成注解与根对象的水平距离
     */
    int getHorizontalIndex();

}
