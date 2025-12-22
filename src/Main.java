import model.Employee;
import service.*;
import data.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        dataStorage data = new dataStorage();
        data.loadData();

        AuthService auth = new AuthService(data);
        EditService editService = new EditService(data);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Employee Login ===");
            System.out.print("Enter User ID: ");
            String id = sc.nextLine().trim();
            System.out.print("Enter Password: ");
            String pass = sc.nextLine().trim();

            if (auth.login(id, pass, data.getEmployees())) {
                System.out.println("Login Successful!");
                Employee user = auth.getCurrentUser();
                System.out.println("Welcome, " + user.getName() + " (" + user.getId() + ")");

                while (auth.getCurrentUser() != null) {
                    System.out.println("\n=== Main Menu ===");
                    if (user.getRole().equalsIgnoreCase("Manager")) {
                        System.out.println("1. Register New Employee");
                    } else {
                        System.out.println("1. (Option Unavailable)");
                    }
                    System.out.println("2. Edit Information");
                    System.out.println("3. Log Out");
                    System.out.print("Enter choice: ");

                    int choice = -1;
                    try {
                        choice = sc.nextInt();
                    } catch (Exception e) {
                        // ignore invalid input
                    }
                    sc.nextLine(); // consume newline

                    if (choice == 1) {
                        if (user.getRole().equalsIgnoreCase("Manager")) {
                            System.out.println("=== Register New Employee ===");
                            System.out.print("Name: "); String name = sc.nextLine().trim();
                            System.out.print("ID: "); String newId = sc.nextLine().trim();
                            System.out.print("Pass: "); String newPass = sc.nextLine().trim();
                            System.out.print("Role: "); String newRole = sc.nextLine().trim();
                            auth.uniqueEmployee(newId, name, newRole, newPass, data.getEmployees());
                        } else {
                            System.out.println("Access Denied.");
                        }
                    }
                    else if (choice == 2) {
                        System.out.println("\n=== Edit Information ===");
                        System.out.println("1. Edit Stock Information");
                        System.out.println("2. Edit Employee Name");
                        System.out.println("3. Edit Employee Password");
                        System.out.print("Select number to edit: ");
                        int editChoice = sc.nextInt();
                        sc.nextLine();

                        if (editChoice == 1) {
                            System.out.println("=== Edit Stock Information ===");
                            System.out.print("Enter Model Name: ");
                            String model = sc.nextLine().trim();

                            System.out.print("Enter Outlet ID (Press Enter for C60): ");
                            String outlet = sc.nextLine().trim();
                            if(outlet.isEmpty()) outlet = "C60";

                            // 1. VIEW CURRENT STOCK
                            int currentQty = editService.getCurrentStock(model, outlet);

                            if (currentQty != -1) {
                                System.out.println("Current Stock: " + currentQty);

                                // 2. EDIT STOCK
                                System.out.print("Enter New Stock Value: ");
                                int newQty = sc.nextInt();
                                sc.nextLine();

                                editService.updateStock(model, outlet, newQty);
                            } else {
                                System.out.println("Model not found (or Outlet code invalid).");
                            }
                        }
                        else if (editChoice == 2) {
                            System.out.print("Enter Employee ID: ");
                            String empId = sc.nextLine().trim();
                            System.out.print("Enter New Name: ");
                            String newName = sc.nextLine().trim();
                            editService.updateName(empId, newName);
                        }
                        else if (editChoice == 3) {
                            System.out.print("Enter Employee ID: ");
                            String empId = sc.nextLine().trim();
                            System.out.print("Enter New Password: ");
                            String newPass = sc.nextLine().trim();
                            editService.updatePassword(empId, newPass);
                        }
                    }
                    else if (choice == 3) {
                        auth.logOut();
                    }
                }
            } else {
                System.out.println("Login Failed: Invalid User ID or Password.");
            }
        }
    }
}