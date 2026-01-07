package service;

import model.Sale;
import java.util.*;

public class EmployeePerformanceService {

        // Internal helper class to store metrics per employee
        private static class Metric {
            String name;
            double totalSalesValue = 0;
            int transactionCount = 0;

            Metric(String name) { this.name = name; }
        }

        public void displayMetrics(ArrayList<Sale> allSales) {
            if (allSales == null || allSales.isEmpty()) {
                System.out.println("No sales data available for performance tracking.");
                return;
            }

            //Group data by Employee Name
            Map<String, Metric> performanceMap = new HashMap<>();

            for (Sale sale : allSales) {
                String empName = sale.getEmployeeName();
                // Requirement: calculate total sales value and transaction count
                Metric m = performanceMap.getOrDefault(empName, new Metric(empName));
                m.totalSalesValue += sale.getSubtotal();
                m.transactionCount++;
                performanceMap.put(empName, m);
            }

            //Sort by performance descending (highest to lowest)
            List<Metric> sortedList = new ArrayList<>(performanceMap.values());
            sortedList.sort((a, b) -> Double.compare(b.totalSalesValue, a.totalSalesValue));

            //Display in tabular format for clear insight
            System.out.println("\n--------------------------------------------------------------");
            System.out.printf("%-20s | %-15s | %-15s\n", "Employee Name", "Total Sales", "Transactions");
            System.out.println("--------------------------------------------------------------");

            for (Metric m : sortedList) {
                System.out.printf("%-20s | RM%-13.2f | %-15d\n",
                        m.name, m.totalSalesValue, m.transactionCount);
            }
            System.out.println("--------------------------------------------------------------\n");
        }
}

