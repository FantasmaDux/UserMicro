package io.github.pavelshe11.networkingmicro.services;

import com.google.protobuf.Value;
import io.github.pavelshe11.networking.grpc.AccountValidatorProto;
import io.github.pavelshe11.networking.grpc.ErrorProto;
import io.github.pavelshe11.networkingmicro.api.dto.ErrorDto;
import io.github.pavelshe11.networkingmicro.validators.AccountDataValidation;
import io.github.pavelshe11.networkingmicro.validators.GrpcConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountValidatorService {

    private final AccountDataValidation accountDataValidator;

    public AccountValidatorProto.ValidateUserDataResponse validateUserData(Map<String, Value> userData) {

        Map<String, Object> dataForValidation = GrpcConvertor.convertToObjectMap(userData);

        Set<ErrorDto> validationErrors = accountDataValidator.validateRegistrationData(dataForValidation);
        List<ErrorProto.FieldError> errors = validationErrors.stream()
                .map((errorDto) -> this.mapToProto(errorDto))
                .collect(Collectors.toList());

        boolean isAccountValid = errors.isEmpty();

        return AccountValidatorProto.ValidateUserDataResponse.newBuilder()
                .setAccept(isAccountValid)
                .setError(isAccountValid ? "" : "Валидация пользовательских данных не пройдена.")
                .addAllDetailedErrors(errors)
                .build();
    }

    private ErrorProto.FieldError mapToProto(ErrorDto dto) {
        return ErrorProto.FieldError.newBuilder()
                .setField(dto.getField())
                .setMessage(dto.getMessage())
                .build();
    }
}
