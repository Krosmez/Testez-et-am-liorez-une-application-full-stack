package com.openclassroom.starterjwt.controllers;

import com.openclassrooms.starterjwt.controllers.TeacherController;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

  @Mock
  private TeacherService teacherService;

  @Mock
  private TeacherMapper teacherMapper;

  @InjectMocks
  private TeacherController teacherController;

  private Teacher teacher;
  private TeacherDto teacherDto;

  @BeforeEach
  void setUp() {
    teacher = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();

    teacherDto = new TeacherDto();
    teacherDto.setId(1L);
    teacherDto.setFirstName("John");
    teacherDto.setLastName("Doe");
  }

  // ===========================
  //   GET /api/teacher/{id}
  // ===========================

  @Test
  void testFindByIdShouldReturn200WithTeacherDtoWhenTeacherExists() {
    when(teacherService.findById(1L)).thenReturn(teacher);
    when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

    ResponseEntity<?> response = teacherController.findById("1");

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(teacherDto, response.getBody());
    verify(teacherService, times(1)).findById(1L);
    verify(teacherMapper, times(1)).toDto(teacher);
  }

  @Test
  void testFindByIdShouldReturn404WhenTeacherNotFound() {
    when(teacherService.findById(1L)).thenReturn(null);

    ResponseEntity<?> response = teacherController.findById("1");

    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
    verify(teacherService, times(1)).findById(1L);
    verify(teacherMapper, never()).toDto(any(Teacher.class));
  }

  @Test
  void testFindByIdShouldThrowNumberFormatExceptionWhenIdIsNotNumeric() {
    assertThrows(NumberFormatException.class, () -> teacherController.findById("invalid"));
    verify(teacherService, never()).findById(anyLong());
  }

  // ========================
  //   GET /api/teacher
  // ========================

  @Test
  void testFindAllShouldReturn200WithTeacherDtoList() {
    List<Teacher> teachers = List.of(teacher);
    List<TeacherDto> teacherDtos = List.of(teacherDto);
    when(teacherService.findAll()).thenReturn(teachers);
    when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

    ResponseEntity<?> response = teacherController.findAll();

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(teacherDtos, response.getBody());
    verify(teacherService, times(1)).findAll();
    verify(teacherMapper, times(1)).toDto(teachers);
  }
}
