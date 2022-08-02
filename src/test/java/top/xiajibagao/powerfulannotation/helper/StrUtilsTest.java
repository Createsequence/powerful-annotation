package top.xiajibagao.powerfulannotation.helper;

import org.junit.Assert;
import org.junit.Test;

/**
 * test for {@link StrUtils}
 *
 * @author huangchengxing
 */
public class StrUtilsTest {

    @Test
    public void testIsStartWithAny() {
        Assert.assertTrue(StrUtils.isStartWithAny("abcd", "b", "d", "c", "a"));
    }

    @Test
    public void testIsNotStartWithAny() {
        Assert.assertTrue(StrUtils.isNotStartWithAny("abcd", "b", "d", "c", "ad"));
    }

}
