package collection_domain.non_indexed_domain;

import collection_domain.ICollection;

import java.util.Iterator;
import java.util.function.Predicate;

public class CustomSet<T> implements ICollection<T> {
    @Override
    public boolean add(T obj) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean remove(T obj) {
        return false;
    }

    @Override
    public boolean contains(T obj) {
        return false;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public void addAll(ICollection<T> other) {

    }

    @Override
    public boolean removeAll(T obj) {
        return false;
    }

    @Override
    public boolean removeIf(Predicate<T> predicate) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }
}
