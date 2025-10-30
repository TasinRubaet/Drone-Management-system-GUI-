package core;

import models.DroneStatus;
import java.util.*;

public class FleetManager {
    private List<DroneStatus> drones = new ArrayList<>();
    public void addDrone(DroneStatus d) { drones.add(d); }
    public List<DroneStatus> getDrones() { return drones; }

    public DroneStatus findById(String id) {
        for (DroneStatus d : drones) if (d.getDroneId().equals(id)) return d;
        return null;
    }

    public List<DroneStatus> availableDrones() {
        List<DroneStatus> list = new ArrayList<>();
        for (DroneStatus d : drones)
            if (!d.isInMaintenance() && d.getBattery() > 20) list.add(d);
        return list;
    }
}
