package top.xiajibagao.powerfulannotation.helper;

import org.junit.Assert;
import org.junit.Test;

/**
 * test for {@link ObjectUtils}
 *
 * @author huangchengxing
 */
public class ObjectUtilsTest {

    @Test
    public void testIsNotEquals() {
        Integer a = 1;
        Integer b = 1;
        Integer c = 3;
        Assert.assertTrue(ObjectUtils.isNotEquals(a, c));
        Assert.assertTrue(ObjectUtils.isNotEquals(a, null));
        Assert.assertFalse(ObjectUtils.isNotEquals(null, null));
        Assert.assertFalse(ObjectUtils.isNotEquals(a, b));
    }

    @Test
    public void testDefaultIfNull() {
        Object target = new Object();
        Assert.assertEquals(target, ObjectUtils.defaultIfNull(null, target));
    }
}