package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.city.CityCreateRequest;
import com.bobridze5.TeleMed_backend.api.dto.city.CityListResponse;
import com.bobridze5.TeleMed_backend.api.dto.city.CityUpdateRequest;
import com.bobridze5.TeleMed_backend.api.dto.city.CityResponse;
import com.bobridze5.TeleMed_backend.core.service.city.CityService;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(API.CITY)
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;
    private final UrlBuilder urlBuilder;

    @GetMapping("/{id}")
    public CityResponse getCity(@PathVariable Long id) {
        return cityService.getCity(id);
    }

    @GetMapping("/")
    public CityListResponse getCities(@RequestParam(value = "page") Integer pageNumber){
        return cityService.getCities(pageNumber);
    }

    @PostMapping("/")
    public ResponseEntity<CityResponse> createCity(
            @RequestBody CityCreateRequest request,
            HttpServletRequest servletRequest
    ) {
        CityResponse response = cityService.createCity(request);
        URI location = urlBuilder.buildAbsoluteUrl(servletRequest, API.CITY + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public CityResponse updateCity(
            @PathVariable Long id,
            @RequestBody CityUpdateRequest request
    ) {
        return cityService.updateCity(id, request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
    }
}
