package io.github.pavelshe11.networkingmicro.annotations;

import io.github.pavelshe11.networkingmicro.api.dto.ErrorDto;
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
@Retention(value = RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = "401",
                description = "Запрос не прошёл аутентификацию",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorDto.class),
                        examples = @ExampleObject(
                                name = "UnauthorizedError",
                                summary = "Ошибка авторизации",
                                value = """
                                        {
                                          "error": "Unauthorized"
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
                        schema = @Schema(implementation = ErrorDto.class),
                        examples = @ExampleObject(
                                name = "ServerError",
                                summary = "Ошибка сервера",
                                value = """
                                        {
                                          "error": "Внутренняя ошибка сервера."
                                        }
                                        """
                        )
                )
        )
})
public @interface CommonApiResponses {

}
