import model.Employee;
import service.*;
import data.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        dataStorage data = new dataStorage();
        data.loadData();

        AuthService auth = new AuthService(data);
        EditService editor = new EditService(data);

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Employee Login =====");
            System.out.print("Enter User ID: ");
            String id = sc.nextLine();
            System.out.print("Enter Password: ");
            String pass = sc.nextLine();

            boolean success = auth.login(id, pass, data.getEmployees());

            if (success) {
                Employee currentUser = auth.getCurrentUser();
                System.out.println("Welcome, " + currentUser.getName());

                while (auth.getCurrentUser() != null) {
                    System.out.println("\n===== MAIN MENU =====");
                    System.out.println("1. Register New Employee (Manager)");
                    System.out.println("2. Edit Employee Profile");
                    System.out.println("3. Edit Stock Quantity");
                    System.out.println("4. Log Out");
                    System.out.print("Choice: ");

                    String choice = sc.nextLine();

                    if (choice.equals("1")) {
                        if (currentUser.getRole().equalsIgnoreCase("Manager")) {
                            System.out.println("--- Register New Employee ---");
                            System.out.print("Name: "); String n = sc.nextLine();
                            System.out.print("ID: "); String i = sc.nextLine();
                            System.out.print("Pass: "); String p = sc.nextLine();
                            System.out.print("Role: "); String r = sc.nextLine();
                            auth.uniqueEmployee(i, n, r, p, data.getEmployees());
                        } else {
                            System.out.println("Access Denied.");
                        }

                    } else if (choice.equals("2")) {
                        System.out.println("--- Edit Profile ---");
                        System.out.print("Target ID: "); String tId = sc.nextLine();
                        System.out.println("1. Name\n2. Password");
                        String type = sc.nextLine();
                        if(type.equals("1")) {
                            System.out.print("New Name: "); editor.updateName(tId, sc.nextLine());
                        } else if(type.equals("2")) {
                            System.out.print("New Pass: "); editor.updatePassword(tId, sc.nextLine());
                        }

                    } else if (choice.equals("3")) {
                        // --- EDIT STOCK WITH PREVIEW ---
                        System.out.println("\n--- Edit Stock ---");
                        System.out.print("Enter Model Name (e.g. DW2300-1): ");
                        String mName = sc.nextLine();

                        System.out.print("Enter Outlet Code (e.g. C60): ");
                        String oCode = sc.nextLine();

                        // 1. GET CURRENT STOCK
                        int currentQty = editor.getCurrentStock(mName, oCode);

                        if (currentQty == -1) {
                            System.out.println("Error: Outlet not found.");
                        } else if (currentQty == -2) {
                            System.out.println("Error: Model not found.");
                        } else {
                            // 2. SHOW IT AND ASK FOR UPDATE
                            System.out.println("Current Stock: " + currentQty);
                            System.out.print("Enter New Quantity: ");
                            try {
                                int newQty = Integer.parseInt(sc.nextLine());
                                editor.updateStock(mName, oCode, newQty);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid number.");
                            }
                        }

                    } else if (choice.equals("4")) {
                        auth.logOut();
                    }
                }
            } else {
                System.out.println("Login Failed.");
            }
        }
    }
}