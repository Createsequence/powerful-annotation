package top.xiajibagao.powerfulannotation.scanner;

import top.xiajibagao.powerfulannotation.helper.AnnotationUtils;
import top.xiajibagao.powerfulannotation.helper.CollUtils;
import top.xiajibagao.powerfulannotation.helper.ObjectUtils;
import top.xiajibagao.powerfulannotation.helper.ReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * <p>注解扫描器，用于从指定的{@link AnnotatedElement}及其可能存在的层级结构中获取注解对象，
 * 是{@link AbstractAnnotationScanner}的基本实现。在该抽象类的基础上，定义了支持扫描的{@link AnnotatedElement}层级结构：
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
 * 此外，扫描器支持在获取到层级结构中的注解对象后，再对注解对象的元注解进行扫描。
 *
 * @author huangchengxing
 */
public class GenericAnnotationScanner extends AbstractAnnotationScanner {

    /**
     * 构造一个注解扫描器，
     * 默认不处理包括{@link java.lang}，与{@link javax}还有{@link com.sun}包下的类的注解，
     * 并不允许重复访问一个已经扫描过的普通类或注解类
     *
     * @param enableScanSuperClass     是否扫描父类
     * @param enableScanInterface      是否扫描接口
     * @param enableScanMetaAnnotation 是否扫描父类
     */
    public GenericAnnotationScanner(
        boolean enableScanSuperClass, boolean enableScanInterface, boolean enableScanMetaAnnotation) {
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
     * 若{@link ScanOptions#isEnableScanMetaAnnotation}为{@code true}，则将目标类元注解也添加到队列
     *
     * @param nextTypeHierarchies  下一层级待处理的类队列
     * @param type                 当前正在处理的类对象
     */
    @Override
    protected void collectAnnotationTypeIfNecessary(List<Class<?>> nextTypeHierarchies, Class<?> type) {
        Stream.of(type.getDeclaredAnnotations())
            .map(Annotation::annotationType)
            .forEach(nextTypeHierarchies::add);
    }

    /**
     * 若{@link ScanOptions#isEnableScanInterface}为{@code true}，则将目标类的父接口也添加到队列
     *
     * @param nextTypeHierarchies  下一层级待处理的类队列
     * @param type                 当前正在处理的类对象
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
     * @param type                当前正在处理的类对象
     */
    @Override
    protected void collectSuperTypeIfNecessary(List<Class<?>> nextTypeHierarchies, Class<?> type) {
        final Class<?> superClass = type.getSuperclass();
        if (ObjectUtils.isNotEquals(superClass, Object.class) && Objects.nonNull(superClass)) {
            nextTypeHierarchies.add(superClass);
        }
    }

    // ======================== 从类对象中获取所需注解 ========================

    /**
     * 从类中的指定属性获取注解对象
     *
     * @param type    当前正在处理的类对象
     * @param element 最开始扫描的对象
     * @return 注解对象
     */
    @Override
    protected Annotation[] getAnnotationsFromTypeDeclaredField(Class<?> type, Field element) {
        return Stream.of(type.getDeclaredFields())
            .filter(field -> Objects.equals(field, element))
            .map(AnnotationUtils::getDeclaredAnnotations)
            .flatMap(Stream::of)
            .toArray(Annotation[]::new);
    }

    /**
     * 从类中的指定方法获取注解对象
     *
     * @param type    当前正在处理的类对象
     * @param element 最开始扫描的对象
     * @return 注解对象
     */
    @Override
    protected Annotation[] getAnnotationsFromTypeDeclaredMethod(Class<?> type, Method element) {
        return Stream.of(ReflectUtils.getDeclaredMethods(type))
            .filter(superMethod -> !superMethod.isBridge())
            .filter(superMethod -> hasSameMethodSignature(element, superMethod))
            .map(AnnotationUtils::getDeclaredAnnotations)
            .flatMap(Stream::of)
            .toArray(Annotation[]::new);
    }

    /**
     * 从类获取注解对象
     *
     * @param type    当前正在处理的类对象
     * @param element 最开始扫描的对象
     * @return 注解对象
     */
    @Override
    protected Annotation[] getAnnotationFromType(Class<?> type, Class<?> element) {
        return AnnotationUtils.getDeclaredAnnotations(type);
    }

    /**
     * 该方法是否具备与扫描的方法相同的方法签名
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
