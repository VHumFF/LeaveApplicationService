package com.dcom.serviceLocator;

import com.dcom.DataRetrievalInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServiceLocator {
    private static DataRetrievalInterface dbService;

    private ServiceLocator() {}

    public static DataRetrievalInterface getDbService() {
        if (dbService == null) {
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                dbService = (DataRetrievalInterface) registry.lookup("Server");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dbService;
    }
}
