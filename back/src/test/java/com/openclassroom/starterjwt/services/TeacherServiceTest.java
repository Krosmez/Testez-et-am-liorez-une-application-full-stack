package com.openclassroom.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

  @Mock
  private TeacherRepository teacherRepository;

  @InjectMocks
  private TeacherService teacherService;

  private Teacher teacher1;
  private Teacher teacher2;

  @BeforeEach
  void setUp() {
    teacher1 = new Teacher();
    teacher1.setId(1L);
    teacher1.setFirstName("John");
    teacher1.setLastName("Doe");

    teacher2 = new Teacher();
    teacher2.setId(2L);
    teacher2.setFirstName("Jane");
    teacher2.setLastName("Smith");
  }

  @Test
  void testFindAllShouldReturnAllTeachers() {
    List<Teacher> teachers = Arrays.asList(teacher1, teacher2);
    when(teacherRepository.findAll()).thenReturn(teachers);

    List<Teacher> result = teacherService.findAll();

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(teacherRepository, times(1)).findAll();
  }

  @Test
  void testFindByIdShouldReturnTeacherWhenTeacherExists() {
    when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));

    Teacher result = teacherService.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("John", result.getFirstName());
    verify(teacherRepository, times(1)).findById(1L);
  }

  @Test
  void testFindByIdShouldReturnNullWhenTeacherDoesNotExist() {
    when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

    Teacher result = teacherService.findById(999L);

    assertNull(result);
    verify(teacherRepository, times(1)).findById(999L);
  }
}