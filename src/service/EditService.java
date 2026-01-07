package service;

import data.dataStorage;
import model.Employee;
import model.Sale;
import model.WatchModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EditService {
    private dataStorage storage;

    public EditService(dataStorage storage) {
        this.storage = storage;
    }

    public void handleEditMenu(Scanner sc) {
        boolean editing = true;
        while (editing) {
            System.out.println("\n===== Edit Information =====");
            System.out.println("1. Edit Employee Profile");
            System.out.println("2. Edit Stock Information");
            System.out.println("3. Edit Sales Information");
            System.out.println("4. Back to Main Menu");
            System.out.print("Select Option: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                editEmployeeProfile(sc);
            } else if (choice.equals("2")) {
                editStockInformation(sc);
            } else if (choice.equals("3")) {
                editSalesInformation(sc);
            } else if (choice.equals("4")) {
                System.out.println("Returning to main menu......\n");
                editing=false;
                return;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private void editEmployeeProfile(Scanner sc) {
        System.out.println("\n--- Edit Employee Profile ---");
        System.out.print("Enter Target Employee ID: ");
        String tId = sc.nextLine();

        Employee target = null;
        for (Employee e : storage.getEmployees()) {
            if (e.getId().equalsIgnoreCase(tId)) {
                target = e;
                break;
            }
        }

        if (target == null) {
            System.out.println("Employee ID not found.");
            return;
        }

        System.out.println("Editing User: " + target.getName());
        System.out.println("1. Edit Name");
        System.out.println("2. Edit Password");
        System.out.print("Choice: ");
        String type = sc.nextLine();

        if (type.equals("1")) {
            System.out.print("Enter New Name: ");
            target.setName(sc.nextLine());
            storage.saveAllEmployees();
            System.out.println("Name updated successfully.");
        } else if (type.equals("2")) {
            System.out.print("Enter New Password: ");
            target.setPassword(sc.nextLine());
            storage.saveAllEmployees();
            System.out.println("Password updated successfully.");
        }else{
            System.out.println("Invalid choice.");
            return;
        }

    }

    private void editStockInformation(Scanner sc) {
        System.out.println("\n--- Edit Stock Information ---");
        System.out.print("Enter Model Name: ");
        String modelName = sc.nextLine();

        WatchModel targetModel = null;
        for (WatchModel m : storage.getModels()) {
            if (m.getName().equalsIgnoreCase(modelName)) {
                targetModel = m;
                break;
            }
        }

        if (targetModel == null) {
            System.out.println("Model not found.");
            return;
        }

        System.out.print("Enter Outlet Code (e.g. C60): ");
        String outlet = sc.nextLine().toUpperCase();

        int current = targetModel.getStock(outlet);
        System.out.println("Current Stock at " + outlet + ": " + current);

        System.out.print("Enter New Stock Value: ");
        try {
            int newQty = Integer.parseInt(sc.nextLine());
            targetModel.setStock(outlet, newQty);
            storage.saveModels();
            System.out.println("Stock information updated successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private void editSalesInformation(Scanner sc) {
        System.out.println("\n--- Edit Sales Information ---");
        System.out.print("Enter Transaction Date (YYYY-MM-DD): ");
        String date = sc.nextLine();
        System.out.print("Enter Customer Name: ");
        String name = sc.nextLine();

        List<Sale> allSales = storage.loadSale();
        Sale targetSale = null;

        for (Sale s : allSales) {
            if (s.getDate().equals(date) && s.getCustomerName().equalsIgnoreCase(name)) {
                targetSale = s;
                break;
            }
        }

        if (targetSale == null) {
            System.out.println("Sales Record Not Found.");
            return;
        }

        System.out.println("\nSales Record Found:");
        ArrayList<String> items = targetSale.getWatchModel();
        for(String item : items) {
            System.out.println("Item: " + item);
        }
        System.out.println("Total: RM" + targetSale.getSubtotal());
        System.out.println("Transaction Method: " + targetSale.getTransactionMethod());

        System.out.println("\nSelect number to edit:");
        System.out.println("1. Customer Name");
        System.out.println("2. Model");
        System.out.println("3. Quantity");
        System.out.println("4. Total Price");
        System.out.println("5. Transaction Method");
        System.out.print("> ");
        String choice = sc.nextLine();

        if (choice.equals("1")) {
            System.out.print("Enter New Name: ");
            targetSale.setCustomerName(sc.nextLine());

        } else if (choice.equals("2")) {
            // --- EDIT MODEL NAME (With Validation) ---
            System.out.println("Current Items:");
            for(int i=0; i<items.size(); i++) System.out.println((i+1) + ". " + items.get(i));
            System.out.print("Select Item Number to Edit Model: ");
            try {
                int index = Integer.parseInt(sc.nextLine()) - 1;
                if(index >= 0 && index < items.size()) {
                    String[] parts = items.get(index).split(":"); // "ModelName:Quantity"
                    String currentQty = (parts.length > 1) ? parts[1] : "1";

                    System.out.println("Current Model: " + parts[0]);
                    System.out.print("Enter New Model Name: ");
                    String newName = sc.nextLine();

                    //Validate if model exists in CSV
                    boolean modelExists = false;
                    for (WatchModel m : storage.getModels()) {
                        if (m.getName().equalsIgnoreCase(newName)) {
                            modelExists = true;
                            newName = m.getName(); // Use correct casing from file
                            break;
                        }
                    }

                    if (modelExists) {
                        // Reconstruct string: NewName : OldQuantity
                        items.set(index, newName + ":" + currentQty);
                        targetSale.setWatchModel(items);
                        System.out.println("Model updated.");
                    } else {
                        System.out.println("Error: Model '" + newName + "' not found in model.csv database.");
                    }
                    // --------------------------------------------

                } else {
                    System.out.println("Invalid item number.");
                }
            } catch(Exception e) { System.out.println("Invalid input."); }

        } else if (choice.equals("3")) {
            // --- EDIT QUANTITY ONLY ---
            System.out.println("Current Items:");
            for(int i=0; i<items.size(); i++) System.out.println((i+1) + ". " + items.get(i));
            System.out.print("Select Item Number to Edit Quantity: ");
            try {
                int index = Integer.parseInt(sc.nextLine()) - 1;
                if(index >= 0 && index < items.size()) {
                    String[] parts = items.get(index).split(":");
                    String currentModel = parts[0];
                    String currentQty = (parts.length > 1) ? parts[1] : "1";

                    System.out.println("Current Quantity for " + currentModel + ": " + currentQty);
                    System.out.print("Enter New Quantity: ");
                    String newQty = sc.nextLine();

                    // Reconstruct string: OldName : NewQuantity
                    items.set(index, currentModel + ":" + newQty);
                    targetSale.setWatchModel(items);
                    System.out.println("Quantity updated.");
                } else {
                    System.out.println("Invalid item number.");
                }
            } catch(Exception e) { System.out.println("Invalid input."); }

        } else if (choice.equals("4")) {
            System.out.print("Enter New Total (RM): ");
            try { targetSale.setSubtotal(Double.parseDouble(sc.nextLine())); }
            catch(Exception e) { System.out.println("Invalid number."); }

        } else if (choice.equals("5")) {
            System.out.print("Enter New Transaction Method: ");
            targetSale.setTransactionMethod(sc.nextLine());
        }

        System.out.print("Confirm Update? (Y/N): ");
        if (sc.nextLine().equalsIgnoreCase("Y")) {
            storage.saveAllSales(allSales);
            System.out.println("Sales information updated successfully.");
        } else {
            System.out.println("Update cancelled.");
        }
    }
}