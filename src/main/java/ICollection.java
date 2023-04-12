import java.util.Comparator;
import java.util.function.Predicate;

public interface ICollection<T> extends Iterable<T> {

    boolean add(T obj);

    boolean add(int index, T obj);

    T get(int index);

    int size();

    int indexOf(T obj);

    int lastIndexOf(T obj);

    T remove(int index);

    boolean remove(T obj);

    boolean contains(T obj);

    Object[] toArray();

    boolean set(T obj, int index);

    void addAll(ICollection<T> other);

    boolean removeAll(T obj);

    void addAll(ICollection<T> other, int index);

    void sort(Comparator<T> comp);

    boolean removeIf(Predicate<T> predicate);

    int indexOf(Predicate<T> predicate);

    int lastIndexOf(Predicate<T> predicate);

    boolean isEmpty();

    void clear();
}
