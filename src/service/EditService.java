package service;

import data.dataStorage;
import model.Employee;
import model.Model;

public class EditService {
    private dataStorage data;

    public EditService(dataStorage data) {
        this.data = data;
    }

    // --- HELPER: VIEW STOCK (For PDF Requirement) ---
    public int getCurrentStock(String modelName, String outlet) {
        for (Model m : data.getModels()) {
            if (m.getModelName().equalsIgnoreCase(modelName.trim())) {
                return m.getQuantity(outlet.trim());
            }
        }
        return -1; // -1 means model not found
    }

    // --- ACTION: UPDATE STOCK ---
    public void updateStock(String modelName, String targetOutlet, int newQuantity) {
        Model foundModel = null;
        for (Model m : data.getModels()) {
            if (m.getModelName().equalsIgnoreCase(modelName.trim())) {
                foundModel = m;
                break;
            }
        }

        if (foundModel != null) {
            foundModel.setQuantity(targetOutlet.trim(), newQuantity);
            data.saveModels();
            System.out.println("Stock information updated successfully.");
        } else {
            System.out.println("Error: Model name '" + modelName + "' not found.");
        }
    }

    // --- EMPLOYEE UPDATES ---
    public void updateName(String id, String newName) {
        Employee emp = findEmployee(id);
        if (emp != null) {
            emp.setName(newName);
            data.saveEmployees();
            System.out.println("Name updated successfully!");
        } else {
            System.out.println("Employee not found.");
        }
    }

    public void updatePassword(String id, String newPass) {
        Employee emp = findEmployee(id);
        if (emp != null) {
            emp.setPassword(newPass);
            data.saveEmployees();
            System.out.println("Password updated successfully!");
        } else {
            System.out.println("Employee not found.");
        }
    }

    private Employee findEmployee(String id) {
        for (Employee emp : data.getEmployees()) {
            if (emp.getId().equalsIgnoreCase(id.trim())) return emp;
        }
        return null;
    }
}