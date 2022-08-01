package top.xiajibagao.powerfulannotation.scanner;

import top.xiajibagao.powerfulannotation.helper.Assert;
import top.xiajibagao.powerfulannotation.helper.StrUtils;

import java.util.function.Predicate;

/**
 * 注解扫描配置
 *
 * @author huangchengxing
 */
public class ScanOptions {

    /**
     * 是否支持扫描父类
     */
    private boolean enableScanSuperClass;

    /**
     * 是否支持扫描接口
     */
    private boolean enableScanInterface;

    /**
     * 是否支持扫描元注解
     */
    private boolean enableScanMetaAnnotation;

    /**
     * 若一个非注解类已经被处理过，再次扫描到时是否再次处理
     */
    private boolean enableScanAccessedType;

    /**
     * 类型过滤器，若该类型——包括普通类与注解类——无法通过过滤器，则不会被扫描器扫描
     */
    private Predicate<Class<?>> typeFilter;

    /**
     * 是否锁定配置
     */
    private boolean locked;

    /**
     * 构造一个扫描配置，默认不重复扫描已经访问过的普通类和注解类，
     * 并且不处理包括{@link java.lang}，与{@link javax}还有{@link com.sun}包下的类的注解
     *
     * @param enableScanSuperClass     是否扫描父类
     * @param enableScanInterface      是否扫描接口
     * @param enableScanMetaAnnotation 是否扫描父类
     */
    public ScanOptions(boolean enableScanSuperClass, boolean enableScanInterface, boolean enableScanMetaAnnotation) {
        this.locked = false;
        this.enableScanSuperClass = enableScanSuperClass;
        this.enableScanInterface = enableScanInterface;
        this.enableScanMetaAnnotation = enableScanMetaAnnotation;
        this.enableScanAccessedType = false;
        this.typeFilter = t -> StrUtils.isNotStartWithAny(t.getName(), "java.lang", "javax", "com.sum");
    }

    /**
     * 构造一个扫描配置，默认不重复扫描已经访问过的普通类和注解类，
     * 并且不处理包括{@link java.lang}，与{@link javax}还有{@link com.sun}包下的类的注解
     */
    public ScanOptions() {
        this.locked = false;
        this.enableScanSuperClass = true;
        this.enableScanInterface = true;
        this.enableScanMetaAnnotation = true;
        this.enableScanAccessedType = false;
        this.typeFilter = t -> StrUtils.isNotStartWithAny(t.getName(), "java.lang", "javax", "com.sum");
    }

    /**
     * 拷贝配置
     */
    ScanOptions(ScanOptions options) {
        this.locked = false;
        this.enableScanSuperClass = options.enableScanSuperClass;
        this.enableScanInterface = options.enableScanInterface;
        this.enableScanMetaAnnotation = options.enableScanMetaAnnotation;
        this.enableScanAccessedType = options.enableScanAccessedType;
        this.typeFilter = options.typeFilter;
    }

    /**
     * 是否支持扫描父类
     *
     * @return 是否
     */
    public boolean isEnableScanSuperClass() {
        return enableScanSuperClass;
    }

    /**
     * 是否支持扫描接口
     *
     * @return 是否
     */
    public boolean isEnableScanInterface() {
        return enableScanInterface;
    }

    /**
     * 是否支持扫描元注解
     *
     * @return 是否
     */
    public boolean isEnableScanMetaAnnotation() {
        return enableScanMetaAnnotation;
    }

    /**
     * 若一个非注解类已经被处理过，再次扫描到时是否再次处理
     *
     * @return 是否
     */
    public boolean isEnableScanAccessedType() {
        return enableScanAccessedType;
    }

    /**
     * 设置过滤器，若该类型——包括普通类与注解类——无法通过过滤器，则不会被扫描器扫描
     *
     * @return 类型过滤器
     */
    public Predicate<Class<?>> getTypeFilter() {
        return typeFilter;
    }

    /**
     * 设置是否支持扫描父类
     *
     * @param enableScanSuperClass 是否支持扫描父类
     * @return 配置对象
     */
    public ScanOptions setEnableScanSuperClass(boolean enableScanSuperClass) {
        checkLocked();
        this.enableScanSuperClass = enableScanSuperClass;
        return this;
    }

    /**
     * 设置是否支持扫描接口
     *
     * @param enableScanInterface 是否支持扫描接口
     * @return 配置对象
     */
    public ScanOptions setEnableScanInterface(boolean enableScanInterface) {
        checkLocked();
        this.enableScanInterface = enableScanInterface;
        return this;
    }

    /**
     * 设置是否支持扫描元注解
     *
     * @param enableScanMetaAnnotation 是否支持扫描元注解
     * @return 配置对象
     */
    public ScanOptions setEnableScanMetaAnnotation(boolean enableScanMetaAnnotation) {
        checkLocked();
        this.enableScanMetaAnnotation = enableScanMetaAnnotation;
        return this;
    }

    /**
     * 设置若一个类已经被处理过，再次扫描到时是否再次处理
     *
     * @param enableScanAccessedType 若一个类已经被处理过，再次扫描到时是否再次处理
     * @return 配置对象
     */
    public ScanOptions setEnableScanAccessedType(boolean enableScanAccessedType) {
        checkLocked();
        this.enableScanAccessedType = enableScanAccessedType;
        return this;
    }

    /**
     * 设置类型过滤器，若该类型——包括普通类与注解类——无法通过过滤器，则不会被扫描器扫描
     *
     * @param typeFilter 类型过滤器
     * @return 配置对象
     */
    public ScanOptions setTypeFilter(Predicate<Class<?>> typeFilter) {
        checkLocked();
        this.typeFilter = typeFilter;
        return this;
    }

    /**
     * 锁定配置
     */
    public void lockOptions() {
        locked = true;
    }

    /**
     * 检查配置是否锁定
     */
    private void checkLocked() {
        Assert.isFalse(locked, "options is locked");
    }

}
