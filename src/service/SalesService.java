package service;
import data.dataStorage;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.Employee;
import model.Sale;
import model.WatchModel;

public class SalesService {
    private AttendanceService attendanceService;
    private dataStorage storage;
    ArrayList<String>watchModel = new ArrayList<>();

    public SalesService(dataStorage storage, AttendanceService attendanceService) {
        this.storage = storage;
        this.attendanceService = attendanceService;
    }

    private WatchModel findModel(String name) {
        for (WatchModel m : storage.getModels()) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }
    

    public void recordSale(Employee user) {
        if (user == null) {
            System.out.println("Please login first.");
            return;
        }
        String outletCode = attendanceService.getOutletCode();
        if (outletCode == "") {
            System.out.println("Please Clock In First");
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
            watchModel.add(modelName+":"+qty); //for searchService
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

        storage.saveModels();

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
            pw.println("Employee: " + user.getName());
            pw.println();
        } catch (Exception e) {
            System.err.println("Error generating receipt: " + e.getMessage());
        }

        System.out.println("Receipt generated: " + receiptFile);

        

        //for searchService
        Sale sale = new Sale(dateStr,timeStr,customerName,watchModel,subtotal, user.getName(), transactionMethod,"Success");
        sale.salesToCSV();
        storage.recordSale(sale);
    }
    //extra method for gui
    public void recordSaleGUI(Employee user, String customerName, String modelName, int qty, String method) {
        if (user == null || attendanceService.getOutletCode().isEmpty()) return;

        String outletCode = attendanceService.getOutletCode();
        WatchModel model = findModel(modelName);
        
        if (model == null || model.getStock(outletCode) < qty) return;

        //update stock if success
        model.setStock(outletCode, model.getStock(outletCode) - qty);
        storage.saveModels();

        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timeStr = now.format(DateTimeFormatter.ofPattern("hh:mm a"));
        double total = model.getPrice() * qty;

        ArrayList<String> itemDetails = new ArrayList<>();
        itemDetails.add(modelName + ":" + qty);

        Sale sale = new Sale(dateStr, timeStr, customerName, itemDetails, total, user.getName(), method, "Success");
        //save to sales.csv
        sale.salesToCSV();
        storage.recordSale(sale);
        //generate receipt
        generateTextReceipt(dateStr, timeStr, customerName, modelName, qty, model.getPrice(), total, method, user.getName());
    }

    private void generateTextReceipt(String date, String time, String cust, String model, int qty, double price, double total, String method, String empName) {
        String receiptFile = "sales_" + date + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(receiptFile, true))) {
            pw.println("=== Sale Receipt (GUI) ===");
            pw.println("Date: " + date + " | Time: " + time);
            pw.println("Customer: " + cust);
            pw.println("Item: " + model + " x" + qty);
            pw.println("Total: RM" + total);
            pw.println("Method: " + method);
            pw.println("Staff: " + empName);
            pw.println("--------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
