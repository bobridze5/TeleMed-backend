package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureResponse;

public interface BloodPressureService extends CrudParamService<BloodPressureRequest, BloodPressureRequest,
        BloodPressureResponse, BloodPressureFilterRequest> {
}
