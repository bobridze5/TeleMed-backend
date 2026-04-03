package com.bobridze5.TeleMed_backend.core.service.params.unit;

import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaResponse;
import com.bobridze5.TeleMed_backend.api.mappers.params.GlycemiaMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.Glycemia;
import com.bobridze5.TeleMed_backend.core.entity.report.GlycemiaType;
import com.bobridze5.TeleMed_backend.core.repository.GlycemiaRepository;
import com.bobridze5.TeleMed_backend.core.service.params.GlycemiaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlycemiaServiceTest {

    @Mock
    private GlycemiaRepository glycemiaRepository;

    @Mock
    private GlycemiaMapper glycemiaMapper;

    @InjectMocks
    private GlycemiaServiceImpl glycemiaService;

    private Patient patient;
    private Glycemia glycemia;
    private final Long recordId = 1L;

    @BeforeEach
    void setUp() {
        patient = Patient.builder().id(10L).build();
        glycemia = Glycemia.builder()
                .id(recordId)
                .patient(patient)
                .build();
    }


    @Test
    @DisplayName("Добавление записи уровня измерения гликемии")
    void add_glycemia_success() {
        double level = 5.5;
        GlycemiaType type = GlycemiaType.FASTING;
        GlycemiaRequest request = new GlycemiaRequest(level, type);
        GlycemiaResponse expectedResponse = new GlycemiaResponse(recordId, level, type, LocalDateTime.now());

        when(glycemiaMapper.mapToEntity(request, patient)).thenReturn(glycemia);
        when(glycemiaRepository.save(any(Glycemia.class))).thenReturn(glycemia);
        when(glycemiaMapper.mapToResponse(glycemia)).thenReturn(expectedResponse);

        GlycemiaResponse response = glycemiaService.addRecord(patient, request);

        assertNotNull(response);
        assertEquals(recordId, response.id());
        verify(glycemiaRepository).save(glycemia);
    }
}
