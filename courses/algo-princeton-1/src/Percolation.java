/**
 * This class is implemented with weighted quick union algorithm.
 * <p/>
 * For simplicity and correctness only top row is represented as a 'virtual' point.
 * So call to {@link #percolates()} takes linear time.
 * <p/>
 * Note: last submission result is 86.11 and not 100 since I haven't used {@link WeightedQuickUnionUF} and
 * test expects students to be using it.
 */
public class Percolation {

    private static final int TOP = 0;

    private final boolean[] values;
    private final int[] roots;
    private final int[] count;
    private final int N;

    public Percolation(int N) {
        // create N-by-N grid, with all sites blocked
        this.N = N;
        this.values = new boolean[N * N + 1];
        this.roots = new int[N * N + 1];
        this.count = new int[N * N + 1];
        for (int i = 0; i < N * N + 1; i++) {
            roots[i] = i;
            count[i] = 1;
        }
        // hook up top and bottom rows to virtual sites
        for (int i = 1; i < N + 1; i++)
            roots[i] = TOP;
    }

    // --------------- public methods ---------------
    public boolean isOpen(int i, int j) {
        // is site (row i, column j) open?
        validate(i, j);
        return values[translate(i, j)];
    }

    public boolean isFull(int i, int j) {
        // is site (row i, column j) full?
        validate(i, j);
        return isOpen(i, j) && connected(translate(i, j), TOP);
    }

    public void open(int i, int j) {
        // open site (row i, column j) if it is not already
        validate(i, j);
        values[translate(i, j)] = true;
        if (i > 1 && isOpen(i - 1, j))
            union(translate(i, j), translate(i - 1, j)); // up
        if (j < N && isOpen(i, j + 1))
            union(translate(i, j), translate(i, j + 1)); // right
        if (i < N && isOpen(i + 1, j))
            union(translate(i, j), translate(i + 1, j)); // down
        if (j > 1 && isOpen(i, j - 1))
            union(translate(i, j), translate(i, j - 1)); // left
    }

    public boolean percolates() {
        // does the system percolate?
        boolean percolates = false;
        for (int j = 1; j < N + 1; j++) {
            percolates = isOpen(N, j) && connected(translate(N, j), TOP);
            if (percolates)
                break;
        }
        return percolates;
    }

    // --------------- private methods ---------------
    private void validate(int i, int j) {
        if (i < 1 || i > N || j < 1 || j > N)
            throw new IndexOutOfBoundsException();
    }

    // whether two sites are connected to each other
    private boolean connected(int s1, int s2) {
        return find(s1) == find(s2);
    }

    // find root site of the given site
    private int find(int site) {
        int root = roots[site];
        while (root != roots[root])
            root = roots[root];
        return root;
    }

    // union two sites
    private void union(int site1, int site2) {
        if (connected(site1, site2))
            return;
        int root1 = find(site1);
        int root2 = find(site2);
        if (root1 == TOP || root2 == TOP) {
            if (root1 == TOP) {
                join(TOP, root2);
            } else {
                join(TOP, root1);
            }
        } else {
            if (count[root1] >= count[root2]) {
                join(root1, root2);
            } else {
                join(root2, root1);
            }
        }
    }

    // helper method
    private void join(int root, int child) {
        roots[child] = root;
        count[root] += count[child];
    }

    // since we're using one-dimensional array, we need translation from two-dimensional index into one-dimensional
    private int translate(int i, int j) {
        return (i - 1) * N + j;
    }

    // --------------- helper methods to print contents ---------------
//    public void printRoots() {
//        StringBuilder builder;
//        for (int i = 1; i < N * N + 1; i += N) {
//            builder = new StringBuilder("[");
//            for (int j = i; j < i + N; j++)
//                builder.append(String.format("%4d", roots[j])).append(",");
//            builder.deleteCharAt(builder.length() - 1);
//            builder.append("]");
//            System.out.println(builder.toString());
//        }
//        System.out.println("-----");
//    }
//
//    public void printValues() {
//        StringBuilder builder;
//        for (int i = 1; i < N * N + 1; i += N) {
//            builder = new StringBuilder("[");
//            for (int j = i; j < i + N; j++)
//                builder.append(String.format("%2d", values[j] ? 1 : 0)).append(",");
//            builder.deleteCharAt(builder.length() - 1);
//            builder.append("]");
//            System.out.println(builder.toString());
//        }
//        System.out.println("-----");
//    }
}