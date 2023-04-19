package collection_domain;

import java.util.Collection;
import java.util.function.Predicate;

public interface ICollection<T> extends Iterable<T> {
    boolean add(T obj);

    int size();

    boolean remove(T obj);

    boolean contains(T obj);

    Object[] toArray();

    boolean addAll(Collection<T> other);

    boolean removeIf(Predicate<T> predicate);

    boolean isEmpty();

    void clear();

    boolean containsAll(Collection<T> c);

    boolean retainAll(Collection<T> c);

    boolean removeAll(Collection<T> c);
}
