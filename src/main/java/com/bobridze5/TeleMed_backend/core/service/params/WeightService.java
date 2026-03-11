package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.weight.WeightFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.weight.WeightRequest;
import com.bobridze5.TeleMed_backend.api.dto.weight.WeightResponse;
import com.bobridze5.TeleMed_backend.core.entity.Patient;
import org.springframework.data.domain.Page;

public interface WeightService {
    Page<WeightResponse> getWeightRecords(Patient patient, WeightFilterRequest request);

    WeightResponse addWeightRecord(Patient patient, WeightRequest request);

    WeightResponse getWeightRecordById(Patient patient, Long weightId);

    WeightResponse updateWeightRecord(WeightRequest request, Patient patient, Long weightId);

    void deleteWeightRecord(Patient patient, Long weightId);
}
