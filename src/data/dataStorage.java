package data;

import model.Employee;
import model.Model;
import java.io.*;
import java.util.ArrayList;

public class dataStorage {
    private ArrayList<Employee> employeeList = new ArrayList<>();
    private ArrayList<Model> modelList = new ArrayList<>();

    // Header columns for outlets
    private String[] outletColumns = {};

    public void loadData() {
        loadEmployee();
        loadModels();
    }

    // --- EMPLOYEE SECTION (Keeps using Commas) ---
    private void loadEmployee() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("employees.csv"));
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                // employees.csv still uses commas
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Employee employee = new Employee(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
                    employeeList.add(employee);
                }
            }
            System.out.println("Employees Loaded: " + employeeList.size());
        } catch (Exception e) {
            System.out.println("Error loading employees: " + e.getMessage());
        }
    }

    public void registerEmployee(String newID, String newName, String newRole, String newPass) {
        try (FileWriter writer = new FileWriter("employees.csv", true)) {
            writer.write("\n" + newID.trim() + "," + newName.trim() + "," + newRole.trim() + "," + newPass.trim());
            System.out.println("Employee Registered Successfully");
            Employee employee = new Employee(newID, newName, newRole, newPass);
            employeeList.add(employee);
        } catch (IOException e) {
            System.out.println("Failed to register employee.");
        }
    }

    public ArrayList<Employee> getEmployees() { return employeeList; }

    public void saveEmployees() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("employees.csv"))) {
            writer.write("EmployeeID,EmployeeName,Role,Password");
            writer.newLine();
            for (Employee emp : employeeList) {
                writer.write(emp.getId() + "," + emp.getName() + "," + emp.getRole() + "," + emp.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Failed to save employees.");
        }
    }

    // --- MODEL SECTION (Updated for TABS) ---
    private void loadModels() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("model.csv"));

            // 1. Read Header
            String headerLine = reader.readLine();
            if (headerLine != null) {
                // CRITICAL FIX: Split by TAB ("\t") not comma
                String[] parts = headerLine.split("\\t");

                if (parts.length > 2) {
                    outletColumns = new String[parts.length - 2];
                    for (int i = 0; i < outletColumns.length; i++) {
                        outletColumns[i] = parts[i + 2].trim();
                    }
                }
            }

            // 2. Read Data Rows
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // CRITICAL FIX: Split by TAB ("\t")
                String[] parts = line.split("\\t");

                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    double price = 0.0;
                    try {
                        price = Double.parseDouble(parts[1].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid price for " + name);
                    }

                    Model model = new Model(name, price);

                    // Parse quantities for each outlet
                    for (int i = 0; i < outletColumns.length; i++) {
                        if (i + 2 < parts.length) {
                            try {
                                int qty = Integer.parseInt(parts[i + 2].trim());
                                model.setQuantity(outletColumns[i], qty);
                            } catch (NumberFormatException e) {
                                model.setQuantity(outletColumns[i], 0);
                            }
                        }
                    }
                    modelList.add(model);
                }
            }
            System.out.println("Models Loaded: " + modelList.size());
        } catch (FileNotFoundException e) {
            System.out.println("model.csv not found.");
        } catch (IOException e) {
            System.out.println("Error reading model.csv.");
        }
    }

    public void saveModels() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("model.csv"))) {
            // 1. Write Header (Using TABS to match your file format)
            writer.write("Model\tPrice");
            for (String outlet : outletColumns) {
                writer.write("\t" + outlet);
            }
            writer.newLine();

            // 2. Write Data (Using TABS)
            for (Model m : modelList) {
                writer.write(m.getModelName() + "\t" + m.getPrice());
                for (String outlet : outletColumns) {
                    writer.write("\t" + m.getQuantity(outlet));
                }
                writer.newLine();
            }
            System.out.println("Stock changes saved to model.csv.");
        } catch (IOException e) {
            System.out.println("Failed to save stock data.");
        }
    }

    public ArrayList<Model> getModels() { return modelList; }
}