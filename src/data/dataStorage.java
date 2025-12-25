package data;

import model.Employee;
import model.Model; // using your uploaded Model class
import java.io.*;
import java.util.ArrayList;

public class dataStorage {
    private ArrayList<Employee> employeeList = new ArrayList<>();
    private ArrayList<String> outletCodes = new ArrayList<>();

    // new lists to store the stock data
    private ArrayList<Model> modelList = new ArrayList<>();
    private ArrayList<String> stockHeaders = new ArrayList<>(); // keeps track of C60, C61...

    public void loadData() {
        loadEmployee();
        loadOutlets();
        loadStock(); // now we load the stock too
    }

    // --- NEW: Loading Stock ---
    private void loadStock() {
        modelList.clear();
        stockHeaders.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("model.csv"));

            // 1. read the header to find the outlet columns
            String headerLine = reader.readLine();
            if (headerLine != null) {
                String[] parts = headerLine.split(",");
                // start at index 2 (skipping Model and Price)
                for (int i = 2; i < parts.length; i++) {
                    stockHeaders.add(parts[i].trim());
                }
            }

            // 2. read the data rows
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");

                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    double price = Double.parseDouble(parts[1].trim());

                    Model model = new Model(name, price);

                    // assign quantities to the right outlet code
                    for (int i = 0; i < stockHeaders.size(); i++) {
                        // check if there is data for this column
                        if (i + 2 < parts.length) {
                            int qty = Integer.parseInt(parts[i + 2].trim());
                            model.setQuantity(stockHeaders.get(i), qty);
                        }
                    }
                    modelList.add(model);
                }
            }
            System.out.println("Stock Loaded: " + modelList.size() + " items");
        } catch (Exception e) {
            System.out.println("model.csv not found.");
        }
    }

    // --- NEW: Saving Stock ---
    public void saveStock() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("model.csv"))) {
            // write header
            writer.write("Model,Price");
            for (String code : stockHeaders) {
                writer.write("," + code);
            }
            writer.newLine();

            // write data rows
            for (Model m : modelList) {
                writer.write(m.getModelName() + "," + m.getPrice());
                // use the header list to write quantities in the correct order
                for (String code : stockHeaders) {
                    writer.write("," + m.getQuantity(code));
                }
                writer.newLine();
            }
            System.out.println("Stock saved to file.");
        } catch (IOException e) {
            System.out.println("Failed to save stock.");
        }
    }

    // --- YOUR ORIGINAL LOADERS (Kept the same) ---
    private void loadEmployee() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("employees.csv"));
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    employeeList.add(new Employee(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()));
                }
            }
            System.out.println("Employees Loaded: " + employeeList.size());
        } catch (Exception e) { System.out.println("Employee file error."); }
    }

    private void loadOutlets() {
        outletCodes.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("outlet.csv"));
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 1) outletCodes.add(parts[0].trim());
            }
            System.out.println("Outlets Loaded: " + outletCodes.size());
        } catch (Exception e) { System.out.println("Outlet file error."); }
    }

    // helpers used by other files
    public void registerEmployee(String newID, String newName, String newRole, String newPass) {
        employeeList.add(new Employee(newID, newName, newRole, newPass));
        saveEmployees();
        System.out.println("Employee Registered.");
    }
    public void saveEmployees() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("employees.csv"))) {
            writer.write("EmployeeID,EmployeeName,Role,Password");
            writer.newLine();
            for (Employee emp : employeeList) {
                writer.write(emp.getId() + "," + emp.getName() + "," + emp.getRole() + "," + emp.getPassword());
                writer.newLine();
            }
        } catch (IOException e) { }
    }

    public ArrayList<Employee> getEmployees() { return employeeList; }
    public ArrayList<String> getOutletCodes() { return outletCodes; }
    public ArrayList<Model> getModels() { return modelList; } // getter for stock list
}