package service;

import data.dataStorage;
import model.Employee;
import model.Model;
import java.time.LocalDate;
import java.util.Scanner;

public class EditService {
    private dataStorage data;

    public EditService(dataStorage data) {
        this.data = data;
    }

    // ==========================================
    //       SUB-MENU HANDLERS (Interactions)
    // ==========================================

    public void handleEditProfile(Scanner sc) {
        System.out.println("\n--- Edit Profile ---");
        System.out.print("Target ID: "); String tId = sc.nextLine();
        System.out.println("1.Name 2.Pass");
        if (sc.nextLine().equals("1")) {
            System.out.print("New Name: "); updateName(tId, sc.nextLine());
        } else {
            System.out.print("New Pass: "); updatePassword(tId, sc.nextLine());
        }
    }

    public void handleEditStock(Scanner sc) {
        System.out.println("\n--- Edit Stock ---");
        System.out.print("Model: "); String m = sc.nextLine();
        System.out.print("Outlet: "); String o = sc.nextLine();
        int current = getCurrentStock(m, o);

        if (current == -1) {
            System.out.println("Error: Model/Outlet not found.");
        } else {
            System.out.println("Current Stock: " + current);
            System.out.print("New Qty: ");
            try { updateStock(m, o, Integer.parseInt(sc.nextLine())); }
            catch (Exception e) { System.out.println("Invalid number"); }
        }
    }

    public void handleRecordSale(Scanner sc, Employee currentUser) {
        System.out.println("\n--- Record New Sale ---");
        System.out.print("Sale ID (e.g. S01): "); String sId = sc.nextLine();
        System.out.print("Customer Name: "); String cName = sc.nextLine();
        System.out.print("Model: "); String mName = sc.nextLine();

        String outletCode = currentUser.getId().substring(0, 3);
        int currentStock = getCurrentStock(mName, outletCode);
        double price = getModelPrice(mName); // Get price for total calc

        if (currentStock == -1) {
            System.out.println("Error: Model not found at your outlet.");
        } else {
            System.out.println("Available: " + currentStock);
            System.out.print("Quantity: ");
            try {
                int qty = Integer.parseInt(sc.nextLine());
                if (qty > currentStock) {
                    System.out.println("Error: Not enough stock.");
                } else {
                    System.out.print("Payment Type (Cash/Card/QR): ");
                    String pType = sc.nextLine();

                    // Calculate Total automatically
                    double total = price * qty;
                    System.out.println("Total Price: RM" + total);

                    // Add Sale (Now includes Total Price)
                    data.addSale(new Transaction(sId, cName, mName, qty, LocalDate.now().toString(), outletCode, pType, total));

                    // Deduct Stock
                    updateStock(mName, outletCode, currentStock - qty);
                    System.out.println("Sale Recorded.");
                }
            } catch (Exception e) { System.out.println("Invalid number."); }
        }
    }

    public void handleEditSaleInfo(Scanner sc) {
        System.out.println("\n=== Edit Sales Information ===");
        System.out.print("Enter Sale ID: ");
        String targetID = sc.nextLine();
        Transaction t = getSale(targetID);

        if (t != null) {
            System.out.println("\nSales Record Found:");
            System.out.println("Model: " + t.getModelName() + "  Quantity: " + t.getQuantity());
            System.out.println("Total: RM" + t.getTotalPrice());
            System.out.println("Transaction Method: " + t.getPaymentType());
            System.out.println("\nSelect number to edit:");
            System.out.println("1. Name    2. Model    3. Quantity    4. Total");
            System.out.println("5. Transaction Method");
            System.out.print("> ");

            String choice = sc.nextLine();

            if (choice.equals("1")) {
                System.out.print("Enter New Name: "); updateSaleCustomer(targetID, sc.nextLine());
            } else if (choice.equals("2")) {
                System.out.print("Enter New Model: "); updateSaleModel(targetID, sc.nextLine());
            } else if (choice.equals("3")) {
                System.out.print("Enter New Quantity: ");
                try { updateSaleQuantity(targetID, Integer.parseInt(sc.nextLine())); }
                catch(Exception e) { System.out.println("Invalid number"); }
            } else if (choice.equals("4")) {
                System.out.print("Enter New Total: ");
                try { updateSaleTotal(targetID, Double.parseDouble(sc.nextLine())); }
                catch(Exception e) { System.out.println("Invalid number"); }
            } else if (choice.equals("5")) {
                System.out.print("Enter New Method: "); updateSalePaymentType(targetID, sc.nextLine());
            }
        } else {
            System.out.println("Sale ID not found.");
        }
    }

