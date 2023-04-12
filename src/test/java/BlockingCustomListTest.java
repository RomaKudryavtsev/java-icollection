import collection_domain.array.CustomArray;
import collection_domain.ICollection;
import collection_domain.list.BlockingCustomList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlockingCustomListTest {
    private ICollection<Integer> numbers;
    private final Integer[] arrNumbers = {10, 7, 11, -2, 13, 10, 2000};
    private final BiFunction<Integer, Integer, Predicate<Integer>> getPredicateSearchingNumInRange = (n1, n2) ->
            num -> num >= n1 && num < n2;
    final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @BeforeEach
    void setUp() {
        numbers = new BlockingCustomList<>();
        Arrays.stream(arrNumbers).forEach(n -> numbers.add(n));
    }

    @Test
    void testIterator() {
        int index = 0;
        for (Integer i : numbers) {
            assertEquals(i, arrNumbers[index++]);
        }
    }

    @Test
    void testRemoveIfNumberIsInRange() {
        assertTrue(numbers.removeIf(getPredicateSearchingNumInRange.apply(10, 13)));
        Integer[] expected = {7, -2, 13, 2000};
        assertArrayEquals(expected, numbers.toArray());
    }

    @Test
    void testIndexOfWithPredicate() {
        assertEquals(0, numbers.indexOf(getPredicateSearchingNumInRange.apply(10, 11)));
    }

    @Test
    void testLastIndexOfWithPredicate() {
        assertEquals(5, numbers.lastIndexOf(getPredicateSearchingNumInRange.apply(9, 11)));
    }

    @Test
    void testSort() {
        numbers.sort((o1, o2) -> o2 - o1);
        Object[] expected = {2000, 13, 11, 10, 10, 7, -2};
        assertArrayEquals(expected, numbers.toArray());
    }

    @Test
    void testAddAllOnIndexMiddle() {
        CustomArray<Integer> additionalNumbs = new CustomArray<>();
        Arrays.stream(arrNumbers).forEach(additionalNumbs::add);
        numbers.addAll(additionalNumbs, 1);
        Integer[] expectedNumbs = {10, 10, 7, 11, -2, 13, 10, 2000, 7, 11, -2, 13, 10, 2000};
        for (int i = 0; i < numbers.size(); ++i) {
            assertEquals(expectedNumbs[i], numbers.get(i));
        }
    }

    @Test
    void testAddAllOnIndexFirst() {
        CustomArray<Integer> additionalNumbs = new CustomArray<>();
        Arrays.stream(arrNumbers).forEach(additionalNumbs::add);
        numbers.addAll(additionalNumbs, 0);
        Integer[] expectedNumbs = {10, 7, 11, -2, 13, 10, 2000, 10, 7, 11, -2, 13, 10, 2000};
        for (int i = 0; i < numbers.size(); ++i) {
            assertEquals(expectedNumbs[i], numbers.get(i));
        }
    }

    @Test
    void testRemoveAll() {
        assertTrue(numbers.removeAll(2000));
        Integer[] expected = {10, 7, 11, -2, 13, 10};
        for (int i = 0; i < numbers.size(); ++i) {
            assertEquals(expected[i], numbers.get(i));
        }
    }

    @Test
    void testSet() {
        Integer[] expectedNumbs = {11, 7, 11, -2, 13, 10, 2000};
        numbers.set(11, 0);
        for (int i = 0; i < numbers.size(); ++i) {
            assertEquals(expectedNumbs[i], numbers.get(i));
        }
    }

    @Test
    void testAddAll() {
        CustomArray<Integer> additionalNumbs = new CustomArray<>();
        Arrays.stream(arrNumbers).forEach(additionalNumbs::add);
        numbers.addAll(additionalNumbs);
        Integer[] expectedNumbs = {10, 7, 11, -2, 13, 10, 2000, 10, 7, 11, -2, 13, 10, 2000};
        for (int i = 0; i < numbers.size(); ++i) {
            assertEquals(expectedNumbs[i], numbers.get(i));
        }
    }

    @Test
    void testContains() {
        assertTrue(numbers.contains(-2));
        assertFalse(numbers.contains(54));
    }

    @Test
    void testContainsOnZeroIndex() {
        assertTrue(numbers.contains(10));
    }

    @Test
    void testAddGetSize() {
        int sizeNumbers = numbers.size();
        assertEquals(sizeNumbers, arrNumbers.length);
        for (int i = 0; i < sizeNumbers; ++i) {
            assertEquals(numbers.get(i), arrNumbers[i]);
        }
    }

    @Test
    void testToArray() {
        assertArrayEquals(numbers.toArray(), arrNumbers);
    }

    @Test
    void testAddOnIndex() {
        //Add in the middle
        assertTrue(numbers.add(1, 44));
        //Add first
        assertTrue(numbers.add(0, 66));
        //Add last
        assertTrue(numbers.add(8, 3000));
        int[] expectedNumbers = {66, 10, 44, 7, 11, -2, 13, 10, 2000, 3000};
        assertEquals(10, numbers.size());
        for (int i = 0; i < expectedNumbers.length; ++i) {
            assertEquals(numbers.get(i), expectedNumbers[i]);
        }
    }

    @Test
    void testIndexOf() {
        assertEquals(0, numbers.indexOf(10));
        assertEquals(6, numbers.indexOf(2000));
        assertEquals(-1, numbers.indexOf(100));
    }

    @Test
    void testLastIndexOf() {
        numbers.add(0, -2);
        assertEquals(4, numbers.lastIndexOf(-2));
    }

    @Test
    void testLastIndexOfElementAtTheZeroIndex() {
        numbers.add(0, 194);
        assertEquals(0, numbers.lastIndexOf(194));
    }

    @Test
    void testRemoveOnIndex() {
        Integer[] actual = {10, 7, -2, 13, 10, 2000}; // remove on index = 2
        assertThrows(IllegalArgumentException.class, () -> numbers.remove(-1));
        assertEquals(11, numbers.remove(2));
        assertEquals(numbers.size(), actual.length);
        for (int i = 0; i < actual.length; ++i) {
            assertEquals(numbers.get(i), actual[i]);
        }
    }

    @Test
    void testRemoveObject() {
        Integer[] actual = {10, 7, -2, 13, 10, 2000}; // remove 11
        assertTrue(numbers.remove((Integer) 11));
        assertEquals(numbers.size(), actual.length);
        for (int i = 0; i < actual.length; ++i) {
            assertEquals(numbers.get(i), actual[i]);
        }
    }

    @Test
    void testAddNullFail() {
        assertFalse(numbers.add(null));
        assertFalse(numbers.add(0, null));
    }

    @Test
    void testAddWrongIndexFail() {
        assertFalse(numbers.add(-1, 5));
        assertFalse(numbers.add(100, 5));
    }

    @Test
    void testSetFailNullObject() {
        assertFalse(numbers.set(null, 0));
    }

    @Test
    void testSetFailWrongIndex() {
        assertFalse(numbers.set(5, -1));
        assertFalse(numbers.set(5, 100));
    }

    @Test
    void testGetFailWrongIndex() {
        assertThrows(IllegalArgumentException.class, () -> numbers.get(-1));
        assertThrows(IllegalArgumentException.class, () -> numbers.get(100));
    }

    @Test
    void testIndexOfFailNull() {
        assertEquals(-1, numbers.indexOf((Integer) null));
        assertEquals(-1, numbers.lastIndexOf((Integer) null));
    }

    @Test
    void clearAndIsEmpty() {
        numbers.clear();
        assertTrue(numbers.isEmpty());
        assertEquals(numbers.size(), 0);
    }

    @Test
    public void testThreadSafeAdd() throws InterruptedException {
        numbers = new BlockingCustomList<>();
        for (int i = 0; i < 100; i++) {
            final int value = i;
            executorService.submit(() -> numbers.add(value));
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        assertEquals(100, numbers.size());
    }

    @Test
    public void testThreadSafeAddRemove() throws InterruptedException {
        numbers = new BlockingCustomList<>();
        for (int i = 0; i < 100; i++) {
            final int value = i;
            executorService.submit(() -> {
                numbers.add(value);
                numbers.remove((Integer) value);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        assertEquals(0, numbers.size());
    }
}
