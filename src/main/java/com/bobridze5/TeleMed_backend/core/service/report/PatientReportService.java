package com.bobridze5.TeleMed_backend.core.service.report;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;

import java.time.LocalDate;

public interface PatientReportService {
    byte[] generateReport(Patient patient, LocalDate from, LocalDate to, ReportType type);
}
