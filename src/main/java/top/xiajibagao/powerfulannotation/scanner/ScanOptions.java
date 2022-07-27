package top.xiajibagao.powerfulannotation.scanner;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.Predicate;

/**
 * 注解扫描配置
 *
 * @author huangchengxing
 */
@Accessors(chain = true)
@Setter
@Getter
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
     * 是否支持扫描父类
     */
    private boolean enableScanMetaAnnotation;

    /**
     * 若一个注解类已经被处理过，再次扫描到时是否不再处理
     */
    private boolean enableScanAccessedAnnotationType;

    /**
     * 若一个非注解类已经被处理过，再次扫描到时是否不再处理
     */
    private boolean enableScanAccessedType;

    /**
     * 类型过滤器，若该类型——包括普通类与注解类——无法通过过滤器，则不会被扫描器扫描
     */
    private Predicate<Class<?>> typeFilter;

    /**
     * 构造一个扫描配置
     */
    public ScanOptions() {
        this.enableScanSuperClass = true;
        this.enableScanInterface = true;
        this.enableScanMetaAnnotation = true;
        this.enableScanAccessedAnnotationType = false;
        this.enableScanAccessedType = false;
        this.typeFilter = type -> true;
    }

}
