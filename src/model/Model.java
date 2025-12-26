package model;

import java.util.HashMap;
import java.util.Map;

public class Model {
    private String modelName;
    private double price;
    // Store stock for multiple outlet
    private Map<String, Integer> stockMap;

    public Model(String modelName, double price) {
        this.modelName = modelName;
        this.price = price;
        this.stockMap = new HashMap<>();
    }

    public String getModelName() { return modelName; }
    public double getPrice() { return price; }

    // Get quantity for a specific outlet
    public int getQuantity(String outletID) {
        if (outletID == null) return 0;
        return stockMap.getOrDefault(outletID.trim().toUpperCase(), 0);
    }

    // Set quantity for a specific outlet
    public void setQuantity(String outletID, int quantity) {
        if (outletID != null) {
            stockMap.put(outletID.trim().toUpperCase(), quantity);
        }
    }
}