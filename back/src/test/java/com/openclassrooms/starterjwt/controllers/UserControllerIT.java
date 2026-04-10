package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.TestSecurityConfig;
import com.openclassrooms.starterjwt.services.AuthService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class UserControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private UserService userService;

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
  void testFindByIdShouldReturn400WhenIdIsNotNumeric() throws Exception {
    mockMvc.perform(get("/api/user/invalid")).andExpect(status().isBadRequest());

    verify(userService, never()).findById(anyLong());
  }

  // ========================
  //   DELETE /api/user/{id}
  // ========================

  @Test
  void testDeleteShouldReturn400WhenIdIsNotNumeric() throws Exception {
    mockMvc.perform(delete("/api/user/invalid")).andExpect(status().isBadRequest());

    verify(userService, never()).delete(anyLong());
  }
}
