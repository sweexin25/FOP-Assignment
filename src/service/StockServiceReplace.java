package service;
import data.dataStorage;
import model.Employee;
import model.WatchModel;
import service.AttendanceService;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StockServiceReplace {
    private AttendanceService attendanceService;
    private dataStorage storage;
    WatchModel watchModel;

    public StockServiceReplace(dataStorage storage, AttendanceService attendanceService) {
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

    public void performStockCount(Employee user) {
        if (user == null) {
            System.out.println("Please login first.");
            return;
        }
        String outletCode = attendanceService.getOutletCode();
        if (outletCode == "") {
            System.out.println("Please Clock In first.");
            return;
        }

        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();
        if (now.isBefore(LocalTime.NOON)){
            System.out.println("=== Morning Stock Count ===");
            System.out.println("Date: " + today);
            System.out.println("Time: " + now.format(DateTimeFormatter.ofPattern("hh:mm a")));
            System.out.println();
        }else if(now.isAfter(LocalTime.NOON)){
            System.out.println("=== Night Stock Count ===");
            System.out.println("Date: " + today);
            System.out.println("Time: " + now.format(DateTimeFormatter.ofPattern("hh:mm a")));
            System.out.println();
        }

        int totalChecked = storage.getModels().size();
        int tallyCorrect = 0;
        int mismatches = 0;

        Scanner sc = new Scanner(System.in);

            for (WatchModel model : storage.getModels()) {
                System.out.print("Model: " + model.getName() + " – Counted: ");
                int counted = sc.nextInt();
                sc.nextLine();
                int record = model.getStock(outletCode);
                System.out.println("Store Record: " + record);

                if (counted == record) {
                    System.out.println("Stock tally correct.");
                    tallyCorrect++;
                } else {
                    System.out.println("! Mismatch detected (" + Math.abs(counted - record) + " unit difference)");
                    mismatches++;
                }
                System.out.println();
            }

            System.out.println("Total Models Checked: " + totalChecked);
            System.out.println("Tally Correct: " + tallyCorrect);
            System.out.println("Mismatches: " + mismatches);
            System.out.println("Morning stock count completed.");
            if (mismatches > 0) {
                System.out.println("Warning: Please verify stock.");
            }

    }

    public void stockMovement(Employee user) {
        if (user == null) {
            System.out.println("Please login first.");
            return;
        }

        List<String> modelEntries = new ArrayList<>();
        int totalQuantity = 0;
        String outletCode = attendanceService.getOutletCode();
        int choice;
        String type = "";
        String modelLabel = "";
        Scanner sc = new Scanner(System.in);

        System.out.println("Do you want to Stock In or Stock Out");
        System.out.println("1. Stock In");
        System.out.println("2. Stock Out");

        while (true){
            System.out.println("Enter your choice: ");
            choice =  sc.nextInt();
            sc.nextLine();
            if (choice == 1) {
                type = "Stock In";
                modelLabel = "Models Received:";
                break;
            }else if (choice == 2) {
                type = "Stock Out";
                modelLabel = "Models Transferred:";
                break;
            }
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        String dateStr = now.format(dateFormatter);
        String timeStr = now.format(timeFormatter);

        System.out.println("=== " + type + " ===");
        System.out.println("Date: " + dateStr);
        System.out.println("Time: " + timeStr);

        //during stock in, from is sender, to is ME
        //during stock out, from is ME, to is receiver
        System.out.print("From (Outlet Code or HQ): ");
        String from = sc.nextLine();
        System.out.print("To (Outlet Code): ");
        String to = sc.nextLine();

        while (true) {
            System.out.print("Enter Model (or leave blank to finish): ");
            String modelName = sc.nextLine();
            if (modelName.isEmpty()) break;

            WatchModel model = findModel(modelName);
            if (model == null) {
                System.out.println("Model not found.");
                continue;
            }

            System.out.print("Quantity: ");
            int qty = sc.nextInt();
            sc.nextLine(); // Consume newline

            int senderStock = model.getStock(from);
            if (senderStock < qty) {
                System.out.println("Error: Insufficient stock!");
                System.out.println(from + " only has " + senderStock + " units.");
                continue;
            }

            model.setStock(from, senderStock - qty); // Sender decreases

            int receiverStock = model.getStock(to);
            model.setStock(to, receiverStock + qty); // Receiver increases

            modelEntries.add("- " + modelName + " (Quantity: " + qty + ")");
            totalQuantity += qty;
        }

        storage.saveModels();
        System.out.println("Model quantities updated successfully.");
        System.out.println(type + " recorded.");

        // Generate and append receipt
        String receiptFile = "receipts_" + dateStr + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(receiptFile, true))) {
            pw.println("=== " + type + " ===");
            pw.println("Date: " + dateStr);
            pw.println("Time: " + timeStr);
            pw.println("From: " + from);
            pw.println("To: " + to);
            pw.println(modelLabel);
            for (String entry : modelEntries) {
                pw.println(entry);
            }
            pw.println("Total Quantity: " + totalQuantity);
            pw.println("Employee in Charge: " + user.getName());
            pw.println();
        } catch (Exception e) {
            System.err.println("Error generating receipt: " + e.getMessage());
        }

        System.out.println("Receipt generated: " + receiptFile);
    }
}
