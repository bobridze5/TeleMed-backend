package com.bobridze5.TeleMed_backend.core.service.medcard;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository recordRepository;
    private final AppointmentRepository appointmentRepository;

    public Page<MedicalRecordResponse> getRecordsForPatient(Patient patient, int page, int size) {
        return recordRepository
                .findByPatientId(patient.getId(), PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toResponse);
    }

    public List<MedicalRecordResponse> getLatestRecordsForPatient(Patient patient) {
        return recordRepository.findTop5ByPatientIdOrderByCreatedAtDesc(patient.getId())
                .stream().map(this::toResponse).toList();
    }

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
                .diagnosis(request.diagnosis())
                .recommendations(request.recommendations())
                .prescriptions(request.prescriptions())
                .build();

        return toResponse(recordRepository.save(record));
    }

    @Transactional
    public MedicalRecordResponse updateRecord(Doctor doctor, Long recordId, MedicalRecordUpdateRequest request) {
        MedicalRecord record = recordRepository.findByIdAndDoctorId(recordId, doctor.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена или принадлежит другому врачу"));

        if (request.title() != null && !request.title().isBlank()) {
            record.setTitle(request.title());
        }
        if (request.complaints() != null) {
            record.setComplaints(request.complaints());
        }
        if (request.diagnosis() != null) {
            record.setDiagnosis(request.diagnosis());
        }
        if (request.recommendations() != null) {
            record.setRecommendations(request.recommendations());
        }
        if (request.prescriptions() != null) {
            record.setPrescriptions(request.prescriptions());
        }

        return toResponse(recordRepository.save(record));
    }

    @Transactional
    public void deleteRecord(Doctor doctor, Long recordId) {
        MedicalRecord record = recordRepository.findByIdAndDoctorId(recordId, doctor.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена или принадлежит другому врачу"));
        recordRepository.delete(record);
    }

    private MedicalRecordResponse toResponse(MedicalRecord r) {
        return new MedicalRecordResponse(
                r.getId(),
                r.getPatient().getId(),
                r.getDoctor().getId(),
                r.getDoctor().getFullName(),
                r.getAppointment() != null ? r.getAppointment().getId() : null,
                r.getTitle(),
                r.getComplaints(),
                r.getDiagnosis(),
                r.getRecommendations(),
                r.getPrescriptions(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
}
