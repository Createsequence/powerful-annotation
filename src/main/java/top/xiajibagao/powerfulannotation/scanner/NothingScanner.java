package top.xiajibagao.powerfulannotation.scanner;

import top.xiajibagao.powerfulannotation.scanner.processor.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * 默认不扫描任何元素的扫描器
 *
 * @author huangchengxing
 */
public class NothingScanner implements AnnotationScanner {

	@Override
	public boolean support(AnnotatedElement annotatedEle) {
		return true;
	}

	@Override
	public List<Annotation> getAnnotations(AnnotatedElement annotatedEle) {
		return Collections.emptyList();
	}

	@Override
	public void scan(AnnotationProcessor processor, AnnotatedElement annotatedEle, Predicate<Annotation> filter) {
		// do nothing
	}

}
