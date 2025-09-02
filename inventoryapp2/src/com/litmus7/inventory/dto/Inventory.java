
package com.litmus7.inventory.dto;

public class Inventory {
    private String sku;
    private String productName;
    private int quantity;
    private double price;

    public Inventory(String sku, String productName, int quantity, double price) {
        this.sku = sku;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getSku() { return sku; }
       public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
      public double getPrice() { return price; }
    @Override
    public String toString() {
        return "InventoryItemDTO{" +
                "sku='" + sku + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
