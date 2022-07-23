package top.xiajibagao.powerfulannotation.aggerate.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * <p>表示一个被包装过的原始{@link AnnotationAttribute}，
 * 该原始属性存在一个关联属性，该关联属性的值可能会直接替换原始属性的值，
 * 或者基于特定的规则影响到原始属性的值。
 *
 * <p>当包装类被包装了多层后，则规则生效优先级按包装的顺序倒序执行 <br>
 * eg:
 * 比如a属性别被{@code Wrapper1}包装，然后包装后的a属性又再次被{@code Wrapper2}包装，
 * 此时处理a属性，则优先执行{@code Wrapper2}的逻辑，再处理{@code Wrapper1}的逻辑
 * <pre>{@code
 *  generate: original -> Wrapper1 -> Wrapper2
 *  execute: Wrapper2 -> Wrapper1 -> original
 * }</pre>
 *
 * @author huangchengxing
 * @see AnnotationAttribute
 */
public interface WrappedAnnotationAttribute extends AnnotationAttribute {

    // =========================== 新增方法 ===========================

    /**
     * 获取被包装的{@link AnnotationAttribute}对象，该对象也可能是{@link AnnotationAttribute}
     *
     * @return 被包装的{@link AnnotationAttribute}对象
     */
    AnnotationAttribute getOriginal();

    /**
     * 获取最初的被包装的{@link AnnotationAttribute}
     *
     * @return 最初的被包装的{@link AnnotationAttribute}
     */
    AnnotationAttribute getNonWrappedOriginal();

    /**
     * 获取包装{@link #getOriginal()}的{@link AnnotationAttribute}对象，该对象也可能是{@link AnnotationAttribute}
     *
     * @return 包装对象
     */
    AnnotationAttribute getLinked();

    /**
     * 遍历以当前实例为根节点的树结构，获取所有未被包装的属性
     *
     * @return 叶子节点
     */
    Collection<AnnotationAttribute> getAllLinkedNonWrappedAttributes();

    // =========================== 代理实现 ===========================

    /**
     * 获取注解对象
     *
     * @return 注解对象
     */
    @Override
    default Annotation getAnnotation() {
        return getOriginal().getAnnotation();
    }

    /**
     * 获取注解属性对应的方法
     *
     * @return 注解属性对应的方法
     */
    @Override
    default Method getAttribute() {
        return getOriginal().getAttribute();
    }

    /**
     * 该注解属性的值是否等于默认值 <br>
     * 默认仅当{@link #getOriginal()}与{@link #getLinked()}返回的注解属性
     * 都为默认值时，才返回{@code true}
     *
     * @return 该注解属性的值是否等于默认值
     */
    @Override
    boolean isValueEquivalentToDefaultValue();

    /**
     * 获取属性类型
     *
     * @return 属性类型
     */
    @Override
    default Class<?> getAttributeType() {
        return getOriginal().getAttributeType();
    }

    /**
     * 获取属性上的注解
     *
     * @param annotationType 注解类型
     * @return 注解对象
     */
    @Override
    default <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return getOriginal().getAnnotation(annotationType);
    }

    /**
     * 当前注解属性是否已经被{@link WrappedAnnotationAttribute}包装
     *
     * @return boolean
     */
    @Override
    default boolean isWrapped() {
        return true;
    }

}
