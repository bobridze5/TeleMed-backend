package com.bobridze5.TeleMed_backend.core.service.test;

import com.bobridze5.TeleMed_backend.api.dto.test.TestFindRiscFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.test.TestFindRiscRequest;
import com.bobridze5.TeleMed_backend.api.dto.test.TestFindRiscResponse;
import com.bobridze5.TeleMed_backend.api.mappers.test.TestFindRiscMapper;
import com.bobridze5.TeleMed_backend.core.entity.TestFindRisc;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.TestFindRiscRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestFindRiscService {
    private final TestFindRiscRepository testFindRiscRepository;
    private final TestFindRiscMapper testFindRiscMapper;

    @Transactional(readOnly = true)
    public Page<TestFindRiscResponse> getTests(
            Patient patient,
            int page,
            int size,
            TestFindRiscFilterRequest filterRequest
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(filterRequest.getSortDirection(), filterRequest.getSortField())
        );

        Long patientId = patient.getId();
        Specification<TestFindRisc> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("patient").get("id"), patientId));
            if (filterRequest.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filterRequest.startDate()));
            }
            if (filterRequest.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filterRequest.endDate()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return testFindRiscRepository.findAll(spec, pageable)
                .map(testFindRiscMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    public TestFindRiscResponse getTestById(Long testId, Patient patient) {
        TestFindRisc testResults = testFindRiscRepository.findByIdAndUserId(testId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Тест не найден"));

        return testFindRiscMapper.mapToResponse(testResults);
    }

    @Transactional
    public TestFindRiscResponse writeTestResults(Patient patient, TestFindRiscRequest request) {
        TestFindRisc testResults = testFindRiscMapper.mapToEntity(request, patient);
        testResults = testFindRiscRepository.save(testResults);
        return testFindRiscMapper.mapToResponse(testResults);
    }

    @Transactional
    public void deleteTestById(Long testId, Patient patient) {
        TestFindRisc testResults = testFindRiscRepository.findByIdAndUserId(testId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Тест не найден"));

        testFindRiscRepository.delete(testResults);
    }
}
