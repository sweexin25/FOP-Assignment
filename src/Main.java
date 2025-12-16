import model.Employee;
import service.*;
import data.*;

import java.sql.SQLOutput;
import java.util.Scanner;

public class Main{
    public static void main(String[] args){

        dataStorage data = new dataStorage();
        data.loadData();

        AuthService auth = new AuthService();
        Scanner sc = new Scanner(System.in);

        System.out.println("===== Employee login =====");
        System.out.print("Enter User ID:");
        String id = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();

        boolean user = auth.login(id, pass, data.getEmployees());

        if (user) {
            System.out.println("\nLogin successful");
            Employee logInUser = auth.getCurrentUser();
            System.out.println("Welcome, " +logInUser.getName() +"("+logInUser.getId()+")");
            if(auth.getCurrentUser().getRole().equalsIgnoreCase("Manager")){
                System.out.println("Do you want to register new Employee? (Y?N)");
                boolean register = sc.nextBoolean();
                if(register){
                    System.out.println("===== Register New Employee =====");
                    System.out.print("Enter Employee Name:");
                    String newName = sc.nextLine();
                    System.out.print("Enter Employee ID:");
                    String newID = sc.nextLine();
                    System.out.println("Set Password:");
                    String newPass = sc.nextLine();
                    System.out.print("Set Role:");
                    String newRole = sc.nextLine();
                    data.registerEmployee(newID, newName, newRole, newPass);
                }
            }
        }else {
            System.out.println("Login failed. Invalid user ID or password!");
        }

    }
}
