package top.xiajibagao.powerfulannotation.repeatable;

import top.xiajibagao.powerfulannotation.helper.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.util.*;
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
		this.mappingForestMap = new LinkedForestMap<>(false);
		this.mappingParsers = new ArrayList<>();
		CollUtils.addAll(this.mappingParsers, parsers);
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
		final Deque<List<RepeatableMapping>> deque = CollUtils.newLinkedList(parseRepeatableMappings(annotationType));
		while (!deque.isEmpty()) {
			final List<RepeatableMapping> mappings = deque.removeFirst();
			final List<RepeatableMapping> next = new ArrayList<>();
			for (final RepeatableMapping mapping : mappings) {
				// 以元素注解为子节点，容器注解为父节点，构建树结构
				mappingForestMap.putNode(mapping.getElementType(), mapping);
				mappingForestMap.linkNodes(mapping.getContainerType(), mapping.getElementType());
				CollUtils.addAll(next, parseRepeatableMappings(mapping.getContainerType()));
			}
			if (CollUtils.isNotEmpty(next)) {
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
			.filter(CollUtils::isNotEmpty)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
	}

	/**
	 * 指定注解是否为一个容器注解
	 *
	 * @return 是否
	 */
	@Override
	public boolean isContainer(Class<? extends Annotation> annotationType) {
		return Optional.ofNullable(annotationType)
			.map(mappingForestMap::get)
			.map(TreeEntry::hasChildren)
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
		return Optional.ofNullable(annotationType)
			.map(mappingForestMap::get)
			.map(TreeEntry::hasParent)
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
		return mappingForestMap.containsChildNode(containerType, elementType);
	}

	/**
	 * 获取指定注解的容器注解
	 *
	 * @return 容器注解
	 */
	@Override
	public List<RepeatableMapping> getContainers(Class<? extends Annotation> annotationType) {
		TreeEntry<Class<? extends Annotation>, RepeatableMapping> target = mappingForestMap.get(annotationType);
		if (Objects.isNull(target) || Objects.isNull(target.getValue())) {
			return Collections.emptyList();
		}
		List<RepeatableMapping> results = CollUtils.newArrayList(target.getValue());
		while (target.hasParent()) {
			target = target.getDeclaredParent();
			if (Objects.nonNull(target.getValue())) {
				results.add(target.getValue());
			}
		}
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
		// 容器注解为null
		if (Objects.isNull(container)) {
			return Collections.emptyList();
		}
		// 容器类型即为元素类型
		Class<? extends Annotation> containerType = container.annotationType();
		if (Objects.equals(containerType, elementType)) {
			return (List<T>)Collections.singletonList(container);
		}
		// 元素注解未在当前容器注册
		TreeEntry<Class<? extends Annotation>, RepeatableMapping> elementMapping = mappingForestMap.get(elementType);
		if (Objects.isNull(elementMapping)) {
			return Collections.emptyList();
		}
		// 元素注解与该容器注解不存在关联关系
		if (!elementMapping.containsParent(containerType)) {
			return Collections.emptyList();
		}

		// 获取从容器注解到该元素注解的转换路线
		List<RepeatableMapping> route = new ArrayList<>();
		TreeEntry<Class<? extends Annotation>, RepeatableMapping> cursor = elementMapping;
		while (cursor.hasParent() && ObjectUtils.isNotEquals(cursor.getKey(), containerType)) {
			route.add(cursor.getValue());
			cursor = cursor.getDeclaredParent();
		}
		Collections.reverse(route);

		// 从容器注解一层一层的转换为元素注解
		List<Annotation> elements = Collections.singletonList(container);
		for (RepeatableMapping repeatableMapping : route) {
			if (Objects.isNull(repeatableMapping)) {
				continue;
			}
			elements = elements.stream()
				.map(repeatableMapping::getElementsFromContainer)
				.flatMap(Stream::of)
				.collect(Collectors.toList());
		}
		return (List<T>)elements;
	}

}
