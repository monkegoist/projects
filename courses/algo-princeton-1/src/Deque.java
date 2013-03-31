import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node first;
    private Node last;
    private int count;

    public Deque() {
        // construct an empty deque
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public void addFirst(Item item) {
        if (item == null)
            throw new NullPointerException();
        // create new node
        Node node = new Node();
        node.value = item;
        node.previous = null;
        node.next = first;
        // update ex-first node
        if (first != null)
            first.previous = node;
        first = node;
        // if this is the first node, last should point to it as well
        if (last == null)
            last = first;
        count++;
    }

    public void addLast(Item item) {
        if (item == null)
            throw new NullPointerException();
        // create new node
        Node node = new Node();
        node.value = item;
        node.previous = last;
        node.next = null;
        // update ex-last node
        if (last != null)
            last.next = node;
        last = node;
        // if this is the first node, first should point to it as well
        if (first == null)
            first = last;
        count++;
    }

    public Item removeFirst() {
        // delete and return the item at the front
        if (isEmpty())
            throw new NoSuchElementException();
        Node node = first;
        first = node.next;
        if (first != null)
            first.previous = null;
        count--;
        if (isEmpty())
            last = null;
        return node.value;
    }

    public Item removeLast() {
        // delete and return the item at the end
        if (isEmpty())
            throw new NoSuchElementException();
        Node node = last;
        last = node.previous;
        if (last != null)
            last.next = null;
        count--;
        if (isEmpty())
            first = null;
        return node.value;
    }

    @Override
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class Node {
        Item value;
        Node previous;
        Node next;
    }

    private class DequeIterator implements Iterator<Item> {

        private Node current;

        private DequeIterator() {
            current = first;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException();
            Node node = current;
            current = node.next;
            return node.value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}