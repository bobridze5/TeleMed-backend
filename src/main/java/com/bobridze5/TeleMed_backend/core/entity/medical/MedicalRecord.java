package com.bobridze5.TeleMed_backend.core.entity.medical;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medical_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", referencedColumnName = "appointment_id")
    private Appointment appointment;

    @Column(name = "record_title", nullable = false, length = 255)
    private String title;

    @Column(name = "record_complaints", columnDefinition = "TEXT")
    private String complaints;

    /** Анамнез заболевания (anamnesis morbi) — история текущего заболевания. */
    @Column(name = "record_anamnesis_morbi", columnDefinition = "TEXT")
    private String anamnesisMorbi;

    /** Анамнез жизни (anamnesis vitae) — перенесённые болезни, наследственность, вредные привычки. */
    @Column(name = "record_anamnesis_vitae", columnDefinition = "TEXT")
    private String anamnesisVitae;

    /** Объективный статус (status praesens) — общее состояние, осмотр по системам. */
    @Column(name = "record_objective_status", columnDefinition = "TEXT")
    private String objectiveStatus;

    /** Локальный статус — описание зоны жалоб/поражения. */
    @Column(name = "record_local_status", columnDefinition = "TEXT")
    private String localStatus;

    @Column(name = "record_diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    /**
     * Список кодов по МКБ-10 (E11.9, I10, …). Хранится в отдельной join-таблице
     * {@code medical_record_icd10_codes}, чтобы можно было выбрать несколько
     * кодов и связывать их с записью без отдельной entity. Коды — свободные
     * строки, не FK на справочник {@code icd10_codes}: фронт обычно выбирает
     * из справочника, но врач может вписать кастомный код.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "medical_record_icd10_codes",
            joinColumns = @JoinColumn(name = "record_id")
    )
    @Column(name = "icd10_code", length = 20)
    @Builder.Default
    private List<String> icd10Codes = new ArrayList<>();

    /** План обследования — анализы, инструментальные методы, консультации. */
    @Column(name = "record_examination_plan", columnDefinition = "TEXT")
    private String examinationPlan;

    @Column(name = "record_recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "record_prescriptions", columnDefinition = "TEXT")
    private String prescriptions;

    /** Дата следующего визита (если врач назначил повторный приём). */
    @Column(name = "record_next_visit_date")
    private LocalDate nextVisitDate;

    @CreationTimestamp
    @Column(name = "record_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "record_updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
