package top.xiajibagao.powerfulannotation.helper;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;

/**
 * test for {@link CollUtils}
 *
 * @author huangchengxing
 */
public class CollUtilsTest {

    // ================== coll ==================

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(CollUtils.isEmpty(Collections.emptyList()));
        Assert.assertTrue(CollUtils.isEmpty((Collection<?>)null));
    }

    @Test
    public void testIsNotEmpty() {
        Assert.assertFalse(CollUtils.isNotEmpty(Collections.emptyList()));
        Assert.assertFalse(CollUtils.isNotEmpty((Collection<?>)null));
    }

    @Test
    public void testAddAll() {
        List<Integer> list = new ArrayList<>();
        CollUtils.addAll(list, 1, 2, 3, 4);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), list);

        list.clear();
        CollUtils.addAll(list, Arrays.asList(1, 2, 3, 4));
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), list);
    }

    @Test
    public void testNewLinkedList() {
        Deque<Integer> deque = CollUtils.newLinkedList(1, 2, 3, 4);
        Assert.assertTrue(deque instanceof LinkedList);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), deque);
    }

    @Test
    public void testNewHashSet() {
        Set<Integer> set = CollUtils.newHashSet(1, 2, 3, 4);
        Assert.assertTrue(set instanceof HashSet);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), new ArrayList<>(set));
    }

    @Test
    public void testGetLast() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        Assert.assertEquals(list.get(3), CollUtils.getLast(list));
    }

    @Test
    public void testToList() {
        Set<Integer> set = new LinkedHashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        Assert.assertEquals(Arrays.asList(1, 2, 3), CollUtils.toList(set, Function.identity()));
    }

    @Test
    public void testToSet() {
        Set<Integer> set = new LinkedHashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        Assert.assertEquals(set, CollUtils.toSet(Arrays.asList(1, 2, 3), Function.identity()));
    }

    // ================== array ==================

    @Test
    public void testArrayIsEmpty() {
        Object[] array = new Object[0];
        Assert.assertTrue(CollUtils.isEmpty(array));
        Assert.assertTrue(CollUtils.isEmpty((Object[])null));
    }

    @Test
    public void testArrayIsNotEmpty() {
        Object[] array = new Object[0];
        Assert.assertFalse(CollUtils.isNotEmpty(array));
        Assert.assertFalse(CollUtils.isNotEmpty((Object[])null));
    }

    @Test
    public void testIsContainsAll() {
        Integer[] target = new Integer[] {1, 2, 3};

        Integer[] source1 = new Integer[] {1, 2};
        Assert.assertTrue(CollUtils.isContainsAll(target, source1));

        Integer[] source2 = new Integer[] {1, 2, 3, 4};
        Assert.assertFalse(CollUtils.isContainsAll(target, source2));
    }

    @Test
    public void testIsContainsAny() {
        Integer[] target = new Integer[] {1, 2, 3};
        Assert.assertTrue(CollUtils.isContainsAny(target, 1));
        Assert.assertFalse(CollUtils.isContainsAny(target, 5));
    }

    // ================== map ==================

    @Test
    public void testMapIsEmpty() {
        Assert.assertTrue(CollUtils.isEmpty(Collections.emptyMap()));
        Assert.assertTrue(CollUtils.isEmpty((Map<?, ?>)null));
    }

    @Test
    public void testMapIsNotEmpty() {
        Assert.assertFalse(CollUtils.isNotEmpty(Collections.emptyMap()));
        Assert.assertFalse(CollUtils.isNotEmpty((Map<?, ?>)null));
    }

}
