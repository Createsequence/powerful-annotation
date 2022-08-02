package top.xiajibagao.powerfulannotation.helper;

import org.junit.Test;

/**
 * test for {@link Assert}
 *
 * @author huangchengxing
 */
public class AssertTest {

    @Test
    public void testIsTrue() {
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> Assert.isTrue(false, "msg")
        );
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> Assert.isTrue(false, "%s", "msg")
        );
    }

    @Test
    public void testIsFalse() {
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> Assert.isFalse(true, "msg")
        );
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> Assert.isFalse(true, "%s", "msg")
        );
    }

    @Test
    public void testNotNull() {
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> Assert.notNull(null, "msg")
        );
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> Assert.notNull(null, "%s", "msg")
        );
    }

    @Test
    public void testIsAssignable() {
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> Assert.isAssignable(Integer.class, Long.class, "msg")
        );
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> Assert.isAssignable(Integer.class, Long.class, "%s", "msg")
        );
    }

    @Test
    public void testEquals() {
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> Assert.equals(Integer.class, Long.class, "msg")
        );
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> Assert.equals(Integer.class, Long.class, "%s", "msg")
        );
    }

    @Test
    public void testIsNotEquals() {
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> Assert.isNotEquals(Integer.class, Integer.class, "msg")
        );
        org.junit.Assert.assertThrows(
            "msg", IllegalArgumentException.class,
            () -> Assert.isNotEquals(Integer.class, Integer.class, "%s", "msg")
        );
    }

}
