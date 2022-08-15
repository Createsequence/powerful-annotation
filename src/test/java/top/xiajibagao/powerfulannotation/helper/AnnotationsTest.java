package top.xiajibagao.powerfulannotation.helper;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.Annotation;

/**
 * test for {@link Annotations}
 *
 * @author huangchengxing
 */
public class AnnotationsTest {

    @Test
    public void testEmptyAnnotations() {
        Annotation[] annotations = Annotations.emptyArray();
        Assert.assertEquals(0, annotations.length);
        Assert.assertSame(annotations, Annotations.emptyArray());
        Assert.assertArrayEquals(annotations, Annotations.emptyArray());
    }

}
