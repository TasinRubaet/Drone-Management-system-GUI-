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
    private DefaultListModel<String> chargingModel = new DefaultListModel<>();

    private java.util.List<DroneStatus> drones;
    private java.util.List<Order> orders;
    private FleetManager fleet;
    private DroneAI ai;
    private TimerManager timer;

    private final String DRONE_FILE = "drones.txt";
    private final String ORDER_FILE = "orders.txt";

    private JList<String> droneList;
    private JList<String> orderList;
    private JList<String> chargingList;

    public MainWindow() {
        setTitle("🚁 Drone Management System");
        setSize(850, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load data
        fleet = new FleetManager();
        drones = FileHandler.loadDrones(DRONE_FILE);
        orders = FileHandler.loadOrders(ORDER_FILE);
        for (DroneStatus d : drones) fleet.addDrone(d);
        ai = new DroneAI(fleet);

        // GUI Tabs
        JTabbedPane tabs = new JTabbedPane();

        // 🟦 Drones TAB
        JPanel dronePanel = new JPanel(new BorderLayout());
        droneList = new JList<>(droneModel);
        dronePanel.add(new JScrollPane(droneList), BorderLayout.CENTER);

        JPanel droneButtons = new JPanel();
        JButton addDroneBtn = new JButton("➕ Add Drone");
        JButton editDroneBtn = new JButton("✏️ Edit Drone");
        JButton removeDroneBtn = new JButton("🗑 Remove Drone");
        JButton chargeDroneBtn = new JButton("🔋 Charge Drone");
        droneButtons.add(addDroneBtn);
        droneButtons.add(editDroneBtn);
        droneButtons.add(removeDroneBtn);
        droneButtons.add(chargeDroneBtn);
        dronePanel.add(droneButtons, BorderLayout.SOUTH);
        tabs.add("Drones", dronePanel);

        // 🟧 Orders TAB
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderList = new JList<>(orderModel);
        orderPanel.add(new JScrollPane(orderList), BorderLayout.CENTER);

        JPanel orderButtons = new JPanel();
        JButton addOrderBtn = new JButton("➕ Add Order");
        JButton assignOrderBtn = new JButton("🚀 Auto Assign");
        JButton completeOrderBtn = new JButton("✅ Complete Order");
        orderButtons.add(addOrderBtn);
        orderButtons.add(assignOrderBtn);
        orderButtons.add(completeOrderBtn);
        orderPanel.add(orderButtons, BorderLayout.SOUTH);
        tabs.add("Orders", orderPanel);

        // 🟩 Charging TAB
        JPanel chargePanel = new JPanel(new BorderLayout());
        chargingList = new JList<>(chargingModel);
        chargePanel.add(new JScrollPane(chargingList), BorderLayout.CENTER);
        JLabel info = new JLabel("Charging drones update every 2 seconds.");
        info.setHorizontalAlignment(SwingConstants.CENTER);
        chargePanel.add(info, BorderLayout.SOUTH);
        tabs.add("Charging Drones", chargePanel);

        add(tabs, BorderLayout.CENTER);

        // Add event listeners
        addDroneBtn.addActionListener(e -> addDrone());
        editDroneBtn.addActionListener(e -> editDrone());
        removeDroneBtn.addActionListener(e -> removeDrone());
        chargeDroneBtn.addActionListener(e -> chargeDrone());
        addOrderBtn.addActionListener(e -> addOrder());
        assignOrderBtn.addActionListener(e -> assignOrders());
        completeOrderBtn.addActionListener(e -> completeOrder());

        // Timer background updates
        timer = new TimerManager(drones, orders, this::refreshLists);

        // Initial refresh
        refreshLists();

        // Auto-save on exit
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                FileHandler.saveDrones(DRONE_FILE, drones);
                FileHandler.saveOrders(ORDER_FILE, orders);
            }
        });
    }

    // ➕ Add a drone
    private void addDrone() {
        JTextField id = new JTextField();
        JTextField battery = new JTextField("100");
        JTextField weight = new JTextField("10");
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

    // ✏️ Edit drone details
    private void editDrone() {
        int index = droneList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Select a drone to edit!");
            return;
        }
        DroneStatus d = drones.get(index);
        JTextField battery = new JTextField(String.valueOf(d.getBattery()));
        JTextField weight = new JTextField(String.valueOf(d.getMaxWeight()));
        JCheckBox maint = new JCheckBox("In Maintenance", d.isInMaintenance());

        Object[] fields = {"Battery (%):", battery, "Max Weight (kg):", weight, maint};
        int opt = JOptionPane.showConfirmDialog(this, fields, "Edit Drone " + d.getDroneId(), JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            d.setBattery(Double.parseDouble(battery.getText()));
            d.setInMaintenance(maint.isSelected());
            refreshLists();
        }
    }

    // 🗑 Remove drone
    private void removeDrone() {
        int index = droneList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Select a drone to remove!");
            return;
        }
        drones.remove(index);
        refreshLists();
    }

    // 🔋 Send drone to charging
    private void chargeDrone() {
        int index = droneList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Select a drone to charge!");
            return;
        }
        DroneStatus d = drones.get(index);
        if (d.getBattery() >= 100) {
            JOptionPane.showMessageDialog(this, "Drone already fully charged!");
            return;
        }
        d.setCharging(true);
        refreshLists();
    }

    // ➕ Add order
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

    // 🚀 Assign drones using AI
    private void assignOrders() {
        ai.assignOrders(orders);
        for (Order o : orders) {
            if (o.getAssignedDroneId() != null && !o.isCompleted()) {
                o.setOngoing(true);
                o.setTimer(20 + (int) (Math.random() * 20)); // 20–40 seconds
            }
        }
        refreshLists();
        JOptionPane.showMessageDialog(this, "Drone AI assignment complete!");
    }

    // ✅ Mark order complete manually
    private void completeOrder() {
        int index = orderList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Select an order to complete!");
            return;
        }
        Order o = orders.get(index);
        o.setCompleted(true);
        o.setOngoing(false);
        refreshLists();
    }

    // 🔁 Refresh all display lists
    private void refreshLists() {
        droneModel.clear();
        chargingModel.clear();
        for (DroneStatus d : drones) {
            if (d.getBattery() < 20 && !d.isCharging()) d.setCharging(true);
            if (d.isCharging()) chargingModel.addElement(d.display());
            else droneModel.addElement(d.display());
        }

        orderModel.clear();
        for (Order o : orders)
            orderModel.addElement(o.display());
    }
}
