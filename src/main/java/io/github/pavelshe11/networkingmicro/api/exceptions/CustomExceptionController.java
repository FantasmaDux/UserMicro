package io.github.pavelshe11.networkingmicro.api.exceptions;

import io.github.pavelshe11.networkingmicro.api.dto.ErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;


@RequiredArgsConstructor
@RestControllerAdvice
public class CustomExceptionController {
    private final MessageSource messageSource;
    private static final String GENERIC_ERROR = "error";

    private FieldErrorDto createFieldError(HttpStatus status, String message) {
        return new FieldErrorDto(String.valueOf(status.value()), message);
    }

    private ErrorDto createErrorDto(HttpStatus status, String message) {
        return ErrorDto.builder()
                .error(GENERIC_ERROR)
                .detailedErrors(List.of(createFieldError(status, message)))
                .build();
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ErrorDto> handleFieldValidationExceptions(FieldValidationException ex) {

        String errorMessage = messageSource.getMessage(
                ex.getMessage(),
                null,
                LocaleContextHolder.getLocale()
        );

        List<FieldErrorDto> errors = ex.getErrors().stream()
                .map((error) -> new FieldErrorDto(
                        error.getField(),
                        resolveMessage(error.getMessage())
                )).toList();

        ErrorDto response = ErrorDto.builder()
                .error(errorMessage)
                .detailedErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String errorMessage = messageSource.getMessage(
                "page.not.found", null, LocaleContextHolder.getLocale()
        );

        ErrorDto response = createErrorDto(HttpStatus.NOT_FOUND, errorMessage);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(NoHandlerFoundException ex) {
        String errorMessage = messageSource.getMessage(
                "page.not.found", null, LocaleContextHolder.getLocale()
        );

        ErrorDto response = createErrorDto(HttpStatus.NOT_FOUND, errorMessage);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGeneralExceptions(Exception ex) {
        System.out.println(ex.getMessage());
        String errorMessage = messageSource.getMessage("server.inner.error", null, LocaleContextHolder.getLocale());

        ErrorDto response = createErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<ErrorDto> handleAbstractExceptions(AbstractException ex) {
        String errorMessage = messageSource.getMessage(ex.getMessageCode(), null, LocaleContextHolder.getLocale());

        ErrorDto response = createErrorDto(ex.getStatus(), errorMessage);

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public void handleMaxUploadSizeExceededException() {
        throw new AvatarLargeSizeException();
    }

    private String resolveMessage(String codeOrMessage) {
        try {
            return messageSource.getMessage(codeOrMessage, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return codeOrMessage;
        }
    }
}

