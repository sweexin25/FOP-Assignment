//package service;
//import java.io.*;
//import java.util.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//
//class Outlet {
//    private String code;
//    private String name;
//
//    public Outlet(String code, String name) {
//        this.code = code;
//        this.name = name;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public String getName() {
//        return name;
//    }
//}
//
//class Employee {
//    private String id;
//    private String name;
//    private String role;
//    private String password;
//    private String outletCode;
//
//    public Employee(String id, String name, String role, String password) {
//        this.id = id;
//        this.name = name;
//        this.role = role;
//        this.password = password;
//        this.outletCode = id.substring(0, 3);
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getRole() {
//        return role;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public String getOutletCode() {
//        return outletCode;
//    }
//}
//
//class WatchModel {
//    private String name;
//    private double price;
//    private Map<String, Integer> stockByOutlet = new HashMap<>();
//
//    public WatchModel(String name, double price, Map<String, Integer> stockByOutlet) {
//        this.name = name;
//        this.price = price;
//        this.stockByOutlet.putAll(stockByOutlet);
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public int getStock(String outletCode) {
//        return stockByOutlet.getOrDefault(outletCode, 0);
//    }
//
//    public void setStock(String outletCode, int quantity) {
//        stockByOutlet.put(outletCode, quantity);
//    }
//}
//
//class StockDataManager {
//    private List<Employee> employees = new ArrayList<>();
//    private List<Outlet> outlets = new ArrayList<>();
//    private List<WatchModel> models = new ArrayList<>();
//
//    private static final String EMPLOYEE_FILE = "employees.csv";
//    private static final String MODEL_FILE = "model.csv";
//    private static final String OUTLET_FILE = "outlet.csv";
//
//    public void loadData() {
//        // Load outlets
//        try (BufferedReader br = new BufferedReader(new FileReader(OUTLET_FILE))) {
//            String line = br.readLine(); // header
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split(",");
//                outlets.add(new Outlet(parts[0].trim(), parts[1].trim()));
//            }
//        } catch (Exception e) {
//            System.err.println("Error loading outlets: " + e.getMessage());
//        }
//
//        // Load employees
//        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
//            String line = br.readLine(); // header
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split(",");
//                employees.add(new Employee(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()));
//            }
//        } catch (Exception e) {
//            System.err.println("Error loading employees: " + e.getMessage());
//        }
//
//        // Load models
//        try (BufferedReader br = new BufferedReader(new FileReader(MODEL_FILE))) {
//            String line = br.readLine(); // header
//            String[] headers = line.split(",");
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split(",");
//                String name = parts[0].trim();
//                double price = Double.parseDouble(parts[1].trim());
//                Map<String, Integer> stock = new HashMap<>();
//                for (int i = 2; i < parts.length; i++) {
//                    stock.put(headers[i].trim(), Integer.parseInt(parts[i].trim()));
//                }
//                models.add(new WatchModel(name, price, stock));
//            }
//        } catch (Exception e) {
//            System.err.println("Error loading models: " + e.getMessage());
//        }
//    }
//
//    public void saveModels() {
//        try (PrintWriter pw = new PrintWriter(new FileWriter(MODEL_FILE))) {
//            pw.print("Model,Price");
//            for (Outlet o : outlets) {
//                pw.print("," + o.getCode());
//            }
//            pw.println();
//            for (WatchModel m : models) {
//                pw.print(m.getName() + "," + m.getPrice());
//                for (Outlet o : outlets) {
//                    pw.print("," + m.getStock(o.getCode()));
//                }
//                pw.println();
//            }
//        } catch (Exception e) {
//            System.err.println("Error saving models: " + e.getMessage());
//        }
//    }
//
//    public List<WatchModel> getModels() {
//        return models;
//    }
//
//    public List<Employee> getEmployees() {
//        return employees;
//    }
//
//    public List<Outlet> getOutlets() {
//        return outlets;
//    }
//}
//
//public class StockService {
//    private StockDataManager dataManager = new StockDataManager();
//    private Employee loggedInEmployee;
//
//    public StockService() {
//        dataManager.loadData();
//    }
//
//    private boolean login() {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("=== Employee Login for Stock Management ===");
//        System.out.print("Enter User ID: ");
//        String id = sc.nextLine();
//        System.out.print("Enter Password: ");
//        String pw = sc.nextLine();
//
//        for (Employee e : dataManager.getEmployees()) {
//            if (e.getId().equals(id) && e.getPassword().equals(pw)) {
//                loggedInEmployee = e;
//                System.out.println("\nLogin Successful!");
//                System.out.println("Welcome, " + e.getName() + " (" + e.getOutletCode() + ")\n");
//                return true;
//            }
//        }
//        System.out.println("\nLogin Failed: Invalid User ID or Password.\n");
//        return false;
//    }
//
//    private WatchModel findModel(String name) {
//        for (WatchModel m : dataManager.getModels()) {
//            if (m.getName().equalsIgnoreCase(name)) {
//                return m;
//            }
//        }
//        return null;
//    }
//
//    public void performStockCount(boolean isMorning) {
//        if (loggedInEmployee == null) {
//            System.out.println("Please login first.");
//            return;
//        }
//
//        String type = isMorning ? "Morning" : "Night";
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
//
//        System.out.println("=== " + type + " Stock Count ===");
//        System.out.println("Date: " + now.format(dateFormatter));
//        System.out.println("Time: " + now.format(timeFormatter));
//        System.out.println();
//
//        int totalChecked = dataManager.getModels().size();
//        int tallyCorrect = 0;
//        int mismatches = 0;
//        String outletCode = loggedInEmployee.getOutletCode();
//        Scanner sc = new Scanner(System.in);
//
//        for (WatchModel model : dataManager.getModels()) {
//            System.out.print("Model: " + model.getName() + " – Counted: ");
//            int counted = sc.nextInt();
//            int record = model.getStock(outletCode);
//            System.out.println("Store Record: " + record);
//
//            if (counted == record) {
//                System.out.println("Stock tally correct.");
//                tallyCorrect++;
//            } else {
//                System.out.println("! Mismatch detected (" + Math.abs(counted - record) + " unit difference)");
//                mismatches++;
//            }
//            System.out.println();
//        }
//
//        System.out.println("Total Models Checked: " + totalChecked);
//        System.out.println("Tally Correct: " + tallyCorrect);
//        System.out.println("Mismatches: " + mismatches);
//        System.out.println(type + " stock count completed.");
//        if (mismatches > 0) {
//            System.out.println("Warning: Please verify stock.");
//        }
//    }
//
//    public void stockMovement(boolean isIn) {
//        if (loggedInEmployee == null) {
//            System.out.println("Please login first.");
//            return;
//        }
//
//        String type = isIn ? "Stock In" : "Stock Out";
//        String modelLabel = isIn ? "Models Received:" : "Models Transferred:";
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
//        String dateStr = now.format(dateFormatter);
//        String timeStr = now.format(timeFormatter);
//
//        System.out.println("=== " + type + " ===");
//        System.out.println("Date: " + dateStr);
//        System.out.println("Time: " + timeStr);
//
//        Scanner sc = new Scanner(System.in);
//        System.out.print("From (Outlet Code or HQ): ");
//        String from = sc.nextLine();
//        System.out.print("To (Outlet Code): ");
//        String to = sc.nextLine();
//
//        List<String> modelEntries = new ArrayList<>();
//        int totalQuantity = 0;
//        String outletCode = loggedInEmployee.getOutletCode();
//
//        while (true) {
//            System.out.print("Enter Model (or leave blank to finish): ");
//            String modelName = sc.nextLine();
//            if (modelName.isEmpty()) break;
//
//            WatchModel model = findModel(modelName);
//            if (model == null) {
//                System.out.println("Model not found.");
//                continue;
//            }
//
//            System.out.print("Quantity: ");
//            int qty = sc.nextInt();
//            sc.nextLine(); // Consume newline
//
//            int currentStock = model.getStock(outletCode);
//            if (isIn) {
//                model.setStock(outletCode, currentStock + qty);
//            } else {
//                if (currentStock < qty) {
//                    System.out.println("Insufficient stock!");
//                    continue;
//                }
//                model.setStock(outletCode, currentStock - qty);
//            }
//
//            modelEntries.add("- " + modelName + " (Quantity: " + qty + ")");
//            totalQuantity += qty;
//        }
//
//        dataManager.saveModels();
//        System.out.println("Model quantities updated successfully.");
//        System.out.println(type + " recorded.");
//
//        // Generate and append receipt
//        String receiptFile = "receipts_" + dateStr + ".txt";
//        try (PrintWriter pw = new PrintWriter(new FileWriter(receiptFile, true))) {
//            pw.println("=== " + type + " ===");
//            pw.println("Date: " + dateStr);
//            pw.println("Time: " + timeStr);
//            pw.println("From: " + from);
//            pw.println("To: " + to);
//            pw.println(modelLabel);
//            for (String entry : modelEntries) {
//                pw.println(entry);
//            }
//            pw.println("Total Quantity: " + totalQuantity);
//            pw.println("Employee in Charge: " + loggedInEmployee.getName());
//            pw.println();
//        } catch (Exception e) {
//            System.err.println("Error generating receipt: " + e.getMessage());
//        }
//
//        System.out.println("Receipt generated: " + receiptFile);
//    }
//
//    public static void main(String[] args) {
//        StockService system = new StockService();
//
//        if (!system.login()) {
//            System.out.println("Exiting due to failed login.");
//            return;
//        }
//
//        // Test Stock Management
//        system.performStockCount(true); // Morning count
//        // system.stockMovement(true); // Stock In - uncomment to test
//    }
//}