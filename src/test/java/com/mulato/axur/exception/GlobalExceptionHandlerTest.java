package com.mulato.axur.exception;

import com.mulato.axur.util.LogCapture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        // Setup mock behavior for MethodArgumentNotValidException
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
    }

    @Test
    void testHandleValidationExceptions() {
        // Given
        FieldError fieldError1 = new FieldError("crawlRequest", "keyword", "Keyword is required");
        FieldError fieldError2 = new FieldError("crawlRequest", "baseUrl", "Invalid URL format");
        
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // When
        ResponseEntity<Map<String, String>> response = 
            globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Keyword is required", response.getBody().get("keyword"));
        assertEquals("Invalid URL format", response.getBody().get("baseUrl"));
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testHandleValidationExceptions_WithLogs() {
        // Given
        LogCapture logCapture = new LogCapture(GlobalExceptionHandler.class);
        FieldError fieldError = new FieldError("crawlRequest", "keyword", "Keyword is required");
        
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        // When
        globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Then
        assertTrue(logCapture.hasLogMessage("Validation error:"));
        
        logCapture.stop();
    }

    @Test
    void testHandleIllegalArgument() {
        // Given
        String errorMessage = "Invalid parameter value";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response = 
            globalExceptionHandler.handleIllegalArgument(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().get("error"));
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testHandleIllegalArgument_WithLogs() {
        // Given
        LogCapture logCapture = new LogCapture(GlobalExceptionHandler.class);
        String errorMessage = "Invalid parameter value";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        globalExceptionHandler.handleIllegalArgument(exception);

        // Then
        assertTrue(logCapture.hasLogMessage("Illegal argument: " + errorMessage));
        
        logCapture.stop();
    }

    @Test
    void testHandleGeneralException() {
        // Given
        Exception exception = new RuntimeException("Unexpected database error");

        // When
        ResponseEntity<Map<String, String>> response = 
            globalExceptionHandler.handleGeneralException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().get("error"));
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testHandleGeneralException_WithLogs() {
        // Given
        LogCapture logCapture = new LogCapture(GlobalExceptionHandler.class);
        Exception exception = new RuntimeException("Unexpected database error");

        // When
        globalExceptionHandler.handleGeneralException(exception);

        // Then
        assertTrue(logCapture.hasLogMessage("Unexpected error:"));
        
        logCapture.stop();
    }

    @Test
    void testHandleValidationExceptions_EmptyErrors() {
        // Given
        when(bindingResult.getAllErrors()).thenReturn(List.of());

        // When
        ResponseEntity<Map<String, String>> response = 
            globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testHandleIllegalArgument_NullMessage() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException();

        // When
        ResponseEntity<Map<String, String>> response = 
            globalExceptionHandler.handleIllegalArgument(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().get("error"));
    }

    @Test
    void testHandleGeneralException_NullPointerException() {
        // Given
        NullPointerException exception = new NullPointerException("Null pointer error");

        // When
        ResponseEntity<Map<String, String>> response = 
            globalExceptionHandler.handleGeneralException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().get("error"));
    }

    @Test
    void testResponseStructure_ValidationError() {
        // Given
        FieldError fieldError = new FieldError("test", "field", "error message");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        // When
        ResponseEntity<Map<String, String>> response = 
            globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        assertFalse(response.getBody().isEmpty());
    }
}
