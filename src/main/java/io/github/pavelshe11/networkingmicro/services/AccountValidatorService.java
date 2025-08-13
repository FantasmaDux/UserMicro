package io.github.pavelshe11.networkingmicro.services;

import com.google.protobuf.Value;
import io.github.pavelshe11.networking.grpc.AccountCreationProto;
import io.github.pavelshe11.networking.grpc.AccountValidatorProto;
import io.github.pavelshe11.networking.grpc.ErrorProto;
import io.github.pavelshe11.networkingmicro.validators.AccountDataValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountValidatorService {

    private final AccountDataValidation accountDataValidator;

    public AccountValidatorProto.ValidateUserDataResponse validateUserData(Map<String, Value> userData) {
        List<ErrorProto.FieldError> errors = accountDataValidator.validateAll(userData);

        boolean isAccountValid = errors.isEmpty();

        return AccountValidatorProto.ValidateUserDataResponse.newBuilder()
                .setAccept(isAccountValid)
                .setError(isAccountValid ? "" : "Валидация пользовательских данных не пройдена.")
                .addAllDetailedErrors(errors)
                .build();
    }
}
