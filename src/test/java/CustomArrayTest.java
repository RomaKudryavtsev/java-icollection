import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class CustomArrayTest {
    private ICollection<Integer> numbers;
    private ICollection<String> strings;
    private final Integer[] arrNumbers = {10, 7, 11, -2, 13, 10, 2000};
    private final String[] arrStrings = {"abc", "lmn", "qwerty", "abc"};
    private final BiFunction<Integer, Integer, Predicate<Integer>> getPredicateSearchingNumInRange = (index1, index2) ->
            num -> num >= index1 && num < index2;
    private final Function<Integer, Predicate<String>> getPredicateSearchingStrOfSize = size ->
            str -> str.length() == size;

    @BeforeEach
    void setUp() {
        numbers = new CustomArray<>();
        strings = new CustomArray<>(1);
        Arrays.stream(arrNumbers).forEach(n -> numbers.add(n));
        Arrays.stream(arrStrings).forEach(s -> strings.add(s));
    }

    @Test
    void testIterator() {
        int index = 0;
        for (Integer i : numbers) {
            assertEquals(i, arrNumbers[index++]);
        }
    }

    @Test
    void testRemoveIfNumberIsInRangeOrStringIsOfCertainSize() {
        assertTrue(numbers.removeIf(getPredicateSearchingNumInRange.apply(10, 13)));
        Integer[] expected1 = {7, -2, 13, 2000};
        for (int i = 0; i < numbers.size(); ++i) {
            assertEquals(expected1[i], numbers.get(i));
        }
        assertTrue(strings.removeIf(getPredicateSearchingStrOfSize.apply(3)));
        String[] expected2 = {"qwerty"};
        for (int i = 0; i < strings.size(); ++i) {
            assertEquals(expected2[i], strings.get(i));
        }
        assertFalse(strings.removeIf(getPredicateSearchingStrOfSize.apply(18)));
    }

    @Test
    void testIndexOfWithPredicate() {
        assertEquals(0, numbers.indexOf(getPredicateSearchingNumInRange.apply(10, 11)));
        assertEquals(2, strings.indexOf(getPredicateSearchingStrOfSize.apply(6)));
        assertEquals(-1, strings.indexOf(getPredicateSearchingStrOfSize.apply(10)));
    }

    @Test
    void testLastIndexOfWithPredicate() {
        assertEquals(5, numbers.lastIndexOf(getPredicateSearchingNumInRange.apply(9, 11)));
        assertEquals(3, strings.lastIndexOf(getPredicateSearchingStrOfSize.apply(3)));
        assertEquals(-1, strings.lastIndexOf(getPredicateSearchingStrOfSize.apply(1)));
    }

    @Test
    void testSort() {
        numbers.sort((o1, o2) -> o2 - o1);
        System.out.println(Arrays.toString(numbers.toArray()));
        strings.sort(Comparator.comparingInt(String::length).reversed().thenComparing(String::compareTo));
        System.out.println(Arrays.toString(strings.toArray()));
        numbers.sort(Comparator.comparingInt(o -> o.toString().length()));
        System.out.println(Arrays.toString(numbers.toArray()));
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
        assertTrue(strings.removeAll("abc"));
        String[] expected = {"lmn", "qwerty"};
        for (int i = 0; i < strings.size(); ++i) {
            assertEquals(expected[i], strings.get(i));
        }
    }

    @Test
    void testSet() {
        Integer[] expectedNumbs = {11, 7, 11, -2, 13, 10, 2000};
        numbers.set(11, 0);
        for (int i = 0; i < numbers.size(); ++i) {
            assertEquals(expectedNumbs[i], numbers.get(i));
        }
        String[] expectedStrings = {"hi", "lmn", "qwerty", "abc"};
        strings.set("hi", 0);
        for (int i = 0; i < strings.size(); ++i) {
            assertEquals(expectedStrings[i], strings.get(i));
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
        assertTrue(strings.contains("qwerty"));
        assertFalse(strings.contains("hello"));
    }

    @Test
    void testContainsOnZeroIndex() {
        assertTrue(numbers.contains(10));
    }

    @Test
    void testAddGetSize() {
        int sizeNumbers = numbers.size();
        int sizeStrings = strings.size();
        assertEquals(sizeNumbers, arrNumbers.length);
        assertEquals(sizeStrings, arrStrings.length);
        for (int i = 0; i < sizeNumbers; ++i) {
            assertEquals(numbers.get(i), arrNumbers[i]);
        }
        for (int i = 0; i < sizeStrings; ++i) {
            assertEquals(strings.get(i), arrStrings[i]);
        }
    }

    @Test
    void testToArray() {
        assertArrayEquals(numbers.toArray(), arrNumbers);
        assertArrayEquals(strings.toArray(), arrStrings);
    }

    @Test
    void testAddOnIndex() {
        assertTrue(numbers.add(1, 44));
        assertTrue(numbers.add(0, 66));
        assertTrue(strings.add(2, "lol"));
        assertTrue(strings.add(0, "hello"));
        int[] expectedNumbers = {66, 10, 44, 7, 11, -2, 13, 10, 2000};
        String[] expectedStrings = {"hello", "abc", "lmn", "lol", "qwerty", "abc"};
        assertEquals(9, numbers.size());
        assertEquals(6, strings.size());
        for (int i = 0; i < expectedNumbers.length; ++i) {
            assertEquals(numbers.get(i), expectedNumbers[i]);
        }
        for (int i = 0; i < expectedStrings.length; ++i) {
            assertEquals(strings.get(i), expectedStrings[i]);
        }
    }

    @Test
    void testIndexOf() {
        assertEquals(0, numbers.indexOf(10));
        assertEquals(6, numbers.indexOf(2000));
        assertEquals(-1, numbers.indexOf(100));
        assertEquals(0, strings.indexOf("abc"));
        assertEquals(1, strings.indexOf("lmn"));
        assertEquals(-1, strings.indexOf("hello"));
    }

    @Test
    void testLastIndexOf() {
        numbers.add(0, -2);
        assertEquals(4, numbers.lastIndexOf(-2));
        assertEquals(3, strings.lastIndexOf("abc"));
    }

    @Test
    void testLastIndexOfElementAtTheZeroIndex() {
        numbers.add(0, 194);
        assertEquals(0, numbers.lastIndexOf(194));
    }

    @Test
    void testRemoveOnIndex() {
        Integer[] actual = {10, 7, -2, 13, 10, 2000}; // remove on index = 2
        assertNull(numbers.remove(100));
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
        assertNull(numbers.get(-1));
        assertNull(numbers.get(100));
    }

    @Test
    void testIndexOfFailNull() {
        assertEquals(-1, numbers.indexOf((Integer) null));
        assertEquals(-1, numbers.lastIndexOf((Integer) null));
    }

    @Test
    void clearAndIsEmpty() {
        numbers.clear();
        assertEquals(numbers.size(), 0);
        assertTrue(numbers.isEmpty());
    }
}
