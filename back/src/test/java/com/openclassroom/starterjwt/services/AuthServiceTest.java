package com.openclassroom.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserRepository userRepository;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private AuthService authService;

  private LoginRequest loginRequest;
  private SignupRequest signupRequest;
  private UserDetailsImpl userDetails;
  private User user;

  @BeforeEach
  void setUp() {
    loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("password123");

    signupRequest = new SignupRequest();
    signupRequest.setEmail("newuser@example.com");
    signupRequest.setFirstName("John");
    signupRequest.setLastName("Doe");
    signupRequest.setPassword("password123");

    userDetails = UserDetailsImpl.builder().id(1L).username("test@example.com").firstName("John").lastName("Doe").password("encodedPassword").build();

    user = new User();
    user.setId(1L);
    user.setEmail("test@example.com");
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setAdmin(false);
  }

  @Test
  void testAuthenticateUserWithNonAdminUser() {
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(jwtUtils.generateJwtToken(authentication)).thenReturn("test-jwt-token");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    JwtResponse response = authService.authenticateUser(loginRequest);

    assertNotNull(response);
    assertEquals("test-jwt-token", response.getToken());
    assertEquals(1L, response.getId());
    assertEquals("test@example.com", response.getUsername());
    assertEquals("John", response.getFirstName());
    assertEquals("Doe", response.getLastName());
    assertFalse(response.getAdmin());

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtUtils).generateJwtToken(authentication);
    verify(userRepository).findByEmail("test@example.com");
  }

  @Test
  void testAuthenticateUserWithAdminUser() {
    user.setAdmin(true);

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(jwtUtils.generateJwtToken(authentication)).thenReturn("test-jwt-token");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    JwtResponse response = authService.authenticateUser(loginRequest);

    assertNotNull(response);
    assertTrue(response.getAdmin());
  }

  @Test
  void testAuthenticateUserUserNotFoundInRepository() {
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(jwtUtils.generateJwtToken(authentication)).thenReturn("test-jwt-token");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

    JwtResponse response = authService.authenticateUser(loginRequest);

    assertNotNull(response);
    assertFalse(response.getAdmin());
  }

  @Test
  void testRegisterUser() {
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
    when(userRepository.save(any(User.class))).thenReturn(user);

    MessageResponse response = authService.registerUser(signupRequest);

    assertNotNull(response);
    assertEquals("User registered successfully!", response.getMessage());

    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));
  }
}