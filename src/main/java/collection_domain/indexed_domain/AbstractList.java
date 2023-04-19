package collection_domain.indexed_domain;

public abstract class AbstractList {
    protected int size = 0;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
