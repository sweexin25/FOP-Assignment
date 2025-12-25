import model.Employee;
import service.*;
import data.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        dataStorage data = new dataStorage();
        data.loadData(); // load up all the files

        AuthService auth = new AuthService(data);
        EditService editor = new EditService(data);

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Employee Login =====");
            System.out.print("Enter User ID: ");
            String id = sc.nextLine();
            System.out.print("Enter Password: ");
            String pass = sc.nextLine();

            if (auth.login(id, pass, data.getEmployees())) {
                Employee currentUser = auth.getCurrentUser();
                System.out.println("Welcome, " + currentUser.getName());

                while (auth.getCurrentUser() != null) {
                    System.out.println("\n===== MAIN MENU =====");
                    System.out.println("1. Register New Employee");
                    System.out.println("2. Edit Employee Profile");
                    System.out.println("3. Edit Stock Quantity");
                    System.out.println("4. Record New Sale");
                    System.out.println("5. Edit Sales Information");
                    System.out.println("6. Log Out");
                    System.out.print("Choice: ");

                    String choice = sc.nextLine();

                    if (choice.equals("1")) {
                        // Manager only
                        if (currentUser.getRole().equalsIgnoreCase("Manager")) {
                            System.out.println("Enter Name, ID, Pass, Role:");
                            auth.uniqueEmployee(sc.nextLine(), sc.nextLine(), sc.nextLine(), sc.nextLine(), data.getEmployees());
                        } else System.out.println("Access Denied");

                    } else if (choice.equals("2")) {
                        // Edit Employee
                        System.out.print("Target ID: "); String tId = sc.nextLine();
                        System.out.println("1.Name 2.Pass");
                        if (sc.nextLine().equals("1")) {
                            System.out.print("New Name: "); editor.updateName(tId, sc.nextLine());
                        } else {
                            System.out.print("New Pass: "); editor.updatePassword(tId, sc.nextLine());
                        }

                    } else if (choice.equals("3")) {
                        // Edit Stock
                        System.out.print("Model: "); String m = sc.nextLine();
                        System.out.print("Outlet: "); String o = sc.nextLine();
                        System.out.println("Current: " + editor.getCurrentStock(m, o));
                        System.out.print("New Qty: ");
                        try { editor.updateStock(m, o, Integer.parseInt(sc.nextLine())); }
                        catch (Exception e) { System.out.println("Invalid number"); }

                    } else if (choice.equals("4")) {
                        // --- RECORD NEW SALE ---
                        System.out.println("\n--- New Sale ---");
                        System.out.print("Sale ID (e.g. S01): "); String sId = sc.nextLine();
                        System.out.print("Customer Name: "); String cName = sc.nextLine();
                        System.out.print("Model: "); String mName = sc.nextLine();
                        System.out.print("Quantity: ");
                        int qty = Integer.parseInt(sc.nextLine());
                        String date = LocalDate.now().toString();

                        // Add sale using the nested class
                        data.addSale(new EditService.Transaction(sId, cName, mName, qty, date));
                        System.out.println("Sale Recorded.");

                    } else if (choice.equals("5")) {
                        // --- EDIT SALES INFO ---
                        System.out.println("\n--- Edit Sales Info ---");
                        System.out.print("Enter Sale ID to edit: ");
                        String targetID = sc.nextLine();

                        EditService.Transaction t = editor.getSale(targetID);

                        if (t != null) {
                            System.out.println("Found: " + t.getCustomerName() + " bought " + t.getQuantity() + "x " + t.getModelName());
                            System.out.println("1. Edit Customer Name");
                            System.out.println("2. Edit Quantity");
                            System.out.print("Choice: ");
                            String type = sc.nextLine();

                            if (type.equals("1")) {
                                System.out.print("New Name: ");
                                editor.updateSaleCustomer(targetID, sc.nextLine());
                            } else if (type.equals("2")) {
                                System.out.print("New Qty: ");
                                try { editor.updateSaleQuantity(targetID, Integer.parseInt(sc.nextLine())); }
                                catch(Exception e) { System.out.println("Invalid number"); }
                            }
                        } else {
                            System.out.println("Sale ID not found.");
                        }

                    } else if (choice.equals("6")) {
                        auth.logOut();
                    }
                }
            } else {
                System.out.println("Login Failed.");
            }
        }
    }
}