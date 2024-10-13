package com.dcom.dataModel;

import java.io.Serializable;
import java.sql.Date;

public class LeaveApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    private int leaveApplicationId;
    private int userId;
    private Date date; // Using java.sql.Date for database compatibility
    private int numberOfDays;
    private String type; // e.g., "annual", "sick", etc.
    private String reason;
    private String status; // e.g., "pending", "approved", "rejected"

    public static final String paidLeave = "Paid";
    public static final String unPaidLeave = "Unpaid";

    // Constructor for creating a new leave application
    public LeaveApplication(int userId, Date date, int numberOfDays, String type, String status, String reason) {
        this.userId = userId;
        this.date = date;
        this.numberOfDays = numberOfDays;
        this.type = type;
        this.reason = reason;
        this.status = status;
    }

    public LeaveApplication() {
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    public Date getDate() {
        return date;
    }
    public int getNumberOfDays() {
        return numberOfDays;
    }
    public int getLeaveApplicationId(){return leaveApplicationId;}
    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }
    public String getType() {
        return type;
    }
    public String getReason(){return reason;}

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}
