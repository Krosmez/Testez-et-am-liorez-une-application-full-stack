package com.openclassroom.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

  @Mock
  private SessionRepository sessionRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private SessionService sessionService;

  private Session session;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setEmail("test@test.com");

    session = new Session();
    session.setId(1L);
    session.setUsers(new ArrayList<>());
  }

  @Test
  void testCreate() {
    when(sessionRepository.save(any(Session.class))).thenReturn(session);

    Session result = sessionService.create(session);

    assertNotNull(result);
    assertEquals(session.getId(), result.getId());
    verify(sessionRepository, times(1)).save(session);
  }

  @Test
  void testDeleteSuccess() {
    when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

    sessionService.delete(1L);

    verify(sessionRepository, times(1)).findById(1L);
    verify(sessionRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDeleteNotFound() {
    when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> sessionService.delete(1L));
    verify(sessionRepository, times(1)).findById(1L);
    verify(sessionRepository, never()).deleteById(anyLong());
  }

  @Test
  void testFindAll() {
    List<Session> sessions = Arrays.asList(session);
    when(sessionRepository.findAll()).thenReturn(sessions);

    List<Session> result = sessionService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(sessionRepository, times(1)).findAll();
  }

  @Test
  void testGetByIdFound() {
    when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

    Session result = sessionService.getById(1L);

    assertNotNull(result);
    assertEquals(session.getId(), result.getId());
    verify(sessionRepository, times(1)).findById(1L);
  }

  @Test
  void testGetByIdNotFound() {
    when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

    Session result = sessionService.getById(1L);

    assertNull(result);
    verify(sessionRepository, times(1)).findById(1L);
  }

  @Test
  void testUpdate() {
    when(sessionRepository.save(any(Session.class))).thenReturn(session);

    Session result = sessionService.update(1L, session);

    assertNotNull(result);
    assertEquals(1L, session.getId());
    verify(sessionRepository, times(1)).save(session);
  }

  @Test
  void testParticipateSuccess() {
    when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(sessionRepository.save(any(Session.class))).thenReturn(session);

    sessionService.participate(1L, 1L);

    assertTrue(session.getUsers().contains(user));
    verify(sessionRepository, times(1)).save(session);
  }

  @Test
  void testParticipateSessionNotFound() {
    when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
    verify(sessionRepository, never()).save(any(Session.class));
  }

  @Test
  void testParticipateUserNotFound() {
    when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
    verify(sessionRepository, never()).save(any(Session.class));
  }

  @Test
  void testParticipateAlreadyParticipating() {
    session.getUsers().add(user);
    when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 1L));
    verify(sessionRepository, never()).save(any(Session.class));
  }

  @Test
  void testNoLongerParticipateSuccess() {
    session.getUsers().add(user);
    when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
    when(sessionRepository.save(any(Session.class))).thenReturn(session);

    sessionService.noLongerParticipate(1L, 1L);

    assertFalse(session.getUsers().contains(user));
    verify(sessionRepository, times(1)).save(session);
  }

  @Test
  void testNoLongerParticipateSessionNotFound() {
    when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 1L));
    verify(sessionRepository, never()).save(any(Session.class));
  }

  @Test
  void testNoLongerParticipateNotParticipating() {
    when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

    assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 1L));
    verify(sessionRepository, never()).save(any(Session.class));
  }
}