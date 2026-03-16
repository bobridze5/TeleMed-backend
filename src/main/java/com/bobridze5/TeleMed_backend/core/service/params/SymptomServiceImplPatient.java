package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SymptomServiceImplPatient implements SymptomServicePatient {
    @Override
    public SymptomResponse addRecord(Patient patient, SymptomRequest request) {
        return null;
    }

    @Override
    public SymptomResponse updateRecord(Patient patient, Long id, SymptomUpdateRequest request) {
        return null;
    }

    @Override
    public SymptomResponse getRecordById(Patient patient, Long id) {
        return null;
    }

    @Override
    public Page<SymptomResponse> getRecords(Patient patient, SymptomFilterRequest filter) {
        return null;
    }

    @Override
    public void deleteRecord(Patient patient, Long id) {

    }
}
