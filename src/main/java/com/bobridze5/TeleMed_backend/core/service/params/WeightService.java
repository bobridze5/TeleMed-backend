package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.weight.WeightRequest;
import com.bobridze5.TeleMed_backend.api.dto.weight.WeightResponse;
import com.bobridze5.TeleMed_backend.core.entity.Patient;

public interface WeightService {
    WeightResponse addWeightRecord(Patient patient, WeightRequest request);

    WeightResponse getWeightById(Patient patient, Long weightId);

    WeightResponse updateWeight(WeightRequest request, Patient patient, Long weightId);

    void deleteWeightRecord(Patient patient, Long weightId);
}
