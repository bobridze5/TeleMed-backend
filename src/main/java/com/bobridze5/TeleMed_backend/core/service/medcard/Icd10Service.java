package com.bobridze5.TeleMed_backend.core.service.medcard;

import com.bobridze5.TeleMed_backend.api.dto.medcard.Icd10CodeResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.Icd10Code;
import com.bobridze5.TeleMed_backend.core.repository.Icd10CodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Icd10Service {
    private static final int MAX_RESULTS = 50;

    private final Icd10CodeRepository repository;

    public List<Icd10CodeResponse> search(String query, int limit) {
        if (query == null || query.isBlank()) {
            // Для пустого запроса возвращаем самые верхнеуровневые рубрики,
            // чтобы фронт мог сразу показать осмысленный список — не пустоту.
            return repository.findAll(
                    PageRequest.of(0, Math.min(limit, MAX_RESULTS))
            ).stream().map(this::toResponse).toList();
        }
        int safeLimit = Math.max(1, Math.min(limit, MAX_RESULTS));
        return repository.search(query.trim(), PageRequest.of(0, safeLimit))
                .stream().map(this::toResponse).toList();
    }

    /**
     * Подгружает информацию по списку кодов одним запросом — нужна для
     * сериализации MedicalRecordResponse: рядом с каждым кодом
     * отображаем человеческое название.
     */
    public List<Icd10CodeResponse> findByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyList();
        return repository.findAllById(codes).stream().map(this::toResponse).toList();
    }

    private Icd10CodeResponse toResponse(Icd10Code c) {
        return new Icd10CodeResponse(
                c.getCode(),
                c.getName(),
                c.getParent(),
                Boolean.TRUE.equals(c.getIsRubric())
        );
    }
}
