package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomUpdateRequest;

public interface SymptomServicePatient extends PatientCrudParamService<SymptomRequest, SymptomUpdateRequest, SymptomResponse, SymptomFilterRequest> {
}
