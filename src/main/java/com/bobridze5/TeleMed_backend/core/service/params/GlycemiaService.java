package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaUpdateRequest;

public interface GlycemiaService extends PatientCrudParamService<
        GlycemiaRequest,
        GlycemiaUpdateRequest,
        GlycemiaResponse,
        GlycemiaFilterRequest
        > {
}
