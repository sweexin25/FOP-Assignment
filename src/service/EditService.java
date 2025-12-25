package service;

import data.dataStorage;
import model.Employee;
import model.Model;

public class EditService {
    private dataStorage data;

    public EditService(dataStorage data) {
        this.data = data;
    }

    // --- PART 1: EDIT USERS ---
    public void updateName(String id, String newName) {
        Employee emp = findEmployee(id);
        if (emp != null) {
            emp.setName(newName);
            data.saveEmployees();
            System.out.println("Name updated.");
        } else {
            System.out.println("User not found.");
        }
    }

    public void updatePassword(String id, String newPass) {
        Employee emp = findEmployee(id);
        if (emp != null) {
            emp.setPassword(newPass);
            data.saveEmployees();
            System.out.println("Password updated.");
        } else {
            System.out.println("User not found.");
        }
    }

    private Employee findEmployee(String id) {
        for (Employee emp : data.getEmployees()) {
            if (emp.getId().equalsIgnoreCase(id)) return emp;
        }
        return null;
    }

    // --- PART 2: EDIT STOCK ---

    // NEW: Helper to get the current number (returns -1 if error)
    public int getCurrentStock(String modelName, String outletCode) {
        // 1. Check Outlet
        boolean validOutlet = false;
        for (String code : data.getOutletCodes()) {
            if (code.equalsIgnoreCase(outletCode)) validOutlet = true;
        }
        if (!validOutlet) return -1; // Error code for "Outlet Not Found"

        // 2. Find Model
        for (Model m : data.getModels()) {
            if (m.getModelName().equalsIgnoreCase(modelName)) {
                return m.getQuantity(outletCode.toUpperCase());
            }
        }
        return -2; // Error code for "Model Not Found"
    }

    public void updateStock(String modelName, String outletCode, int newQty) {
        // We find the model again to update it
        for (Model m : data.getModels()) {
            if (m.getModelName().equalsIgnoreCase(modelName)) {
                m.setQuantity(outletCode.toUpperCase(), newQty);
                data.saveStock();
                System.out.println("Success: Stock updated.");
                return;
            }
        }
    }
}