package qual;

import java.util.Scanner;

public class TicTacToeTomek {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        char[][] board = new char[4][4];
        for (int g = 1; g < n + 1; g++) {
            boolean hasFreeSpots = false;
            for (int i = 0; i < 4; i++) {
                String s = scanner.next();
                for (int j = 0; j < 4; j++) {
                    board[i][j] = s.charAt(j);
                    if (!hasFreeSpots && board[i][j] == '.')
                        hasFreeSpots = true;
                }
            }
            scanner.nextLine(); // skip empty line
            Outcome outcome = checkHorizontal(board);
            if (outcome == Outcome.C)
                outcome = checkVertical(board);
            if (outcome == Outcome.C)
                outcome = checkDiagonal(board);
            if (outcome == Outcome.C) {
                if (hasFreeSpots)
                    outcome = Outcome.N;
                else
                    outcome = Outcome.D;
            }
            System.out.println("Case #" + g + ": " + outcome.message);
        }
    }

    private static Outcome checkHorizontal(char[][] board) {
        for (int i = 0; i < 4; i++) {
            char c = board[i][0];
            if (c != '.') {
                boolean success = true;
                int startIndex = 1;
                if (c == 'T') {
                    c = board[i][1];
                    startIndex++;
                }
                for (int j = startIndex; j < 4; j++) {
                    if (board[i][j] != c && board[i][j] != 'T') {
                        success = false;
                        break;
                    }
                }
                if (success)
                    return (c == 'X') ? Outcome.X : Outcome.O;
            }
        }
        return Outcome.C;
    }

    private static Outcome checkVertical(char[][] board) {
        for (int j = 0; j < 4; j++) {
            char c = board[0][j];
            if (c != '.') {
                boolean success = true;
                int startIndex = 1;
                if (c == 'T') {
                    c = board[1][j];
                    startIndex++;
                }
                for (int i = startIndex; i < 4; i++) {
                    if (board[i][j] != c && board[i][j] != 'T') {
                        success = false;
                        break;
                    }
                }
                if (success)
                    return (c == 'X') ? Outcome.X : Outcome.O;
            }
        }
        return Outcome.C;
    }

    private static Outcome checkDiagonal(char[][] board) {
        // first diagonal
        char c = board[0][0];
        if (c != '.') {
            boolean success = true;
            int startIndex = 1;
            if (c == 'T') {
                c = board[1][1];
                startIndex++;
            }
            for (int i = startIndex; i < 4; i++) {
                if (board[i][i] != c && board[i][i] != 'T') {
                    success = false;
                    break;
                }
            }
            if (success)
                return (c == 'X') ? Outcome.X : Outcome.O;
        }
        // second diagonal
        c = board[3][0];
        if (c != '.') {
            boolean success = true;
            int startIndex = 2;
            if (c == 'T') {
                c = board[2][1];
                startIndex--;
            }
            for (int i = startIndex; i >= 0; i--) {
                if (board[i][3 - i] != c && board[i][3 - i] != 'T') {
                    success = false;
                    break;
                }
            }
            if (success)
                return (c == 'X') ? Outcome.X : Outcome.O;
        }
        return Outcome.C;
    }

    private enum Outcome {
        X("X won"),
        O("O won"),
        N("Game has not completed"),
        D("Draw"),
        C("Continue");

        private final String message;

        Outcome(String message) {
            this.message = message;
        }
    }
}
