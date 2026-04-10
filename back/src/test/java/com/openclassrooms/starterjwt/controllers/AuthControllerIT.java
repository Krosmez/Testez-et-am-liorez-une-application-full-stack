package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.TestSecurityConfig;
import com.openclassrooms.starterjwt.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class AuthControllerIT {

  @Mock
  UserRepository userRepository;

  @Autowired
  AuthService authService;

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
  void testAuthenticateUserShouldReturn401WhenCredentialsAreInvalid() throws Exception {
    loginRequest.setPassword("wrongpassword");
    mockMvc.perform(post("/api/auth/login").contentType("application/json").content(objectMapper.writeValueAsBytes(loginRequest)))
           .andExpect(status().isForbidden());
  }

  @Test
  void testAuthenticateUserShouldReturn400WhenEmailIsInvalid() throws Exception {
    loginRequest.setEmail("invalidemail");
    mockMvc.perform(post("/api/auth/login").contentType("application/json").content(objectMapper.writeValueAsBytes(loginRequest)))
           .andExpect(status().isForbidden());
  }

  @Test
  void testAuthenticateUserShouldReturn400WhenPasswordIsInvalid() throws Exception {
    loginRequest.setPassword("abcd");
    mockMvc.perform(post("/api/auth/login").contentType("application/json").content(objectMapper.writeValueAsBytes(loginRequest)))
           .andExpect(status().isForbidden());
  }

  @Test
  void testAuthenticateUserShouldReturn400WhenRequestBodyIsEmpty() throws Exception {
    mockMvc.perform(post("/api/auth/login").contentType("application/json").content("{}")).andExpect(status().isBadRequest());
  }

  // ========================
  //   POST /api/auth/register
  // ========================

  @Test
  void testRegisterUserShouldReturn200WhenRegistrationIsSuccessful() throws Exception {

    mockMvc.perform(post("/api/auth/register").contentType("application/json").content(objectMapper.writeValueAsString(signupRequest)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.message").value("User registered successfully!"));
  }

  @Test
  void testRegisterUserShouldReturn400WhenEmailIsInvalid() throws Exception {
    loginRequest.setEmail("invalid-email");
    mockMvc.perform(post("/api/auth/register").contentType("application/json").content(objectMapper.writeValueAsString(loginRequest)))
           .andExpect(status().isBadRequest());
  }

  @Test
  void testRegisterUserShouldReturn400WhenPasswordIsInvalid() throws Exception {
    signupRequest.setPassword("abcd");
    mockMvc.perform(post("/api/auth/register").contentType("application/json").content(objectMapper.writeValueAsString(signupRequest)))
           .andExpect(status().isBadRequest());
  }

  @Test
  void testRegisterUserShouldReturn400WhenFirstNameIsInvalid() throws Exception {
    signupRequest.setFirstName("");
    mockMvc.perform(post("/api/auth/register").contentType("application/json").content(objectMapper.writeValueAsString(signupRequest)))
           .andExpect(status().isBadRequest());
  }

  @Test
  void testRegisterUserShouldReturn400WhenLastNameIsInvalid() throws Exception {
    signupRequest.setLastName("");
    mockMvc.perform(post("/api/auth/register").contentType("application/json").content(objectMapper.writeValueAsString(signupRequest)))
           .andExpect(status().isBadRequest());
  }

  @Test
  void testRegisterUserShouldReturn400WhenRequestBodyIsEmpty() throws Exception {
    mockMvc.perform(post("/api/auth/register").contentType("application/json").content("{}")).andExpect(status().isBadRequest());
  }
}
