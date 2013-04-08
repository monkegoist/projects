import java.util.concurrent.CountDownLatch;

public class PingPong {

    public static void main(String[] args) {
        Object lock = new Object();
        CountDownLatch latch = new CountDownLatch(2);
        Thread t1 = new Thread(new MyRunnable("Ping!", lock, latch));
        Thread t2 = new Thread(new MyRunnable("Pong!", lock, latch));
        System.out.println("Ready… Set… Go!");
        t1.start();
        t2.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Done!");
    }

    private static class MyRunnable implements Runnable {

        private final String message;
        private final Object lock;
        private final CountDownLatch latch;

        private MyRunnable(String message, Object lock, CountDownLatch latch) {
            this.message = message;
            this.lock = lock;
            this.latch = latch;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                synchronized (lock) {
                    lock.notifyAll();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    lock.notifyAll();
                    System.out.println(message);
                }
            }
            latch.countDown();
        }
    }
}
