package data;

import model.Employee;
import model.Model;
import service.EditService;
import java.io.*;
import java.util.ArrayList;

public class dataStorage {

    private ArrayList<Employee> employeeList = new ArrayList<>();
    private ArrayList<String> outletCodes = new ArrayList<>();
    private ArrayList<Model> modelList = new ArrayList<>();
    private ArrayList<String> stockHeaders = new ArrayList<>();

    private ArrayList<EditService.Transaction> salesList = new ArrayList<>();

    public void loadData() {
        loadEmployee();
        loadOutlets();
        loadStock();
        loadSales();
    }

    //SALES LOAD/SAVE
    private void loadSales() {
        salesList.clear();
        try {
            File f = new File("sales.csv");
            if(!f.exists()) return;

            BufferedReader reader = new BufferedReader(new FileReader(f));
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Checking for 8 parts (including TotalPrice)
                if (parts.length >= 8) {
                    salesList.add(new EditService.Transaction(
                            parts[0], parts[1], parts[2],
                            Integer.parseInt(parts[3]), parts[4], parts[5], parts[6],
                            Double.parseDouble(parts[7]) //Load Total Price
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading sales.");
        }
    }

    public void saveSales() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("sales.csv"))) {
            writer.write("SaleID,CustomerName,Model,Qty,Date,Outlet,Type,Total");
            writer.newLine();
            for (EditService.Transaction t : salesList) {
                writer.write(t.getSaleID() + "," + t.getCustomerName() + "," + t.getModelName() + "," +
                        t.getQuantity() + "," + t.getDate() + "," + t.getOutletCode() + "," +
                        t.getPaymentType() + "," + t.getTotalPrice()); // <--- Save Total Price
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Couldn't save sales.");
        }
    }

    public void addSale(EditService.Transaction t) {
        salesList.add(t);
        saveSales();
    }

    public ArrayList<EditService.Transaction> getSales() { return salesList; }

    public ArrayList<Employee> getEmployees() { return employeeList; }
    public ArrayList<String> getOutletCodes() { return outletCodes; }
    public ArrayList<Model> getModels() { return modelList; }

    private void loadEmployee() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("employees.csv"));
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) employeeList.add(new Employee(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()));
            }
        } catch (Exception e) {}
    }
    private void loadOutlets() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("outlet.csv"));
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1) outletCodes.add(parts[0].trim());
            }
        } catch (Exception e) {}
    }
    private void loadStock() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("model.csv"));
            String h = reader.readLine();
            if(h!=null) { String[] p=h.split(","); for(int i=2; i<p.length; i++) stockHeaders.add(p[i].trim()); }
            String l;
            while((l=reader.readLine())!=null) {
                String[] p=l.split(",");
                if(p.length>=2) {
                    Model m = new Model(p[0].trim(), Double.parseDouble(p[1].trim()));
                    for(int i=0; i<stockHeaders.size(); i++) if(i+2<p.length) m.setQuantity(stockHeaders.get(i), Integer.parseInt(p[i+2].trim()));
                    modelList.add(m);
                }
            }
        } catch(Exception e) {}
    }
    public void saveStock() {
        try(BufferedWriter w = new BufferedWriter(new FileWriter("model.csv"))) {
            w.write("Model,Price"); for(String s:stockHeaders) w.write(","+s); w.newLine();
            for(Model m:modelList) {
                w.write(m.getModelName()+","+m.getPrice());
                for(String s:stockHeaders) w.write(","+m.getQuantity(s));
                w.newLine();
            }
        } catch(IOException e) {}
    }
    public void registerEmployee(String id, String n, String r, String p) { employeeList.add(new Employee(id,n,r,p)); saveEmployees(); }
    public void saveEmployees() {
        try(BufferedWriter w = new BufferedWriter(new FileWriter("employees.csv"))) {
            w.write("ID,Name,Role,Pass"); w.newLine();
            for(Employee e:employeeList) { w.write(e.getId()+","+e.getName()+","+e.getRole()+","+e.getPassword()); w.newLine(); }
        } catch(IOException e) {}
    }
}