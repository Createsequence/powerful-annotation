package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 为需要从类的层级结构中获取注解的{@link AnnotationScanner}提供基本实现
 *
 * @author huangchengxing
 * @see TypeHierarchyScanner
 * @see AnnotationHierarchyScanner
 */
@Accessors(chain = true)
@Getter
@Setter
public abstract class AbstractTypeHierarchyScanner<T extends AbstractTypeHierarchyScanner<T>> extends AbstractHierarchyScanner<T> {

    /**
     * 是否允许扫描父类
     */
    private boolean includeSuperClass;

    /**
     * 是否允许扫描父接口
     */
    private boolean includeInterfaces;

    /**
     * 构造一个类注解扫描器
     *
     * @param includeSuperClass 是否允许扫描父类
     * @param includeInterfaces 是否允许扫描父接口
     * @param filter            过滤器
     * @param excludeTypes      不包含的类型
     */
    protected AbstractTypeHierarchyScanner(
        boolean includeSuperClass, boolean includeInterfaces, Predicate<Class<?>> filter, Set<Class<?>> excludeTypes) {
        super(filter, excludeTypes);
        this.includeSuperClass = includeSuperClass;
        this.includeInterfaces = includeInterfaces;
    }

    /**
     * 从目标类型上获取下一层级需要处理的类，并添加到队列
     *
     * @param nextClassQueue 队列
     * @param targetClass 目标类型
     */
    @Override
    protected void collectToQueue(List<Class<?>> nextClassQueue, Class<?> targetClass) {
        // 扫描父类
        scanSuperClassIfNecessary(nextClassQueue, targetClass);
        // 扫描接口
        scanInterfaceIfNecessary(nextClassQueue, targetClass);
    }

    /**
     * 若{@link #includeInterfaces}为{@code true}，则将目标类的父接口也添加到nextClasses
     *
     * @param nextClasses 下一个类集合
     * @param targetClass 目标类型
     */
    protected void scanInterfaceIfNecessary(List<Class<?>> nextClasses, Class<?> targetClass) {
        if (includeInterfaces) {
            final Class<?>[] interfaces = targetClass.getInterfaces();
            if (ArrayUtil.isNotEmpty(interfaces)) {
                CollUtil.addAll(nextClasses, interfaces);
            }
        }
    }

    /**
     * 若{@link #includeSuperClass}为{@code true}，则将目标类的父类也添加到nextClasses
     *
     * @param nextClassQueue 下一个类队列
     * @param targetClass    目标类型
     */
    protected void scanSuperClassIfNecessary(List<Class<?>> nextClassQueue, Class<?> targetClass) {
        if (includeSuperClass) {
            final Class<?> superClass = targetClass.getSuperclass();
            if (!ObjectUtil.equals(superClass, Object.class) && ObjectUtil.isNotNull(superClass)) {
                nextClassQueue.add(superClass);
            }
        }
    }

}
