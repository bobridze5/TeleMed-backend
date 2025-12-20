package com.bobridze5.TeleMed_backend.core.service;

import com.bobridze5.TeleMed_backend.api.dto.city.CityCreateRequest;
import com.bobridze5.TeleMed_backend.api.dto.city.CityListResponse;
import com.bobridze5.TeleMed_backend.api.dto.city.CityUpdateRequest;
import com.bobridze5.TeleMed_backend.api.dto.city.CityResponse;
import com.bobridze5.TeleMed_backend.api.dto.users.UserResponse;
import com.bobridze5.TeleMed_backend.api.dto.users.UsersResponse;
import com.bobridze5.TeleMed_backend.core.entity.City;
import com.bobridze5.TeleMed_backend.core.entity.User;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityAlreadyExistsException;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.exceptions.RequestParamInvalidException;
import com.bobridze5.TeleMed_backend.core.repository.CityRepository;
import com.bobridze5.TeleMed_backend.core.service.interfaces.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public CityResponse getCity(Long id) {
        City city = cityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("City with id = " + id + " not found")
        );
        return new CityResponse(city.getId(), city.getName());
    }

    @Override
    public CityListResponse getCities(Integer pageNumber) {
        if (pageNumber == null || pageNumber < 0) {
            throw new RequestParamInvalidException("Request parameter page must be int and >= 0");
        }

        Pageable pageable = PageRequest.of(pageNumber, 5, Sort.by("name"));
        List<City> cities = cityRepository.findAll(pageable).toList();

        List<CityResponse> data = cities.stream().map(city -> new CityResponse(
                city.getId(),
                city.getName()
        )).toList();

        return new CityListResponse(data);
    }

    @Override
    public CityResponse createCity(CityCreateRequest request) {
        String name = request.name();
        if (cityRepository.existsCityByName(name)) {
            throw new EntityAlreadyExistsException("City with name = " + name + " already exists");
        }

        City city = City.builder()
                .name(name)
                .build();

        cityRepository.save(city);

        return new CityResponse(city.getId(), city.getName());
    }

    @Override
    public CityResponse updateCity(Long id, CityUpdateRequest request) {
        City city = cityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("City with id = " + id + " not found")
        );

        city.setName(request.name());
        cityRepository.save(city);

        return new CityResponse(city.getId(), city.getName());
    }

    @Override
    public void deleteCity(Long id) {
        City city = cityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("City with id = " + id + " not found")
        );

        cityRepository.delete(city);
    }

}
