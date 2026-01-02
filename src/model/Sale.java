package model;

import java.util.ArrayList;

public class Sale {
    private String date;
    private String time;
    private String customerName;
    private ArrayList<String> watchModel;
    private double subtotal;
    private String employeeName;
    private String transactionMethod;
    private String transactionState;

    public Sale(String date, String time, String customerName, ArrayList<String> watchModel,
                double subtotal, String employeeName, String transactionMethod, String transactionState) {
        this.date = date;
        this.time = time;
        this.customerName = customerName;
        this.watchModel = watchModel;
        this.subtotal = subtotal;
        this.employeeName = employeeName;
        this.transactionMethod = transactionMethod;
        this.transactionState = transactionState;
    }

    //getters
    public String getDate(){ return date; }
    public String getTime(){ return time; }
    public String getCustomerName(){ return customerName; }
    public ArrayList<String> getWatchModel() { return watchModel; }
    public double getSubtotal(){ return subtotal; }
    public String getEmployeeName(){ return employeeName; }
    public String getTransactionMethod(){ return transactionMethod; }
    public String getTransactionState(){ return transactionState; }

    //setters
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public void setTransactionMethod(String method) { this.transactionMethod = method; }
    public void setWatchModel(ArrayList<String> watchModel) { this.watchModel = watchModel; }

    public void printReceipt(){
        System.out.println("-------------- Sales Record --------------");
        System.out.println("Customer Name: " + customerName);
        System.out.println("------------------------------------------");
        System.out.printf("%-25s %5s\n", "Model Name", "Qty");
        for (String entry : watchModel) {
            String[] parts = entry.split(":");
            String modelName = parts[0];
            String quantity = parts.length > 1 ? parts[1] : "1";
            System.out.printf("%-25s %5s\n", modelName, quantity);
        }
        System.out.println("------------------------------------------");
        System.out.println("Date: " + date +"\t Time: " + time);
        System.out.println("Subtotal: RM" + subtotal);
        System.out.println("Employee Name: " + employeeName);
        System.out.println("Transaction Method: " + transactionMethod);
        System.out.println("Transaction Status: " + transactionState);
        System.out.println();
    }

    public String salesToCSV(){
        String watchModelStr ="";
        for (int i =0; i<watchModel.size(); i++){
            watchModelStr+= watchModel.get(i);
            if (i<watchModel.size()-1){
                watchModelStr+="|";
            }
        }
        return date+","+time+","+customerName+","+watchModelStr+","+subtotal+","+employeeName+","+transactionMethod+","+transactionState;
    }
}