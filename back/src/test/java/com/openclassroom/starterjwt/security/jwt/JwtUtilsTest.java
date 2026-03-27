package com.openclassroom.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtUtilsTest {

  private JwtUtils jwtUtils;
  private final String testSecret = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
  private final int testExpirationMs = 3600000;

  @BeforeEach
  public void setup() {
    jwtUtils = new JwtUtils();
    ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testSecret);
    ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", testExpirationMs);
  }

  @Test
  public void testGenerateJwtToken() {
    Authentication authentication = mock(Authentication.class);
    UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("testuser");

    String token = jwtUtils.generateJwtToken(authentication);

    assertNotNull(token);
    assertFalse(token.isEmpty());
  }

  @Test
  public void testGetUserNameFromJwtToken() {
    Authentication authentication = mock(Authentication.class);
    UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("testuser");

    String token = jwtUtils.generateJwtToken(authentication);
    String username = jwtUtils.getUserNameFromJwtToken(token);

    assertEquals("testuser", username);
  }

  @Test
  public void testValidateJwtToken_ValidToken() {
    Authentication authentication = mock(Authentication.class);
    UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("testuser");

    String token = jwtUtils.generateJwtToken(authentication);
    boolean isValid = jwtUtils.validateJwtToken(token);

    assertTrue(isValid);
  }

  @Test
  public void testValidateJwtToken_InvalidSignature() {
    String otherSecret = "fedcba9876543210fedcba9876543210fedcba9876543210fedcba9876543210";
    SecretKey otherKey = Keys.hmacShaKeyFor(otherSecret.getBytes(StandardCharsets.UTF_8));
    String invalidToken = Jwts.builder()
                              .subject("testuser")
                              .issuedAt(new Date())
                              .expiration(new Date(System.currentTimeMillis() + testExpirationMs))
                              .signWith(otherKey)
                              .compact();

    boolean isValid = jwtUtils.validateJwtToken(invalidToken);

    assertFalse(isValid);
  }

  @Test
  public void testValidateJwtToken_ExpiredToken() {
    SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
    String expiredToken = Jwts.builder()
                              .subject("testuser")
                              .issuedAt(new Date(System.currentTimeMillis() - 10000))
                              .expiration(new Date(System.currentTimeMillis() - 5000))
                              .signWith(key)
                              .compact();

    boolean isValid = jwtUtils.validateJwtToken(expiredToken);

    assertFalse(isValid);
  }

  @Test
  public void testValidateJwtToken_MalformedToken() {
    String malformedToken = "malformed.token.string";

    boolean isValid = jwtUtils.validateJwtToken(malformedToken);

    assertFalse(isValid);
  }

  @Test
  public void testValidateJwtToken_EmptyToken() {
    boolean isValid = jwtUtils.validateJwtToken("");

    assertFalse(isValid);
  }

  @Test
  public void testValidateJwtToken_NullToken() {
    boolean isValid = jwtUtils.validateJwtToken(null);

    assertFalse(isValid);
  }
}