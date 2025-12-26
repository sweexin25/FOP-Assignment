package model;

import java.util.HashMap;
import java.util.Map;

public class WatchModel {
        private String name;
        private double price;
        private Map<String, Integer> stockByOutlet = new HashMap<>();

        public WatchModel(String name, double price, Map<String, Integer> stockByOutlet) {
            this.name = name;
            this.price = price;
            this.stockByOutlet.putAll(stockByOutlet);
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getStock(String outletCode) {
            return stockByOutlet.getOrDefault(outletCode, 0);
        }

        public void setStock(String outletCode, int quantity) {
            stockByOutlet.put(outletCode, quantity);
        }
    }


