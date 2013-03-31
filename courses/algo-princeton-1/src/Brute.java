import java.util.Arrays;

public class Brute {
    public static void main(String[] args) {
        In input = new In(args[0]);
        int n = input.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++)
            points[i] = new Point(input.readInt(), input.readInt());
        initializeDraw();
        bruteSearch(points);
    }

    /**
     * Write a program Brute.java that examines 4 points at a time and checks whether they all lie on the same line
     * segment, printing out any such line segments to standard output and drawing them using standard drawing.
     * To check whether the 4 points p, q, r, and s are collinear, check whether the slopes between p and q,
     * between p and r, and between p and s are all equal.
     */
    private static void bruteSearch(Point[] points) {
        Arrays.sort(points);
        if (points.length < 4)
            return;
        // draw all points
        for (Point point : points) point.draw();
        for (int i = 0; i < points.length - 3; i++) {
            for (int j = i + 1; j < points.length - 2; j++) {
                for (int k = j + 1; k < points.length - 1; k++) {
                    for (int m = k + 1; m < points.length; m++) {
                        Double toJ = points[i].slopeTo(points[j]);
                        Double toK = points[i].slopeTo(points[k]);
                        Double toM = points[i].slopeTo(points[m]);
                        if (toJ.equals(toK) && toJ.equals(toM)) {
                            System.out.println(points[i] + " -> " + points[j] + " -> " + points[k] + " -> " + points[m]);
                            points[i].drawTo(points[m]);
                        }
                    }
                }
            }
        }
    }

    private static void initializeDraw() {
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
    }
}