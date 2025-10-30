package models;

public class DroneStatus {
    private String droneId;
    private double battery;
    private double maxWeight;
    private boolean inMaintenance;
    private Location location;

    public DroneStatus(String droneId, double battery, double maxWeight, boolean inMaintenance) {
        this.droneId = droneId;
        this.battery = battery;
        this.maxWeight = maxWeight;
        this.inMaintenance = inMaintenance;
        this.location = new Location(0, 0);
    }

    public String getDroneId() { return droneId; }
    public double getBattery() { return battery; }
    public double getMaxWeight() { return maxWeight; }
    public boolean isInMaintenance() { return inMaintenance; }
    public Location getLocation() { return location; }
    public void setBattery(double b) { this.battery = b; }
    public void setInMaintenance(boolean m) { this.inMaintenance = m; }
    public void setLocation(Location l) { this.location = l; }

    @Override
    public String toString() {
        return droneId + "," + battery + "," + maxWeight + "," + inMaintenance + "," + location;
    }

    public static DroneStatus fromString(String line) {
        String[] p = line.split(",");
        return new DroneStatus(p[0], Double.parseDouble(p[1]),
                Double.parseDouble(p[2]), Boolean.parseBoolean(p[3]));
    }

    public String display() {
        return String.format("Drone %s | Battery: %.1f%% | Max: %.1fkg | Maint: %b",
                droneId, battery, maxWeight, inMaintenance);
    }
}
