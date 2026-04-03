package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("deprecation")
class AuthControllerIT {

  @Autowired
  AuthService authService;

  @Autowired
  PasswordEncoder passEncoder;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;
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
    signupRequest.setPassword("password");
    signupRequest.setFirstName("Test");
    signupRequest.setLastName("User");

    jwtResponse = new JwtResponse("testToken", 1L, "test@test.com", "Test", "User", false);

    messageResponse = new MessageResponse("User registered successfully!");
  }

  // ========================
  //   POST /api/auth/login
  // ========================

  @Test
  void testAuthenticateUserShouldReturn200WithTokenWhenLoginIsSuccessful() throws Exception {

    mockMvc.perform(post("/api/auth/login").contentType("application/json").content(objectMapper.writeValueAsString(loginRequest)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.token").value("testToken"))
           .andExpect(jsonPath("$.id").value(1))
           .andExpect(jsonPath("$.email").value("test@test.com"))
           .andExpect(jsonPath("$.firstName").value("Test"))
           .andExpect(jsonPath("$.lastName").value("User"))
           .andExpect(jsonPath("$.admin").value(false));

    verify(authService, times(1)).authenticateUser(any(LoginRequest.class));
  }

  @Test
  void testAuthenticateUserShouldReturn401WhenCredentialsAreInvalid() throws Exception {
    loginRequest.setPassword("wrongpassword");
    when(authService.authenticateUser(any(LoginRequest.class))).thenThrow(new RuntimeException("Bad credentials"));

    mockMvc.perform(post("/api/auth/login").contentType("application/json").content(objectMapper.writeValueAsString(loginRequest)))
           .andExpect(status().isInternalServerError());

    verify(authService, times(1)).authenticateUser(any(LoginRequest.class));
  }

  @Test
  void testAuthenticateUserShouldReturn400WhenEmailIsInvalid() throws Exception {
    mockMvc.perform(post("/api/auth/login").contentType("application/json").content("{\"email\":\"invalid-email\",\"password\":\"password\"}"))
           .andExpect(status().isBadRequest());

    verify(authService, never()).authenticateUser(any(LoginRequest.class));
  }

  @Test
  void testAuthenticateUserShouldReturn400WhenPasswordIsInvalid() throws Exception {
    mockMvc.perform(post("/api/auth/login").contentType("application/json").content("{\"email\":\"test@test.com\",\"password\":\"short\"}"))
           .andExpect(status().isBadRequest());

    verify(authService, never()).authenticateUser(any(LoginRequest.class));
  }

  @Test
  void testAuthenticateUserShouldReturn400WhenRequestBodyIsEmpty() throws Exception {
    mockMvc.perform(post("/api/auth/login").contentType("application/json").content("{}")).andExpect(status().isBadRequest());

    verify(authService, never()).authenticateUser(any(LoginRequest.class));
  }

  // ========================
  //   POST /api/auth/register
  // ========================

  @Test
  void testRegisterUserShouldReturn200WhenRegistrationIsSuccessful() throws Exception {
    when(authService.registerUser(any(SignupRequest.class))).thenReturn(messageResponse);

    mockMvc.perform(post("/api/auth/register").contentType("application/json")
                                              .content("{\"email\":\"test@test.com\",\"password\":\"password\",\"firstName\":\"Test\"," +
                                                           "\"lastName\":\"User\"}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.message").value("User registered successfully!"));

    verify(authService, times(1)).registerUser(any(SignupRequest.class));
  }

  @Test
  void testRegisterUserShouldReturn400WhenEmailIsInvalid() throws Exception {
    mockMvc.perform(post("/api/auth/register").contentType("application/json")
                                              .content("{\"email\":\"invalid-email\",\"password\":\"password\",\"firstName\":\"Test\"," +
                                                           "\"lastName\":\"User\"}"))
           .andExpect(status().isBadRequest());

    verify(authService, never()).registerUser(any(SignupRequest.class));
  }

  @Test
  void testRegisterUserShouldReturn400WhenPasswordIsInvalid() throws Exception {
    mockMvc.perform(post("/api/auth/register").contentType("application/json")
                                              .content("{\"email\":\"test@test.com\",\"password\":\"short\",\"firstName\":\"Test\"," + "\"lastName" +
                                                           "\":\"User\"}"))
           .andExpect(status().isBadRequest());

    verify(authService, never()).registerUser(any(SignupRequest.class));
  }

  @Test
  void testRegisterUserShouldReturn400WhenFirstNameIsInvalid() throws Exception {
    mockMvc.perform(post("/api/auth/register").contentType("application/json")
                                              .content(
                                                  "{\"email\":\"test@test.com\",\"password\":\"password\",\"firstName\":\"\",\"lastName\":\"User\"}"))
           .andExpect(status().isBadRequest());

    verify(authService, never()).registerUser(any(SignupRequest.class));
  }

  @Test
  void testRegisterUserShouldReturn400WhenLastNameIsInvalid() throws Exception {
    mockMvc.perform(post("/api/auth/register").contentType("application/json")
                                              .content(
                                                  "{\"email\":\"test@test.com\",\"password\":\"password\",\"firstName\":\"Test\",\"lastName\":\"\"}"))
           .andExpect(status().isBadRequest());

    verify(authService, never()).registerUser(any(SignupRequest.class));
  }

  @Test
  void testRegisterUserShouldReturn400WhenRequestBodyIsEmpty() throws Exception {
    mockMvc.perform(post("/api/auth/register").contentType("application/json").content("{}")).andExpect(status().isBadRequest());

    verify(authService, never()).registerUser(any(SignupRequest.class));
  }
}
