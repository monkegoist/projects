import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] arr;
    private int index;

    public RandomizedQueue() {
        // construct an empty randomized queue
        arr = (Item[]) new Object[1];
        index = 0;
    }

    public boolean isEmpty() {
        return index == 0;
    }

    public int size() {
        return index;
    }

    public void enqueue(Item item) {
        if (item == null)
            throw new NullPointerException();
        if (index == arr.length)
            resize(arr.length * 2);
        arr[index++] = item;
    }

    public Item dequeue() {
        if (isEmpty())
            throw new NoSuchElementException();
        int pickedIndex = pickRandom();
        Item pickedItem = arr[pickedIndex];
        arr[pickedIndex] = arr[--index];
        arr[index] = null;
        if (index == arr.length / 4 && arr.length > 1)
            resize(arr.length / 2);
        return pickedItem;
    }

    private void resize(int size) {
        Item[] newArr = (Item[]) new Object[size];
        System.arraycopy(arr, 0, newArr, 0, index);
        arr = newArr;
    }


    public Item sample() {
        if (isEmpty())
            throw new NoSuchElementException();
        return arr[pickRandom()];
    }

    private int pickRandom() {
        return (int) (Math.random() * size());
    }

    @Override
    public Iterator<Item> iterator() {
        return new RandomizedIterator();
    }

    private class RandomizedIterator implements Iterator<Item> {

        private final int[] indices;
        private int current;

        RandomizedIterator() {
            indices = new int[size()];
            // fill in array of indices
            for (int i = 0; i < indices.length; i++)
                indices[i] = i;
            // shuffle
            shuffle();
        }

        private void shuffle() {
            for (int i = 1; i < indices.length; i++) {
                int r = (int) (Math.random() * (i + 1));
                int swap = indices[i];
                indices[i] = indices[r];
                indices[r] = swap;
            }
        }

        @Override
        public boolean hasNext() {
            return current < indices.length;
        }

        @Override
        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return arr[indices[current++]];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}