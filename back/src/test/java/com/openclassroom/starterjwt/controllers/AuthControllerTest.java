package com.openclassroom.starterjwt.controllers;

import com.openclassrooms.starterjwt.controllers.AuthController;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock
  private AuthService authService;

  @InjectMocks
  private AuthController authController;

  private LoginRequest loginRequest;
  private SignupRequest signupRequest;
  private JwtResponse jwtResponse;
  private MessageResponse messageResponse;

  @BeforeEach
  void setUp() {
    loginRequest = new LoginRequest();
    loginRequest.setEmail("test@test.com");
    loginRequest.setPassword("password");

    signupRequest = new SignupRequest();
    signupRequest.setEmail("test@test.com");
    signupRequest.setFirstName("Test");
    signupRequest.setLastName("User");
    signupRequest.setPassword("password");

    jwtResponse = new JwtResponse("token", 1L, "test@test.com", "Test", "User", false);
    messageResponse = new MessageResponse("User registered successfully!");
  }

  @Test
  void testAuthenticateUserShouldReturnJwtResponse() {
    when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse);

    ResponseEntity<JwtResponse> response = authController.authenticateUser(loginRequest);

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(jwtResponse, response.getBody());
    verify(authService, times(1)).authenticateUser(loginRequest);
  }

  @Test
  void testRegisterUserShouldReturnMessageResponse() {
    when(authService.registerUser(any(SignupRequest.class))).thenReturn(messageResponse);

    ResponseEntity<MessageResponse> response = authController.registerUser(signupRequest);

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(messageResponse, response.getBody());
    verify(authService, times(1)).registerUser(signupRequest);
  }
}