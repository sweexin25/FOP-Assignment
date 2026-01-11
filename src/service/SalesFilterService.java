package service;

import model.Sale;
import java.util.*;

public class SalesFilterService {
    public void filterAndSort(ArrayList<Sale> allSales, String start, String end, int sortType) {
        ArrayList<Sale> filtered = new ArrayList<>();
        double total = 0;

        
        for (Sale s : allSales) {
            if (s.getDate().compareTo(start) >= 0 && s.getDate().compareTo(end) <= 0) {
                filtered.add(s);
                total += s.getSubtotal(); // Calculate cumulative total
            }
        }

       
        if (sortType == 1) filtered.sort(Comparator.comparing(Sale::getDate));
        else if (sortType == 2) filtered.sort(Comparator.comparingDouble(Sale::getSubtotal));
        else if (sortType == 3) filtered.sort(Comparator.comparing(Sale::getCustomerName));

        
        System.out.printf("%-12s | %-30s | %-10s | %-10s\n", "Date", "Customer", "Amount", "Method");
        for (Sale s : filtered) {
            System.out.printf("%-12s | %-30s | RM%-8.2f | %-10s\n",
                    s.getDate(), s.getCustomerName(), s.getSubtotal(), s.getTransactionMethod());
        }
        System.out.println("Total Cumulative Sales: RM" + total);
    }
}

