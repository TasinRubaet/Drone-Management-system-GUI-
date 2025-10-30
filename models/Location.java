package models;

public class Location {
    private double x, y;
    public Location(double x, double y) { this.x = x; this.y = y; }
    public double distanceTo(Location other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx*dx + dy*dy);
    }
    @Override
    public String toString() { return "(" + x + ", " + y + ")"; }
    public static Location fromString(String s) {
        s = s.replace("(", "").replace(")", "");
        String[] p = s.split(",");
        return new Location(Double.parseDouble(p[0]), Double.parseDouble(p[1]));
    }
}
