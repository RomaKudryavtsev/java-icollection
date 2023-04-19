import collection_domain.non_indexed_domain.set.CustomSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomSetTest {
    CustomSet<Integer> customSet;
    List<Integer> expected = new ArrayList<>(Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19));

    @BeforeEach
    void setUp() {
        customSet = new CustomSet<>();
        for (Integer i : expected) {
            customSet.add(i);
        }
    }

    @Test
    void size() {
        assertEquals(customSet.size(), expected.size());
    }

    @Test
    void isEmpty() {
        customSet.clear();
        assertTrue(customSet.isEmpty());
    }

    @Test
    void contains() {
        for (Integer num : expected) {
            assertTrue(customSet.contains(num));
        }
        assertFalse(customSet.contains(100));
    }

    @Test
    void iterator() {
        Iterator<Integer> iterator = customSet.iterator();
        while (iterator.hasNext()) {
            assertTrue(expected.contains(iterator.next()));
        }
    }

    @Test
    void toArray() {
        Object[] res = customSet.toArray();
        assertEquals(expected.size(), res.length);
        for (Object num : res) {
            assertTrue(expected.contains(num));
        }
    }

    @Test
    void add() {
        assertEquals(expected.size(), customSet.size());
        assertFalse(customSet.add(2));
        assertEquals(expected.size(), customSet.size());
        assertTrue(customSet.add(1));
        assertEquals(expected.size() + 1, customSet.size());
        assertTrue(customSet.contains(1));
    }

    @Test
    void remove() {
        assertFalse(customSet.remove(1));
        assertEquals(expected.size(), customSet.size());
        assertTrue(customSet.remove(5));
        assertEquals(expected.size() - 1, customSet.size());
        assertFalse(customSet.contains(5));
    }

    @Test
    void containsAll() {
        List<Integer> listContained = List.of(2, 3, 5);
        assertTrue(customSet.containsAll(listContained));
        List<Integer> listNotContained = List.of(100, 200, 150);
        assertFalse(customSet.containsAll(listNotContained));
        List<Integer> listContainedAndNotContained = List.of(2, 3, 100);
        assertFalse(customSet.containsAll(listContainedAndNotContained));
    }

    @Test
    void addAll() {
        List<Integer> expected = new ArrayList<>(Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19, 100, 200));
        List<Integer> toBeAdded = new ArrayList<>(Arrays.asList(100, 200));
        customSet.addAll(toBeAdded);
        assertEquals(expected.size(), customSet.size());
        Iterator<Integer> iterator = expected.iterator();
        while (iterator.hasNext()) {
            assertTrue(customSet.contains(iterator.next()));
        }
    }

    @Test
    void retainAll() {
        List<Integer> toBeRetained = List.of(2, 3, 5);
        customSet.retainAll(toBeRetained);
        assertEquals(toBeRetained.size(), customSet.size());
        Iterator<Integer> iterator = toBeRetained.iterator();
        while (iterator.hasNext()) {
            assertTrue(customSet.contains(iterator.next()));
        }
    }

    @Test
    void removeAll() {
        List<Integer> expected = new ArrayList<>(Arrays.asList(7, 11, 13, 17, 19));
        List<Integer> toBeRemoved = List.of(2, 3, 5);
        customSet.removeAll(toBeRemoved);
        assertEquals(expected.size(), customSet.size());
        Iterator<Integer> iterator = expected.iterator();
        while (iterator.hasNext()) {
            assertTrue(customSet.contains(iterator.next()));
        }
    }

    @Test
    void clear() {
        customSet.clear();
        assertEquals(0, customSet.size());
    }
}