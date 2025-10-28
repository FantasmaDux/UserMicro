package io.github.fantasmadux.usermicro.services;

import com.google.protobuf.Value;
import io.github.fantasmadux.usermicro.grpc.AccountValidatorProto;
import io.github.fantasmadux.usermicro.grpc.ErrorProto;
import io.github.fantasmadux.usermicro.api.dto.FieldErrorDto;
import io.github.fantasmadux.usermicro.validators.GrpcConvertor;
import io.github.fantasmadux.usermicro.validators.RegistrationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountValidatorService {

    private final RegistrationValidator registrationValidator;

    public AccountValidatorProto.ValidateUserDataResponse validateUserData(Map<String, Value> userData) {

        Map<String, Object> dataForValidation = GrpcConvertor.convertToObjectMap(userData);

        Set<FieldErrorDto> validationErrors = registrationValidator.validateRegistrationData(dataForValidation);
        List<ErrorProto.FieldError> errors = validationErrors.stream()
                .map((fieldErrorDto) -> this.mapToProto(fieldErrorDto))
                .collect(Collectors.toList());

        boolean isAccountValid = errors.isEmpty();

        return AccountValidatorProto.ValidateUserDataResponse.newBuilder()
                .setAccept(isAccountValid)
                .setError(isAccountValid ? "" : "Валидация пользовательских данных не пройдена.")
                .addAllDetailedErrors(errors)
                .build();
    }

    private ErrorProto.FieldError mapToProto(FieldErrorDto dto) {
        return ErrorProto.FieldError.newBuilder()
                .setField(dto.getField())
                .setMessage(dto.getMessage())
                .build();
    }
}
