package org.example;

import org.example.Controller.UserController;
import org.example.Service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserController userController;

    @Mock
    private UserService mockUserService;

    @Mock
    private PrintWriter mockPrintWriter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
        userController = new UserController();  // Controller instantiation
        userController.userService = mockUserService;  // Injecting mock service
    }


    @Test
    public void testRegisterUser_Success() throws IOException {
        // Given
        String payload = "{\"username\": \"testuser\", \"password\": \"password123\"}";
        String response = "User registered successfully: testuser";

        // When
        when(mockUserService.registerUser(payload)).thenReturn(response);

        // Call the register method
        userController.registerUser(payload, mockPrintWriter);

        // Then
        verify(mockPrintWriter, times(1)).println("HTTP/1.1 201 Created");  // Verify 201 Created status
        verify(mockPrintWriter, times(1)).println("Content-Type: application/json");  // Verify Content-Type header
        verify(mockPrintWriter, times(1)).println(response);  // Verify the response content
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() throws IOException {
        // Given
        String payload = "{\"username\": \"existinguser\", \"password\": \"password123\"}";
        String response = "User already exists: existinguser";

        // When
        when(mockUserService.registerUser(payload)).thenReturn(response);

        // Call the register method
        userController.registerUser(payload, mockPrintWriter);

        // Then
        verify(mockPrintWriter, times(1)).println("HTTP/1.1 409 User already exists");  // Verify 409 Conflict status
        verify(mockPrintWriter, times(1)).println("Content-Type: application/json");  // Verify Content-Type header
        verify(mockPrintWriter, times(1)).println(response);  // Verify the response content
    }

    @Test
    public void testRegisterUser_Failure() throws IOException {
        // Given
        String payload = "{\"username\": \"testuser\", \"password\": \"password123\"}";
        String response = "Failed to register user: Invalid data";

        // When
        when(mockUserService.registerUser(payload)).thenReturn(response);

        // Call the register method
        userController.registerUser(payload, mockPrintWriter);

        // Then
        verify(mockPrintWriter, times(1)).println("HTTP/1.1 400 Bad Request");  // Verify 400 Bad Request status
        verify(mockPrintWriter, times(1)).println("Content-Type: application/json");  // Verify Content-Type header
        verify(mockPrintWriter, times(1)).println(response);  // Verify the response content
    }

    @Test
    public void testLoginUser_Success() throws IOException {
        // Given
        String payload = "{\"username\": \"testuser\", \"password\": \"password123\"}";
        String response = "{\"token\": \"valid_token\"}";

        // When
        when(mockUserService.loginUser(payload)).thenReturn(response);

        // Call the login method
        userController.loginUser(payload, mockPrintWriter);

        // Then
        verify(mockPrintWriter, times(1)).println("HTTP/1.1 200 OK");  // Verify 200 OK status
        verify(mockPrintWriter, times(1)).println("Content-Type: application/json");  // Verify Content-Type header
        verify(mockPrintWriter, times(1)).println(response);  // Verify the response content
    }

    @Test
    public void testLoginUser_Failure() throws IOException {
        // Given
        String payload = "{\"username\": \"testuser\", \"password\": \"wrongpassword\"}";
        String response = "{\"error\": \"Invalid credentials\"}";

        // When
        when(mockUserService.loginUser(payload)).thenReturn(response);

        // Call the login method
        userController.loginUser(payload, mockPrintWriter);

        // Then
        verify(mockPrintWriter, times(1)).println("HTTP/1.1 401 Login failed");  // Verify 401 Unauthorized status
        verify(mockPrintWriter, times(1)).println("Content-Type: application/json");  // Verify Content-Type header
        verify(mockPrintWriter, times(1)).println(response);  // Verify the response content
    }
}
