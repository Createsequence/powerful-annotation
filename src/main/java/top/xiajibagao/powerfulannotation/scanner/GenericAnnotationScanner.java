package top.xiajibagao.powerfulannotation.scanner;

import top.xiajibagao.powerfulannotation.helper.CollUtils;
import top.xiajibagao.powerfulannotation.helper.ObjectUtils;
import top.xiajibagao.powerfulannotation.helper.ReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * <p>注解扫描器，用于从指定的{@link AnnotatedElement}及其可能存在的层级结构中获取注解对象，是{@link AbstractAnnotationScanner}的基本实现。
 *
 * <h3>层级结构</h3>
 * <p>以下为{@link AnnotatedElement}的层级结构在当前实例中的定义：
 * <ul>
 *     <li>
 *         当元素为非注解类，即为{@link Class}、且{@link Class#isAnnotation()}返回{@code false}时，
 *         此处层级结构即指<strong>类本身与其父类、父接口共同构成的层级结构</strong>，
 *         扫描器将扫描层级结构中类、接口声明的注解；
 *     </li>
 *     <li>
 *         当元素为注解类，即为{@link Class}、且{@link Class#isAnnotation()}返回{@code true}时，
 *         此处层级结构指<strong>注解类及其元注解构成的层级结构</strong>，
 *         扫描器将扫描器该注解层级结构中的元注解，并对其进行处理；
 *     </li>
 *     <li>
 *         当元素为{@link Method}时，此处层级结构指<strong>声明该方法的类的层级结构</strong>，
 *         扫描器将从层级结构中寻找与该方法签名相同的非桥接方法，并对其进行扫描；
 *     </li>
 *     <li>
 *         当元素为{@link Field}时，此处层级结构指<strong>声明该属性的类的层级结构</strong>，
 *         扫描器将从层级结构中寻找该属性，并对其注解进行扫描；
 *     </li>
 *     <li>当元素不为上述四者时，则认为其层级结构仅有其本身一层，将只直接扫描器该元素上的注解；</li>
 * </ul>
 *
 * <h3>元注解支持</h3>
 * <p>扫描器支持扫描注解的元注解，但是需要注意：
 * <ul>
 *     <li>当入参为{@link Class}时，不管是普通类还是注解类，则扫描的元注解都为类上注解的元注解；</li>
 *     <li>
 *         当入参不为{@link Class}时，将先从层级结构中获得注解，然后再从这些注解对应的注解类获取元注解，
 *         若{@link AnnotationFilter}不支持处理该注解，则该注解的元注解也将不会被处理；
 *     </li>
 * </ul>
 * <strong>注意</strong>：同时由于在同一次扫描中，一个{@link Class}仅会被扫描一次，
 * 因此当同一类型的元注解多次出现时，仅会在最开始被扫描一次，后续都将被忽略。
 *
 * @author huangchengxing
 * @see AbstractAnnotationScanner
 */
public class GenericAnnotationScanner extends AbstractAnnotationScanner {

    /**
     * 构造一个注解扫描器，
     * 默认不处理包括{@link java}包下的类的注解，
     * 并不允许重复访问一个已经扫描过的普通类或注解类
     *
     * @param enableScanSuperClass     是否扫描父类
     * @param enableScanInterface      是否扫描接口
     */
    public GenericAnnotationScanner(boolean enableScanSuperClass, boolean enableScanInterface, boolean enableScanMetaAnnotation) {
        this(new ScanOptions(enableScanSuperClass, enableScanInterface, enableScanMetaAnnotation));
    }

    /**
     * 构造一个通用注解扫描器
     *
     * @param options 扫描配置
     */
    protected GenericAnnotationScanner(ScanOptions options) {
        super(options);
    }

    // ======================== 获取需要扫描的类对象 ========================

    /**
     * 若{@link ScanOptions#isEnableScanInterface}为{@code true}，则将目标类的父接口也添加到队列
     *
     * @param nextTypeHierarchies  下一层级待处理的类队列
     * @param type 当前正在处理的类对象
     */
    @Override
    protected void collectInterfaceTypeIfNecessary(List<Class<?>> nextTypeHierarchies, Class<?> type) {
        final Class<?>[] interfaces = type.getInterfaces();
        if (CollUtils.isNotEmpty(interfaces)) {
            CollUtils.addAll(nextTypeHierarchies, interfaces);
        }
    }

    /**
     * 若{@link ScanOptions#isEnableScanSuperClass}为{@code true}，则将目标类的父类也添加到队列
     *
     * @param nextTypeHierarchies 下一层级待处理的类队列
     * @param type 当前正在处理的类对象
     */
    @Override
    protected void collectSuperTypeIfNecessary(List<Class<?>> nextTypeHierarchies, Class<?> type) {
        final Class<?> superClass = type.getSuperclass();
        if (ObjectUtils.isNotEquals(superClass, Object.class) && Objects.nonNull(superClass)) {
            nextTypeHierarchies.add(superClass);
        }
    }

    /**
     * 若{@link ScanOptions#isEnableScanMetaAnnotation()}为{@code true}，则将处理的注解的元注解也添加到队列
     *
     * @param nextTypeHierarchies 下一层级待处理的类队列
     * @param annotations 需要收集的注解对象
     */
    @Override
    protected void collectMetaAnnotationTypeIfNecessary(List<Class<?>> nextTypeHierarchies, List<Annotation> annotations) {
        nextTypeHierarchies.addAll(CollUtils.toList(annotations, Annotation::annotationType));
    }

    // ======================== 从类对象中获取所需注解 ========================
    
    /**
     * 返回{@code type}中与
     *
     * @param type 类
     * @param element 属性
     * @return java.lang.reflect.AnnotatedElement
     */
    @Override
    protected AnnotatedElement getElementFromTypeDeclaredField(Class<?> type, Field element) {
        return Stream.of(type.getDeclaredFields())
            .filter(field -> Objects.equals(field, element))
            .findFirst()
            .orElse(null);
    }

    /**
     * 类本身
     *
     * @param type 当前正在处理的类对象
     * @param element 最开始扫描的对象
     * @return 注解对象
     */
    @Override
    protected AnnotatedElement getElementFromType(Class<?> type, Class<?> element) {
        return type;
    }

    /**
     * 从类中的指定方法获取注解对象，依次遵循下述判断：
     * <ul>
     *     <li>若是桥接方法：直接返回null；</li>
     *     <li>若是私有方法：仅当{@code type}为该方法声明类时才返回其本身；</li>
     *     <li>若是私有方法：当{@code type}中存在于{@code element}的方法签名皆相同时，返回该方法；</li>
     * </ul>
     *
     * @param type 当前正在处理的类对象
     * @param element 最开始扫描的对象
     * @return 注解对象
     */
    @Override
    protected AnnotatedElement getElementFromTypeDeclaredMethod(Class<?> type, Method element) {
        // 是桥接方法
        if (element.isBridge()) {
            return null;
        }
        // 是私有方法，则仅返回其本身
        if (Modifier.isPrivate(element.getModifiers())) {
            return Objects.equals(type, element.getDeclaringClass()) ? element : null;
        }
        // 非私有方法
        return Stream.of(ReflectUtils.getDeclaredMethods(type))
            .filter(superMethod -> !superMethod.isBridge())
            .filter(superMethod -> hasSameMethodSignature(element, superMethod))
            .findFirst()
            .orElse(null);
    }

    /**
     * 该方法是否具备与扫描的方法相同的方法签名，包括：
     * <ul>
     *     <li>方法名是否一致；</li>
     *     <li>参数数量、参数类型是否一致，但是不区分泛型；</li>
     *     <li>返回值类型是否一致，不区分基础数据类型与包装类型；</li>
     * </ul>
     */
    private boolean hasSameMethodSignature(Method element, Method superMethod) {
        // check name
        if (ObjectUtils.isNotEquals(element.getName(), superMethod.getName())) {
            return false;
        }
        // check params
        final Class<?>[] sourceParameterTypes = element.getParameterTypes();
        final Class<?>[] targetParameterTypes = superMethod.getParameterTypes();
        if ((sourceParameterTypes.length != targetParameterTypes.length)
            || !CollUtils.isContainsAll(sourceParameterTypes, targetParameterTypes)) {
            return false;
        }
        // check return
        return ReflectUtils.isAssignable(superMethod.getReturnType(), element.getReturnType());
    }

}
