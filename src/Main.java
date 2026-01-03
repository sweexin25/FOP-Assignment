import model.Employee;
import model.Sale;
import service.*;
import data.*;

import java.util.ArrayList;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {

        dataStorage data = new dataStorage();
        data.loadData();

        AuthService auth = new AuthService(data);
        AttendanceService att = new AttendanceService(data);
        StockServiceReplace stock = new StockServiceReplace(data, att);
        SalesServiceReplace sales = new SalesServiceReplace(data, att);
        SearchService search = new SearchService(data);
        EditService edit = new EditService(data);
        DataAnalyticService analysis = new DataAnalyticService();
        SalesFilterService filter = new SalesFilterService();
        EmployeePerformanceService performance = new EmployeePerformanceService();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("===== Employee login =====");
            System.out.print("Enter User ID: ");
            String id = sc.nextLine();
            System.out.print("Enter Password: ");
            String pass = sc.nextLine().trim();

            boolean user = auth.login(id, pass, data.getEmployees());
            if (user) {
                System.out.println("\nLogin successful");
                Employee logInUser = auth.getCurrentUser();
                System.out.println("Welcome, " + logInUser.getName() + "(" + logInUser.getId() + ")");
                while (auth.getCurrentUser() != null) {
                    System.out.println("===== Main Menu =====");
                    if (auth.getCurrentUser().getRole().equalsIgnoreCase("Manager")) {
                        System.out.println("1. Register New Employee");
                    } else {
                        System.out.println("1. (Option Unavailable)");
                    }
                    System.out.println("2. Clock in");
                    System.out.println("3. Clock out");
                    System.out.println("4. Stock Count");
                    System.out.println("5. Stock Movement");
                    System.out.println("6. Sales Record");
                    System.out.println("7. Search Infomation");
                    System.out.println("8. Edit Infomation");
                    if (auth.getCurrentUser().getRole().equalsIgnoreCase("Manager")) {
                        System.out.println("9. View Employee Performance Metrics");
                    } else {
                        System.out.println("9. (Option Unavailable)");
                    }
                    System.out.println("10. View Data Analytics");
                    System.out.println("11. Filter and Sort Sales History");
                    System.out.println("12. Log Out");
                    System.out.println("Enter your choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();
                    if (choice == 1) {
                        if (auth.getCurrentUser().getRole().equalsIgnoreCase("Manager")) {
                            System.out.println("===== Register New Employee =====");
                            System.out.print("Enter Employee Name: ");
                            String newName = sc.nextLine();
                            System.out.print("Enter Employee ID: ");
                            String newID = sc.nextLine();
                            System.out.print("Set Password: ");
                            String newPass = sc.nextLine();
                            System.out.print("Set Role: ");
                            String newRole = sc.nextLine();
                            auth.uniqueEmployee(newID, newName, newRole, newPass, data.getEmployees());
                        }
                        else {
                            System.out.println("Access Denied, You are not Manager");
                        }
                    } else if (choice == 2) {
                        att.clockIn(logInUser);

                    } else if (choice == 3) {
                        att.clockOut(logInUser);

                    } else if (choice == 4) {
                        stock.performStockCount(logInUser);
                    } else if (choice == 5) {
                        stock.stockMovement(logInUser);
                    } else if (choice == 6) {
                        data.loadSale(); //load again so no need rerun program
                        sales.recordSale(logInUser);
                    } else if (choice == 7) {
                        data.loadSale();
                        data.loadModel();
                        search.searchService(logInUser);
                    } else if (choice == 8) {
                        data.loadSale();
                        data.loadModel();
                        edit.handleEditMenu(sc);
                    } else if (choice == 9) {
                        if (auth.getCurrentUser().getRole().equalsIgnoreCase("Manager")) {
                            System.out.println("===== Employee Performance Metrics =====");
                            ArrayList<Sale> allSales = data.loadSale();
                            performance.displayMetrics(allSales);
                        } else {
                            System.out.println("Access Denied: Only managers can view performance metrics.");
                        }

                    } else if (choice == 10) {
                        ArrayList<Sale> allSales = data.loadSale();
                        analysis.generateSalesAnalytics(allSales);
                    } else if (choice == 11) {
                        System.out.print("Enter Start Date (YYYY-MM-DD): ");
                        String startDate = sc.nextLine();

                        System.out.print("Enter End Date (YYYY-MM-DD): ");
                        String endDate = sc.nextLine();

                        System.out.println("Sort by: 1. Date | 2. Amount | 3. Customer Name");
                        System.out.print("Choice: ");
                        int sortType = sc.nextInt();
                        sc.nextLine();
                        ArrayList<Sale> allSales = data.loadSale();
                        filter.filterAndSort(allSales, startDate, endDate, sortType);
                    } else if (choice == 12) {
                        auth.logOut();
                    }

                }
                }else {
                System.out.println("Login failed. Invalid user ID or password!");
            }
        }
    }
}
