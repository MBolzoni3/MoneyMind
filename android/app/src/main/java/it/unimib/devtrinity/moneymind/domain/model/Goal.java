package it.unimib.devtrinity.moneymind.domain.model;

public class Goal {

    private int id;
    private double targetAmount;
    private double savedAmount;
    private String startDate;
    private String endDate;
    private int categoryId;

    public Goal(int id, double targetAmount, double savedAmount, String startDate, String endDate, int categoryId) {
        this.id = id;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(double savedAmount) {
        this.savedAmount = savedAmount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
