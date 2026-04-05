package com.bobridze5.TeleMed_backend.core.service.test;

import com.bobridze5.TeleMed_backend.api.dto.test.TestFindRiscFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.test.TestFindRiscRequest;
import com.bobridze5.TeleMed_backend.api.dto.test.TestFindRiscResponse;
import com.bobridze5.TeleMed_backend.api.mappers.test.TestFindRiscMapper;
import com.bobridze5.TeleMed_backend.core.entity.TestFindRisc;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.TestFindRiscRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return testFindRiscRepository.findAllByUserId(
                patient.getId(),
                filterRequest.startDate(),
                filterRequest.endDate(),
                pageable
        ).map(testFindRiscMapper::mapToResponse);
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
