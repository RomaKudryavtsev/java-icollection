package collection_domain.list;

import collection_domain.ICollection;

import java.util.*;
import java.util.function.Predicate;

public class BlockingCustomList<T> implements ICollection<T> {
    private volatile int size;
    private Node<T> head;
    private Node<T> tail;
    private final Object lock = new Object();

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        public Node(T data, Node<T> previous, Node<T> next) {
            this.data = data;
            this.prev = previous;
            this.next = next;
        }
    }

    private boolean checkIfObjectIsNull(T obj) {
        return obj == null;
    }

    private boolean checkIfIndexIsIncorrect(int index) {
        return index < 0 || index >= size;
    }

    @Override
    public boolean add(T obj) {
        if (checkIfObjectIsNull(obj)) {
            return false;
        }
        synchronized (lock) {
            Node<T> newNode = new Node<>(obj, tail, null);
            if (tail == null) {
                modifyHead(newNode);
            } else {
                tail.next = newNode;
                newNode.prev = tail;
            }
            modifyTail(newNode);
            size += 1;
        }
        return true;
    }

    @Override
    public T remove(int index) {
        if (checkIfIndexIsIncorrect(index)) {
            throw new IllegalArgumentException("Wrong index");
        }
        synchronized (lock) {
            Node<T> removed = getNodeByIndex(index);
            if (size == 1) {
                modifyHead(null);
                modifyTail(null);
            } else if (removed.prev == null) {
                removed.next.prev = null;
                modifyHead(head.next);
            } else if (removed.next == null) {
                removed.prev.next = null;
                modifyTail(tail.prev);
            } else {
                removed.next.prev = removed.prev;
                removed.prev.next = removed.next;
            }
            size -= 1;
            return removed.data;
        }
    }

    @Override
    public boolean remove(T obj) {
        if (checkIfObjectIsNull(obj)) {
            return false;
        }
        synchronized (lock) {
            int indexRemoved = indexOf(obj);
            remove(indexRemoved);
        }
        return true;
    }

    @Override
    public boolean add(int index, T obj) {
        if (checkIfObjectIsNull(obj) || checkIfIndexIsIncorrect(index)) {
            return false;
        }
        synchronized (lock) {
            if (index == 0) {
                Node<T> oldHead = head;
                modifyHead(new Node<>(obj, null, oldHead));
                head.next = oldHead;
                oldHead.prev = head;
            } else if (index == size - 1) {
                Node<T> oldTail = tail;
                modifyTail(new Node<>(obj, oldTail, null));
                oldTail.next = tail;
                tail.prev = oldTail;
            } else {
                Node<T> nodeOnIndex = getNodeByIndex(index);
                Node<T> prevNode = nodeOnIndex.prev;
                Node<T> newNode = new Node<>(obj, prevNode, nodeOnIndex);
                prevNode.next = newNode;
                nodeOnIndex.prev = newNode;
            }
            size += 1;
        }
        return true;
    }

    @Override
    public T get(int index) {
        if (checkIfIndexIsIncorrect(index)) {
            throw new IllegalArgumentException("Wrong index");
        }
        synchronized (lock) {
            Node<T> node = getNodeByIndex(index);
            return node.data;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int indexOf(T obj) {
        synchronized (lock) {
            int index = 0;
            if (checkIfObjectIsNull(obj)) {
                for (Node<T> cur = head; cur != null; cur = cur.next) {
                    if (cur.data == null) {
                        return index;
                    }
                    index += 1;
                }
            } else {
                for (Node<T> cur = head; cur != null; cur = cur.next) {
                    if (cur.data.equals(obj)) {
                        return index;
                    }
                    index += 1;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(T obj) {
        synchronized (lock) {
            int index = size - 1;
            if (checkIfObjectIsNull(obj)) {
                for (Node<T> cur = tail; cur != null; cur = cur.prev) {
                    if (cur.data == null) {
                        return index;
                    }
                    index -= 1;
                }
            } else {
                for (Node<T> cur = tail; cur != null; cur = cur.prev) {
                    if (cur.data.equals(obj)) {
                        return index;
                    }
                    index -= 1;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean contains(T obj) {
        return indexOf(obj) >= 0;
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        synchronized (lock) {
            Iterator<T> it = this.iterator();
            int index = 0;
            while (it.hasNext()) {
                arr[index] = it.next();
                index++;
            }
        }
        return arr;
    }

    @Override
    public boolean set(T obj, int index) {
        if (checkIfObjectIsNull(obj) || checkIfIndexIsIncorrect(index)) {
            return false;
        }
        synchronized (lock) {
            Node<T> nodeToBeModified = getNodeByIndex(index);
            nodeToBeModified.data = obj;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addAll(ICollection<T> other) {
        synchronized (lock) {
            Arrays.stream(other.toArray()).map(o -> (T) o).forEach(this::add);
        }
    }

    @Override
    public boolean removeAll(T obj) {
        if (checkIfObjectIsNull(obj)) {
            return false;
        }
        synchronized (lock) {
            int temp = size;
            for (Node<T> cur = tail; cur != null; cur = cur.prev) {
                if (cur.data.equals(obj)) {
                    removeNode(cur);
                }
            }
            return temp != size;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void addAll(ICollection<T> other, int index) {
        synchronized (lock) {
            final int[] indexCopy = {index};
            Arrays.stream(other.toArray()).map(o -> (T) o).forEach(t -> {
                this.add(indexCopy[0], t);
                ++indexCopy[0];
            });
        }
    }

    @Override
    public void sort(Comparator<T> comp) {
        synchronized (lock) {
            boolean swapped;
            int index = size - 1;
            do {
                swapped = true;
                for (int i = 0; i < index; ++i) {
                    if (comp.compare(getNodeByIndex(i).data, getNodeByIndex(i + 1).data) > 0) {
                        swap(i, i + 1);
                        swapped = false;
                    }
                }
                --index;
            } while (!swapped);
        }
    }

    @Override
    public boolean removeIf(Predicate<T> predicate) {
        synchronized (lock) {
            int temp = size;
            for (Node<T> cur = head; cur != null; cur = cur.next) {
                if (predicate.test(cur.data)) {
                    removeNode(cur);
                }
            }
            return temp != size;
        }
    }

    @Override
    public int indexOf(Predicate<T> predicate) {
        synchronized (lock) {
            for (Node<T> cur = head; cur != null; cur = cur.next) {
                if (predicate.test(cur.data)) {
                    return indexOf(cur.data);
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Predicate<T> predicate) {
        synchronized (lock) {
            for (Node<T> cur = tail; cur != null; cur = cur.prev) {
                if (predicate.test(cur.data)) {
                    return lastIndexOf(cur.data);
                }
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
        synchronized (lock) {
            for (Node<T> cur = head; cur != null; cur = cur.next) {
                removeNode(cur);
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        List<T> list = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return Collections.synchronizedList(list).iterator();
    }

    private synchronized void modifyHead(Node<T> newHead) {
        this.head = newHead;
    }

    private synchronized void modifyTail(Node<T> newTail) {
        this.tail = newTail;
    }

    private synchronized Node<T> getNodeByIndex(int index) {
        if (checkIfIndexIsIncorrect(index)) {
            throw new IllegalArgumentException("Wrong index");
        }
        Node<T> current;
        if (index <= size / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    private synchronized void removeNode(Node<T> node) {
        if (node.prev == null) {
            modifyHead(node.next);
            node.next.prev = head;
        } else if (node.next == null) {
            modifyTail(node.prev);
            node.prev.next = tail;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        size -= 1;
    }

    private synchronized void swap(int index1, int index2) {
        Node<T> nodeOnIndex1 = getNodeByIndex(index1);
        Node<T> nodeOnIndex2 = getNodeByIndex(index2);
        T tempObj = nodeOnIndex1.data;
        nodeOnIndex1.data = nodeOnIndex2.data;
        nodeOnIndex2.data = tempObj;
    }
}
