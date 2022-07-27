package top.xiajibagao.powerfulannotation.helper;

/**
 * 接受三个参数，有返回值的函数式接口
 *
 * @param <T1> 第一个参数类型
 * @param <T2> 第二个参数类型
 * @param <T3> 第三个参数类型
 * @author huangchengxing
 */
@FunctionalInterface
public interface Function3<T1, T2, T3, R> {

    /**
     * 接受三个参数，返回一个值
     *
     * @return 返回值
     */
    R accept(T1 t1, T2 t2, T3 t3);

}
