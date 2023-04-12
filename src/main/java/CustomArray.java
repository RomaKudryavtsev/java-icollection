import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;

public class CustomArray<T> implements ICollection<T> {
    private final static int DEFAULT_CAPACITY = 16;
    private Object[] array;
    private int size = 0;

    public CustomArray(int capacity) {
        array = new Object[capacity];
    }

    public CustomArray() {
        this(DEFAULT_CAPACITY);
    }

    private boolean checkIfObjectIsNull(T obj) {
        return obj == null;
    }

    private boolean checkIfIndexIsIncorrect(int index) {
        return index < 0 || index >= size;
    }

    private void allocateArray() {
        array = Arrays.copyOf(array, array.length + DEFAULT_CAPACITY);
    }

    @Override
    public boolean add(T obj) {
        if (checkIfObjectIsNull(obj)) {
            return false;
        }
        if (size == array.length) {
            allocateArray();
        }
        array[size] = obj;
        ++size;
        return true;
    }

    @Override
    public boolean add(int index, T obj) {
        if (checkIfObjectIsNull(obj) || checkIfIndexIsIncorrect(index)) {
            return false;
        }
        if (index == size) {
            return add(obj);
        }
        if (size == array.length) {
            allocateArray();
        }
        System.arraycopy(array, index, array, index + 1, size - index);
        array[index] = obj;
        ++size;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (checkIfIndexIsIncorrect(index)) {
            return null;
        }
        return (T) array[index];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int indexOf(T obj) {
        if (checkIfObjectIsNull(obj)) {
            return -1;
        }
        for (int i = 0; i < size; ++i) {
            if (array[i].equals(obj)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(T obj) {
        if (checkIfObjectIsNull(obj)) {
            return -1;
        }
        for (int i = size - 1; i >= 0; --i) {
            if (array[i].equals(obj)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        if (checkIfIndexIsIncorrect(index)) {
            return null;
        }
        T objToBeDeleted = (T) array[index];
        if (index < size - 1) {
            System.arraycopy(array, index + 1, array, index, size - index - 1);
        }
        --size;
        return objToBeDeleted;
    }

    @Override
    public boolean remove(T obj) {
        return remove(indexOf(obj)) != null;
    }

    @Override
    public boolean contains(T obj) {
        return indexOf(obj) >= 0;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(array, size);
    }

    @Override
    public boolean set(T obj, int index) {
        if (checkIfObjectIsNull(obj) || checkIfIndexIsIncorrect(index)) {
            return false;
        }
        array[index] = obj;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addAll(ICollection<T> other) {
        Arrays.stream(other.toArray()).map(o -> (T) o).forEach(this::add);
    }

    @Override
    public boolean removeAll(T obj) {
        if (checkIfObjectIsNull(obj)) {
            return false;
        }
        int temp = size;
        for (int i = size - 1; i >= 0; i--) {
            if (array[i].equals(obj))
                remove(i);
        }
        return temp != size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addAll(ICollection<T> other, int index) {
        final int[] indexCopy = {index};
        Arrays.stream(other.toArray()).map(o -> (T) o).forEach(t -> {
            this.add(indexCopy[0], t);
            ++indexCopy[0];
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sort(Comparator<T> comp) {
        boolean swapped;
        int index = size - 1;
        do {
            swapped = true;
            for (int i = 0; i < index; ++i) {
                if (comp.compare((T) array[i], (T) array[i + 1]) > 0) {
                    swap(i, i + 1);
                    swapped = false;
                }
            }
            --index;
        } while (!swapped);
    }

    @SuppressWarnings("unchecked")
    private void swap(int index1, int index2) {
        T temp = (T) array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeIf(Predicate<T> predicate) {
        int temp = size;
        for (int i = size - 1; i >= 0; i--) {
            if (predicate.test((T) array[i]))
                remove(i);
        }
        return temp != size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int indexOf(Predicate<T> predicate) {
        for (int i = 0; i < size; ++i) {
            if (predicate.test((T) array[i])) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int lastIndexOf(Predicate<T> predicate) {
        for (int i = size - 1; i >= 0; --i) {
            if (predicate.test((T) array[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        array = new Object[size];
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new CustomIterator();
    }

    private class CustomIterator implements Iterator<T> {
        int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            return (T) array[currentIndex++];
        }
    }
}