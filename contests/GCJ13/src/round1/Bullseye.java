package round1;

import java.util.Scanner;

/**
 * Note: doesn't work well for large inputs.
 * Arithmetic progression sum function should be applied instead.
 */
public class BullsEye {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        for (int i = 0; i < num; i++) {
            long r = scanner.nextLong();
            long t = scanner.nextLong();
            long constant = 2 * r;
            long count = 0;
            long next = constant + 1;
            while (t >= next) {
                t = t - next;
                count++;
                next = next + 4;
            }
            System.out.println("Case #" + (i + 1) + ": " + count);
        }
    }
}