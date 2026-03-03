package com.bobridze5.TeleMed_backend.api.controllers;

import com.bobridze5.TeleMed_backend.api.dto.city.CityCreateRequest;
import com.bobridze5.TeleMed_backend.api.dto.city.CityListResponse;
import com.bobridze5.TeleMed_backend.api.dto.city.CityUpdateRequest;
import com.bobridze5.TeleMed_backend.api.dto.city.CityResponse;
import com.bobridze5.TeleMed_backend.core.service.city.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/city")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @GetMapping("/{id}")
    public ResponseEntity<CityResponse> getCity(@PathVariable Long id) {
        CityResponse response = cityService.getCity(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<CityListResponse> getCities(@RequestParam(value = "page") Integer pageNumber){
        CityListResponse response = cityService.getCities(pageNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/")
    public ResponseEntity<CityResponse> createCity(@RequestBody CityCreateRequest request) {
        CityResponse response = cityService.createCity(request);
        String applicationURL = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/city/")
                .build()
                .toString();

        URI location = URI.create(applicationURL + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CityResponse> updateCity(
            @PathVariable Long id,
            @RequestBody CityUpdateRequest request
    ) {
        CityResponse response = cityService.updateCity(id, request);

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }
}
