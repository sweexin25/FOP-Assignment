package service;

import data.dataStorage;
import model.Employee;
import model.Model;

public class EditService {
    private dataStorage data;

    public EditService(dataStorage data) {
        this.data = data;
    }

    //EDIT EMPLOYEE
    public void updateName(String id, String newName) {
        for (Employee emp : data.getEmployees()) {
            if (emp.getId().equalsIgnoreCase(id)) {
                emp.setName(newName);
                data.saveEmployees();
                System.out.println("Name fixed.");
                return;
            }
        }
        System.out.println("Can't find that user ID.");
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
        System.out.println("Can't find that user ID.");
    }

    // EDIT STOCK
    public int getCurrentStock(String modelName, String outletCode) {
        for (Model m : data.getModels()) {
            if (m.getModelName().equalsIgnoreCase(modelName)) {
                return m.getQuantity(outletCode.toUpperCase());
            }
        }
        return -1;
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

    //EDIT SALES
    public Transaction getSale(String saleID) {
        for (Transaction t : data.getSales()) {
            if (t.getSaleID().equalsIgnoreCase(saleID)) return t;
        }
        return null;
    }

    public void updateSaleCustomer(String saleID, String newName) {
        Transaction t = getSale(saleID);
        if (t != null) {
            t.setCustomerName(newName);
            data.saveSales();
            System.out.println("Customer name updated.");
        } else {
            System.out.println("Sale ID not found.");
        }
    }

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

    //Method to edit Payment Type
    public void updateSalePaymentType(String saleID, String newType) {
        Transaction t = getSale(saleID);
        if (t != null) {
            t.setPaymentType(newType);
            data.saveSales();
            System.out.println("Payment type updated to " + newType);
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
        private String outletCode;
        private String paymentType; //(Cash/Card)

        public Transaction(String saleID, String customerName, String modelName, int quantity, String date, String outletCode, String paymentType) {
            this.saleID = saleID;
            this.customerName = customerName;
            this.modelName = modelName;
            this.quantity = quantity;
            this.date = date;
            this.outletCode = outletCode;
            this.paymentType = paymentType;
        }

        public String getSaleID() { return saleID; }
        public String getCustomerName() { return customerName; }
        public String getModelName() { return modelName; }
        public int getQuantity() { return quantity; }
        public String getDate() { return date; }
        public String getOutletCode() { return outletCode; }
        public String getPaymentType() { return paymentType; } // <--- New Getter

        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setPaymentType(String paymentType) { this.paymentType = paymentType; } // <--- New Setter
    }
}