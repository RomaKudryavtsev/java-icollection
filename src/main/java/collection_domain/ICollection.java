package collection_domain;

import java.util.function.Predicate;

public interface ICollection<T> extends Iterable<T> {
    boolean add(T obj);

    int size();

    boolean remove(T obj);

    boolean contains(T obj);

    Object[] toArray();

    void addAll(ICollection<T> other);

    boolean removeAll(T obj);

    boolean removeIf(Predicate<T> predicate);

    boolean isEmpty();

    void clear();
}
