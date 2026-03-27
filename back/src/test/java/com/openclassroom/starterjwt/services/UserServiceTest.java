package com.openclassroom.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @Mock
  private UserDetails userDetails;

  @InjectMocks
  private UserService userService;

  @BeforeEach
  void setup() {
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void testFindByIdReturnsUser() {
    User user = new User();
    user.setId(1L);
    user.setEmail("test@example.com");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    User result = userService.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  void testFindByIdReturnsNullWhenUserNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    User result = userService.findById(1L);

    assertNull(result);
    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  void testDeleteSuccess() {
    User user = new User();
    user.setId(1L);
    user.setEmail("test@example.com");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("test@example.com");

    userService.delete(1L);

    verify(userRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDeleteThrowsNotFoundExceptionWhenUserNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.delete(1L));
    verify(userRepository, never()).deleteById(any());
  }

  @Test
  void testDeleteThrowsUnauthorizedExceptionWhenUserEmailMismatch() {
    User user = new User();
    user.setId(1L);
    user.setEmail("test@example.com");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("different@example.com");

    assertThrows(UnauthorizedException.class, () -> userService.delete(1L));
    verify(userRepository, never()).deleteById(any());
  }
}