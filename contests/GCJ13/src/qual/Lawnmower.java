package qual;

import java.util.Scanner;

public class Lawnmower {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        for (int g = 1; g < num + 1; g++) {
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            int[][] arr = new int[n][m];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < m; j++)
                    arr[i][j] = scanner.nextInt();
            System.out.println("Case #" + g + ": " + (check(arr, n, m) ? "YES" : "NO"));
        }
    }

    private static boolean check(int[][] arr, int n, int m) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (!checkElement(arr, i, j, n, m))
                    return false;
            }
        }
        return true;
    }

    private static boolean checkElement(int[][] arr, int i, int j, int n, int m) {
        int num = arr[i][j];
        boolean isOk = true;
        // check horizontal
        for (int k = 0; k < m; k++) {
            if (num < arr[i][k]) {
                isOk = false;
                break;
            }
        }
        if (isOk)
            return true;
        isOk = true;
        // check vertical
        for (int k = 0; k < n; k++) {
            if (num < arr[k][j]) {
                isOk = false;
                break;
            }
        }
        return isOk;
    }
}