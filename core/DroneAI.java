package core;

import models.*;
import java.util.*;

public class DroneAI {
    private FleetManager fleet;

    public DroneAI(FleetManager fleet) {
        this.fleet = fleet;
    }

    public void assignOrders(List<Order> orders) {
        for (Order order : orders) {
            if (order.getAssignedDroneId() != null) continue;

            DroneStatus best = null;
            double bestBattery = 0;

            for (DroneStatus d : fleet.availableDrones()) {
                if (d.getMaxWeight() < order.getWeight()) continue;
                double distance = d.getLocation().distanceTo(order.getLocation());
                double batteryNeeded = distance * 0.05 + order.getWeight() * 0.2;
                if (d.getBattery() > batteryNeeded && d.getBattery() > bestBattery) {
                    bestBattery = d.getBattery();
                    best = d;
                }
            }

            if (best != null) {
                order.setAssignedDroneId(best.getDroneId());
                double distance = best.getLocation().distanceTo(order.getLocation());
                double batteryUsed = distance * 0.05 + order.getWeight() * 0.2;
                best.setBattery(Math.max(0, best.getBattery() - batteryUsed));
                best.setLocation(order.getLocation());
            }
        }
    }
}
