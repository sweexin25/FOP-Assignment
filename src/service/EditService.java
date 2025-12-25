package service;

import data.dataStorage;
import model.Employee;
import model.Model;

public class EditService {
    private dataStorage data;

    public EditService(dataStorage data) {
        this.data = data;
    }

    //edit employee

    // Fix a typo in the name
    public void updateName(String id, String newName) {
        for (Employee emp : data.getEmployees()) {
            if (emp.getId().equalsIgnoreCase(id)) {
                emp.setName(newName);
                data.saveEmployees(); // save to file immediately
                System.out.println("Name fixed.");
                return;
            }
        }
        System.out.println("Can't find that user ID.");
    }

    // Reset a password
    public void updatePassword(String id, String newPass) {
        for (Employee emp : data.getEmployees()) {
            if (emp.getId().equalsIgnoreCase(id)) {
                emp.setPassword(newPass);
                data.saveEmployees();
                System.out.println("Password changed.");
                return;
            }
        }
        System.out.println("Can't find that user ID.");
    }

    //stock edit

    // Check how many we have before changing it
    public int getCurrentStock(String modelName, String outletCode) {
        for (Model m : data.getModels()) {
            if (m.getModelName().equalsIgnoreCase(modelName)) {
                return m.getQuantity(outletCode.toUpperCase());
            }
        }
        return -1; // -1 means we couldn't find it
    }

    // Update the stock count
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

    // edit sales

    // Helper to find a specific sale
    public Transaction getSale(String saleID) {
        for (Transaction t : data.getSales()) {
            if (t.getSaleID().equalsIgnoreCase(saleID)) return t;
        }
        return null;
    }

    // Fix a mistake in the customer's name
    public void updateSaleCustomer(String saleID, String newName) {
        Transaction t = getSale(saleID);
        if (t != null) {
            t.setCustomerName(newName);
            data.saveSales(); // save to csv
            System.out.println("Customer name updated.");
        } else {
            System.out.println("Sale ID not found.");
        }
    }

    // Fix the quantity sold
    public void updateSaleQuantity(String saleID, int newQty) {
        Transaction t = getSale(saleID);
        if (t != null) {
            t.setQuantity(newQty);
            data.saveSales();
            System.out.println("Quantity corrected.");
        } else {
            System.out.println("Sale ID not found.");
        }
    }


    public static class Transaction {
        private String saleID;
        private String customerName;
        private String modelName;
        private int quantity;
        private String date;

        public Transaction(String saleID, String customerName, String modelName, int quantity, String date) {
            this.saleID = saleID;
            this.customerName = customerName;
            this.modelName = modelName;
            this.quantity = quantity;
            this.date = date;
        }

        // Getters
        public String getSaleID() { return saleID; }
        public String getCustomerName() { return customerName; }
        public String getModelName() { return modelName; }
        public int getQuantity() { return quantity; }
        public String getDate() { return date; }

        //(for editing)
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}