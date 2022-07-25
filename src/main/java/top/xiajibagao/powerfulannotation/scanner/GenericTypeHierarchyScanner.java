package top.xiajibagao.powerfulannotation.scanner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import top.xiajibagao.powerfulannotation.helper.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * <p>支持对类层次结构进行扫描的扫描器通用实现
 *
 * <p>当{@link AnnotatedElement}类型不同时，从层级结构中扫描注解的对象将有所区别：
 * <ul>
 *     <li>
 *         当元素为{@link Class}时，此处层级结构即指类本身与其父类、父接口共同构成的层级结构，
 *         扫描器将扫描层级结构中类、接口声明的注解；
 *     </li>
 *     <li>
 *         当元素为{@link Method}时，此处层级结构指声明方法的类的层级结构，
 *         扫描器将从层级结构中寻找与该方法签名相同的非桥接方法，并对其进行扫描；
 *     </li>
 *     <li>
 *         当元素为{@link Field}时，此处层级结构指声明属性的类的层级结构，
 *         扫描器将从层级结构中寻找与该属性具有相同类型与名称的属性，并对其注解进行扫描；
 *     </li>
 *     <li>当元素不为{@link Method}或{@link Class}时，则其层级结构仅有其本身一层；</li>
 * </ul>
 * 此外，扫描器支持在获取到层级结构中的注解对象后，再对注解对象的元注解进行扫描。
 *
 * @author huangchengxing
 */
@Getter
public class GenericTypeHierarchyScanner extends AbstractTypeHierarchyScanner {

    /**
     * 是否支持扫描父类
     */
    private final boolean enableScanSuperClass;

    /**
     * 是否支持扫描接口
     */
    private final boolean enableScanInterface;

    /**
     * 是否支持扫描父类
     */
    private final boolean enableScanMetaAnnotation;

    /**
     * 构造一个通用注解扫描器，默认不处理包括{@link java.lang}, 与{@link javax}还有{@link com.sun}包下的类
     *
     * @param enableScanSuperClass 是否支持扫描父类
     * @param enableScanInterface 是否支持扫描接口
     * @param enableScanMetaAnnotation 是否支持扫描父类
     */
    public GenericTypeHierarchyScanner(
        boolean enableScanSuperClass, boolean enableScanInterface, boolean enableScanMetaAnnotation) {
        this(
            enableScanSuperClass, enableScanInterface, enableScanMetaAnnotation,
            t -> !CharSequenceUtil.startWithAny(t.getName(), "java.lang", "javax", "com.sum")
        );
    }

    /**
     * 构造一个通用注解扫描器
     *
     * @param enableScanSuperClass 是否支持扫描父类
     * @param enableScanInterface 是否支持扫描接口
     * @param enableScanMetaAnnotation 是否支持扫描父类
     * @param typeFilter 类型过滤器，若该类型无法通过过滤器，则不会被扫描器扫描
     */
    public GenericTypeHierarchyScanner(
        boolean enableScanSuperClass,
        boolean enableScanInterface,
        boolean enableScanMetaAnnotation,
        Predicate<Class<?>> typeFilter) {
        super(typeFilter);
        this.enableScanSuperClass = enableScanSuperClass;
        this.enableScanInterface = enableScanInterface;
        this.enableScanMetaAnnotation = enableScanMetaAnnotation;
    }

    // ======================== 扫描注解 ========================

    /**
     * 收集当前类接口、父类或者元注解类，将其加入队列用于下一次递归获取注解
     *
     * @param nextTypeHierarchies 队列
     * @param type 目标类型
     */
    @Override
    protected void collectToQueue(List<Class<?>> nextTypeHierarchies, Class<?> type) {
        scanMetaAnnotationIfNecessary(nextTypeHierarchies, type);
        scanSuperClassIfNecessary(nextTypeHierarchies, type);
        scanInterfaceIfNecessary(nextTypeHierarchies, type);
    }

    /**
     * 若{@link #enableScanMetaAnnotation}为{@code true}，则将目标类的父接口也添加到nextClasses
     *
     * @param nextTypeHierarchies 下一个类集合
     * @param type 目标类型
     */
    protected void scanMetaAnnotationIfNecessary(List<Class<?>> nextTypeHierarchies, Class<?> type) {
        if (!enableScanMetaAnnotation) {
            return;
        }
        Stream.of(AnnotationUtils.getDeclaredAnnotations(type))
            .map(Annotation::annotationType)
            .forEach(nextTypeHierarchies::add);
    }

    /**
     * 若{@link #enableScanInterface}为{@code true}，则将目标类的父接口也添加到nextClasses
     *
     * @param nextTypeHierarchies 下一个类集合
     * @param type 目标类型
     */
    protected void scanInterfaceIfNecessary(List<Class<?>> nextTypeHierarchies, Class<?> type) {
        if (!enableScanInterface) {
            return;
        }
        final Class<?>[] interfaces = type.getInterfaces();
        if (ArrayUtil.isNotEmpty(interfaces)) {
            CollUtil.addAll(nextTypeHierarchies, interfaces);
        }
    }

