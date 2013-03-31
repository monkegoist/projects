import java.util.Comparator;

/**
 * Immutable class representing plot point.
 */
public class Point implements Comparable<Point> {

    // compare points by slope
    public final Comparator<Point> SLOPE_ORDER = new Comparator<Point>() {
        @Override
        public int compare(Point left, Point right) {
            return Double.compare(slopeTo(left), slopeTo(right));
        }
    };

    private final int x;                              // x coordinate
    private final int y;                              // y coordinate

    // create the point (x, y)
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // plot this point to standard drawing
    public void draw() {
        StdDraw.point(x, y);
    }

    // draw line between this point and that point to standard drawing
    public void drawTo(Point that) {
        if (that == null)
            throw new NullPointerException();
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    // slope between this point and that point
    public double slopeTo(Point that) {
        if (that == null)
            throw new NullPointerException();
        if (this.compareTo(that) == 0)
            return Double.NEGATIVE_INFINITY;
        int diffY = that.y - this.y;
        int diffX = that.x - this.x;
        if (diffY == 0)
            return 0;
        if (diffX == 0)
            return Double.POSITIVE_INFINITY;
        return (double) diffY / diffX;
    }

    // is this point lexicographically smaller than that one?
    // comparing y-coordinates and breaking ties by x-coordinates
    @Override
    public int compareTo(Point that) {
        if (that == null)
            throw new NullPointerException();
        if (this.y - that.y != 0)
            return this.y - that.y;
        else
            return this.x - that.x;
    }

    // return string representation of this point
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}