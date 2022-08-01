package top.xiajibagao.powerfulannotation.synthesis.resolver;

import top.xiajibagao.powerfulannotation.annotation.HierarchicalAnnotation;
import top.xiajibagao.powerfulannotation.annotation.attribute.AnnotationAttribute;
import top.xiajibagao.powerfulannotation.annotation.attribute.MirroredAnnotationAttribute;
import top.xiajibagao.powerfulannotation.helper.Assert;
import top.xiajibagao.powerfulannotation.synthesis.AnnotationSynthesizer;
import top.xiajibagao.powerfulannotation.synthesis.Link;
import top.xiajibagao.powerfulannotation.synthesis.RelationType;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * <p>用于处理注解对象中带有{@link Link}注解，且{@link Link#type()}为{@link RelationType#MIRROR_FOR}的属性。<br>
 * 当该处理器执行完毕后，原始合成注解中被{@link Link}注解的属性与{@link Link}注解指向的目标注解的属性，
 * 都将会被被包装并替换为{@link MirroredAnnotationAttribute}。
 *
 * @author huangchengxing
 * @see RelationType#MIRROR_FOR
 * @see MirroredAnnotationAttribute
 */
public class MirrorAttributeResolver extends AbstractDynamicAttributeResolver {

	private static final RelationType[] PROCESSED_RELATION_TYPES = new RelationType[]{ RelationType.MIRROR_FOR };

	/**
	 * 排序值，固定返回{@link SyntheticAnnotationResolver#MIRROR_ATTRIBUTE_RESOLVER_ORDER}
	 *
	 * @return 排序值
	 */
	@Override
	public int order() {
		return SyntheticAnnotationResolver.MIRROR_ATTRIBUTE_RESOLVER_ORDER;
	}

	/**
	 * 该处理器只处理{@link Link#type()}类型为{@link RelationType#MIRROR_FOR}的注解属性
	 *
	 * @return 仅有{@link RelationType#MIRROR_FOR}数组
	 */
	@Override
	protected RelationType[] processTypes() {
		return PROCESSED_RELATION_TYPES;
	}

	/**
	 * 将存在镜像关系的合成注解属性分别包装为{@link MirroredAnnotationAttribute}对象，
	 * 并使用包装后{@link MirroredAnnotationAttribute}替换在它们对应合成注解实例中的{@link AnnotationAttribute}
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

		// 镜像属性必然成对出现，因此此处必定存在三种情况：
		// 1.两属性都不为镜像属性，此时继续进行后续处理；
		// 2.两属性都为镜像属性，并且指向对方，此时无需后续处理；
		// 3.两属性仅有任意一属性为镜像属性，此时镜像属性必然未指向当前原始属性，此时应该抛出异常；
		if (originalAttribute instanceof MirroredAnnotationAttribute
			|| linkedAttribute instanceof MirroredAnnotationAttribute) {
			checkMirrored(originalAttribute, linkedAttribute);
			return;
		}

		// 校验镜像关系
		checkMirrorRelation(annotation, originalAttribute, linkedAttribute);
		// 包装这一对镜像属性，并替换原注解中的对应属性
		final AnnotationAttribute mirroredOriginalAttribute = new MirroredAnnotationAttribute(originalAttribute, linkedAttribute);
		originalAnnotation.replaceAttribute(originalAttribute.getAttributeName(), attribute -> mirroredOriginalAttribute);
		final AnnotationAttribute mirroredTargetAttribute = new MirroredAnnotationAttribute(linkedAttribute, originalAttribute);
		linkedAnnotation.replaceAttribute(annotation.attribute(), attribute -> mirroredTargetAttribute);
	}

	/**
	 * 检查映射关系是否正确
	 */
	private void checkMirrored(AnnotationAttribute original, AnnotationAttribute mirror) {
		final boolean originalAttributeMirrored = original instanceof MirroredAnnotationAttribute;
		final boolean mirrorAttributeMirrored = mirror instanceof MirroredAnnotationAttribute;

		// 校验通过
		final boolean passed = originalAttributeMirrored && mirrorAttributeMirrored
			&& Objects.equals(((MirroredAnnotationAttribute)original).getLinked(), ((MirroredAnnotationAttribute)mirror).getOriginal());
		if (passed) {
			return;
		}

		// 校验失败，拼装异常信息用于抛出异常
		String errorMsg;
		// 原始字段已经跟其他字段形成镜像
		if (originalAttributeMirrored && !mirrorAttributeMirrored) {
			errorMsg = String.format(
				"attribute [%s] cannot mirror for [%s], because it's already mirrored for [%s]",
				original.getAttribute(), mirror.getAttribute(), ((MirroredAnnotationAttribute)original).getLinked()
			);
		}
		// 镜像字段已经跟其他字段形成镜像
		else if (!originalAttributeMirrored && mirrorAttributeMirrored) {
			errorMsg = String.format(
				"attribute [%s] cannot mirror for [%s], because it's already mirrored for [%s]",
				mirror.getAttribute(), original.getAttribute(), ((MirroredAnnotationAttribute)mirror).getLinked()
			);
		}
		// 两者都形成了镜像，但是都未指向对方，理论上不会存在该情况
		else {
			errorMsg = String.format(
				"attribute [%s] cannot mirror for [%s], because [%s] already mirrored for [%s] and [%s] already mirrored for [%s]",
				mirror.getAttribute(), original.getAttribute(),
				mirror.getAttribute(), ((MirroredAnnotationAttribute)mirror).getLinked(),
				original.getAttribute(), ((MirroredAnnotationAttribute)original).getLinked()
			);
		}

		throw new IllegalArgumentException(errorMsg);
	}

	/**
	 * 基本校验
	 */
	private void checkMirrorRelation(Link annotation, AnnotationAttribute original, AnnotationAttribute mirror) {
		// 镜像属性必须存在
		checkLinkedAttributeNotNull(original, mirror, annotation);
		// 镜像属性返回值必须一致
		checkAttributeType(original, mirror);
		// 镜像属性上必须存在对应的注解
		final Link mirrorAttributeAnnotation = getAttributeAnnotation(mirror, RelationType.MIRROR_FOR);
		Assert.isTrue(
			Objects.nonNull(mirrorAttributeAnnotation) && RelationType.MIRROR_FOR.equals(mirrorAttributeAnnotation.type()),
			"mirror attribute [%s] of original attribute [%s] must marked by @Link, and also @LinkType.type() must is [%s]",
			mirror.getAttribute(), original.getAttribute(), RelationType.MIRROR_FOR
		);
		checkLinkedSelf(original, mirror);
	}

}
