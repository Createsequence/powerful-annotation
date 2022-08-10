package top.xiajibagao.powerfulannotation.helper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 集合工具类
 *
 * @author huangchengxing
 */
public class CollUtils {

    private CollUtils() {
    }

    public static boolean isEmpty(Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    @SafeVarargs
    public static <T> void addAll(Collection<T> collection, T... elements) {
        if (Objects.nonNull(collection) && Objects.nonNull(elements)) {
            collection.addAll(Arrays.asList(elements));
        }
    }

    public static <T> void addAll(Collection<T> collection, Collection<T> elements) {
        if (Objects.nonNull(collection) && Objects.nonNull(elements)) {
            collection.addAll(elements);
        }
    }

    @SafeVarargs
    public static <T> Deque<T> newLinkedList(T... elements) {
        return newCollection(LinkedList::new, elements);
    }

    @SafeVarargs
    public static <T> List<T> newArrayList(T... elements) {
        return newCollection(ArrayList::new, elements);
    }

    @SafeVarargs
    public static <T> Set<T> newLinkedHashSet(T... elements) {
        return newCollection(LinkedHashSet::new, elements);
    }

    @SafeVarargs
    public static <T> Set<T> newHashSet(T... elements) {
        return newCollection(HashSet::new, elements);
    }

    @SafeVarargs
    private static <T, C extends Collection<T>> C newCollection(Supplier<C> collFactory, T... elements) {
        final C coll = collFactory.get();
        addAll(coll, elements);
        return coll;
    }

    public static <T> T getLast(Iterable<T> collection) {
        if (Objects.isNull(collection)) {
            return null;
        }
        Iterator<T> iterator = collection.iterator();
        T last = null;
        while (iterator.hasNext()) {
            last = iterator.next();
        }
        return last;
    }

    public static <T, R> List<R> toList(Collection<T> collection, Function<T, R> mapping) {
        return isEmpty(collection) ?
            Collections.emptyList() : collection.stream().map(mapping).collect(Collectors.toList());
    }

    public static <T, R> Set<R> toSet(Collection<T> collection, Function<T, R> mapping) {
        return isEmpty(collection) ?
            Collections.emptySet() : collection.stream().map(mapping).collect(Collectors.toSet());
    }

    // ================== array ==================

    public static <T> boolean isEmpty(T[] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    @SafeVarargs
    public static <T> boolean isContainsAll(T[] sources, T... elements) {
        return Arrays.asList(sources)
            .containsAll(Arrays.asList(elements));
    }

    public static <T> boolean isContainsAny(T[] sources, T element) {
        if (isEmpty(sources)) {
            return false;
        }
        return Arrays.asList(sources).contains(element);
    }

    // ================== map ==================

    public static boolean isEmpty(Map<?, ?> map) {
        return Objects.isNull(map) || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

}
