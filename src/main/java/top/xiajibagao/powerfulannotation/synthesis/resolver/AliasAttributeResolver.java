package top.xiajibagao.powerfulannotation.synthesis.resolver;

import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.attribute.*;
import top.xiajibagao.powerfulannotation.helper.Assert;
import top.xiajibagao.powerfulannotation.helper.ObjectUtils;
import top.xiajibagao.powerfulannotation.synthesis.AnnotationSynthesizer;
import top.xiajibagao.powerfulannotation.synthesis.Link;
import top.xiajibagao.powerfulannotation.synthesis.RelationType;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;

/**
 * <p>用于处理注解对象中带有{@link Link}注解，且{@link Link#type()}为
 * {@link RelationType#ALIAS_FOR}或{@link RelationType#FORCE_ALIAS_FOR}的属性。<br>
 * 当该处理器执行完毕后，{@link Link}注解指向的目标注解的属性将会被包装并替换为
 * {@link AliasedAnnotationAttribute}或{@link ForceAliasedAnnotationAttribute}。
 *
 * @author huangchengxing
 * @see RelationType#ALIAS_FOR
 * @see AliasedAnnotationAttribute
 * @see RelationType#FORCE_ALIAS_FOR
 * @see ForceAliasedAnnotationAttribute
 */
public class AliasAttributeResolver extends AbstractDynamicAttributeResolver {

	private static final RelationType[] PROCESSED_RELATION_TYPES = new RelationType[]{ RelationType.ALIAS_FOR, RelationType.FORCE_ALIAS_FOR };

	/**
	 * 合成注解排序器，只有靠前的注解属性允许覆盖靠后的注解属性，当为空时将不限制
	 */
	private final Comparator<HierarchicalAnnotation<Annotation>> comparator;

	/**
	 * 创建一个别名字段处理器
	 *
	 * @param comparator 比较器，若为空则不对别名字段的优先级做出校验
	 */
	public AliasAttributeResolver(Comparator<HierarchicalAnnotation<Annotation>> comparator) {
		this.comparator = comparator;
	}

	/**
	 * 创建一个别名字段处理器
	 */
	public AliasAttributeResolver() {
		this(null);
	}

	/**
	 * 排序值，固定返回{@link SyntheticAnnotationResolver#ALIAS_ATTRIBUTE_RESOLVER_ORDER}
	 *
	 * @return 排序值
	 */
	@Override
	public int order() {
		return SyntheticAnnotationResolver.ALIAS_ATTRIBUTE_RESOLVER_ORDER;
	}

	/**
	 * 该处理器只处理{@link Link#type()}类型为{@link RelationType#ALIAS_FOR}和{@link RelationType#FORCE_ALIAS_FOR}的注解属性
	 *
	 * @return 含有{@link RelationType#ALIAS_FOR}和{@link RelationType#FORCE_ALIAS_FOR}的数组
	 */
	@Override
	protected RelationType[] processTypes() {
		return PROCESSED_RELATION_TYPES;
	}

	/**
	 * 获取{@link Link}指向的目标注解属性，并根据{@link Link#type()}的类型是
	 * {@link RelationType#ALIAS_FOR}或{@link RelationType#FORCE_ALIAS_FOR}
	 * 将目标注解属性包装为{@link AliasedAnnotationAttribute}或{@link ForceAliasedAnnotationAttribute}，
	 * 然后用包装后注解属性在对应的合成注解中替换原始的目标注解属性
	 *
	 * @param synthesizer        注解合成器
	 * @param annotation         {@code originalAttribute}上的{@link Link}注解对象
	 * @param originalAnnotation 当前正在处理的{@link HierarchicalAnnotation}对象
	 * @param originalAttribute  {@code originalAnnotation}上的待处理的属性
	 * @param linkedAnnotation   {@link Link}指向的关联注解对象
	 * @param linkedAttribute    {@link Link}指向的{@code originalAnnotation}中的关联属性，该参数可能为空
	 */
	@Override
	protected void processLinkedAttribute(
		AnnotationSynthesizer synthesizer, Link annotation,
		HierarchicalAnnotation<Annotation> originalAnnotation, AnnotationAttribute originalAttribute,
		HierarchicalAnnotation<Annotation> linkedAnnotation, AnnotationAttribute linkedAttribute) {
		// 校验别名关系
		checkAliasRelation(annotation, originalAttribute, linkedAttribute);
		// 若指定了排序，则需要保证元素属性所属的注解优先级必须大于等于关联属性
		Assert.isTrue(
			Objects.isNull(comparator)
				|| Objects.equals(originalAnnotation, linkedAnnotation)
				|| comparator.compare(originalAnnotation, linkedAnnotation) <= 0,
			"link attribute [%s] priority cannot be higher than original attribute [%s]",
			linkedAttribute, originalAttribute
		);
		// 处理aliasFor类型的关系
		if (RelationType.ALIAS_FOR.equals(annotation.type())) {
			wrappingLinkedAttribute(
				synthesizer,
				originalAnnotation, originalAttribute, linkedAttribute,
				AliasedAnnotationAttribute::new
			);
			return;
		}
		// 处理forceAliasFor类型的关系
		wrappingLinkedAttribute(
			synthesizer,
			originalAnnotation, originalAttribute, linkedAttribute,
			ForceAliasedAnnotationAttribute::new
		);
	}

