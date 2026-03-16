package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import org.springframework.data.domain.Page;

public interface WeightService {
    Page<WeightResponse> getWeightRecords(Patient patient, WeightFilterRequest request);

    WeightResponse addWeightRecord(Patient patient, WeightRequest request);

    WeightResponse getWeightRecordById(Patient patient, Long weightId);

    WeightResponse updateWeightRecord(Patient patient, Long weightId, WeightRequest request);

    void deleteWeightRecord(Patient patient, Long weightId);
}
