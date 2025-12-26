import model.Employee;
import service.*;
import data.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize Data
        dataStorage data = new dataStorage();
        data.loadData();

        // 2. Initialize Services
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
                    // ==========================================
                    //              MAIN MENU
                    // ==========================================
                    System.out.println("\n===== MAIN MENU =====");
                    System.out.println("1. Register New Employee");
                    System.out.println("2. Edit Information");
                    System.out.println("3. Log Out");
                    System.out.print("Choice: ");

                    String mainChoice = sc.nextLine();

                    if (mainChoice.equals("1")) {
                        if (currentUser.getRole().equalsIgnoreCase("Manager")) {
                            System.out.println("Enter Name, ID, Pass, Role:");
                            auth.uniqueEmployee(sc.nextLine(), sc.nextLine(), sc.nextLine(), sc.nextLine(), data.getEmployees());
                        } else System.out.println("Access Denied");

                    } else if (mainChoice.equals("2")) {
                        // ==========================================
                        //        SUB-MENU: EDIT INFORMATION
                        // ==========================================
                        boolean editing = true;
                        while(editing) {
                            System.out.println("\n--- Edit Information Menu ---");
                            System.out.println("1. Edit Employee Profile");
                            System.out.println("2. Edit Stock Quantity");
                            System.out.println("3. Record New Sale");
                            System.out.println("4. Edit Sales Information");
                            System.out.println("5. Back to Main Menu");
                            System.out.print("Select: ");
                            String sub = sc.nextLine();

                            // Use the clean handlers from EditService
                            if(sub.equals("1")) editor.handleEditProfile(sc);
                            else if(sub.equals("2")) editor.handleEditStock(sc);
                            else if(sub.equals("3")) editor.handleRecordSale(sc, currentUser);
                            else if(sub.equals("4")) editor.handleEditSaleInfo(sc);
                            else if(sub.equals("5")) editing = false;
                            else System.out.println("Invalid.");
                        }

                    } else if (mainChoice.equals("3")) {
                        auth.logOut();
                    }
                }
            } else {
                System.out.println("Login Failed.");
            }
        }
    }
}