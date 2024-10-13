package com.dcom;


import com.dcom.dataModel.Employee;
import com.dcom.dataModel.LeaveApplication;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DataRetrievalInterface extends Remote {

    Employee retrieveEmployee(int employeeId) throws RemoteException;
    boolean updateEmployee(Employee employee) throws RemoteException;

    boolean createLeaveApplication(LeaveApplication leaveApplication) throws RemoteException;
    List<LeaveApplication> retrieveLeaveApplicationByUserId(int userId) throws RemoteException;
    List<LeaveApplication> retrieveLeaveApplication() throws RemoteException;
    boolean updateLeaveApplication(LeaveApplication leaveApplication) throws RemoteException;
    LeaveApplication getLeaveApplicationByLeaveApplicationId(int leaveApplicationId) throws RemoteException;
    boolean deleteLeaveApplication(int leaveApplicationId) throws RemoteException;
}
