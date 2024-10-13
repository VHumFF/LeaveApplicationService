package com.dcom.services;

import com.dcom.DataRetrievalInterface;
import com.dcom.dataModel.Employee;
import com.dcom.dataModel.LeaveApplication;
import com.dcom.dataModel.UserSessionInfo;
import com.dcom.rmi.LeaveApplicationService;
import com.dcom.serviceLocator.ServiceLocator;
import com.dcom.utils.JWTUtil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class LeaveApplicationServiceImpl extends UnicastRemoteObject implements LeaveApplicationService {
    public LeaveApplicationServiceImpl() throws RemoteException {
        super();
    }


    public Map.Entry<Boolean, String> applyLeave(String token, Date date, int numberOfDays, String type, String reason) {
        boolean isSuccess = false;
        String message;

        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        DataRetrievalInterface dbService = ServiceLocator.getDbService();

        if (userSessionInfo != null) {
            try {
                Employee employee = dbService.retrieveEmployee(userSessionInfo.getUserId());

                if (numberOfDays <= 0) {
                    message = "Number of days must be greater than zero.";
                    return new AbstractMap.SimpleEntry<>(isSuccess, message);
                }

                if (type.equals(LeaveApplication.paidLeave) && employee.getAvailablePaidLeave() < numberOfDays) {
                    message = "Insufficient paid leave balance.";
                    return new AbstractMap.SimpleEntry<>(isSuccess, message);
                }


                LeaveApplication leaveApplication = new LeaveApplication();
                leaveApplication.setUserId(userSessionInfo.getUserId());
                leaveApplication.setDate(date);
                leaveApplication.setNumberOfDays(numberOfDays);
                leaveApplication.setType(type);
                leaveApplication.setReason(reason);

                dbService.createLeaveApplication(leaveApplication);


                if (type.equals(LeaveApplication.paidLeave)) {
                    employee.setAvailablePaidLeave(employee.getAvailablePaidLeave() - numberOfDays);
                    dbService.updateEmployee(employee);
                }

                isSuccess = true;
                message = "Leave application submitted successfully.";
                return new AbstractMap.SimpleEntry<>(isSuccess, message);
            } catch (Exception e) {
                System.out.println("Error occurred while retrieving employee information: " + e.getMessage());
                message = "An error occurred while applying for leave.";
                return new AbstractMap.SimpleEntry<>(isSuccess, message);
            }

        } else {
            System.out.println("Invalid or expired token: Unable to validate user session.");
            message = "Invalid request.";
            return new AbstractMap.SimpleEntry<>(false, message);
        }
    }

    public Map.Entry<Boolean, String> deleteLeaveApplication(String token, int leaveApplicationId) {
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        DataRetrievalInterface dbService = ServiceLocator.getDbService();

        if (userSessionInfo == null) {
            return new AbstractMap.SimpleEntry<>(false, "Invalid or expired token.");
        }

        try {
            LeaveApplication leaveApplicationToDelete = dbService.getLeaveApplicationByLeaveApplicationId(leaveApplicationId);

            if (leaveApplicationToDelete == null) {
                return new AbstractMap.SimpleEntry<>(false, "Leave application not found.");
            }

            if (userSessionInfo.getUserId() != leaveApplicationToDelete.getUserId()) {
                return new AbstractMap.SimpleEntry<>(false, "User do not have permission to delete this leave application.");
            }

            if (leaveApplicationToDelete.getStatus().equals("Approved") || leaveApplicationToDelete.getStatus().equals("Rejected")) {
                return new AbstractMap.SimpleEntry<>(false, "Cannot delete an approved or rejected leave application.");
            }

            dbService.deleteLeaveApplication(leaveApplicationId);

            if (leaveApplicationToDelete.getType().equals(LeaveApplication.paidLeave)) {
                Employee employee = dbService.retrieveEmployee(userSessionInfo.getUserId());
                employee.setAvailablePaidLeave(employee.getAvailablePaidLeave() + 1);
                dbService.updateEmployee(employee);
            }

            return new AbstractMap.SimpleEntry<>(true, "Leave application deleted successfully.");

        } catch (Exception e) {
            System.out.println("An error occurred while retrieving leave application: " + e.getMessage());
            return new AbstractMap.SimpleEntry<>(false, "An error occurred while deleting the leave application.");
        }
    }

    public Map.Entry<Boolean, String> processLeaveApplication(String token, int leaveApplicationId, String status) {
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        DataRetrievalInterface dbService = ServiceLocator.getDbService();

        if (userSessionInfo == null) {
            return new AbstractMap.SimpleEntry<>(false, "Invalid or expired token.");
        }

        try {
            LeaveApplication leaveApplication = dbService.getLeaveApplicationByLeaveApplicationId(leaveApplicationId);
            if (leaveApplication == null) {
                return new AbstractMap.SimpleEntry<>(false, "Leave application not found.");
            }

            if (!userSessionInfo.getUserType().equals("HR")) {
                return new AbstractMap.SimpleEntry<>(false, "User do not have permission to process this leave application.");
            }

            if (!leaveApplication.getStatus().equals("Pending")) {
                return new AbstractMap.SimpleEntry<>(false, "Leave application must be in 'Pending' status to be processed.");
            }

            if (!status.equals("Approved") && !status.equals("Rejected")) {
                return new AbstractMap.SimpleEntry<>(false, "Invalid status provided. Must be 'Approved' or 'Rejected'.");
            }

            leaveApplication.setStatus(status);
            dbService.updateLeaveApplication(leaveApplication);

            return new AbstractMap.SimpleEntry<>(true, "Leave application processed successfully.");

        } catch (Exception e) {
            System.out.println("An error occurred while processing the leave application: " + e.getMessage());
            return new AbstractMap.SimpleEntry<>(false, "An error occurred while processing the leave application.");
        }
    }

    public List<LeaveApplication> retrieveLeaveApplicationForEmployee(String token){
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        DataRetrievalInterface dbService = ServiceLocator.getDbService();
        if (userSessionInfo == null) {
            System.out.println("Invalid or expired token");
            return null;
        }
        List<LeaveApplication> leaveApplicationList;
        try{
            leaveApplicationList = dbService.retrieveLeaveApplicationByUserId(userSessionInfo.getUserId());
        }catch (Exception e){
            System.out.println("An error occurred while retrieving leave application.");
            return null;
        }

        return leaveApplicationList;
    }

    public List<LeaveApplication> retrieveLeaveApplicationForAllEmployee(String token){
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        DataRetrievalInterface dbService = ServiceLocator.getDbService();
        if (userSessionInfo == null) {
            System.out.println("Invalid or expired token");
            return null;
        }
        if (!userSessionInfo.getUserType().equals("HR")) {
            System.out.println("User do not have permission to perform this action");
            return null;
        }

        List<LeaveApplication> leaveApplicationList;
        try{
            leaveApplicationList = dbService.retrieveLeaveApplication();
        }catch (Exception e){
            System.out.println("An error occurred while retrieving leave application");
            return null;
        }

        return leaveApplicationList;
    }











}
