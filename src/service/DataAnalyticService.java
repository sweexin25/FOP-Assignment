package service;

import model.Sale;
import java.util.*;

public class DataAnalyticService {


    public void generateSalesAnalytics(List<Sale> allSales) {
        if (allSales == null || allSales.isEmpty()) {
            System.out.println("No sales data available for analysis.");
            return;
        }

        double totalRevenue = 0;
        Map<String, Integer> modelCountMap = new HashMap<>();
        // using string for dates since Sale.java stores date as a string
        Set<String> uniqueDays = new HashSet<>();

        for (Sale sale : allSales) {
            // calculate total revenue using getSubtotal() from Sale.java
            totalRevenue += sale.getSubtotal();
            uniqueDays.add(sale.getDate());

            // track quantity per model for "Most Sold Product"
            for (String entry : sale.getWatchModel()) {
                String[] parts = entry.split(":");
                String modelName = parts[0];
                int quantity = 1; // Default if no quantity specified
                if (parts.length > 1) {
                    try {
                        quantity = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        quantity = 1;
                    }
                }
                modelCountMap.put(modelName, modelCountMap.getOrDefault(modelName, 0) + quantity);
            }
        }

        String mostSoldModel = Collections.max(modelCountMap.entrySet(), Map.Entry.comparingByValue()).getKey();

        double averageDailyRevenue = totalRevenue / uniqueDays.size();

        displayAnalytics(totalRevenue, mostSoldModel, averageDailyRevenue);
    }

    private void displayAnalytics(double total, String topModel, double avg) {
        System.out.println("\n========= SALES DATA ANALYTICS =========");
        System.out.printf("Total Cumulative Revenue : RM%.2f\n", total);
        System.out.println("Most Popular Model       : " + topModel);
        System.out.printf("Average Daily Revenue    : RM%.2f\n", avg);
        System.out.println("========================================");
    }
}
