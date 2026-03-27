package com.openclassroom.starterjwt.controllers;

import com.openclassrooms.starterjwt.controllers.SessionController;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

  @Mock
  private SessionService sessionService;

  @Mock
  private SessionMapper sessionMapper;

  @InjectMocks
  private SessionController sessionController;

  private Session session;
  private SessionDto sessionDto;

  @BeforeEach
  void setUp() {
    session = Session.builder().id(1L).name("Yoga Session").date(new Date()).description("A relaxing yoga session").build();

    sessionDto = new SessionDto();
    sessionDto.setId(1L);
    sessionDto.setName("Yoga Session");
    sessionDto.setDate(new Date());
    sessionDto.setDescription("A relaxing yoga session");
    sessionDto.setTeacher_id(1L);
  }

  // ========================
  //   GET /api/session/{id}
  // ========================

  @Test
  void testFindByIdShouldReturn200WithSessionDtoWhenSessionExists() {
    when(sessionService.getById(1L)).thenReturn(session);
    when(sessionMapper.toDto(session)).thenReturn(sessionDto);

    ResponseEntity<?> response = sessionController.findById("1");

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(sessionDto, response.getBody());
    verify(sessionService, times(1)).getById(1L);
    verify(sessionMapper, times(1)).toDto(session);
  }

  @Test
  void testFindByIdShouldThrowNotFoundExceptionWhenSessionNotFound() {
    when(sessionService.getById(1L)).thenReturn(null);

    assertThrows(NotFoundException.class, () -> sessionController.findById("1"));
    verify(sessionService, times(1)).getById(1L);
    verify(sessionMapper, never()).toDto(any(Session.class));
  }

  @Test
  void testFindByIdShouldThrowNumberFormatExceptionWhenIdIsNotNumeric() {
    assertThrows(NumberFormatException.class, () -> sessionController.findById("invalid"));
    verify(sessionService, never()).getById(anyLong());
  }

  // ========================
  //   GET /api/session
  // ========================

  @Test
  void testFindAllShouldReturn200WithSessionDtoList() {
    List<Session> sessions = List.of(session);
    List<SessionDto> sessionDtos = List.of(sessionDto);
    when(sessionService.findAll()).thenReturn(sessions);
    when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

    ResponseEntity<?> response = sessionController.findAll();

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(sessionDtos, response.getBody());
    verify(sessionService, times(1)).findAll();
    verify(sessionMapper, times(1)).toDto(sessions);
  }

  // ========================
  //   POST /api/session
  // ========================

  @Test
  void testCreateShouldReturn200WithCreatedSessionDto() {
    when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
    when(sessionService.create(session)).thenReturn(session);
    when(sessionMapper.toDto(session)).thenReturn(sessionDto);

    ResponseEntity<?> response = sessionController.create(sessionDto);

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(sessionDto, response.getBody());
    verify(sessionService, times(1)).create(session);
  }

  // ========================
  //   PUT /api/session/{id}
  // ========================

  @Test
  void testUpdateShouldReturn200WithUpdatedSessionDto() {
    when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
    when(sessionService.update(1L, session)).thenReturn(session);
    when(sessionMapper.toDto(session)).thenReturn(sessionDto);

    ResponseEntity<?> response = sessionController.update("1", sessionDto);

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(sessionDto, response.getBody());
    verify(sessionService, times(1)).update(1L, session);
  }

  @Test
  void testUpdateShouldThrowNumberFormatExceptionWhenIdIsNotNumeric() {
    assertThrows(NumberFormatException.class, () -> sessionController.update("invalid", sessionDto));
    verify(sessionService, never()).update(anyLong(), any(Session.class));
  }

  // ===========================
  //   DELETE /api/session/{id}
  // ===========================

  @Test
  void testDeleteShouldReturn200WhenDeletionIsSuccessful() {
    doNothing().when(sessionService).delete(1L);

    ResponseEntity<?> response = sessionController.save("1");

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    verify(sessionService, times(1)).delete(1L);
  }

  @Test
  void testDeleteShouldThrowNotFoundExceptionWhenSessionDoesNotExist() {
    doThrow(new NotFoundException()).when(sessionService).delete(1L);

    assertThrows(NotFoundException.class, () -> sessionController.save("1"));
    verify(sessionService, times(1)).delete(1L);
  }

  @Test
  void testDeleteShouldThrowNumberFormatExceptionWhenIdIsNotNumeric() {
    assertThrows(NumberFormatException.class, () -> sessionController.save("invalid"));
    verify(sessionService, never()).delete(anyLong());
  }

  // ==============================================
  //   POST /api/session/{id}/participate/{userId}
  // ==============================================

  @Test
  void testParticipateShouldReturn200WhenParticipationIsSuccessful() {
    doNothing().when(sessionService).participate(1L, 2L);

    ResponseEntity<?> response = sessionController.participate("1", "2");

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    verify(sessionService, times(1)).participate(1L, 2L);
  }

  @Test
  void testParticipateShouldThrowNotFoundExceptionWhenSessionOrUserNotFound() {
    doThrow(new NotFoundException()).when(sessionService).participate(1L, 2L);

    assertThrows(NotFoundException.class, () -> sessionController.participate("1", "2"));
    verify(sessionService, times(1)).participate(1L, 2L);
  }

  @Test
  void testParticipateShouldThrowBadRequestExceptionWhenAlreadyParticipating() {
    doThrow(new BadRequestException()).when(sessionService).participate(1L, 2L);

    assertThrows(BadRequestException.class, () -> sessionController.participate("1", "2"));
    verify(sessionService, times(1)).participate(1L, 2L);
  }

  @Test
  void testParticipateShouldThrowNumberFormatExceptionWhenIdIsNotNumeric() {
    assertThrows(NumberFormatException.class, () -> sessionController.participate("invalid", "2"));
    verify(sessionService, never()).participate(anyLong(), anyLong());
  }

  // ================================================
  //   DELETE /api/session/{id}/participate/{userId}
  // ================================================

  @Test
  void testNoLongerParticipateShouldReturn200WhenSuccessful() {
    doNothing().when(sessionService).noLongerParticipate(1L, 2L);

    ResponseEntity<?> response = sessionController.noLongerParticipate("1", "2");

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    verify(sessionService, times(1)).noLongerParticipate(1L, 2L);
  }

  @Test
  void testNoLongerParticipateShouldThrowNotFoundExceptionWhenSessionNotFound() {
    doThrow(new NotFoundException()).when(sessionService).noLongerParticipate(1L, 2L);

    assertThrows(NotFoundException.class, () -> sessionController.noLongerParticipate("1", "2"));
    verify(sessionService, times(1)).noLongerParticipate(1L, 2L);
  }

  @Test
  void testNoLongerParticipateShouldThrowBadRequestExceptionWhenNotParticipating() {
    doThrow(new BadRequestException()).when(sessionService).noLongerParticipate(1L, 2L);

    assertThrows(BadRequestException.class, () -> sessionController.noLongerParticipate("1", "2"));
    verify(sessionService, times(1)).noLongerParticipate(1L, 2L);
  }

  @Test
  void testNoLongerParticipateShouldThrowNumberFormatExceptionWhenIdIsNotNumeric() {
    assertThrows(NumberFormatException.class, () -> sessionController.noLongerParticipate("invalid", "2"));
    verify(sessionService, never()).noLongerParticipate(anyLong(), anyLong());
  }
}