    /**
     * 若{@link #enableScanSuperClass}为{@code true}，则将目标类的父类也添加到nextClasses
     *
     * @param nextTypeHierarchies 下一个类队列
     * @param type    目标类型
     */
    protected void scanSuperClassIfNecessary(List<Class<?>> nextTypeHierarchies, Class<?> type) {
        if (!enableScanSuperClass) {
            return;
        }
        final Class<?> superClass = type.getSuperclass();
        if (!ObjectUtil.equals(superClass, Object.class) && ObjectUtil.isNotNull(superClass)) {
            nextTypeHierarchies.add(superClass);
        }
    }

    // ======================== 获取注解 ========================

    /**
     * <ul>
     *     <li>若元素是{@link Class}，则直接返回；</li>
     *     <li>若元素是{@link Member}，则返回{@link Member#getDeclaringClass()}；</li>
     *     <li>若元素是不为上述两者，则返回{@code null};</li>
     * </ul>
     *
     * @param element 待扫描的元素
     * @return 元素的类
     */
    @Override
    protected Class<?> getClassFormElement(AnnotatedElement element) {
        if (element instanceof Class) {
            return (Class<?>)element;
        }
        if (element instanceof Member) {
            return ((Member)element).getDeclaringClass();
        }
        return null;
    }
    
    /**
     * <ul>
     *     <li>若根元素是{@link Class}，则直接返回{@code type}上直接声明的注解；</li>
     *     <li>若根元素是{@link Method}，则返回{@code type}中，与根元素具有完全一致的签名的非桥接方法上直接声明的注解；</li>
     *     <li>若元素是{@link Field}，则返回{@code type}中，与跟原生具有一致的名称与类型的属性上直接声明的注解;</li>
     *     <li>若元素不为上述三者中的任意一种，则将直接抛出{@link IllegalArgumentException}异常；</li>
     * </ul>
     *
     * @param context 所有
     * @param type 类型
     * @return 待处理的注解
     */
    @Override
    protected Annotation[] getAnnotationsFromType(Context context, Class<?> type) {
        AnnotatedElement element = context.getSource();
        // 扫描的元素是类
        if (element instanceof Class) {
            return AnnotationUtils.getDeclaredAnnotations(type);
        }
        // 扫描的元素是方法
        if (element instanceof Method) {
            Method sourceMethod = (Method)element;
            return Stream.of(ClassUtil.getDeclaredMethods(type))
                .filter(superMethod -> !superMethod.isBridge())
                .filter(superMethod -> hasSameMethodSignature(sourceMethod, superMethod))
                .map(AnnotationUtils::getDeclaredAnnotations)
                .flatMap(Stream::of)
                .toArray(Annotation[]::new);
        }
        // 扫描的元素是属性
        if (element instanceof Field) {
            Field sourceField = (Field)element;
            return Stream.of(ClassUtil.getDeclaredFields(type))
                .filter(supperField -> hasSameFieldSignature(sourceField, supperField))
                .map(AnnotationUtils::getDeclaredAnnotations)
                .flatMap(Stream::of)
                .toArray(Annotation[]::new);
        }
        throw new IllegalArgumentException(CharSequenceUtil.format(
            "cannot get annotations from type [{}], because scanning source element type is [{}]", type, element
        ));
    }

    /**
     * 该方法是否具备与扫描的方法相同的方法签名
     */
    private boolean hasSameMethodSignature(Method sourceMethod, Method superMethod) {
        if (ObjectUtil.equals(sourceMethod, sourceMethod)) {
            return true;
        }
        if (!CharSequenceUtil.equals(sourceMethod.getName(), superMethod.getName())) {
            return false;
        }
        final Class<?>[] sourceParameterTypes = sourceMethod.getParameterTypes();
        final Class<?>[] targetParameterTypes = superMethod.getParameterTypes();
        if (sourceParameterTypes.length != targetParameterTypes.length) {
            return false;
        }
        if (!ArrayUtil.containsAll(sourceParameterTypes, targetParameterTypes)) {
            return false;
        }
        return ClassUtil.isAssignable(superMethod.getReturnType(), sourceMethod.getReturnType());
    }

    /**
     * 该方法是否具备与扫描的属性具有相同的类型和名称
     */
    private boolean hasSameFieldSignature(Field sourceField, Field superField) {
        if (ObjectUtil.equals(sourceField, superField)) {
            return true;
        }
        return CharSequenceUtil.equals(sourceField.getName(), superField.getName())
            && ObjectUtil.equals(sourceField.getType(), superField.getType());
    }

}
