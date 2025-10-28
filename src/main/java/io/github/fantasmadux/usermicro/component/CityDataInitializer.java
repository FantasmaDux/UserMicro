package io.github.fantasmadux.usermicro.component;

import io.github.fantasmadux.usermicro.store.entities.CityEntity;
import io.github.fantasmadux.usermicro.store.repositories.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityDataInitializer implements ApplicationRunner {
    private final CityRepository cityRepository;

    @Override
    public void run(ApplicationArguments args) {
        String cityName = "TestCity";
        boolean cityExists = cityRepository.existsByName(cityName);

        if (!cityExists) {
            CityEntity city = CityEntity.builder()
                    .name(cityName)
                    .build();
            cityRepository.save(city);
        }
    }
}
