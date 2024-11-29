package it.unimib.devtrinity.moneymind.domain.model;

public class Transaction {

    private int id;
    private String type;
    private double amount;
    private String date;
    private int categoryId;
    private String description;

    public Transaction(int id, String type, double amount, String date, int categoryId, String description) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.categoryId = categoryId;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
