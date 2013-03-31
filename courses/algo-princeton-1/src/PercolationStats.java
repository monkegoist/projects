public class PercolationStats {

    private final int T;

    private final double mean;
    private final double stddev;

    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0)
            throw new IllegalArgumentException();
        this.T = T;
        double[] results = new double[T];
        // simulation
        for (int i = 0; i < T; i++) {
            Percolation object = new Percolation(N);
            while (!object.percolates()) {
                int x = random(N);
                int y = random(N);
                while (object.isOpen(x, y)) {
                    x = random(N);
                    y = random(N);
                }
                object.open(x, y);
            }
            int count = 0;
            for (int j = 1; j < N + 1; j++) {
                for (int k = 1; k < N + 1; k++) {
                    if (object.isOpen(j, k))
                        count++;
                }
            }
            results[i] = (double) count / (N * N);
        }
        this.mean = StdStats.mean(results);
        this.stddev = StdStats.stddev(results);
    }

    public double mean() {
        // sample mean of percolation threshold
        return mean;
    }

    public double stddev() {
        // sample standard deviation of percolation threshold
        return stddev;
    }

    public double confidenceLo() {
        // returns lower bound of the 95% confidence interval
        return mean() - (1.96 * stddev() / Math.sqrt(T));
    }

    public double confidenceHi() {
        // returns upper bound of the 95% confidence interval
        return mean() + (1.96 * stddev() / Math.sqrt(T));
    }

    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);
        PercolationStats stats = new PercolationStats(N, T);
        System.out.println("mean                    = " + stats.mean());
        System.out.println("stddev                  = " + stats.stddev());
        System.out.println("95% confidence interval = " + stats.confidenceLo() + ", " + stats.confidenceHi());
    }

    private static int random(int N) {
        return StdRandom.uniform(N) + 1;
    }
}