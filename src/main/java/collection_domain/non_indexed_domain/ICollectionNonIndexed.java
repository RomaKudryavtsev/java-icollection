package collection_domain.non_indexed_domain;

import collection_domain.ICollection;

import java.util.Collection;

public interface ICollectionNonIndexed<T> extends ICollection<T> {
    boolean containsAll(Collection<T> c);

    boolean retainAll(Collection<T> c);

    boolean removeAll(Collection<T> c);
}
