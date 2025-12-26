package data;
import model.AttendanceLog;
import model.Employee;
import model.Outlet;
import model.WatchModel;

import java.io.*;
import java.util.*;

public class dataStorage {
    private ArrayList<Employee> employeeList = new  ArrayList<>();
    private Outlet [] allOutlet = new Outlet[100];
    private int outletCount =0;

    public void loadData() {
        loadEmployee();
        loadOutlet();
        loadModel();
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
    public void registerEmployee(String newID, String newName, String newRole,String newPass) {
        try (FileWriter writer = new FileWriter("employees.csv",true)){
            writer.write("\n"+newID+","+newName+","+newRole+","+newPass);
            System.out.println("Employee Registered Successfully");
            Employee employee = new Employee(newID,newName,newRole,newPass);
            employeeList.add(employee);
        }catch(IOException e){
            System.out.println("Failed to register employees.");
        }
    }

    public void saveAttendance(AttendanceLog log) {
        try(FileWriter writer = new FileWriter("attendance.csv",true)){
            writer.write("\n"+ log.attendanceToCSV());
        }catch(IOException e){
            System.out.println("Failed to save attendance");
        }
    }

    public ArrayList<Employee> getEmployees() {
        return employeeList;
    }

    public void loadOutlet(){
        try{
            BufferedReader reader = new BufferedReader(new FileReader("outlet.csv"));
            reader.readLine(); //to skip the header
            String line;
            while((line = reader.readLine()) != null && outletCount< allOutlet.length){
                String [] parts =line.split(",");
                if (parts.length >= 2) {
                    allOutlet[outletCount] = new Outlet(parts[0].trim(), parts[1].trim());
                    outletCount++;
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Something went wrong.");;
        }
    }

    public Outlet[] getAllOutlet() {
        return allOutlet;
    }

    public int getOutletCount() {
        return outletCount;
    }

    //load model
    private List<WatchModel> models = new ArrayList<>();
    public void loadModel(){
        try {
            BufferedReader br = new BufferedReader(new FileReader("model.csv"));
            String line = br.readLine(); // header
            String[] headers = line.split(",");
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0].trim();
                double price = Double.parseDouble(parts[1].trim());
                Map<String, Integer> stock = new HashMap<>();
                for (int i = 2; i < parts.length; i++) {
                    stock.put(headers[i].trim(), Integer.parseInt(parts[i].trim()));
                }
                models.add(new WatchModel(name, price, stock));
            }
        } catch (IOException e) {
            System.out.println("Error loading models: " + e.getMessage());
        }
    }

    public void saveModels() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("model.csv"))) {
            pw.print("Model,Price");
            for (Outlet o : allOutlet) {
                if(o!=null){
                    pw.print("," + o.getOutletCode());
                }
            }
            pw.println();
            for (WatchModel m : models) {
                pw.print(m.getName() + "," + m.getPrice());
                for (Outlet o : allOutlet) {
                    if(o!=null){
                        pw.print("," + m.getStock(o.getOutletCode()));
                    }
                }
                pw.println();
            }
        } catch (Exception e) {
            System.err.println("Error saving models: " + e.getMessage());
        }
    }

    public List<WatchModel> getModels() {
        return models;
    }


}
