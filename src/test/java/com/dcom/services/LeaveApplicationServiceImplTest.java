package com.dcom.services;

import com.dcom.DataRetrievalInterface;
import com.dcom.dataModel.Employee;
import com.dcom.dataModel.LeaveApplication;
import com.dcom.dataModel.UserSessionInfo;
import com.dcom.rmi.LeaveApplicationService;
import com.dcom.serviceLocator.ServiceLocator;
import com.dcom.utils.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.rmi.RemoteException;
import java.sql.Date;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveApplicationServiceImplTest {

    private LeaveApplicationService leaveApplicationService;

    @Mock
    private UserSessionInfo userSessionInfo;

    @Mock
    private DataRetrievalInterface dbService;

    @Mock
    private Employee employee;

    @Mock
    private LeaveApplication leaveApplication;

    @BeforeEach
    void setUp() throws RemoteException {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Initialize the LeaveApplicationService instance
        leaveApplicationService = new LeaveApplicationServiceImpl();

        // Mock ServiceLocator to return the mocked dbService
        try (MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(dbService);
        }
    }

    @Test
    void applyLeaveTest() throws RemoteException {
        // Setup
        String token = "validToken";
        Date date = Date.valueOf("2024-11-28");
        int numberOfDays = 3;
        String type = LeaveApplication.paidLeave;
        String reason = "Vacation";

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {

            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);

            when(mockDbService.retrieveEmployee(userSessionInfo.getUserId())).thenReturn(employee);
            when(employee.getAvailablePaidLeave()).thenReturn(10); // mock available leave balance

            // When
            Map.Entry<Boolean, String> result = leaveApplicationService.applyLeave(token, date, numberOfDays, type, reason);

            // Then
            assertTrue(result.getKey());
            assertEquals("Leave application submitted successfully.", result.getValue());
            verify(mockDbService).createLeaveApplication(any(LeaveApplication.class));
            verify(mockDbService).updateEmployee(any(Employee.class));
        }


    }

    @Test
    void applyLeave_InsufficientPaidLeaveTest() throws RemoteException {
        // Setup
        String token = "validToken";
        Date date = Date.valueOf("2024-11-28");
        int numberOfDays = 15; // Greater than available paid leave
        String type = LeaveApplication.paidLeave;
        String reason = "Vacation";

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {

            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);

            when(mockDbService.retrieveEmployee(userSessionInfo.getUserId())).thenReturn(employee);
            when(employee.getAvailablePaidLeave()).thenReturn(10); // mock available leave balance

            // When
            Map.Entry<Boolean, String> result = leaveApplicationService.applyLeave(token, date, numberOfDays, type, reason);

            // Then
            assertFalse(result.getKey());
            assertEquals("Insufficient paid leave balance.", result.getValue());
            verify(mockDbService, never()).createLeaveApplication(any(LeaveApplication.class));
        }

    }

    @Test
    void deleteLeaveApplicationTest() throws RemoteException {
        // Setup
        String token = "validToken";
        int leaveApplicationId = 1;

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {

            // Mock the return value of JWTUtil.validateToken
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mock the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);

            // Mocking behavior for leave application
            LeaveApplication leaveApplication = mock(LeaveApplication.class);
            when(mockDbService.getLeaveApplicationByLeaveApplicationId(leaveApplicationId)).thenReturn(leaveApplication);
            when(leaveApplication.getUserId()).thenReturn(1001); // mock userId to match
            when(leaveApplication.getStatus()).thenReturn("Pending");
            when(leaveApplication.getType()).thenReturn(LeaveApplication.paidLeave); // Mock type to avoid null

            // Mock behavior for userSessionInfo
            when(userSessionInfo.getUserId()).thenReturn(1001); // mock the userId in userSessionInfo

            // Mock employee return value
            Employee employee = mock(Employee.class);
            when(mockDbService.retrieveEmployee(1001)).thenReturn(employee);

            // When
            Map.Entry<Boolean, String> result = leaveApplicationService.deleteLeaveApplication(token, leaveApplicationId);

            // Then
            assertTrue(result.getKey());
            assertEquals("Leave application deleted successfully.", result.getValue());

            // Verify method calls
            verify(mockDbService).deleteLeaveApplication(leaveApplicationId);
            verify(mockDbService).updateEmployee(any(Employee.class));
        }
    }



    @Test
    void deleteLeaveApplication_NotFoundTest() throws RemoteException {
        // Setup
        String token = "validToken";
        int leaveApplicationId = 1;
        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {

            // Mock the return value of JWTUtil.validateToken
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mock the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);

            when(mockDbService.getLeaveApplicationByLeaveApplicationId(leaveApplicationId)).thenReturn(null);

            // When
            Map.Entry<Boolean, String> result = leaveApplicationService.deleteLeaveApplication(token, leaveApplicationId);

            // Then
            assertFalse(result.getKey());
            assertEquals("Leave application not found.", result.getValue());
        }
    }

    @Test
    void processLeaveApplicationTest() throws RemoteException {
        // Setup
        String token = "validToken";
        int leaveApplicationId = 1;
        String status = "Approved";

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {

            // Mock the return value of JWTUtil.validateToken
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mock the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);

            when(mockDbService.getLeaveApplicationByLeaveApplicationId(leaveApplicationId)).thenReturn(leaveApplication);
            when(leaveApplication.getStatus()).thenReturn("Pending");
            when(userSessionInfo.getUserType()).thenReturn("HR");

            // When
            Map.Entry<Boolean, String> result = leaveApplicationService.processLeaveApplication(token, leaveApplicationId, status);

            // Then
            assertTrue(result.getKey());
            assertEquals("Leave application processed successfully.", result.getValue());
            verify(mockDbService).updateLeaveApplication(leaveApplication);
        }

    }

    @Test
    void processLeaveApplication_NotHRTest() throws RemoteException {
        // Setup
        String token = "validToken";
        int leaveApplicationId = 1;
        String status = "Approved";

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {

            // Mock the return value of JWTUtil.validateToken
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mock the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);

            when(mockDbService.getLeaveApplicationByLeaveApplicationId(leaveApplicationId)).thenReturn(leaveApplication);
            when(userSessionInfo.getUserType()).thenReturn("Employee");

            // When
            Map.Entry<Boolean, String> result = leaveApplicationService.processLeaveApplication(token, leaveApplicationId, status);

            // Then
            assertFalse(result.getKey());
            assertEquals("User do not have permission to process this leave application.", result.getValue());
        }
    }

    @Test
    void retrieveLeaveApplicationForEmployeeTest() throws RemoteException {
        // Setup
        String token = "validToken";

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {

            // Mock the return value of JWTUtil.validateToken
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mock the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);

            when(mockDbService.retrieveLeaveApplicationByUserId(userSessionInfo.getUserId())).thenReturn(List.of(leaveApplication));

            // When
            List<LeaveApplication> leaveApplications = leaveApplicationService.retrieveLeaveApplicationForEmployee(token);

            // Then
            assertNotNull(leaveApplications);
            assertFalse(leaveApplications.isEmpty());
        }


    }

    @Test
    void retrieveLeaveApplicationForAllEmployeeTest() throws RemoteException {
        // Setup
        String token = "validToken";

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {

            // Mock the return value of JWTUtil.validateToken
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mock the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);

            when(userSessionInfo.getUserType()).thenReturn("HR");
            when(mockDbService.retrieveLeaveApplication()).thenReturn(List.of(leaveApplication));

            // When
            List<LeaveApplication> leaveApplications = leaveApplicationService.retrieveLeaveApplicationForAllEmployee(token);

            // Then
            assertNotNull(leaveApplications);
            assertFalse(leaveApplications.isEmpty());
        }
    }
}
