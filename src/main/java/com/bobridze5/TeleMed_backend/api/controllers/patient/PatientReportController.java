package com.bobridze5.TeleMed_backend.api.controllers.patient;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.report.PatientReportService;
import com.bobridze5.TeleMed_backend.core.service.report.ReportType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(API.PATIENT_ME_REPORT)
@RequiredArgsConstructor
@Tag(name = "Отчёт пациента", description = "Формирование PDF-отчётов по дневнику самоконтроля")
public class PatientReportController {

    private final PatientReportService reportService;

    @GetMapping(produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Скачать PDF-отчёт", description = "type: GENERAL | WEIGHT | GLYCEMIA | BLOOD_PRESSURE")
    public ResponseEntity<byte[]> getReport(
            @CurrentPatient Patient patient,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "GENERAL") ReportType type
    ) {
        byte[] pdf = reportService.generateReport(patient, from, to, type);
        String filename = "report_" + type.name().toLowerCase() + "_" + from + "_" + to + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
