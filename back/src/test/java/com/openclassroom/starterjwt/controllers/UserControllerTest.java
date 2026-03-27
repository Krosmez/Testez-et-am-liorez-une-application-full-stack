package com.openclassroom.starterjwt.controllers;

import com.openclassrooms.starterjwt.controllers.UserController;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  private UserService userService;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserController userController;

  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    user = User.builder().id(1L).email("test@test.com").firstName("Test").lastName("User").password("encodedPassword").admin(false).build();

    userDto = new UserDto();
    userDto.setId(1L);
    userDto.setEmail("test@test.com");
    userDto.setFirstName("Test");
    userDto.setLastName("User");
    userDto.setAdmin(false);
  }

  // ========================
  //   GET /api/user/{id}
  // ========================

  @Test
  void testFindByIdShouldReturn200WithUserDtoWhenUserExists() {
    when(userService.findById(1L)).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(userDto);

    ResponseEntity<?> response = userController.findById("1");

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(userDto, response.getBody());
    verify(userService, times(1)).findById(1L);
    verify(userMapper, times(1)).toDto(user);
  }

  @Test
  void testFindByIdShouldReturn404WhenUserNotFound() {
    when(userService.findById(1L)).thenReturn(null);

    ResponseEntity<?> response = userController.findById("1");

    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
    verify(userService, times(1)).findById(1L);
    verify(userMapper, never()).toDto(any(User.class));
  }

  @Test
  void testFindByIdShouldThrowNumberFormatExceptionWhenIdIsNotNumeric() {
    assertThrows(NumberFormatException.class, () -> userController.findById("invalid"));
    verify(userService, never()).findById(anyLong());
  }

  // ========================
  //   DELETE /api/user/{id}
  // ========================

  @Test
  void testDeleteShouldReturn200WhenDeletionIsSuccessful() {
    doNothing().when(userService).delete(1L);

    ResponseEntity<?> response = userController.save("1");

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    verify(userService, times(1)).delete(1L);
  }

  @Test
  void testDeleteShouldThrowNotFoundExceptionWhenUserDoesNotExist() {
    doThrow(new NotFoundException()).when(userService).delete(1L);

    assertThrows(NotFoundException.class, () -> userController.save("1"));
    verify(userService, times(1)).delete(1L);
  }

  @Test
  void testDeleteShouldThrowUnauthorizedExceptionWhenUserIsNotOwner() {
    doThrow(new UnauthorizedException()).when(userService).delete(1L);

    assertThrows(UnauthorizedException.class, () -> userController.save("1"));
    verify(userService, times(1)).delete(1L);
  }

  @Test
  void testDeleteShouldThrowNumberFormatExceptionWhenIdIsNotNumeric() {
    assertThrows(NumberFormatException.class, () -> userController.save("invalid"));
    verify(userService, never()).delete(anyLong());
  }
}