    // ==========================================
    //       BACKEND LOGIC
    // ==========================================

    public void updateName(String id, String newName) {
        for (Employee emp : data.getEmployees()) {
            if (emp.getId().equalsIgnoreCase(id)) {
                emp.setName(newName);
                data.saveEmployees();
                System.out.println("Name fixed.");
                return;
            }
        }
        System.out.println("User ID not found.");
    }

    public void updatePassword(String id, String newPass) {
        for (Employee emp : data.getEmployees()) {
            if (emp.getId().equalsIgnoreCase(id)) {
                emp.setPassword(newPass);
                data.saveEmployees();
                System.out.println("Password changed.");
                return;
            }
        }
        System.out.println("User ID not found.");
    }

    public int getCurrentStock(String modelName, String outletCode) {
        for (Model m : data.getModels()) {
            if (m.getModelName().equalsIgnoreCase(modelName)) {
                return m.getQuantity(outletCode.toUpperCase());
            }
        }
        return -1;
    }

    // Helper to get price for calculations
    public double getModelPrice(String modelName) {
        for (Model m : data.getModels()) {
            if (m.getModelName().equalsIgnoreCase(modelName)) {
                return m.getPrice();
            }
        }
        return 0.0;
    }

    public void updateStock(String modelName, String outletCode, int newQty) {
        for (Model m : data.getModels()) {
            if (m.getModelName().equalsIgnoreCase(modelName)) {
                m.setQuantity(outletCode.toUpperCase(), newQty);
                data.saveStock();
                System.out.println("Stock updated.");
                return;
            }
        }
    }

    public Transaction getSale(String saleID) {
        for (Transaction t : data.getSales()) {
            if (t.getSaleID().equalsIgnoreCase(saleID)) return t;
        }
        return null;
    }

    // --- SALES UPDATE METHODS ---
    public void updateSaleCustomer(String saleID, String newName) {
        Transaction t = getSale(saleID);
        if (t != null) { t.setCustomerName(newName); data.saveSales(); System.out.println("Sales information updated successfully."); }
    }

    public void updateSaleModel(String saleID, String newModel) {
        Transaction t = getSale(saleID);
        if (t != null) {
            // Note: In a real app, you would adjust stock here (refund old model, deduct new).
            // For simplicity, we are just updating the record string as requested.
            t.setModelName(newModel);
            data.saveSales();
            System.out.println("Sales information updated successfully.");
        }
    }

    public void updateSaleQuantity(String saleID, int newQty) {
        Transaction t = getSale(saleID);
        if (t != null) { t.setQuantity(newQty); data.saveSales(); System.out.println("Sales information updated successfully."); }
    }

    public void updateSaleTotal(String saleID, double newTotal) {
        Transaction t = getSale(saleID);
        if (t != null) { t.setTotalPrice(newTotal); data.saveSales(); System.out.println("Sales information updated successfully."); }
    }

    public void updateSalePaymentType(String saleID, String newType) {
        Transaction t = getSale(saleID);
        if (t != null) { t.setPaymentType(newType); data.saveSales(); System.out.println("Sales information updated successfully."); }
    }

    // ==========================================
    //    UPDATED TRANSACTION CLASS
    // ==========================================
    public static class Transaction {
        private String saleID, customerName, modelName, date, outletCode, paymentType;
        private int quantity;
        private double totalPrice; // <--- NEW FIELD

        public Transaction(String saleID, String customerName, String modelName, int quantity, String date, String outletCode, String paymentType, double totalPrice) {
            this.saleID = saleID;
            this.customerName = customerName;
            this.modelName = modelName;
            this.quantity = quantity;
            this.date = date;
            this.outletCode = outletCode;
            this.paymentType = paymentType;
            this.totalPrice = totalPrice;
        }

        // Getters
        public String getSaleID() { return saleID; }
        public String getCustomerName() { return customerName; }
        public String getModelName() { return modelName; }
        public int getQuantity() { return quantity; }
        public String getDate() { return date; }
        public String getOutletCode() { return outletCode; }
        public String getPaymentType() { return paymentType; }
        public double getTotalPrice() { return totalPrice; } // <--- New Getter

        // Setters
        public void setCustomerName(String n) { this.customerName = n; }
        public void setModelName(String m) { this.modelName = m; } // <--- New Setter
        public void setQuantity(int q) { this.quantity = q; }
        public void setPaymentType(String p) { this.paymentType = p; }
        public void setTotalPrice(double t) { this.totalPrice = t; } // <--- New Setter
    }
}