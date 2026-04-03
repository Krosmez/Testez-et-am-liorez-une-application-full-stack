package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.AuthService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private AuthService authService;

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
    void testFindByIdShouldReturn200WithUserDtoWhenUserExists() throws Exception {
        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.admin").value(false));

        verify(userService, times(1)).findById(1L);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testFindByIdShouldReturn404WhenUserNotFound() throws Exception {
        when(userService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(1L);
        verify(userMapper, never()).toDto(any(User.class));
    }

    @Test
    void testFindByIdShouldReturn400WhenIdIsNotNumeric() throws Exception {
        mockMvc.perform(get("/api/user/invalid"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).findById(anyLong());
    }

    // ========================
    //   DELETE /api/user/{id}
    // ========================

    @Test
    void testDeleteShouldReturn200WhenDeletionIsSuccessful() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    void testDeleteShouldReturn404WhenUserDoesNotExist() throws Exception {
        doThrow(new NotFoundException()).when(userService).delete(1L);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    void testDeleteShouldReturn401WhenUserIsNotOwner() throws Exception {
        doThrow(new UnauthorizedException()).when(userService).delete(1L);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isUnauthorized());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    void testDeleteShouldReturn400WhenIdIsNotNumeric() throws Exception {
        mockMvc.perform(delete("/api/user/invalid"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).delete(anyLong());
    }
}
