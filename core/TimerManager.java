package core;

import java.util.List;
import models.*;

public class TimerManager {
    private javax.swing.Timer timer; // explicitly Swing Timer
    private List<DroneStatus> drones;
    private List<Order> orders;
    private Runnable refreshCallback;

    public TimerManager(List<DroneStatus> drones, List<Order> orders, Runnable refreshCallback) {
        this.drones = drones;
        this.orders = orders;
        this.refreshCallback = refreshCallback;

        // Update every 2 seconds
        timer = new javax.swing.Timer(2000, e -> update());
        timer.start();
    }

    private void update() {
        for (DroneStatus d : drones) {
            // Charging logic
            if (d.isCharging()) {
                d.setBattery(Math.min(100, d.getBattery() + 5));
                if (d.getBattery() >= 100) {
                    d.setCharging(false);
                }
            }
        }

        for (Order o : orders) {
            // Delivery countdown
            if (o.isOngoing()) {
                o.setTimer(o.getRemainingTime() - 2);
                if (o.getRemainingTime() <= 0) {
                    o.setOngoing(false);
                    o.setCompleted(true);
                }
            }
        }

        refreshCallback.run(); // refresh GUI
    }
}
