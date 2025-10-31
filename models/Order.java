package models;

public class Order {
    private String orderId;
    private double weight;
    private Location location;
    private String assignedDroneId;
    private boolean completed;
    private boolean ongoing;
    private int remainingTime; // seconds

    public Order(String orderId, double weight, Location location) {
        this.orderId = orderId;
        this.weight = weight;
        this.location = location;
        this.completed = false;
        this.ongoing = false;
        this.remainingTime = 0;
    }

    public String getOrderId() { return orderId; }
    public double getWeight() { return weight; }
    public Location getLocation() { return location; }
    public String getAssignedDroneId() { return assignedDroneId; }
    public void setAssignedDroneId(String id) { this.assignedDroneId = id; }
    public boolean isCompleted() { return completed; }
    public boolean isOngoing() { return ongoing; }
    public int getRemainingTime() { return remainingTime; }

    public void setCompleted(boolean c) { this.completed = c; }
    public void setOngoing(boolean o) { this.ongoing = o; }
    public void setTimer(int t) { this.remainingTime = t; }
    public void reduceTimer(int sec) { this.remainingTime -= sec; }

    @Override
    public String toString() {
        return orderId + "," + weight + "," + location + "," +
                (assignedDroneId == null ? "" : assignedDroneId) + "," +
                completed + "," + ongoing + "," + remainingTime;
    }

    public static Order fromString(String line) {
        String[] p = line.split(",");
        Order o = new Order(p[0], Double.parseDouble(p[1]), Location.fromString("(" + p[2] + "," + p[3] + ")"));
        if (p.length > 4 && !p[4].isBlank()) o.setAssignedDroneId(p[4]);
        if (p.length > 5) o.setCompleted(Boolean.parseBoolean(p[5]));
        if (p.length > 6) o.setOngoing(Boolean.parseBoolean(p[6]));
        if (p.length > 7) o.setTimer(Integer.parseInt(p[7]));
        return o;
    }

    public String display() {
        if (completed)
            return String.format("✅ %s | Done", orderId);
        if (ongoing)
            return String.format("🚚 %s | ETA: %ds | Drone: %s", orderId, remainingTime, assignedDroneId);
        return String.format("Order %s | %.1fkg | Loc: %s | Drone: %s",
                orderId, weight, location, assignedDroneId == null ? "Unassigned" : assignedDroneId);
    }
}
