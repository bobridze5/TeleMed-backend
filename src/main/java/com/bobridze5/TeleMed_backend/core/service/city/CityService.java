package com.bobridze5.TeleMed_backend.core.service.city;

import com.bobridze5.TeleMed_backend.api.dto.city.CityCreateRequest;
import com.bobridze5.TeleMed_backend.api.dto.city.CityListResponse;
import com.bobridze5.TeleMed_backend.api.dto.city.CityResponse;
import com.bobridze5.TeleMed_backend.api.dto.city.CityUpdateRequest;

public interface CityService {
    CityResponse getCity(Long id);
    CityResponse createCity(CityCreateRequest request);
    CityResponse updateCity(Long id, CityUpdateRequest request);
    void deleteCity(Long id);

    CityListResponse getCities(Integer pageNumber);
}
