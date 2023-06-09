package collection_domain.list_domain;

import java.util.Collection;

public abstract class AbstractList<T> implements ICollectionIndexed<T> {
    protected int size = 0;

    protected boolean checkIfObjectIsNull(T obj) {
        return obj == null;
    }

    protected boolean checkIfIndexIsIncorrect(int index) {
        return index < 0 || index >= size;
    }

    public abstract int indexOf(T obj);

    public abstract T remove(int index);

    public abstract boolean add(T obj);

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean remove(T obj) {
        if (checkIfObjectIsNull(obj)) {
            return false;
        }
        int indexRemoved = indexOf(obj);
        remove(indexRemoved);
        return true;
    }

    @Override
    public boolean contains(T obj) {
        return indexOf(obj) >= 0;
    }

    @Override
    public boolean addAll(Collection<T> other) {
        if (other == null) {
            return false;
        }
        other.forEach(this::add);
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
    public boolean retainAll(Collection<T> c) {
        if (c == null) {
            return false;
        } else {
            this.forEach(elem -> {
                if (!c.contains(elem)) {
                    remove(elem);
                }
            });
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<T> c) {
        if (c == null) {
            return false;
        }
        c.forEach(this::remove);
        return true;
    }
}
