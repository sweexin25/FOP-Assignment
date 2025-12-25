import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;


class Outlet {
    private String code;
    private String name;

    public Outlet(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

class Employee {
    private String id;
    private String name;
    private String role;
    private String password;
    private String outletCode;

    public Employee(String id, String name, String role, String password) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.password = password;
        this.outletCode = id.substring(0, 3);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public String getOutletCode() {
        return outletCode;
    }
}

class WatchModel {
    private String name;
    private double price;
    private Map<String, Integer> stockByOutlet = new HashMap<>();

    public WatchModel(String name, double price, Map<String, Integer> stockByOutlet) {
        this.name = name;
        this.price = price;
        this.stockByOutlet.putAll(stockByOutlet);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock(String outletCode) {
        return stockByOutlet.getOrDefault(outletCode, 0);
    }

    public void setStock(String outletCode, int quantity) {
        stockByOutlet.put(outletCode, quantity);
    }
}

class SalesDataManager {
    private List<Employee> employees = new ArrayList<>();
    private List<Outlet> outlets = new ArrayList<>();
    private List<WatchModel> models = new ArrayList<>();

    private static final String EMPLOYEE_FILE = "employee.csv";
    private static final String MODEL_FILE = "model.csv";
    private static final String OUTLET_FILE = "outlet.csv";

    public void loadData() {
        // Load outlets
        try (BufferedReader br = new BufferedReader(new FileReader(OUTLET_FILE))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                outlets.add(new Outlet(parts[0].trim(), parts[1].trim()));
            }
        } catch (Exception e) {
            System.err.println("Error loading outlets: " + e.getMessage());
        }

        // Load employees
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                employees.add(new Employee(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()));
            }
        } catch (Exception e) {
            System.err.println("Error loading employees: " + e.getMessage());
        }

        // Load models
        try (BufferedReader br = new BufferedReader(new FileReader(MODEL_FILE))) {
            String line = br.readLine(); // header
            String[] headers = line.split(",");
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0].trim();
                double price = Double.parseDouble(parts[1].trim());
                Map<String, Integer> stock = new HashMap<>();
                for (int i = 2; i < parts.length; i++) {
                    stock.put(headers[i].trim(), Integer.parseInt(parts[i].trim()));
                }
                models.add(new WatchModel(name, price, stock));
            }
        } catch (Exception e) {
            System.err.println("Error loading models: " + e.getMessage());
        }
    }

    public void saveModels() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MODEL_FILE))) {
            pw.print("Model,Price");
            for (Outlet o : outlets) {
                pw.print("," + o.getCode());
            }
            pw.println();
            for (WatchModel m : models) {
                pw.print(m.getName() + "," + m.getPrice());
                for (Outlet o : outlets) {
                    pw.print("," + m.getStock(o.getCode()));
                }
                pw.println();
            }
        } catch (Exception e) {
            System.err.println("Error saving models: " + e.getMessage());
        }
    }

    public List<WatchModel> getModels() {
        return models;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Outlet> getOutlets() {
        return outlets;
    }
}

public class SalesService {
    private SalesDataManager dataManager = new SalesDataManager();
    private Employee loggedInEmployee;

    public SalesService() {
        dataManager.loadData();
    }

    private boolean login() {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Employee Login for Sales System ===");
        System.out.print("Enter User ID: ");
        String id = sc.nextLine();
        System.out.print("Enter Password: ");
        String pw = sc.nextLine();

        for (Employee e : dataManager.getEmployees()) {
            if (e.getId().equals(id) && e.getPassword().equals(pw)) {
                loggedInEmployee = e;
                System.out.println("\nLogin Successful!");
                System.out.println("Welcome, " + e.getName() + " (" + e.getOutletCode() + ")\n");
                return true;
            }
        }
        System.out.println("\nLogin Failed: Invalid User ID or Password.\n");
        return false;
    }

    private WatchModel findModel(String name) {
        for (WatchModel m : dataManager.getModels()) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public void recordSale() {
        if (loggedInEmployee == null) {
            System.out.println("Please login first.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        String dateStr = now.format(dateFormatter);
        String timeStr = now.format(timeFormatter);

        System.out.println("=== Record New Sale ===");
        System.out.println("Date: " + dateStr);
        System.out.println("Time: " + timeStr);

        Scanner sc = new Scanner(System.in);
        System.out.print("Customer Name: ");
        String customerName = sc.nextLine();

        List<String> itemEntries = new ArrayList<>();
        double subtotal = 0.0;
        String outletCode = loggedInEmployee.getOutletCode();

        while (true) {
            System.out.print("Enter Model: ");
            String modelName = sc.nextLine();

            WatchModel model = findModel(modelName);
            if (model == null) {
                System.out.println("Model not found.");
                continue;
            }

            System.out.print("Enter Quantity: ");
            int qty = sc.nextInt();
            sc.nextLine(); // Consume newline

            int currentStock = model.getStock(outletCode);
            if (currentStock < qty) {
                System.out.println("Insufficient stock!");
                continue;
            }

            double unitPrice = model.getPrice();
            double itemTotal = unitPrice * qty;
            subtotal += itemTotal;

            itemEntries.add("Item(s): " + modelName);
            itemEntries.add("Quantity: " + qty);
            itemEntries.add("Unit Price: RM" + unitPrice);

            model.setStock(outletCode, currentStock - qty);

            System.out.print("Are there more items purchased? (Y/N): ");
            String more = sc.nextLine();
            if (!more.equalsIgnoreCase("Y")) {
                break;
            }
        }

        System.out.println("Subtotal: RM" + subtotal);
        System.out.print("Enter transaction method: ");
        String transactionMethod = sc.nextLine();

        System.out.println("Transaction successful.");
        System.out.println("Sale recorded successfully.");
        System.out.println("Model quantities updated successfully.");

        dataManager.saveModels();

        // Generate and append receipt
        String receiptFile = "sales_" + dateStr + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(receiptFile, true))) {
            pw.println("=== Sale Receipt ===");
            pw.println("Date: " + dateStr);
            pw.println("Time: " + timeStr);
            pw.println("Customer: " + customerName);
            for (String entry : itemEntries) {
                pw.println(entry);
            }
            pw.println("Total: RM" + subtotal);
            pw.println("Transaction Method: " + transactionMethod);
            pw.println("Employee: " + loggedInEmployee.getName());
            pw.println();
        } catch (Exception e) {
            System.err.println("Error generating receipt: " + e.getMessage());
        }

        System.out.println("Receipt generated: " + receiptFile);
    }

    public static void main(String[] args) {
        SalesService system = new SalesService();

        if (!system.login()) {
            System.out.println("Exiting due to failed login.");
            return;
        }

        // Test Sales System
        system.recordSale();
    }
}

