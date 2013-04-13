package qual;

import java.util.*;

public class FairAndSquare {

    private static final NavigableSet<Integer> palindromes = new TreeSet<Integer>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        initialize();
        for (int i = 1; i < num + 1; i++) {
            long a = scanner.nextLong();
            long b = scanner.nextLong();
            System.out.println("Case #" + i + ": " + count(a, b));
        }
//        System.out.println(palindromes);
    }

    private static int count(long a, long b) {
        int count = 0;
        for (int i : palindromes.subSet((int) Math.sqrt(a), true, (int) Math.sqrt(b), true)) {
            long num = i * i;
            if (isPalindrome(num) && num >= a && num <= b)
                count++;
        }
        return count;
    }

    private static boolean isPalindrome(long num) {
        if (num < 10)
            return true;
        String string = String.valueOf(num);
        int length = string.length();
        for (int i = 0; i < length / 2; i++) {
            if (string.charAt(i) != string.charAt(length - i - 1))
                return false;
        }
        return true;
    }

    private static void initialize() {
        // we shall aim for first large dataset
        for (int i = 1; i < 10; i++)
            palindromes.add(i);
        for (int i = 11; i < 100; i+=11)
            palindromes.add(i);
        for (int i = 1; i < 6; i++) { // 9,999,999 -- 5 digits in between
            int start = (int) Math.pow(10, i - 1);
            int end = start * 10;
            int multiplier = (int) Math.pow(10, i + 1);
            Set<Integer> copy = new TreeSet<Integer>(palindromes.subSet(start, end));
            for (int num : copy) {
                for (int j = 1; j < 10; j++)
                    palindromes.add(j * multiplier + num * 10 + j);
            }
            // special case -- 0
            for (int j = 1; j < 10; j++)
                palindromes.add(j * multiplier + j);
        }
    }
}
