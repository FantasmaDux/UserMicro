package io.github.pavelshe11.networkingmicro.api.exceptions;

import io.github.pavelshe11.networkingmicro.api.dto.ErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@RestControllerAdvice
public class CustomExceptionController {
    private final MessageSource messageSource;
    private static final Logger log = LoggerFactory.getLogger(CustomExceptionController.class);

    private FieldErrorDto createFieldError(String field, String message, UUID objectId) {
        return objectId != null
                ? new FieldErrorDto(field, message, objectId)
                : new FieldErrorDto(field, message);
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
                        resolveMessage(error.getMessage()),
                        error.getObjectId()
                )).toList();

        ErrorDto response = ErrorDto.builder()
                .error(errorMessage)
                .detailedErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.error("Метод не поддерживается: ", ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(NoHandlerFoundException ex) {
        log.error("Произошла ошибка доступа к ресурсу: ", ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGeneralExceptions(Exception ex) {
        log.error("Произошла ошибка сервера: ", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<ErrorDto> handleAbstractExceptions(AbstractException ex) {

        String errorText = resolveMessage(ex.getErrorCode());
        String messageText = resolveMessage(ex.getMessageCode());

        ErrorDto response = ErrorDto.builder()
                .error(errorText)
                .detailedErrors(List.of(
                        createFieldError(ex.getFieldName(), messageText, ex.getObjectId())
                ))
                .build();

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<Void> handleStatusOnlyExceptions(HttpStatusException ex) {
        return ResponseEntity.status(ex.getStatus()).build();
    }

    @ExceptionHandler(InvalidMimeTypeException.class)
    public ResponseEntity<ErrorDto> handleInvalidMimeTypeException(InvalidMimeTypeException ex) {
        String errorText = messageSource.getMessage(
                ex.getErrorCode(), null, LocaleContextHolder.getLocale());

        String messageText = messageSource.getMessage(
                ex.getMessageCode(), ex.getMessageArgs(), LocaleContextHolder.getLocale());

        ErrorDto response = ErrorDto.builder()
                .error(errorText)
                .detailedErrors(List.of(
                        createFieldError(ex.getFieldName(), messageText, null)
                ))
                .build();

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleJsonMappingException(HttpMessageNotReadableException ex) {
        log.error("Произошла ошибка json запроса: ", ex);

        String errorText = resolveMessage("handle.error");
        String messageText = resolveMessage("request.invalid");

        ErrorDto response = ErrorDto.builder()
                .error(errorText)
                .detailedErrors(List.of(
                        createFieldError(null, messageText, null)
                ))
                .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorText = resolveMessage("handle.error");
        List<FieldErrorDto> detailedErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> createFieldError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage(),
                        null
                ))
                .toList();

        ErrorDto response = ErrorDto.builder()
                .error(errorText)
                .detailedErrors(detailedErrors)
                .build();

        return ResponseEntity
                .badRequest()
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

