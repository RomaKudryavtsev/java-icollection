package collection_domain.set_domain;

import collection_domain.ICollection;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class CustomSet<T> implements ICollection<T> {
    private List<T>[] table;
    private int size;
    private int capacity;
    private final double loadFactor;

    @SuppressWarnings("unchecked")
    public CustomSet(int capacity, double loadFactor) {
        table = new LinkedList[capacity];
        this.capacity = capacity;
        this.loadFactor = loadFactor;
    }

    @SuppressWarnings("unchecked")
    public CustomSet(int capacity) {
        table = new LinkedList[capacity];
        this.capacity = capacity;
        this.loadFactor = 0.75;
    }

    public CustomSet() {
        this(16, 0.75);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        int index = getHashedIndex(o);
        if (table[index] == null) {
            return false;
        }
        return table[index].contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            int totalCount = 0;
            int tableIndex = 0;
            int bucketIndex = 0;

            @Override
            public boolean hasNext() {
                return totalCount < size;
            }

            @Override
            public T next() {
                while (table[tableIndex] == null || table[tableIndex].isEmpty()) {
                    tableIndex++;
                }
                T res = table[tableIndex].get(bucketIndex);
                totalCount++;
                if (bucketIndex < table[tableIndex].size() - 1) {
                    bucketIndex++;
                } else {
                    bucketIndex = 0;
                    tableIndex++;
                }
                return res;
            }
        };
    }

    @Override
    public boolean removeIf(Predicate<T> predicate) {
        if (predicate == null) {
            return false;
        } else {
            CustomSet<T> temp = new CustomSet<>(this.capacity);
            this.forEach(e -> {
                if (!predicate.test(e)) {
                    temp.add(e);
                }
            });
            this.size = temp.size;
            this.table = temp.table;
            return true;
        }
    }

    @Override
    public boolean retainAll(Collection<T> c) {
        if (c == null) {
            return false;
        } else {
            CustomSet<T> temp = new CustomSet<>(this.capacity);
            this.forEach(e -> {
                if (c.contains(e)) {
                    temp.add(e);
                }
            });
            this.size = temp.size;
            this.table = temp.table;
            return true;
        }
    }

    @Override
    public Object[] toArray() {
        Object[] res = new Object[size];
        int index = 0;
        for (List<T> bucket : table) {
            if (bucket == null || bucket.isEmpty()) {
                continue;
            }
            for (Object elem : bucket) {
                res[index] = elem;
                index++;
            }
        }
        return res;
    }

    @Override
    public boolean add(T e) {
        if (contains(e)) {
            return false;
        }
        if (capacity * loadFactor < size) {
            redistributeElements();
        }
        int index = getHashedIndex(e);
        if (table[index] == null) {
            table[index] = new LinkedList<>();
        }
        table[index].add(e);
        size++;
        return true;
    }

    @SuppressWarnings("unchecked")
    private void redistributeElements() {
        capacity += 16;
        LinkedList<T>[] temp = new LinkedList[capacity];
        for (List<T> bucket : table) {
            if (bucket == null) {
                continue;
            }
            for (T elem : bucket) {
                int index = getHashedIndex(elem);
                if (temp[index] == null) {
                    temp[index] = new LinkedList<>();
                }
                temp[index].add(elem);
            }
        }
        table = temp;
    }

    private int getHashedIndex(Object e) {
        int hash = e.hashCode();
        return Math.abs(hash) % capacity;
    }

    @Override
    public boolean remove(Object o) {
        if (!contains(o)) {
            return false;
        }
        int index = getHashedIndex(o);
        table[index].remove(o);
        --size;
        return true;
    }

    @Override
    public boolean containsAll(Collection<T> c) {
        if (c == null) {
            return false;
        } else {
            return c.stream().filter(this::contains).count() == c.size();
        }
    }

    @Override
    public boolean addAll(Collection<T> c) {
        if (c == null) {
            return false;
        } else {
            final boolean[] flag = {true};
            c.forEach(e -> {
                if (!add(e)) {
                    flag[0] = false;
                }
            });
            return flag[0];
        }
    }

    @Override
    public boolean removeAll(Collection<T> c) {
        if (c == null) {
            return false;
        } else {
            final boolean[] flag = {true};
            c.forEach(e -> {
                if (!remove(e)) {
                    flag[0] = false;
                }
            });
            return flag[0];
        }
    }

    @Override
    public void clear() {
        table = new LinkedList[capacity];
        size = 0;
    }
}
