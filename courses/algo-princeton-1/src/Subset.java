import java.util.Iterator;

public class Subset {
    public static void main(String[] args) {
        RandomizedQueue<String> queue = new RandomizedQueue<String>();
        while (!StdIn.isEmpty())
            queue.enqueue(StdIn.readString());
        int k = Integer.parseInt(args[0]);
        Iterator<String> iterator = queue.iterator();
        int count = 0;
        while (iterator.hasNext() && count < k) {
            System.out.println(iterator.next());
            count++;
        }
    }
}
