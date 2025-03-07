package org.example.Service;

import org.example.Model.User;
import org.example.Repository.UserRepository;
import org.example.TokenManager;
import org.example.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private TokenManager mockTokenManager;

    @Mock
    private User mockUser;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService();

        // Inject mocks into the service
        userService.userRepository = mockUserRepository;
        userService.tokenManager = mockTokenManager;
    }

    @Test
    public void testRegisterUser_Success() throws IOException {
        // Given
        String payload = "{\"username\": \"newuser\", \"password\": \"password123\"}";
        User newUser = new User("newuser", "password123");

        // Mock the behavior of the user repository
        when(mockUserRepository.addUser(any(User.class))).thenReturn(true);

        // When
        String response = userService.registerUser(payload);

        // Then
        assertTrue(response.contains("User registered successfully"));
        verify(mockUserRepository, times(1)).addUser(newUser);  // Verify addUser was called
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() throws IOException {
        // Given
        String payload = "{\"username\": \"existinguser\", \"password\": \"password123\"}";
        User existingUser = new User("existinguser", "password123");

        // Mock the behavior of the user repository
        when(mockUserRepository.addUser(any(User.class))).thenReturn(false);

        // When
        String response = userService.registerUser(payload);

        // Then
        assertTrue(response.contains("User already exists"));
        verify(mockUserRepository, times(1)).addUser(existingUser);  // Verify addUser was called
    }

    @Test
    public void testLoginUser_Success() throws IOException {
        // Given
        String payload = "{\"username\": \"testuser\", \"password\": \"password123\"}";
        User user = new User("testuser", "password123");

        // Mock user validation and token generation
        when(mockUserRepository.validateUser(user)).thenReturn(true);
        when(mockTokenManager.generateToken("testuser")).thenReturn("valid_token");

        // When
        String response = userService.loginUser(payload);

        // Then
        assertTrue(response.contains("token"));
        assertTrue(response.contains("valid_token"));
        verify(mockUserRepository, times(1)).validateUser(user);  // Verify validateUser was called
        verify(mockTokenManager, times(1)).generateToken("testuser");  // Verify token generation
    }

    @Test
    public void testLoginUser_InvalidCredentials() throws IOException {
        // Given
        String payload = "{\"username\": \"testuser\", \"password\": \"wrongpassword\"}";
        User user = new User("testuser", "wrongpassword");

        // Mock user validation failure
        when(mockUserRepository.validateUser(user)).thenReturn(false);

        // When
        String response = userService.loginUser(payload);

        // Then
        assertTrue(response.contains("Invalid credentials"));
        verify(mockUserRepository, times(1)).validateUser(user);  // Verify validateUser was called
    }

    @Test
    public void testUpdateUserData_Success() throws IOException {
        // Given
        String username = "testuser";
        String payload = "{\"password\": \"newpassword\", \"name\": \"New Name\", \"bio\": \"New Bio\"}";
        User existingUser = new User("testuser", "oldpassword");
        User updatedUser = new User("testuser", "newpassword");
        updatedUser.setName("New Name");
        updatedUser.setBio("New Bio");

        // Mock repository behavior
        when(mockUserRepository.getUserByUsername(username)).thenReturn(existingUser);
        when(mockUserRepository.updateUser(updatedUser)).thenReturn(true);

        // When
        String response = userService.updateUserData(username, payload);

        // Then
        assertTrue(response.contains("New Name"));
        assertTrue(response.contains("New Bio"));
        verify(mockUserRepository, times(1)).getUserByUsername(username);  // Verify getUserByUsername was called
        verify(mockUserRepository, times(1)).updateUser(updatedUser);  // Verify updateUser was called
    }

    @Test
    public void testUpdateUserData_UserNotFound() throws IOException {
        // Given
        String username = "nonexistentuser";
        String payload = "{\"password\": \"newpassword\", \"name\": \"New Name\"}";

        // Mock repository behavior
        when(mockUserRepository.getUserByUsername(username)).thenReturn(null);

        // When
        String response = userService.updateUserData(username, payload);

        // Then
        assertTrue(response.contains("User not found"));
        verify(mockUserRepository, times(1)).getUserByUsername(username);  // Verify getUserByUsername was called
    }

    @Test
    public void testGetUserData_Success() throws IOException {
        // Given
        String username = "testuser";
        User user = new User("testuser", "password123");
        user.setCoins(30);
        user.setElo(150);

        // Mock repository behavior
        when(mockUserRepository.getUserByUsername(username)).thenReturn(user);

        // When
        String response = userService.getUserData(username);

        // Then
        assertTrue(response.contains("\"username\": \"testuser\""));
        assertTrue(response.contains("\"coins\": 30"));
        assertTrue(response.contains("\"elo\": 150"));
    }

    @Test
    public void testGetUserData_UserNotFound() throws IOException {
        // Given
        String username = "nonexistentuser";

        // Mock repository behavior
        when(mockUserRepository.getUserByUsername(username)).thenReturn(null);

        // When
        String response = userService.getUserData(username);

        // Then
        assertTrue(response.contains("User not found"));
        verify(mockUserRepository, times(1)).getUserByUsername(username);  // Verify getUserByUsername was called
    }
}
