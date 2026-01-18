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
            Map<String, Double> dailyRevenue = new TreeMap<>();
            Map<String, Double> weeklyRevenue = new TreeMap<>();
            Map<String, Double> monthlyRevenue = new TreeMap<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            WeekFields weekFields = WeekFields.of(Locale.getDefault());

            for (Sale sale : allSales) {
                double subtotal = sale.getSubtotal();
                totalRevenue += subtotal;
                String dateStr = sale.getDate();

                try {
                    LocalDate date = LocalDate.parse(dateStr, formatter);
                    int year = date.getYear();
                    dailyRevenue.put(dateStr, dailyRevenue.getOrDefault(dateStr, 0.0) + subtotal);
                    int weekNum = date.get(weekFields.weekOfYear());
                    String weekKey = String.format("%d-W%02d", year, weekNum);
                    weeklyRevenue.put(weekKey, weeklyRevenue.getOrDefault(weekKey, 0.0) + subtotal);
                    String monthKey = String.format("%d-%02d (%s)", year, date.getMonthValue(), date.getMonth().toString());
                    monthlyRevenue.put(monthKey, monthlyRevenue.getOrDefault(monthKey, 0.0) + subtotal);

                } catch (Exception e) {
                    System.out.println("Warning: Skipping malformed date: " + dateStr);
                }

                for (String entry : sale.getWatchModel()) {
                    String[] parts = entry.split(":");
                    String modelName = parts[0];
                    int quantity = (parts.length > 1) ? Integer.parseInt(parts[1]) : 1;
                    modelCountMap.put(modelName, modelCountMap.getOrDefault(modelName, 0) + quantity);
                }
            }

            String mostSoldModel = Collections.max(modelCountMap.entrySet(), Map.Entry.comparingByValue()).getKey();
            double averageDailyRevenue = totalRevenue / dailyRevenue.size();

            displayAnalytics(totalRevenue, mostSoldModel, averageDailyRevenue, dailyRevenue, weeklyRevenue, monthlyRevenue);
        }

        private void displayAnalytics(double total, String topModel, double avg,Map<String, Double> daily, Map<String, Double> weekly, Map<String, Double> monthly) {
            System.out.println("\n========= GOLDENHOUR SALES ANALYTICS =========");
            System.out.printf("Total Cumulative Revenue : RM%.2f\n", total);
            System.out.println("Most Popular Model       : " + topModel);
            System.out.printf("Average Daily Revenue    : RM%.2f\n", avg);

            System.out.println("\n--- Revenue By Day ---");
            daily.forEach((date, rev) -> System.out.printf("  %s: RM%.2f\n", date, rev));

            System.out.println("\n--- Revenue By Week ---");
            weekly.forEach((week, rev) -> System.out.printf("  %s: RM%.2f\n", week, rev));

            System.out.println("\n--- Revenue By Month ---");
            monthly.forEach((month, rev) -> System.out.printf("  %s: RM%.2f\n", month, rev));
            System.out.println("==============================================");
        }
}
