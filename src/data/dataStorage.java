package data;
import model.*;

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

    // method to overwrite employees file for edit
    public void saveAllEmployees() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("employees.csv"))) {
            writer.println("ID,Name,Role,Pass");
            for (Employee e : employeeList) {
                writer.println(e.getId() + "," + e.getName() + "," + e.getRole() + "," + e.getPassword());
            }
        } catch (IOException e) {
            System.out.println("Failed to update employees.");
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
        this.models.clear(); //clears java memory, so that user no need to rerun program
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

    public void recordSale(Sale sale){
        try(PrintWriter writer = new PrintWriter(new FileWriter("sales.csv",true))){
            if(sale.salesToCSV() != null){
                writer.print(sale.salesToCSV());
                writer.println();
            }
        }catch(IOException e){
            System.out.println("Error recording sale: " + e.getMessage());
        }
    }

    //method to overwrite sales file for edit
    public void saveAllSales(List<Sale> sales) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("sales.csv"))) {
            writer.println("Date,Time,Customer,Items,Subtotal,Employee,Method,Status");
            for (Sale s : sales) {
                if (s.salesToCSV() != null) {
                    writer.println(s.salesToCSV());
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating sales file: " + e.getMessage());
        }
    }

    public ArrayList<Sale>loadSale(){
        ArrayList<Sale> salesList = new ArrayList<>();
        ArrayList<String> watchModels = null;
        double subtotal = 0;

        try(Scanner reader = new Scanner(new File("sales.csv"))){
            reader.nextLine();
            while(reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                String[] data = line.split(",");

                if (data.length >= 7) {
                    String[]tempArray = data[3].split("\\|");
                    subtotal = Double.parseDouble(data[4]);
                    watchModels = new ArrayList<>(Arrays.asList(tempArray));
                }
                Sale sales = new Sale(data[0], data[1], data[2], watchModels, subtotal, data[5], data[6], data[7]);
                salesList.add(sales);
            }

        }catch(FileNotFoundException e){
            System.out.println("File not found: " + e.getMessage());
        }
        return salesList;
    }
}