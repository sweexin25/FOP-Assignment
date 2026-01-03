package gui;

import data.dataStorage;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;
import service.*;

public class MainGUI extends JFrame {
    private dataStorage storage;
    private AuthService authService;
    private AttendanceService attendanceService;
    private StockServiceReplace stockService;
    private SalesServiceReplace salesService;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField idField;
    private JPasswordField passwordField;

    public MainGUI() {
        // 1. Initialize data and services
        storage = new dataStorage();
        storage.loadData(); // Load data from CSV files
        
        authService = new AuthService(storage);
        attendanceService = new AttendanceService(storage);
        stockService = new StockServiceReplace(storage, attendanceService); 
        salesService = new SalesServiceReplace(storage, attendanceService);

        // 2. Basic window settings
        setTitle("GoldenHour Management System (GUI Mode)");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize different interface screens
        initLoginScreen();
        initDashboard();
        initAttendanceScreen();
        initSalesScreen(); // Fully GUI-based sales interface

        add(mainPanel);
        setVisible(true);
    }

    // --- [1. Login Page] ---
    private void initLoginScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        idField = new JTextField(15);
        passwordField = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("GoldenHour Store Login");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(new JLabel("Staff ID:"), gbc);
        gbc.gridx = 1; panel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; panel.add(passwordField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {
            if (authService.login(idField.getText(), new String(passwordField.getPassword()), storage.getEmployees())) {
                cardLayout.show(mainPanel, "Dashboard");
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: Invalid Credentials");
            }
        });
        mainPanel.add(panel, "Login");
    }

    // --- [2. Main Menu Dashboard] ---
    private void initDashboard() {
        JPanel dashboard = new JPanel(new GridLayout(5, 1, 15, 15));
        dashboard.setBorder(BorderFactory.createEmptyBorder(50, 220, 50, 220));

        JButton btnAtt = new JButton("1. Attendance & Clocking");
        JButton btnStock = new JButton("2. Current Stock Status");
        JButton btnSales = new JButton("3. Sales Recording");
        JButton btnLogout = new JButton("4. Logout");

        btnAtt.addActionListener(e -> cardLayout.show(mainPanel, "Attendance"));
        btnStock.addActionListener(e -> refreshStockTable()); 
        btnSales.addActionListener(e -> cardLayout.show(mainPanel, "Sales"));
        btnLogout.addActionListener(e -> {
            authService.logOut();
            cardLayout.show(mainPanel, "Login");
        });

        dashboard.add(new JLabel("Operation Menu", SwingConstants.CENTER));
        dashboard.add(btnAtt); dashboard.add(btnStock);
        dashboard.add(btnSales); dashboard.add(btnLogout);
        mainPanel.add(dashboard, "Dashboard");
    }

    // --- [3. Attendance Screen (Using GUI dialog inputs)] ---
    private void initAttendanceScreen() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 100));
        JButton btnIn = new JButton("Clock In");
        JButton btnOut = new JButton("Clock Out");
        JButton btnBack = new JButton("Back");

        btnIn.addActionListener(e -> {
            // Use GUI dialog instead of Console Scanner
            String code = JOptionPane.showInputDialog(this, "Enter Outlet Code (e.g., O001, O002):");
            if (code != null && !code.isEmpty()) {
                // Manually set the outletCode for the Service
                attendanceService.setOutletCode(code); 
                attendanceService.clockIn(authService.getCurrentUser());
                JOptionPane.showMessageDialog(this, "Clocked In successfully at " + code);
            }
        });

        btnOut.addActionListener(e -> {
            attendanceService.clockOut(authService.getCurrentUser());
            JOptionPane.showMessageDialog(this, "Clock Out Recorded.");
        });

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        panel.add(btnIn); panel.add(btnOut); panel.add(btnBack);
        mainPanel.add(panel, "Attendance");
    }

    // --- [4. Stock Page] ---
    private void refreshStockTable() {
        JPanel panel = new JPanel(new BorderLayout());
        String outlet = attendanceService.getOutletCode(); // Fix for potential null/empty outlet

        if (outlet == null || outlet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: You must Clock In first!");
            cardLayout.show(mainPanel, "Dashboard");
            return;
        }

        String[] cols = {"Watch Model", "Unit Price", "Stock Count"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (WatchModel wm : storage.getModels()) {
            model.addRow(new Object[]{wm.getName(), String.format("%.2f", wm.getPrice()), wm.getStock(outlet)});
        }

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnBack = new JButton("Back to Menu");
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        panel.add(btnBack, BorderLayout.SOUTH);

        mainPanel.add(panel, "Stock");
        cardLayout.show(mainPanel, "Stock");
    }

    // --- [5. Sales Page (Replaces Terminal logic)] ---
    private void initSalesScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField custField = new JTextField(15);
        JTextField modelField = new JTextField(15);
        JTextField qtyField = new JTextField(15);
        String[] methods = {"Cash", "Credit Card", "E-Wallet"};
        JComboBox<String> methodBox = new JComboBox<>(methods);
        
        JButton btnSubmit = new JButton("Submit Sales Record");
        JButton btnBack = new JButton("Back");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1; panel.add(custField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Watch Model:"), gbc);
        gbc.gridx = 1; panel.add(modelField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; panel.add(qtyField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1; panel.add(methodBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(btnSubmit, gbc);
        gbc.gridy = 5;
        panel.add(btnBack, gbc);

        // 找到 MainGUI.java 中的 btnSubmit.addActionListener
    btnSubmit.addActionListener(e -> {
        String outlet = attendanceService.getOutletCode();
        if (outlet == null || outlet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please Clock In first!");
            return;
        }

        String custName = custField.getText().trim();
        String modelName = modelField.getText().trim();
        String method = (String) methodBox.getSelectedItem();
        int qty;

        try { 
            qty = Integer.parseInt(qtyField.getText()); 
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }

        // 查找模型
        WatchModel found = null;
        for (WatchModel m : storage.getModels()) {
            if (m.getName().equalsIgnoreCase(modelName)) {
                found = m;
                break;
            }
        }

        if (found == null) {
            JOptionPane.showMessageDialog(this, "Model not found.");
        } else if (found.getStock(outlet) < qty) {
            JOptionPane.showMessageDialog(this, "Insufficient stock!");
        } else {
            // 调用 Service 处理所有逻辑（包含写入 CSV 和生成收据）
            salesService.recordSaleGUI(authService.getCurrentUser(), custName, modelName, qty, method);
            
            double total = found.getPrice() * qty;
            JOptionPane.showMessageDialog(this, "Sale Recorded & Saved to CSV!\nTotal: RM " + total);
            
            // 清空字段
            custField.setText(""); modelField.setText(""); qtyField.setText("");
        }
    });

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        mainPanel.add(panel, "Sales");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}