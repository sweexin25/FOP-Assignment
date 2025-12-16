import model.AttendanceLog;
import model.Employee;
import service.*;
import data.*;

import java.sql.SQLOutput;
import java.util.Scanner;

public class Main{
    public static void main(String[] args){

        dataStorage data = new dataStorage();
        data.loadData();

        AuthService auth = new AuthService(data);
        AttendanceService att = new AttendanceService(data);
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("===== Employee login =====");
            System.out.print("Enter User ID: ");
            String id = sc.nextLine();
            System.out.print("Enter Password: ");
            String pass = sc.nextLine();

            boolean user=auth.login(id, pass, data.getEmployees());
            if (user) {
                System.out.println("\nLogin successful");
                Employee logInUser = auth.getCurrentUser();
                System.out.println("Welcome, " +logInUser.getName() +"("+logInUser.getId()+")");
                while (auth.getCurrentUser() != null) {
                    System.out.println("===== Main Menu =====");
                    if (auth.getCurrentUser().getRole().equalsIgnoreCase("Manager")){
                        System.out.println("1. Register New Employee");
                    }else{
                        System.out.println("1. (Option Unavailable)");
                    }
                    System.out.println("2. Clock in");
                    System.out.println("3. Clock out");
                    System.out.println("4. Log Out");
                    System.out.println("Enter your choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();
                    if (choice ==1 ){
                        if(auth.getCurrentUser().getRole().equalsIgnoreCase("Manager")){
                            System.out.println("===== Register New Employee =====");
                            System.out.print("Enter Employee Name: ");
                            String newName = sc.nextLine();
                            System.out.print("Enter Employee ID: ");
                            String newID = sc.nextLine();
                            System.out.print("Set Password: ");
                            String newPass = sc.nextLine();
                            System.out.print("Set Role: ");
                            String newRole = sc.nextLine();
                            auth.uniqueEmployee(newID, newName, newRole, newPass,data.getEmployees());
                        }else{
                            System.out.println("Access Denied, You are not Manager");
                        }
                    }else if (choice ==2){
                        att.clockIn(logInUser);

                    }else if (choice ==3){
                        att.clockOut(logInUser);

                    }else if (choice ==4){
                        auth.logOut();

                    }
                }
            }else {
                System.out.println("Login failed. Invalid user ID or password!");
            }
        }
    }
}
