package data;
import model.Employee;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class dataStorage {
    private ArrayList<Employee> employeeList = new  ArrayList<>();

    public void loadData() {
        loadEmployee();
    }
    private void loadEmployee(){
        try{
            BufferedReader reader = new BufferedReader(new FileReader("employees.csv"));
            reader.readLine(); //to skip the header
            String line;
            while((line = reader.readLine()) != null ){
                String [] parts =line.split(",");
                Employee employee = new Employee(parts[0], parts[1], parts[2], parts[3]);
                employeeList.add(employee);
            }
            System.out.println("Employees Loaded: "+employeeList.size());

        }catch(FileNotFoundException e){
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Something went wrong.");
        }

    }
    public void registerEmployee(String newName, String newID, String newRole,String newPass)  {
        try (FileWriter writer = new FileWriter("employees.csv",true)){
            writer.write(newID+","+newName+","+newRole+","+newPass);
            System.out.println("Employee Registered Successfully");
            Employee employee = new Employee(newID,newName,newRole,newPass);
            employeeList.add(employee);
        }catch(IOException e){
            System.out.println("Failed to register employees.");
        }
    }

    public ArrayList<Employee> getEmployees() {
        return employeeList;
    }
}
