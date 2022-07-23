package top.xiajibagao.powerfulannotation.repeatable;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import top.xiajibagao.powerfulannotation.helper.ForestMap;
import top.xiajibagao.powerfulannotation.helper.FuncUtils;
import top.xiajibagao.powerfulannotation.helper.LinkedForestMap;
import top.xiajibagao.powerfulannotation.helper.TreeEntry;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>{@link RepeatableMappingRegistry}的简单实现。<br>
 * 调用者可以通过注册不同的映射关系解析器{@link RepeatableMappingParser}
 * 从而支持基于不同规则构建容器注解与元素注解的映射关系。
 *
 * @author huangchengxing
 * @see RepeatableMappingParser
 * @see StandardRepeatableMappingParser
 * @see RepeatableByMappingParser
 */
public class SimpleRepeatableMappingRegistry implements RepeatableMappingRegistry {

	/**
	 * 映射关系
	 */
	protected final ForestMap<Class<? extends Annotation>, RepeatableMapping> mappingForestMap;

	/**
	 * 解析器
	 */
	protected final List<RepeatableMappingParser> mappingParsers;

	/**
	 * 构造一个支持{@link Repeatable}注解的容器关系映射表
	 *
	 * @param parsers 要使用的解析器
	 */
	public SimpleRepeatableMappingRegistry(RepeatableMappingParser... parsers) {
		Assert.notEmpty(parsers, "registry requires at least one parser");
		this.mappingForestMap = new LinkedForestMap<>(false);
		this.mappingParsers = new ArrayList<>();
		CollUtil.addAll(this.mappingParsers, parsers);
	}

	/**
	 * 获取关系解析器
	 *
	 * @return 关系解析器
	 */
	@Override
	public List<RepeatableMappingParser> getMappingParser() {
		return mappingParsers;
	}

	/**
	 * 注册关系解析器
	 *
	 * @param mappingParser 关系解析器
	 */
	@Override
	public void registerMappingParser(RepeatableMappingParser mappingParser) {
		Assert.notNull(mappingParser, "mappingParser must not null");
		mappingParsers.add(mappingParser);
	}

	/**
	 * <p>注册指定注解类，将会解析其与关联的容器或元素注解的映射关系 <br>
	 * 注意，不允许出现注解的容器注解直接或间接以其元素注解作为容器注解的情况，
	 * 即不允许出现 a -&gt; b -&gt; a 这种注解间循环引用的关系。
	 *
	 * @param annotationType 注解类
	 * @throws IllegalArgumentException 当出现注解间的循环引用时抛出
	 */
	@Override
	public void register(Class<? extends Annotation> annotationType) {
		if (mappingForestMap.containsKey(annotationType)) {
			return;
		}
		// 若未缓存，则解析该元素注解的与其容器注解的映射关系，并添加缓存
		final Deque<List<RepeatableMapping>> deque = CollUtil.newLinkedList(parseRepeatableMappings(annotationType));
		while (!deque.isEmpty()) {
			final List<RepeatableMapping> mappings = deque.removeFirst();
			final List<RepeatableMapping> next = new ArrayList<>();
			for (final RepeatableMapping mapping : mappings) {
				mappingForestMap.putLinkedNodes(
					mapping.getElementType(), mapping.getContainerType(), mapping
				);
				CollUtil.addAll(next, parseRepeatableMappings(mapping.getContainerType()));
			}
			if (CollUtil.isNotEmpty(next)) {
				deque.addLast(next);
			}
		}
	}

	/**
	 * 解析类型，然后为指定的注解类型创建与容器注解的映射关系
	 *
	 * @param annotationType 注解类型
	 * @return 映射关系
	 */
	private List<RepeatableMapping> parseRepeatableMappings(Class<? extends Annotation> annotationType) {
		return mappingParsers.stream()
			.map(p -> p.parse(annotationType, this))
			.filter(ObjectUtil::isNotNull)
			.collect(Collectors.toList());
	}

	/**
	 * 指定注解是否为一个容器注解
	 *
	 * @return 是否
	 */
	@Override
	public boolean isContainer(Class<? extends Annotation> annotationType) {
		return Opt.ofNullable(annotationType)
			.map(mappingForestMap::get)
			.map(TreeEntry::hasParent)
			.orElse(false);
	}

