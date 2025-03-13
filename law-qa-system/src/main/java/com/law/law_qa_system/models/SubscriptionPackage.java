package com.law.law_qa_system.models;

public class SubscriptionPackage {

    private String name;        // Tên gói dịch vụ (ví dụ: "CƠ BẢN", "TIẾT KIỆM", "CHUYÊN NGHIỆP")
    private int tokenAmount;    // Số lượng token trong gói
    private String price;       // Giá của gói (ví dụ: "500.000 VNĐ", "2.000.000 VNĐ", "3.000.000 VNĐ")

    // Constructor
    public SubscriptionPackage(String name, int tokenAmount, String price) {
        this.name = name;
        this.tokenAmount = tokenAmount;
        this.price = price;
    }

    // Getter và Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTokenAmount() {
        return tokenAmount;
    }

    public void setTokenAmount(int tokenAmount) {
        this.tokenAmount = tokenAmount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    // Phương thức để hiển thị thông tin gói
    @Override
    public String toString() {
        return "Gói: " + name + ", Token: " + tokenAmount + ", Giá: " + price;
    }
}