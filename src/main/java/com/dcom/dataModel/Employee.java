package com.dcom.dataModel;

import java.io.Serializable;

public class Employee implements Serializable{
    private static final long serialVersionUID = 1L;

    private int userId;
    private String name;
    private double salary;
    private int totalDaysOfWork;
    private int availablePaidLeave;

    // Constructor
    public Employee(int userId, String name, double salary, int totalDaysOfWork, int availablePaidLeave) {
        this.userId = userId;
        this.name = name;
        this.salary = salary;
        this.totalDaysOfWork = totalDaysOfWork;
        this.availablePaidLeave = availablePaidLeave;
    }

    // Constructor with default values
    public Employee(int userId) {
        this(userId, "unknown", 0, 20, 10); // Default values for name, salary, etc.
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }

    public int getTotalDaysOfWork() {
        return totalDaysOfWork;
    }

    public int getAvailablePaidLeave() {
        return availablePaidLeave;
    }

    public void setAvailablePaidLeave(int availablePaidLeave){
        this.availablePaidLeave = availablePaidLeave;
    }

}

