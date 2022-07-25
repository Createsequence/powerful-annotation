package top.xiajibagao.powerfulannotation.helper;

/**
 * {@link Hierarchical}选择器，用于根据一定的规则从两个{@link Hierarchical}实现类中选择并返回一个最合适的对象
 *
 * <p>此外，还提供了{@link HierarchySelector}接口用于根据一定的规则从两个{@link Hierarchical}实现类中选择并返回一个最合适的对象，
 * 默认提供了四个实现类：
 * <ul>
 *     <li>{@link HierarchySelector#NEAREST_AND_OLDEST_PRIORITY}: 返回距离根对象更近的对象，当距离一样时优先返回旧对象；</li>
 *     <li>{@link HierarchySelector#NEAREST_AND_NEWEST_PRIORITY}: 返回距离根对象更近的对象，当距离一样时优先返回新对象；</li>
 *     <li>{@link HierarchySelector#FARTHEST_AND_OLDEST_PRIORITY}: 返回距离根对象更远的对象，当距离一样时优先返回旧对象；</li>
 *     <li>{@link HierarchySelector#FARTHEST_AND_NEWEST_PRIORITY}: 返回距离根对象更远的对象，当距离一样时优先返回新对象；</li>
 * </ul>
 *
 * @author huangchengxing
 * @see Hierarchical
 */
@FunctionalInterface
public interface HierarchySelector<H extends Hierarchical> {

    /**
     * 比较两个被合成的对象，选择其中的一个并返回
     *
     * @param prev 上一对象，该参数不允许为空
     * @param next 下一对象，该参数不允许为空
     * @return 对象
     */
    H choose(H prev, H next);

    // ============================== 静态实例 ==============================

    /**
     * 返回距离根对象更近的对象，当距离一样时优先返回旧对象
     */
    HierarchySelector<? extends Hierarchical> NEAREST_AND_OLDEST_PRIORITY = new NearestAndOldestPrioritySelector<>();

    /**
     * 返回距离根对象更近的对象，当距离一样时优先返回新对象
     */
    HierarchySelector<? extends Hierarchical> NEAREST_AND_NEWEST_PRIORITY = new NearestAndNewestPrioritySelector<>();

    /**
     * 返回距离根对象更远的对象，当距离一样时优先返回旧对象
     */
    HierarchySelector<? extends Hierarchical> FARTHEST_AND_OLDEST_PRIORITY = new FarthestAndOldestPrioritySelector<>();

    /**
     * 返回距离根对象更远的对象，当距离一样时优先返回新对象
     */
    HierarchySelector<? extends Hierarchical> FARTHEST_AND_NEWEST_PRIORITY = new FarthestAndNewestPrioritySelector<>();

    // ============================== 工厂方法 ==============================

    /**
     * 返回距离根对象更近的对象，当距离一样时优先返回旧对象
     *
     * @return top.xiajibagao.powerfulannotation.aggregate.HierarchySelector<T>
     */
    @SuppressWarnings("unchecked")
    static <T extends Hierarchical> HierarchySelector<T> nearestAndOldestPriority() {
        return (HierarchySelector<T>)NEAREST_AND_OLDEST_PRIORITY;
    }

    /**
     * 返回距离根对象更近的对象，当距离一样时优先返回新对象
     *
     * @return top.xiajibagao.powerfulannotation.aggregate.HierarchySelector<T>
     */
    @SuppressWarnings("unchecked")
    static <T extends Hierarchical> HierarchySelector<T> nearestAndNewestPriority() {
        return (HierarchySelector<T>)NEAREST_AND_NEWEST_PRIORITY;
    }

    /**
     * 返回距离根对象更远的对象，当距离一样时优先返回旧对象
     *
     * @return top.xiajibagao.powerfulannotation.aggregate.HierarchySelector<T>
     */
    @SuppressWarnings("unchecked")
    static <T extends Hierarchical> HierarchySelector<T> farthestAndOldestPriority() {
        return (HierarchySelector<T>)FARTHEST_AND_OLDEST_PRIORITY;
    }

    /**
     * 返回距离根对象更远的对象，当距离一样时优先返回新对象
     *
     * @return top.xiajibagao.powerfulannotation.aggregate.HierarchySelector<T>
     */
    @SuppressWarnings("unchecked")
    static <T extends Hierarchical> HierarchySelector<T> farthestAndNewestPriority() {
        return (HierarchySelector<T>)FARTHEST_AND_NEWEST_PRIORITY;
    }

    // ============================== 默认实现 ==============================

    /**
     * 返回距离根对象更近的注解，当距离一样时优先返回旧注解
     */
    class NearestAndOldestPrioritySelector<T extends Hierarchical> implements HierarchySelector<T> {
        @Override
        public T choose(T oldAnnotation, T newAnnotation) {
            return newAnnotation.getVerticalIndex() < oldAnnotation.getVerticalIndex() ? newAnnotation : oldAnnotation;
        }
    }

    /**
     * 返回距离根对象更近的注解，当距离一样时优先返回新注解
     */
    class NearestAndNewestPrioritySelector<T extends Hierarchical> implements HierarchySelector<T> {
        @Override
        public T choose(T oldAnnotation, T newAnnotation) {
            return newAnnotation.getVerticalIndex() <= oldAnnotation.getVerticalIndex() ? newAnnotation : oldAnnotation;
        }
    }

    /**
     * 返回距离根对象更远的注解，当距离一样时优先返回旧注解
     */
    class FarthestAndOldestPrioritySelector<T extends Hierarchical> implements HierarchySelector<T> {
        @Override
        public T choose(T oldAnnotation, T newAnnotation) {
            return newAnnotation.getVerticalIndex() > oldAnnotation.getVerticalIndex() ? newAnnotation : oldAnnotation;
        }
    }

    /**
     * 返回距离根对象更远的注解，当距离一样时优先返回新注解
     */
    class FarthestAndNewestPrioritySelector<T extends Hierarchical> implements HierarchySelector<T> {
        @Override
        public T choose(T oldAnnotation, T newAnnotation) {
            return newAnnotation.getVerticalIndex() >= oldAnnotation.getVerticalIndex() ? newAnnotation : oldAnnotation;
        }
    }

}
