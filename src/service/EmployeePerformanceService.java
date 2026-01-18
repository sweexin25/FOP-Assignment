package service;

import model.Sale;
import java.util.*;

public class EmployeePerformanceService {

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

            Map<String, Metric> performanceMap = new HashMap<>();

            for (Sale sale : allSales) {
                String empName = sale.getEmployeeName();
                
                Metric m = performanceMap.getOrDefault(empName, new Metric(empName));
                m.totalSalesValue += sale.getSubtotal();
                m.transactionCount++;
                performanceMap.put(empName, m);
            }

            
             List<Metric> sortedList = new ArrayList<>(performanceMap.values());
            sortedList.sort((a, b) -> {
                int result = Double.compare(b.totalSalesValue, a.totalSalesValue);
                if (result != 0) {
                    return result;
                }
                return Integer.compare(b.transactionCount, a.transactionCount);
            });

            
            System.out.println("\n--------------------------------------------------------------");
            System.out.printf("%-35s | %-15s | %-15s\n", "Employee Name", "Total Sales", "Transactions");
            System.out.println("--------------------------------------------------------------");

            for (Metric m : sortedList) {
                System.out.printf("%-35s | RM%-13.2f | %-15d\n",
                        m.name, m.totalSalesValue, m.transactionCount);
            }
            System.out.println("--------------------------------------------------------------\n");
        }
}

