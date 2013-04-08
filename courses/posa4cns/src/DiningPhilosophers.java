public class DiningPhilosophers {

    public static void main(String[] args) throws InterruptedException {
        Chopstick[] chopsticks = {
                new Chopstick(1), new Chopstick(2), new Chopstick(3), new Chopstick(4), new Chopstick(5)
        };

        Philosopher[] philosophers = {
                new Philosopher("Philosopher 1", chopsticks[0], chopsticks[4]),
                new Philosopher("Philosopher 2", chopsticks[1], chopsticks[0]),
                new Philosopher("Philosopher 3", chopsticks[2], chopsticks[1]),
                new Philosopher("Philosopher 4", chopsticks[3], chopsticks[2]),
                new Philosopher("Philosopher 5", chopsticks[4], chopsticks[3])
        };

        System.out.println("Dinner is starting!");

        Thread[] threads = new Thread[philosophers.length];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(philosophers[i]);
            threads[i].start();
        }
        for (Thread thread : threads) thread.join();

        System.out.println("Dinner is over!");
    }

    private static class Philosopher implements Runnable {

        private final String name;
        private final Chopstick left;
        private final Chopstick right;

        private Philosopher(String name, Chopstick left, Chopstick right) {
            this.name = name;
            this.left = left;
            this.right = right;
        }

        @Override
        public void run() {
            // to avoid deadlock we should take locks in order of chopsticks' ids
            final Chopstick first;
            final Chopstick second;
            if (left.id > right.id) {
                first = right;
                second = left;
            } else {
                first = left;
                second = right;
            }
            for (int i = 0; i < 5; i++) {
                synchronized (first) {
                    System.out.println(name + " picks up " + chopstick(first) + " chopstick.");
                    synchronized (second) {
                        System.out.println(name + " picks up " + chopstick(second) + " chopstick.");
                        System.out.println(name + " eats.");
                        System.out.println(name + " puts down " + chopstick(second) + " chopstick.");
                    }
                    System.out.println(name + " puts down " + chopstick(first) + " chopstick.");
                }
            }
        }

        private String chopstick(Chopstick chopstick) {
            return chopstick == left ? "left" : "right";
        }
    }

    private static class Chopstick {
        private final int id;

        private Chopstick(int id) {
            this.id = id;
        }
    }
}