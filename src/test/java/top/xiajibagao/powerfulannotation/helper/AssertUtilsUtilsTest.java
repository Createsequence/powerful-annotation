package top.xiajibagao.powerfulannotation.helper;

import org.junit.Test;

/**
 * test for {@link AssertUtils}
 *
 * @author huangchengxing
 */
public class AssertUtilsUtilsTest {

    @Test
    public void testIsTrue() {
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> AssertUtils.isTrue(false, "msg")
        );
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> AssertUtils.isTrue(false, "%s", "msg")
        );
    }

    @Test
    public void testIsFalse() {
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> AssertUtils.isFalse(true, "msg")
        );
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> AssertUtils.isFalse(true, "%s", "msg")
        );
    }

    @Test
    public void testNotNull() {
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> AssertUtils.notNull(null, "msg")
        );
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> AssertUtils.notNull(null, "%s", "msg")
        );
    }

    @Test
    public void testIsAssignable() {
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> AssertUtils.isAssignable(Integer.class, Long.class, "msg")
        );
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> AssertUtils.isAssignable(Integer.class, Long.class, "%s", "msg")
        );
    }

    @Test
    public void testEquals() {
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> AssertUtils.equals(Integer.class, Long.class, "msg")
        );
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> AssertUtils.equals(Integer.class, Long.class, "%s", "msg")
        );
    }

    @Test
    public void testIsNotEquals() {
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> AssertUtils.isNotEquals(Integer.class, Integer.class, "msg")
        );
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> AssertUtils.isNotEquals(Integer.class, Integer.class, "%s", "msg")
        );
    }

}
