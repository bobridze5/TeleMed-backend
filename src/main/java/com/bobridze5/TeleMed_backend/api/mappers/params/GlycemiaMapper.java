package com.bobridze5.TeleMed_backend.api.mappers.params;

import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.glycemia.Glycemia;
import org.springframework.stereotype.Component;

@Component
public final class GlycemiaMapper {
    public Glycemia mapToEntity(GlycemiaRequest request, Patient patient) {
        return Glycemia.builder()
                .id(null)
                .patient(patient)
                .level(request.level())
                .type(request.type())
                .build();
    }

    public GlycemiaResponse mapToResponse(Glycemia entity) {
        return new GlycemiaResponse(
                entity.getId(),
                entity.getLevel(),
                entity.getType(),
                entity.getUpdatedAt()
        );
    }

    public void updateEntity(GlycemiaUpdateRequest request, Glycemia entity) {
        if (request.level() != null) entity.setLevel(request.level());
        if (request.type() != null) entity.setType(request.type());
    }
}
