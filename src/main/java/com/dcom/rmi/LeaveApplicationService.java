package com.dcom.rmi;

import com.dcom.dataModel.LeaveApplication;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface LeaveApplicationService extends Remote {
    Map.Entry<Boolean, String> applyLeave(String token, Date date, int numberOfDays, String type, String reason) throws RemoteException;
    Map.Entry<Boolean, String> deleteLeaveApplication(String token, int leaveApplicationId) throws RemoteException;
    Map.Entry<Boolean, String> processLeaveApplication(String token, int leaveApplicationId, String status) throws RemoteException;
    List<LeaveApplication> retrieveLeaveApplicationForEmployee(String token) throws RemoteException;
    List<LeaveApplication> retrieveLeaveApplicationForAllEmployee(String token) throws RemoteException;
}
