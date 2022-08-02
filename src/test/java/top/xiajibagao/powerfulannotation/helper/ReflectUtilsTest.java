package top.xiajibagao.powerfulannotation.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * test for {@link ReflectUtils}
 *
 * @author huangchengxing
 */
public class ReflectUtilsTest {

    @SneakyThrows
    @Test
    public void testInvoke() {
        Method method = Foo.class.getDeclaredMethod("getName");
        Foo foo = new Foo("foo");
        Assert.assertEquals("foo", ReflectUtils.invoke(foo, method));
    }

    @SneakyThrows
    @Test
    public void testGetDeclaredMethods() {
        Assert.assertArrayEquals(Foo.class.getDeclaredMethods(), ReflectUtils.getDeclaredMethods(Foo.class));
    }

    @SneakyThrows
    @Test
    public void testGetDeclaredMethod() {
        Assert.assertEquals(
            Foo.class.getDeclaredMethod("getName"),
            ReflectUtils.getDeclaredMethod(Foo.class, "getName")
        );
    }

    @Test
    public void testIsAssignable() {
        Assert.assertTrue(ReflectUtils.isAssignable(Integer.class, int.class));
        Assert.assertTrue(ReflectUtils.isAssignable(int.class, Integer.class));
        Assert.assertTrue(ReflectUtils.isAssignable(Number.class, Integer.class));
        Assert.assertTrue(ReflectUtils.isAssignable(Integer.class, Integer.class));
    }

    @Getter
    @RequiredArgsConstructor
    private static class Foo {
        private final String name;
    }

}
