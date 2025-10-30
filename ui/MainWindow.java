package ui;

import core.*;
import models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MainWindow extends JFrame {
    private DefaultListModel<String> droneModel = new DefaultListModel<>();
    private DefaultListModel<String> orderModel = new DefaultListModel<>();
    private java.util.List<DroneStatus> drones;
    private java.util.List<Order> orders;
    private FleetManager fleet;
    private DroneAI ai;

    private final String DRONE_FILE = "drones.txt";
    private final String ORDER_FILE = "orders.txt";

    public MainWindow() {
        setTitle("🚁 Drone Delivery System");
        setSize(750, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        fleet = new FleetManager();
        drones = FileHandler.loadDrones(DRONE_FILE);
        orders = FileHandler.loadOrders(ORDER_FILE);
        for (DroneStatus d : drones) fleet.addDrone(d);
        ai = new DroneAI(fleet);

        JTabbedPane tabs = new JTabbedPane();

        // Drones tab
        JPanel dronePanel = new JPanel(new BorderLayout());
        JList<String> droneList = new JList<>(droneModel);
        dronePanel.add(new JScrollPane(droneList), BorderLayout.CENTER);
        JButton addDroneBtn = new JButton("Add Drone");
        addDroneBtn.addActionListener(e -> addDrone());
        dronePanel.add(addDroneBtn, BorderLayout.SOUTH);
        tabs.add("Drones", dronePanel);

        // Orders tab
        JPanel orderPanel = new JPanel(new BorderLayout());
        JList<String> orderList = new JList<>(orderModel);
        orderPanel.add(new JScrollPane(orderList), BorderLayout.CENTER);

        JButton addOrderBtn = new JButton("Add Order");
        addOrderBtn.addActionListener(e -> addOrder());
        orderPanel.add(addOrderBtn, BorderLayout.SOUTH);
        tabs.add("Orders", orderPanel);

        // AI assign button
        JButton assignBtn = new JButton("🚀 Auto Assign Drones");
        assignBtn.addActionListener(e -> assignOrders());

        add(tabs, BorderLayout.CENTER);
        add(assignBtn, BorderLayout.SOUTH);

        refreshLists();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                FileHandler.saveDrones(DRONE_FILE, drones);
                FileHandler.saveOrders(ORDER_FILE, orders);
            }
        });
    }

    private void addDrone() {
        JTextField id = new JTextField();
        JTextField battery = new JTextField();
        JTextField weight = new JTextField();
        JCheckBox maint = new JCheckBox("In Maintenance");

        Object[] fields = {"Drone ID:", id, "Battery (%):", battery, "Max Weight (kg):", weight, maint};
        int opt = JOptionPane.showConfirmDialog(this, fields, "Add Drone", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            DroneStatus d = new DroneStatus(id.getText(),
                    Double.parseDouble(battery.getText()),
                    Double.parseDouble(weight.getText()),
                    maint.isSelected());
            drones.add(d);
            fleet.addDrone(d);
            refreshLists();
        }
    }

    private void addOrder() {
        JTextField id = new JTextField();
        JTextField weight = new JTextField();
        JTextField x = new JTextField();
        JTextField y = new JTextField();

        Object[] fields = {"Order ID:", id, "Weight (kg):", weight, "Location X:", x, "Location Y:", y};
        int opt = JOptionPane.showConfirmDialog(this, fields, "Add Order", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            Location loc = new Location(Double.parseDouble(x.getText()), Double.parseDouble(y.getText()));
            orders.add(new Order(id.getText(), Double.parseDouble(weight.getText()), loc));
            refreshLists();
        }
    }

    private void assignOrders() {
        ai.assignOrders(orders);
        refreshLists();
        JOptionPane.showMessageDialog(this, "Drone AI assignment complete!");
    }

    private void refreshLists() {
        droneModel.clear();
        for (DroneStatus d : drones) droneModel.addElement(d.display());
        orderModel.clear();
        for (Order o : orders) orderModel.addElement(o.display());
    }
}
