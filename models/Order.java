package models;

public class Order {
    private String orderId;
    private double weight;
    private Location location;
    private String assignedDroneId;

    public Order(String orderId, double weight, Location location) {
        this.orderId = orderId;
        this.weight = weight;
        this.location = location;
    }

    public String getOrderId() { return orderId; }
    public double getWeight() { return weight; }
    public Location getLocation() { return location; }
    public String getAssignedDroneId() { return assignedDroneId; }
    public void setAssignedDroneId(String id) { this.assignedDroneId = id; }

    @Override
    public String toString() {
        return orderId + "," + weight + "," + location + "," + (assignedDroneId == null ? "" : assignedDroneId);
    }

    public static Order fromString(String line) {
        String[] p = line.split(",");
        Order o = new Order(p[0], Double.parseDouble(p[1]),
                Location.fromString("(" + p[2] + "," + p[3] + ")"));
        if (p.length > 4 && !p[4].isBlank()) o.setAssignedDroneId(p[4]);
        return o;
    }

    public String display() {
        return String.format("Order %s | %.1fkg | Loc: %s | Drone: %s",
                orderId, weight, location, assignedDroneId == null ? "Unassigned" : assignedDroneId);
    }
}
