package com.dcom;

import com.dcom.rmi.LeaveApplicationService;
import com.dcom.services.LeaveApplicationServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Main {
    public static void main(String[] args) {
        try {
            LeaveApplicationService leaveApplicationService = new LeaveApplicationServiceImpl();
            Registry registry = LocateRegistry.createRegistry(8083);
            registry.rebind("leaveApplicationService", leaveApplicationService);
            System.out.println("Leave Application Service is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}