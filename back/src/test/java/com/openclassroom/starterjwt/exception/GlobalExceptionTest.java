package com.openclassroom.starterjwt.exception;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.GlobalException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionTest {

  private final GlobalException handler = new GlobalException();
  private final WebRequest webRequest = mock(WebRequest.class);

  @Test
  void testHandleNumberFormatException_returnsBadRequest() {
    ResponseEntity<?> response = handler.handleNumberFormatException(new NumberFormatException("bad"), webRequest);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNull();
  }

  @Test
  void testHandleBadRequestException_returnsBadRequest() {
    ResponseEntity<?> response = handler.handleBadRequestException(new BadRequestException(), webRequest);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNull();
  }

  @Test
  void testHandleNotFoundException_returnsNotFound() {
    ResponseEntity<?> response = handler.handleNotFoundException(new NotFoundException(), webRequest);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNull();
  }

  @Test
  void testHandleUnauthorizedException_returnsUnauthorized() {
    ResponseEntity<?> response = handler.handleUnauthorizedException(new UnauthorizedException(), webRequest);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNull();
  }
}