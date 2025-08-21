package io.github.pavelshe11.networkingmicro.annotations;

import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(value= RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Код выслан на почту"
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Запрос не прошёл аутентификацию",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = FieldErrorDto.class),
                        examples = @ExampleObject(
                                name = "UnauthorizedError",
                                summary = "Ошибка авторизации",
                                value = """
                                            {
                                              "field": "Unauthorized",
                                              "message": "Токен недействителен или отсутствует"
                                            }
                                            """
                        )
                )

        ),
        @ApiResponse(
                responseCode = "500",
                description = "Внутренняя ошибка сервера",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = FieldErrorDto.class),
                        examples = @ExampleObject(
                                name = "ServerError",
                                summary = "Ошибка сервера",
                                value = """
                                            {
                                              "field": "InternalServerError",
                                              "message": "Произошла непредвиденная ошибка"
                                            }
                                            """
                        )
                )
        ),
        @ApiResponse(responseCode = "400", description = "Неверно указаны данные")
})
public @interface CommonApiResponses {

}
