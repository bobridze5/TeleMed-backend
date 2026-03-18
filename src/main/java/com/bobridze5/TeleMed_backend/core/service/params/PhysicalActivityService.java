package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityUpdateRequest;

public interface PhysicalActivityService extends PatientCrudParamService<
        PhysicalActivityRequest,
        PhysicalActivityUpdateRequest,
        PhysicalActivityResponse,
        PhysicalActivityFilterRequest
        > {
}
