package com.bobridze5.TeleMed_backend.core.service.medcard;

import com.bobridze5.TeleMed_backend.api.dto.medcard.Icd10CodeResponse;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalRecordRequest;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalRecordResponse;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalRecordUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.MedicalRecord;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.exceptions.AccessForbiddenException;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityAlreadyExistsException;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.AppointmentRepository;
import com.bobridze5.TeleMed_backend.core.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository recordRepository;
    private final AppointmentRepository appointmentRepository;
    private final Icd10Service icd10Service;

    @Transactional(readOnly = true)
    public Page<MedicalRecordResponse> getRecordsForPatient(Patient patient, int page, int size) {
        return recordRepository
                .findByPatientId(patient.getId(), PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getLatestRecordsForPatient(Patient patient) {
        return recordRepository.findTop5ByPatientIdOrderByCreatedAtDesc(patient.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponse getRecordForPatient(Patient patient, Long recordId) {
        MedicalRecord record = recordRepository.findByIdAndPatientId(recordId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));
        return toResponse(record);
    }

    @Transactional
    public MedicalRecordResponse createRecord(Doctor doctor, Patient patient, MedicalRecordRequest request) {
        Appointment appointment = null;
        if (request.appointmentId() != null) {
            appointment = appointmentRepository.findById(request.appointmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Запись на приём не найдена"));
            if (!appointment.getPatient().getId().equals(patient.getId())
                    || !appointment.getDoctor().getId().equals(doctor.getId())) {
                throw new AccessForbiddenException("Запись на приём не относится к этому пациенту и врачу");
            }
            // Один приём — одна медицинская запись. Если врач хочет
            // дополнить заметки, он редактирует существующую через PATCH.
            if (recordRepository.existsByAppointmentId(request.appointmentId())) {
                throw new EntityAlreadyExistsException(
                        "Для этого приёма уже создана запись консультации. " +
                                "Откройте существующую запись для редактирования.");
            }
        }

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .appointment(appointment)
                .title(request.title())
                .complaints(request.complaints())
                .anamnesisMorbi(request.anamnesisMorbi())
                .anamnesisVitae(request.anamnesisVitae())
                .objectiveStatus(request.objectiveStatus())
                .localStatus(request.localStatus())
                .diagnosis(request.diagnosis())
                .icd10Codes(normalizeCodes(request.icd10Codes()))
                .examinationPlan(request.examinationPlan())
                .recommendations(request.recommendations())
                .prescriptions(request.prescriptions())
                .nextVisitDate(request.nextVisitDate())
                .build();

        MedicalRecord saved = recordRepository.save(record);
        log.info("Created MedicalRecord id={} for patientId={}, doctorId={}, appointmentId={}",
                saved.getId(), patient.getId(), doctor.getId(),
                appointment != null ? appointment.getId() : null);
        return toResponse(saved);
    }

    @Transactional
    public MedicalRecordResponse updateRecord(Doctor doctor, Long recordId, MedicalRecordUpdateRequest request) {
        MedicalRecord record = recordRepository.findByIdAndDoctorId(recordId, doctor.getId())
                .orElseThrow(() -> {
                    log.warn("MedicalRecord not found: recordId={}, doctorId={}", recordId, doctor.getId());
                    return new EntityNotFoundException("Запись не найдена или принадлежит другому врачу");
                });

        if (request.title() != null && !request.title().isBlank()) {
            record.setTitle(request.title());
        }
        if (request.complaints() != null) {
            record.setComplaints(request.complaints());
        }
        if (request.anamnesisMorbi() != null) {
            record.setAnamnesisMorbi(request.anamnesisMorbi());
        }
        if (request.anamnesisVitae() != null) {
            record.setAnamnesisVitae(request.anamnesisVitae());
        }
        if (request.objectiveStatus() != null) {
            record.setObjectiveStatus(request.objectiveStatus());
        }
        if (request.localStatus() != null) {
            record.setLocalStatus(request.localStatus());
        }
        if (request.diagnosis() != null) {
            record.setDiagnosis(request.diagnosis());
        }
        // icd10Codes: null = не менять, иначе заменить целиком (включая пустой
        // список — очистить). Hibernate сам перерасчитает delta join-таблицы.
        if (request.icd10Codes() != null) {
            List<String> normalized = normalizeCodes(request.icd10Codes());
            record.getIcd10Codes().clear();
            record.getIcd10Codes().addAll(normalized);
        }
        if (request.examinationPlan() != null) {
            record.setExaminationPlan(request.examinationPlan());
        }
        if (request.recommendations() != null) {
            record.setRecommendations(request.recommendations());
        }
        if (request.prescriptions() != null) {
            record.setPrescriptions(request.prescriptions());
        }
        // Дата следующего визита: null = «не менять» (обычное правило PATCH).
        // Чтобы очистить дату — пока нет отдельного флага, фронт может не
        // показывать поле в режиме редактирования. Этого хватает для шаблона.
        if (request.nextVisitDate() != null) {
            record.setNextVisitDate(request.nextVisitDate());
        }

        return toResponse(recordRepository.save(record));
    }

    @Transactional
    public void deleteRecord(Doctor doctor, Long recordId) {
        MedicalRecord record = recordRepository.findByIdAndDoctorId(recordId, doctor.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена или принадлежит другому врачу"));
        recordRepository.delete(record);
    }

    /**
     * Тримит, отбрасывает пустые строки, удаляет дубликаты с сохранением порядка.
     * Принимаемая длина — до 20 символов (валидация на DTO-уровне).
     */
    private List<String> normalizeCodes(List<String> raw) {
        if (raw == null || raw.isEmpty()) return new ArrayList<>();
        LinkedHashSet<String> seen = new LinkedHashSet<>();
        for (String c : raw) {
            if (c == null) continue;
            String t = c.trim();
            if (!t.isEmpty()) seen.add(t);
        }
        return new ArrayList<>(seen);
    }

    private MedicalRecordResponse toResponse(MedicalRecord r) {
        List<String> codes = r.getIcd10Codes() == null
                ? Collections.emptyList()
                : List.copyOf(r.getIcd10Codes());
        List<Icd10CodeResponse> details = icd10Service.findByCodes(codes);
        return new MedicalRecordResponse(
                r.getId(),
                r.getPatient().getId(),
                r.getDoctor().getId(),
                r.getDoctor().getFullName(),
                r.getAppointment() != null ? r.getAppointment().getId() : null,
                r.getTitle(),
                r.getComplaints(),
                r.getAnamnesisMorbi(),
                r.getAnamnesisVitae(),
                r.getObjectiveStatus(),
                r.getLocalStatus(),
                r.getDiagnosis(),
                codes,
                details,
                r.getExaminationPlan(),
                r.getRecommendations(),
                r.getPrescriptions(),
                r.getNextVisitDate(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
}
