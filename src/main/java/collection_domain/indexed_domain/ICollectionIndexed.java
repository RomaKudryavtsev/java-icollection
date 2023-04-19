package collection_domain.indexed_domain;

import collection_domain.ICollection;

import java.util.Comparator;
import java.util.function.Predicate;

public interface ICollectionIndexed<T> extends Iterable<T>, ICollection<T> {
    boolean add(int index, T obj);

    T get(int index);

    int indexOf(T obj);

    int lastIndexOf(T obj);

    boolean removeAll(T obj);

    T remove(int index);

    boolean set(T obj, int index);

    void addAll(ICollection<T> other, int index);

    void sort(Comparator<T> comp);

    int indexOf(Predicate<T> predicate);

    int lastIndexOf(Predicate<T> predicate);
}
