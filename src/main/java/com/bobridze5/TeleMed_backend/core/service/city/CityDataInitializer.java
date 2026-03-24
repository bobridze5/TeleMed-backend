package com.bobridze5.TeleMed_backend.core.service.city;

import com.bobridze5.TeleMed_backend.api.dto.city.CityRegionRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.City;
import com.bobridze5.TeleMed_backend.core.entity.medical.Region;
import com.bobridze5.TeleMed_backend.core.repository.CityRepository;
import com.bobridze5.TeleMed_backend.core.repository.RegionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CityDataInitializer implements CommandLineRunner {
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (cityRepository.count() > 0) return;

        InputStream inputStream = getClass().getResourceAsStream("/cities.json");
        List<CityRegionRequest> rawData = objectMapper.readValue(inputStream, new TypeReference<>() {
        });

        Map<String, List<String>> groupedData = rawData.stream()
                .collect(Collectors.groupingBy(
                        CityRegionRequest::region,
                        Collectors.mapping(CityRegionRequest::city, Collectors.toList())
                ));

        System.out.println("Записей в JSON: " + rawData.size());
        System.out.println("Уникальных регионов: " + groupedData.size());


        groupedData.forEach((regionName, cityNames) -> {
            Region region = new Region(null, regionName);
            regionRepository.save(region);

            List<City> cities = cityNames.stream()
                    .map(cityName -> City.builder()
                            .name(cityName)
                            .region(region)
                            .build()
                    ).toList();

            cityRepository.saveAll(cities);
        });

    }
}
