package service;

import data.dataStorage;
import model.Employee;
import model.Outlet;
import model.Sale;
import model.WatchModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchService {
    Scanner sc = new Scanner(System.in);
    WatchModel watchModel;
    dataStorage storage;
    ArrayList<Sale> salesList;

    public SearchService(dataStorage storage) {
        this.storage = storage;
        this.salesList = storage.loadSale();
    }

    public void searchService(Employee user) {
        while (true) {
            this.salesList = storage.loadSale();
            System.out.println("What do you want to search?");
            System.out.println("1. Stock Information");
            System.out.println("2. Sales Information");
            System.out.println("3. Back to Main Menu");
            System.out.println("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1 -> searchStock();
                case 2 -> searchSales();
                case 3 -> {
                    System.out.println("Returning to Main Menu......");
                    return;
                }
                default -> {
                    System.out.println("Invalid choice");
                    return;
                }
            }
        }
    }

    private WatchModel findModel(String name) {
        for (WatchModel m : storage.getModels()) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public void searchStock() {
        while (true) {
            System.out.println("=== Search Stock Information ===");
            System.out.print("Search Model Name (Enter blank to exit): ");
            String modelName = sc.nextLine().trim();
            WatchModel model = findModel(modelName);
            if (modelName.isEmpty()) {
                return;
            }
            if (model == null) {
                System.out.println("Invalid model name\n");
                continue;
            }

            List<WatchModel> models = storage.getModels();
            System.out.println("Seaching.........\n");

            System.out.println("Model: " + model.getName());
            System.out.println("Unit Price: RM" + model.getPrice());

            System.out.println("Stock by Outlet:");

            for (Outlet outlet : storage.getAllOutlet()) {
                if (outlet == null) {
                    break;
                }
                System.out.println(outlet.getOutletName() + ": " + model.getStock(outlet.getOutletCode()));
            }
        }
    }

    public void searchSales() {
        while (true) {
            System.out.println("=== Search Sales Information ===");
            System.out.println("Search keyword:");
            System.out.println("1. Search by Customer Name");
            System.out.println("2. Search by Date");
            System.out.println("3. Search by Model Name");
            System.out.println("4. Back to Search Menu");
            System.out.println("Enter your choice:");
            if (sc.hasNextInt()){
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> {
                        System.out.println("Enter Name keyword:");
                        String keyword = sc.nextLine().trim();
                        System.out.println("Searching..... \n");
                        searchByName(salesList, keyword);
                    }
                    case 2 -> {
                        System.out.println("Enter Date (YYYY-MM-DD):");
                        String dateKeyword = sc.nextLine().trim();
                        System.out.println("Searching..... \n");
                        searchByDate(salesList, dateKeyword);
                    }
                    case 3 -> {
                        System.out.println("Enter Model Name:");
                        String modelKeyword = sc.nextLine().trim();
                        System.out.println("Searching..... \n");
                        searchByModel(salesList, modelKeyword);
                    }
                    case 4 -> {
                        return;
                    }
                    default -> {
                        System.out.println("Invalid choice. Enter again.");
                    }
                }

            }else{
                System.out.println("Invalid choice. Enter number only.");
                sc.nextLine();
            }
        }
    }

    public void searchByName(ArrayList<Sale> salesList, String name) {
        boolean found = false;

        for (Sale s : salesList) {
            if (s.getCustomerName().toLowerCase().contains(name.toLowerCase())) {
                System.out.println("Record Found");
                found = true;
                s.printReceipt();
            }
        }
        if (!found) {
            System.out.println("Record Not Found");
        }
    }

    public void searchByDate(ArrayList<Sale> salesList, String date) {
        boolean found = false;

        for (Sale s : salesList) {
            if (s.getDate().contains(date)) {
                System.out.println("Record Found");
                found = true;
                s.printReceipt();
            }
        }
        if (!found) {
            System.out.println("Record Not Found");
        }
    }

    public void searchByModel(ArrayList<Sale> salesList, String model) {
        boolean found = false;

        for (Sale s : salesList) {
            // Loop through every item line in that receipt
            for (String itemString : s.getWatchModel()) {

                //Separate the Model Name from the Quantity
                String[] parts = itemString.split(":");
                String actualModelName = parts[0];

                //search for identical record
                if (actualModelName.toLowerCase().contains(model.toLowerCase())) {
                    System.out.println("Record Found!");
                    s.printReceipt();
                    found = true;

                    // Stop checking this specific receipt
                    break;

                }
            }
        }
        if (!found) {
            System.out.println("Record Not Found");
        }
    }
}

