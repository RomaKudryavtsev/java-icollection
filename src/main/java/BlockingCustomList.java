import java.util.*;
import java.util.function.Predicate;

public class BlockingCustomList<T> implements ICollection<T> {
    private volatile int size;
    private Node<T> head;
    private Node<T> tail;
    private Object lock = new Object();

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
    public synchronized boolean add(T obj) {
        if (checkIfObjectIsNull(obj)) {
            return false;
        }
        Node<T> newNode = new Node<>(obj, tail, null);
        if (tail == null) {
            modifyHead(newNode);
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        modifyTail(newNode);
        size++;
        return true;
    }

    @Override
    public synchronized boolean add(int index, T obj) {
        if (checkIfObjectIsNull(obj) || checkIfIndexIsIncorrect(index)) {
            return false;
        }
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
        ++size;
        return true;
    }

    @Override
    public T get(int index) {
        if (checkIfIndexIsIncorrect(index)) {
            throw new IllegalArgumentException("Wrong index");
        }
        Node<T> node = getNodeByIndex(index);
        return node.data;
    }

    private Node<T> getNodeByIndex(int index) {
        if (checkIfIndexIsIncorrect(index)) {
            throw new IllegalArgumentException("Wrong index");
        }
        synchronized (lock) {
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
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int indexOf(T obj) {
        int index = 0;
        synchronized (lock) {
            if (checkIfObjectIsNull(obj)) {
                for (Node<T> cur = head; cur != null; cur = cur.next) {
                    if (cur.data == null) {
                        return index;
                    }
                    index++;
                }
            } else {
                for (Node<T> cur = head; cur != null; cur = cur.next) {
                    if (cur.data.equals(obj)) {
                        return index;
                    }
                    index++;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(T obj) {
        int index = size - 1;
        synchronized (lock) {
            if (checkIfObjectIsNull(obj)) {
                for (Node<T> cur = tail; cur != null; cur = cur.prev) {
                    if (cur.data == null) {
                        return index;
                    }
                    index--;
                }
            } else {
                for (Node<T> cur = tail; cur != null; cur = cur.prev) {
                    if (cur.data.equals(obj)) {
                        return index;
                    }
                    index--;
                }
            }
        }
        return -1;
    }

    @Override
    public synchronized T remove(int index) {
        if (checkIfIndexIsIncorrect(index)) {
            throw new IllegalArgumentException("Wrong index");
        }
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
        size--;
        return removed.data;
    }

    @Override
    public synchronized boolean remove(T obj) {
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
    public synchronized Object[] toArray() {
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
    public synchronized boolean set(T obj, int index) {
        if (checkIfObjectIsNull(obj) || checkIfIndexIsIncorrect(index)) {
            return false;
        }
        Node<T> nodeToBeModified = getNodeByIndex(index);
        nodeToBeModified.data = obj;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void addAll(ICollection<T> other) {
        Arrays.stream(other.toArray()).map(o -> (T) o).forEach(this::add);
    }

    @Override
    public synchronized boolean removeAll(T obj) {
        if (checkIfObjectIsNull(obj)) {
            return false;
        }
        int temp = size;
        for (Node<T> cur = tail; cur != null; cur = cur.prev) {
            if (cur.data.equals(obj)) {
                removeNode(cur);
            }
        }
        return temp != size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void addAll(ICollection<T> other, int index) {
        final int[] indexCopy = {index};
        Arrays.stream(other.toArray()).map(o -> (T) o).forEach(t -> {
            this.add(indexCopy[0], t);
            ++indexCopy[0];
        });
    }

    @Override
    public synchronized void sort(Comparator<T> comp) {
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

    @Override
    public synchronized boolean removeIf(Predicate<T> predicate) {
        int temp = size;
        for (Node<T> cur = head; cur != null; cur = cur.next) {
            if (predicate.test(cur.data)) {
                removeNode(cur);
            }
        }
        return temp != size;
    }

    @Override
    public int indexOf(Predicate<T> predicate) {
        for (Node<T> cur = head; cur != null; cur = cur.next) {
            if (predicate.test(cur.data)) {
                return indexOf(cur.data);
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Predicate<T> predicate) {
        for (Node<T> cur = tail; cur != null; cur = cur.prev) {
            if (predicate.test(cur.data)) {
                return lastIndexOf(cur.data);
            }
        }
        return -1;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public synchronized void clear() {
        for (Node<T> cur = head; cur != null; cur = cur.next) {
            removeNode(cur);
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
        size--;
    }

    private synchronized void swap(int index1, int index2) {
        Node<T> nodeOnIndex1 = getNodeByIndex(index1);
        Node<T> nodeOnIndex2 = getNodeByIndex(index2);
        T tempObj = nodeOnIndex1.data;
        nodeOnIndex1.data = nodeOnIndex2.data;
        nodeOnIndex2.data = tempObj;
    }
}
