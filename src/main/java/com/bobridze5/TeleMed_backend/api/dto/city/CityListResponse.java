package com.bobridze5.TeleMed_backend.api.dto.city;

import java.util.List;

public record CityListResponse(
        List<CityResponse> cities
) {
}
