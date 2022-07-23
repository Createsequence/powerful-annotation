package top.xiajibagao.powerfulannotation.repeatable;


import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.util.List;

/**
 * <p>可重复注解映射关系注册表，用于集中管理{@link RepeatableMapping},
 * 并提供间接关联的容器注解与元素注解间的转换功能
 *
 * @author huangchengxing
 * @see RepeatableMapping
 */
public interface RepeatableMappingRegistry {

	/**
	 * 创建一个默认的映射关系注册表，支持处理原生的{@link Repeatable}注解与{@link RepeatableBy}注解
	 *
	 * @return 映射关系注册表
	 */
	static RepeatableMappingRegistry create() {
		return new SimpleRepeatableMappingRegistry(
			RepeatableMappingParser.STANDARD_REPEATABLE_MAPPING_PARSER, RepeatableMappingParser.REPEATABLE_BY_MAPPING_PARSER
		);
	}

	/**
	 * 获取关系解析器
	 *
	 * @return 关系解析器
	 */
	List<RepeatableMappingParser> getMappingParser();

	/**
	 * 注册关系解析器
	 *
	 * @param mappingParser 关系解析器
	 */
	void registerMappingParser(RepeatableMappingParser mappingParser);

	/**
	 * 注册指定注解类
	 *
	 * @param annotationType 注解类
	 */
	void register(Class<? extends Annotation> annotationType);

	/**
	 * 指定注解是否为一个容器注解
	 *
	 * @param annotationType 注解类型
	 * @return 是否
	 */
	boolean isContainer(Class<? extends Annotation> annotationType);

	/**
	 * 指定注解是否存容器注解
	 *
	 * @param annotationType 注解类型
	 * @return 是否
	 */
	boolean hasContainer(Class<? extends Annotation> annotationType);

	/**
	 * {@code containerType}是否为{@code elementType}的容器注解
	 *
	 * @param elementType   元素注解类型
	 * @param containerType 容器注解类型
	 * @return 是否
	 */
	boolean isContainerOf(Class<? extends Annotation> elementType, Class<? extends Annotation> containerType);

	/**
	 * 获取指定注解的容器注解
	 *
	 * @param annotationType 注解类型
	 * @return 容器注解
	 */
	List<RepeatableMapping> getContainers(Class<? extends Annotation> annotationType);

	/**
	 * 获取注解容器中的全部的元素注解
	 *
	 * @param container 容器对象
	 * @return 注解对象
	 */
	List<Annotation> getAllElementsFromContainer(Annotation container);

	/**
	 * 从容器注解中获得指定的元素注解，若该容器注解不为指定对元素的容器，或任意一者为空时返回null
	 *
	 * @param container   容器注解对象
	 * @param elementType 元素注解类型
	 * @return 元素注解
	 */
	<T extends Annotation> List<T> getElementsFromContainer(Annotation container, Class<T> elementType);

}