	/**
	 * 对指定注解属性进行包装，若该属性已被包装过，则递归以其为根节点的树结构，对树上全部的叶子节点进行包装
	 */
	private void wrappingLinkedAttribute(
		AnnotationSynthesizer synthesizer,
		HierarchicalAnnotation<Annotation> originalAnnotation, AnnotationAttribute originalAttribute,
		AnnotationAttribute linkedAttribute, BinaryOperator<AnnotationAttribute> wrapping) {
		// 不是包装属性
		if (!linkedAttribute.isWrapped()) {
			processAttribute(synthesizer, originalAttribute, linkedAttribute, wrapping);
		}
		else {
			// 是包装属性
			final WrappedAnnotationAttribute wrapper = (WrappedAnnotationAttribute)linkedAttribute;
			wrapper.getAllLinkedNonWrappedAttributes().forEach(
				t -> processAttribute(synthesizer, originalAttribute, t, wrapping)
			);
		}
		// 包装别名属性
		originalAnnotation.replaceAttribute(
			originalAttribute.getAttributeName(),
			old -> new AliasAnnotationAttribute(old, linkedAttribute)
		);
	}

	/**
	 * 获取指定注解属性，然后将其再进行一层包装
	 */
	private void processAttribute(
		AnnotationSynthesizer synthesizer,
		AnnotationAttribute originalAttribute, AnnotationAttribute aliasAttribute,
		BinaryOperator<AnnotationAttribute> wrapping) {
		Optional.ofNullable(aliasAttribute.getAnnotationType())
			.map(synthesizer::getAnnotation)
			.ifPresent(t -> t.replaceAttribute(aliasAttribute.getAttributeName(), old -> wrapping.apply(old, originalAttribute)));
	}

	/**
	 * 基本校验
	 */
	private void checkAliasRelation(Link annotation, AnnotationAttribute originalAttribute, AnnotationAttribute linkedAttribute) {
		checkLinkedAttributeNotNull(originalAttribute, linkedAttribute, annotation);
		checkAttributeType(originalAttribute, linkedAttribute);
		checkCircularDependency(originalAttribute, linkedAttribute);
	}

	/**
	 * 检查两个属性是否互为别名
	 */
	private void checkCircularDependency(AnnotationAttribute original, AnnotationAttribute alias) {
		checkLinkedSelf(original, alias);
		Link annotation = getAttributeAnnotation(alias, RelationType.ALIAS_FOR, RelationType.FORCE_ALIAS_FOR);
		if (Objects.isNull(annotation)) {
			return;
		}
		final Class<?> aliasAnnotationType = getLinkedAnnotationType(annotation, alias.getAnnotationType());
		if (ObjectUtils.isNotEquals(aliasAnnotationType, original.getAnnotationType())) {
			return;
		}
		Assert.isNotEquals(
			annotation.attribute(), original.getAttributeName(),
			"circular reference between the alias attribute [%s] and the original attribute [%s]",
			alias.getAttribute(), original.getAttribute()
		);
	}

}
