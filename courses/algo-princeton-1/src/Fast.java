import java.util.*;

public class Fast {
    public static void main(String[] args) {
        In input = new In(args[0]);
        int n = input.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++)
            points[i] = new Point(input.readInt(), input.readInt());
        initializeDraw();
        fastSearch(points);
    }

    private static void fastSearch(Point[] points) {
        // solutions
        Set<String> solutions = new LinkedHashSet<String>();
        // initial sort
        Arrays.sort(points);
        if (points.length < 4)
            return;
        // draw all points
        for (Point point : points) point.draw();
        List<Point> collinear = new ArrayList<Point>();
        for (int i = 0; i < points.length - 3; i++) {
            Point currentPoint = points[i];
            // points to consider
            Point[] arr = Arrays.copyOfRange(points, i + 1, points.length);
            Arrays.sort(arr, currentPoint.SLOPE_ORDER);
            // consideration
            int j = 0;
            Point nextPoint = arr[j];
            Double slope = currentPoint.slopeTo(nextPoint);
            collinear.add(currentPoint);
            collinear.add(nextPoint);
            while (j + 1 < arr.length) {
                nextPoint = arr[j + 1];
                Double nextSlope = currentPoint.slopeTo(nextPoint);
                if (nextSlope.equals(slope)) {
                    collinear.add(nextPoint);
                } else {
                    checkCollinear(collinear, solutions);
                    collinear.clear();
                    collinear.add(currentPoint);
                    collinear.add(nextPoint);
                    slope = nextSlope;
                }
                j++;
            }
            checkCollinear(collinear, solutions);
            collinear.clear();
        }
    }

    private static void checkCollinear(List<Point> points, Set<String> solutions) {
        if (points.size() >= 4) {
            StringBuilder builder = new StringBuilder();
            for (Point p : points)
                builder.append(p).append(" -> ");
            String candidate = builder.substring(0, builder.length() - 4);
            boolean isNew = true;
            for (String solution : solutions) {
                if (solution.contains(candidate))
                    isNew = false;
            }
            if (isNew) {
                System.out.println(candidate);
                solutions.add(candidate);
                // draw
                points.get(0).drawTo(points.get(points.size() - 1));
            }
        }
    }

    private static void initializeDraw() {
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
    }
}