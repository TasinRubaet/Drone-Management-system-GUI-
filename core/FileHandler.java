package core;

import models.*;
import java.io.*;
import java.util.*;

public class FileHandler {
    public static List<DroneStatus> loadDrones(String path) {
        List<DroneStatus> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null)
                if (!line.isBlank()) list.add(DroneStatus.fromString(line));
        } catch (IOException ignored) {}
        return list;
    }

    public static void saveDrones(String path, List<DroneStatus> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (DroneStatus d : list) {
                bw.write(d.toString());
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static List<Order> loadOrders(String path) {
        List<Order> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null)
                if (!line.isBlank()) list.add(Order.fromString(line));
        } catch (IOException ignored) {}
        return list;
    }

    public static void saveOrders(String path, List<Order> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (Order o : list) {
                bw.write(o.toString());
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