	/**
	 * 指定注解是否存容器注解
	 *
	 * @param annotationType 注解类型
	 * @return 是否
	 */
	@Override
	public boolean hasContainer(Class<? extends Annotation> annotationType) {
		return Opt.ofNullable(annotationType)
			.map(mappingForestMap::get)
			.map(TreeEntry::hasChildren)
			.orElse(false);
	}

	/**
	 * {@code containerType}是否为{@code elementType}的容器注解
	 *
	 * @param elementType   元素注解类型
	 * @param containerType 容器注解类型
	 * @return 是否
	 */
	@Override
	public boolean isContainerOf(Class<? extends Annotation> elementType, Class<? extends Annotation> containerType) {
		return mappingForestMap.containsParentNode(containerType, elementType);
	}

	/**
	 * 获取指定注解的容器注解
	 *
	 * @return 容器注解
	 */
	@Override
	public List<RepeatableMapping> getContainers(Class<? extends Annotation> annotationType) {
		return Opt.ofNullable(annotationType)
			.map(mappingForestMap::get)
			.map(TreeEntry::getChildren)
			.map(Map::values)
			.map(t -> CollStreamUtil.toList(t, TreeEntry::getValue))
			.orElseGet(Collections::emptyList);
	}

	/**
	 * 获取包括自己在内的注解容器中的全部的注解
	 *
	 * @param container 容器注解
	 * @return 注解对象
	 */
	@Override
	public List<Annotation> getAllElementsFromContainer(Annotation container) {
		if (ObjectUtil.isNull(container)) {
			return Collections.emptyList();
		}
		// 若容器注解未在本表中注册，则直接返回其本身
		final Class<? extends Annotation> containerType = container.annotationType();
		if (!mappingForestMap.containsKey(containerType)) {
			return Collections.singletonList(container);
		}
		final TreeEntry<Class<? extends Annotation>, RepeatableMapping> containerMapping = mappingForestMap.get(containerType);
		List<Annotation> results = CollUtil.newArrayList(container);
		forEachElements(container, containerMapping.getRoot().getKey(), containerMapping, results::addAll);
		return results;
	}

	/**
	 * 从容器注解中获得指定的元素注解，若该容器注解不为指定对元素的容器，或任意一者为空时返回null
	 *
	 * @param container   容器注解对象
	 * @param elementType 元素注解类型
	 * @return 元素注解
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> List<T> getElementsFromContainer(Annotation container, Class<T> elementType) {
		if (ObjectUtil.isNull(container)) {
			return Collections.emptyList();
		}
		// 若元素注解类型与容器注解类型相同，则返回本身
		final Class<? extends Annotation> containerType = container.annotationType();
		if (ObjectUtil.equals(containerType, elementType)) {
			return (List<T>)Collections.singletonList(container);
		}
		// 若容器注解为null，或者未在本表中注册，则直接返回空集合
		if (!mappingForestMap.containsKey(containerType)) {
			return Collections.emptyList();
		}
		// 获取容器注解的映射，并确定其与指定的元素注解存在关系
		final TreeEntry<Class<? extends Annotation>, RepeatableMapping> containerMapping = mappingForestMap.get(containerType);
		if (ObjectUtil.isNull(containerMapping) || !containerMapping.containsParent(elementType)) {
			return Collections.emptyList();
		}
		// 将容器注解一层一层的兑换为元素注解
		List<Annotation> results = forEachElements(container, elementType, containerMapping, FuncUtils.doNothing());
		return (List<T>)results;
	}

	private <T extends Annotation> List<Annotation> forEachElements(
		Annotation container, Class<T> elementType,
		TreeEntry<Class<? extends Annotation>, RepeatableMapping> containerMapping,
		Consumer<List<Annotation>> consumer) {

		List<Annotation> results = CollUtil.newArrayList(container);
		TreeEntry<Class<? extends Annotation>, RepeatableMapping> mapping = containerMapping;
		do {
			final RepeatableMapping currContainerMapping = mapping.getValue();
			if (ObjectUtil.isNull(currContainerMapping)) {
				continue;
			}
			results = results.stream()
				.map(currContainerMapping::getElementsFromContainer)
				.filter(ArrayUtil::isNotEmpty)
				.flatMap(Stream::of)
				.collect(Collectors.toList());
			consumer.accept(results);
			if (ObjectUtil.equals(currContainerMapping.getElementType(), elementType)) {
				break;
			}
			mapping = mapping.getDeclaredParent();
		} while (ObjectUtil.isNotNull(mapping) && mapping.hasParent());
		return results;
	}
}
