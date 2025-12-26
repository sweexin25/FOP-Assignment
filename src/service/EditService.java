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

        // 1. Ask for Date (User Input) instead of auto-generating it
        System.out.print("Enter Transaction Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        System.out.print("Sale ID (e.g. S01): "); String sId = sc.nextLine();
        System.out.print("Customer Name: "); String cName = sc.nextLine();
        System.out.print("Model: "); String mName = sc.nextLine();

        String outletCode = currentUser.getId().substring(0, 3);
        int currentStock = getCurrentStock(mName, outletCode);
        double price = getModelPrice(mName);

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

                    // Add Sale using the USER INPUT DATE
                    data.addSale(new Transaction(sId, cName, mName, qty, date, outletCode, pType, total));

                    // Deduct Stock
                    updateStock(mName, outletCode, currentStock - qty);
                    System.out.println("Sale Recorded.");
                }
            } catch (Exception e) { System.out.println("Invalid number."); }
        }
    }

    public void handleEditSaleInfo(Scanner sc) {
        System.out.println("\n=== Edit Sales Information ===");

        // Search using Date and Name
        System.out.print("Enter Transaction Date (YYYY-MM-DD): ");
        String targetDate = sc.nextLine();

        System.out.print("Enter Customer Name: ");
        String targetName = sc.nextLine();

        Transaction t = getSale(targetDate, targetName);

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
                System.out.print("Enter New Name: ");
                updateSaleCustomer(targetDate, targetName, sc.nextLine());
            } else if (choice.equals("2")) {
                System.out.print("Enter New Model: ");
                updateSaleModel(targetDate, targetName, sc.nextLine());
            } else if (choice.equals("3")) {
                System.out.print("Enter New Quantity: ");
                try { updateSaleQuantity(targetDate, targetName, Integer.parseInt(sc.nextLine())); }
                catch(Exception e) { System.out.println("Invalid number"); }
            } else if (choice.equals("4")) {
                System.out.print("Enter New Total: ");
                try { updateSaleTotal(targetDate, targetName, Double.parseDouble(sc.nextLine())); }
                catch(Exception e) { System.out.println("Invalid number"); }
            } else if (choice.equals("5")) {
                System.out.print("Enter New Transaction Method: ");
                updateSalePaymentType(targetDate, targetName, sc.nextLine());
            }
        } else {
            System.out.println("Sales Record Not Found.");
        }
    }


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

    // Search by Date and Customer Name
    public Transaction getSale(String date, String customerName) {
        for (Transaction t : data.getSales()) {
            if (t.getDate().equalsIgnoreCase(date) && t.getCustomerName().equalsIgnoreCase(customerName)) {
                return t;
            }
        }
        return null;
    }

    public void updateSaleCustomer(String date, String currentName, String newName) {
        Transaction t = getSale(date, currentName);
        if (t != null) {
            t.setCustomerName(newName);
            data.saveSales();
            System.out.println("Sales information updated successfully.");
        }
    }

    public void updateSaleModel(String date, String customerName, String newModel) {
        Transaction t = getSale(date, customerName);
        if (t != null) {
            t.setModelName(newModel);
            data.saveSales();
            System.out.println("Sales information updated successfully.");
        }
    }

    public void updateSaleQuantity(String date, String customerName, int newQty) {
        Transaction t = getSale(date, customerName);
        if (t != null) {
            t.setQuantity(newQty);
            data.saveSales();
            System.out.println("Sales information updated successfully.");
        }
    }

    public void updateSaleTotal(String date, String customerName, double newTotal) {
        Transaction t = getSale(date, customerName);
        if (t != null) {
            t.setTotalPrice(newTotal);
            data.saveSales();
            System.out.println("Sales information updated successfully.");
        }
    }

    public void updateSalePaymentType(String date, String customerName, String newType) {
        Transaction t = getSale(date, customerName);
        if (t != null) {
            t.setPaymentType(newType);
            data.saveSales();
            System.out.println("Sales information updated successfully.");
        }
    }

    // ==========================================
    //    TRANSACTION CLASS
    // ==========================================
    public static class Transaction {
        private String saleID, customerName, modelName, date, outletCode, paymentType;
        private int quantity;
        private double totalPrice;

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

        public String getSaleID() { return saleID; }
        public String getCustomerName() { return customerName; }
        public String getModelName() { return modelName; }
        public int getQuantity() { return quantity; }
        public String getDate() { return date; }
        public String getOutletCode() { return outletCode; }
        public String getPaymentType() { return paymentType; }
        public double getTotalPrice() { return totalPrice; }

        public void setCustomerName(String n) { this.customerName = n; }
        public void setModelName(String m) { this.modelName = m; }
        public void setQuantity(int q) { this.quantity = q; }
        public void setPaymentType(String p) { this.paymentType = p; }
        public void setTotalPrice(double t) { this.totalPrice = t; }
    }
}