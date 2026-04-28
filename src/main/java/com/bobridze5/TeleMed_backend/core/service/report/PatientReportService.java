package com.bobridze5.TeleMed_backend.core.service.report;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.food.Meal;
import com.bobridze5.TeleMed_backend.core.entity.report.food.MealItem;
import com.bobridze5.TeleMed_backend.core.entity.report.glycemia.Glycemia;
import com.bobridze5.TeleMed_backend.core.entity.report.insulin.InsulinDose;
import com.bobridze5.TeleMed_backend.core.entity.report.pressure.BloodPressure;
import com.bobridze5.TeleMed_backend.core.entity.report.symptom.Symptom;
import com.bobridze5.TeleMed_backend.core.entity.report.weight.Weight;
import com.bobridze5.TeleMed_backend.core.repository.BloodPressureRepository;
import com.bobridze5.TeleMed_backend.core.repository.GlycemiaRepository;
import com.bobridze5.TeleMed_backend.core.repository.InsulinRepository;
import com.bobridze5.TeleMed_backend.core.repository.MealRepository;
import com.bobridze5.TeleMed_backend.core.repository.SymptomRepository;
import com.bobridze5.TeleMed_backend.core.repository.WeightRepository;
import com.bobridze5.TeleMed_backend.core.service.utils.Constant.Nutrition;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientReportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final Color COLOR_HEADER_BG = new Color(220, 230, 245);
    private static final Color COLOR_ALT_ROW = new Color(248, 248, 248);

    private final WeightRepository weightRepository;
    private final GlycemiaRepository glycemiaRepository;
    private final BloodPressureRepository bloodPressureRepository;
    private final SymptomRepository symptomRepository;
    private final MealRepository mealRepository;
    private final InsulinRepository insulinRepository;
    private final ChartService chartService;

    public byte[] generateReport(Patient patient, LocalDate from, LocalDate to, ReportType type) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        List<Weight> weights = (type == ReportType.GENERAL || type == ReportType.WEIGHT)
                ? weightRepository.findByPatientIdAndCreatedAtBetweenOrderByCreatedAtAsc(patient.getId(), start, end)
                : List.of();
        List<Glycemia> glycemiaList = (type == ReportType.GENERAL || type == ReportType.GLYCEMIA)
                ? glycemiaRepository.findByPatientIdAndCreatedAtBetweenOrderByCreatedAtAsc(patient.getId(), start, end)
                : List.of();
        List<BloodPressure> bloodPressures = (type == ReportType.GENERAL || type == ReportType.BLOOD_PRESSURE)
                ? bloodPressureRepository.findByPatientIdAndCreatedAtBetweenOrderByCreatedAtAsc(patient.getId(), start, end)
                : List.of();
        List<Symptom> symptoms = (type == ReportType.GENERAL)
                ? symptomRepository.findByPatientIdAndCreatedAtBetweenOrderByCreatedAtAsc(patient.getId(), start, end)
                : List.of();
        List<Meal> meals = (type == ReportType.GENERAL || type == ReportType.NUTRITION)
                ? mealRepository.findByPatientIdAndMealDatetimeBetweenOrderByMealDatetimeAsc(patient.getId(), start, end)
                : List.of();
        List<InsulinDose> insulinDoses = (type == ReportType.GENERAL || type == ReportType.INSULIN)
                ? insulinRepository.findByPatientIdAndTakenAtBetweenOrderByTakenAtAsc(patient.getId(), start, end)
                : List.of();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, out);
            doc.open();

            BaseFont bf = loadBaseFont();
            Font fTitle = new Font(bf, 16, Font.BOLD);
            Font fSubtitle = new Font(bf, 10, Font.NORMAL, new Color(100, 100, 100));
            Font fSection = new Font(bf, 11, Font.BOLD);
            Font fNormal = new Font(bf, 10, Font.NORMAL);
            Font fSmall = new Font(bf, 9, Font.NORMAL, new Color(70, 70, 70));
            Font fSmallBold = new Font(bf, 9, Font.BOLD);

            Paragraph title = new Paragraph(reportTitle(type), fTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(2);
            doc.add(title);

            Paragraph period = new Paragraph(from.format(DATE_FMT) + " — " + to.format(DATE_FMT), fSubtitle);
            period.setAlignment(Element.ALIGN_CENTER);
            period.setSpacingAfter(6);
            doc.add(period);

            doc.add(buildPatientInfo(patient, fNormal, fSmall));
            doc.add(gap());

            if (!weights.isEmpty()) {
                doc.add(sectionHeader("Вес", fSection));
                addChart(doc, chartService.createWeightChart(weights));
                doc.add(buildSimpleStatsTable(
                        weights.stream().mapToDouble(Weight::getValue).toArray(),
                        "кг", "%.1f", fSmallBold, fNormal));
                doc.add(gap());
            }

            if (!glycemiaList.isEmpty()) {
                long outOfRange = glycemiaList.stream()
                        .filter(g -> g.getLevel() < patient.getTargetLow() || g.getLevel() > patient.getTargetHigh())
                        .count();
                doc.add(sectionHeader("Гликемия", fSection));
                addChart(doc, chartService.createGlycemiaChart(glycemiaList, patient.getTargetLow(), patient.getTargetHigh()));
                doc.add(buildSimpleStatsTable(
                        glycemiaList.stream().mapToDouble(Glycemia::getLevel).toArray(),
                        "ммоль/л", "%.1f", fSmallBold, fNormal));
                Paragraph targetLine = new Paragraph(
                        "Целевой диапазон: " + patient.getTargetLow() + " – " + patient.getTargetHigh()
                                + " ммоль/л   |   Вне диапазона: " + outOfRange + " из " + glycemiaList.size(), fSmall);
                targetLine.setSpacingBefore(2);
                doc.add(targetLine);
                doc.add(gap());
            }

            if (!bloodPressures.isEmpty()) {
                doc.add(sectionHeader("Артериальное давление", fSection));
                addChart(doc, chartService.createBloodPressureChart(bloodPressures));
                doc.add(buildBpStatsTable(bloodPressures, fSmallBold, fNormal));
                doc.add(gap());
            }

            if (!symptoms.isEmpty()) {
                doc.add(sectionHeader("Симптомы", fSection));
                doc.add(buildSymptomsTable(symptoms, fSmallBold, fNormal, fSmall));
                doc.add(gap());
            }

            if (!meals.isEmpty()) {
                doc.add(sectionHeader("Питание", fSection));
                doc.add(buildMealsTable(meals, fSmallBold, fNormal, fSmall));
                doc.add(gap());
            }

            if (!insulinDoses.isEmpty()) {
                doc.add(sectionHeader("Инсулин", fSection));
                doc.add(buildInsulinDosesTable(insulinDoses, fSmallBold, fNormal, fSmall));
                doc.add(buildInsulinSummaryLine(insulinDoses, fSmall));
                doc.add(gap());
            }

            doc.add(sectionHeader("Анализ ИИ", fSection));
            doc.add(buildAiAnalysisBlock(fSmall, fNormal));
            doc.add(gap());

            if (weights.isEmpty() && glycemiaList.isEmpty() && bloodPressures.isEmpty()
                    && symptoms.isEmpty() && meals.isEmpty() && insulinDoses.isEmpty()) {
                doc.add(new Paragraph("Нет данных за выбранный период.", fNormal));
            }

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка генерации PDF-отчёта", e);
            return new byte[0];
        }
    }

    private String reportTitle(ReportType type) {
        return switch (type) {
            case WEIGHT -> "Отчёт: Вес";
            case GLYCEMIA -> "Отчёт: Гликемия";
            case BLOOD_PRESSURE -> "Отчёт: Артериальное давление";
            case NUTRITION -> "Отчёт: Питание";
            case INSULIN -> "Отчёт: Инсулин";
            case GENERAL -> "Дневник самоконтроля";
        };
    }


    private Table buildMealsTable(List<Meal> meals,
                                  Font headerFont, Font normalFont, Font smallFont) throws DocumentException {
        Table table = new Table(6);
        table.setWidths(new float[]{18, 12, 30, 10, 15, 15});
        table.setWidth(100);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.setPadding(3);
        addHeaderRow(table, new String[]{"Дата и время", "Тип", "Состав", "Ккал", "Углеводы, г", "ХЕ"}, headerFont);
        for (int i = 0; i < meals.size(); i++) {
            Meal m = meals.get(i);
            String composition = m.getItems() == null ? "—"
                    : m.getItems().stream()
                      .filter(it -> it != null && it.getDish() != null)
                      .map(this::formatMealItem)
                      .collect(Collectors.joining("; "));
            Double bu = Nutrition.toBreadUnits(m.totalCarbs());
            addDataRow(table, new String[]{
                    m.getMealDatetime() != null ? m.getMealDatetime().format(DATETIME_FMT) : "—",
                    m.getMealType() != null ? m.getMealType().name() : "—",
                    composition.isEmpty() ? "—" : composition,
                    String.format("%.0f", m.totalCalories()),
                    String.format("%.1f", m.totalCarbs()),
                    bu != null ? String.format("%.1f", bu) : "—"
            }, i % 2 == 1 ? smallFont : normalFont, i % 2 == 1);
        }
        return table;
    }

    private Table buildInsulinDosesTable(List<InsulinDose> doses,
                                         Font headerFont, Font normalFont, Font smallFont) throws DocumentException {
        Table table = new Table(5);
        table.setWidths(new float[]{18, 12, 12, 18, 40});
        table.setWidth(100);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.setPadding(3);
        addHeaderRow(table, new String[]{"Дата и время", "Тип", "Ед.", "Приём пищи", "Заметка"}, headerFont);
        for (int i = 0; i < doses.size(); i++) {
            InsulinDose d = doses.get(i);
            String mealLabel = d.getMeal() != null && d.getMeal().getMealType() != null
                    ? d.getMeal().getMealType().name()
                    : "—";
            addDataRow(table, new String[]{
                    d.getTakenAt() != null ? d.getTakenAt().format(DATETIME_FMT) : "—",
                    d.getInsulinType() != null ? d.getInsulinType().name() : "—",
                    d.getUnits() != null ? String.format("%.1f", d.getUnits()) : "—",
                    mealLabel,
                    d.getNote() != null ? d.getNote() : "—"
            }, i % 2 == 1 ? smallFont : normalFont, i % 2 == 1);
        }
        return table;
    }

    private Paragraph buildInsulinSummaryLine(List<InsulinDose> doses, Font font) {
        double totalShort = doses.stream()
                .filter(d -> d.getInsulinType() != null && d.getInsulinType().name().equals("SHORT"))
                .mapToDouble(d -> d.getUnits() != null ? d.getUnits() : 0.0).sum();
        double totalLong = doses.stream()
                .filter(d -> d.getInsulinType() != null && d.getInsulinType().name().equals("LONG"))
                .mapToDouble(d -> d.getUnits() != null ? d.getUnits() : 0.0).sum();
        double totalMix = doses.stream()
                .filter(d -> d.getInsulinType() != null && d.getInsulinType().name().equals("MIX"))
                .mapToDouble(d -> d.getUnits() != null ? d.getUnits() : 0.0).sum();
        double total = totalShort + totalLong + totalMix;

        Paragraph p = new Paragraph(String.format(
                "Итого за период: %.1f ед   |   Короткий: %.1f   |   Длинный: %.1f   |   Смешанный: %.1f",
                total, totalShort, totalLong, totalMix), font);
        p.setSpacingBefore(2);
        return p;
    }

    private String formatMealItem(MealItem item) {
        String name = item.getDish().getName();
        Double portion = item.getPortionGrams();
        Integer qty = item.getQuantity();
        StringBuilder sb = new StringBuilder(name);
        if (portion != null) sb.append(" ").append(String.format("%.0f", portion)).append(" г");
        if (qty != null && qty > 1) sb.append(" × ").append(qty);
        return sb.toString();
    }

    private Table buildSymptomsTable(List<Symptom> records,
                                     Font headerFont, Font normalFont, Font smallFont) throws DocumentException {
        Table table = new Table(3);
        table.setWidth(100);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.setPadding(3);
        addHeaderRow(table, new String[]{"Дата и время", "Тяжесть", "Описание"}, headerFont);
        for (int i = 0; i < records.size(); i++) {
            Symptom s = records.get(i);
            addDataRow(table, new String[]{
                    s.getCreatedAt().format(DATETIME_FMT),
                    s.getSeverity() != null ? s.getSeverity().name() : "—",
                    s.getDescription() != null ? s.getDescription() : "—"
            }, i % 2 == 1 ? smallFont : normalFont, i % 2 == 1);
        }
        return table;
    }


    private Table buildSimpleStatsTable(double[] values, String unit, String fmt,
                                        Font headerFont, Font normalFont) throws DocumentException {
        double min = Double.MAX_VALUE, max = -Double.MAX_VALUE, sum = 0;
        for (double v : values) {
            if (v < min) min = v;
            if (v > max) max = v;
            sum += v;
        }
        double avg = sum / values.length;

        Table table = new Table(5);
        table.setWidth(100);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.setPadding(3);
        addHeaderRow(table, new String[]{"Показатель", "Мин", "Макс", "Среднее", "Записей"}, headerFont);
        addDataRow(table, new String[]{
                unit,
                String.format(fmt, min),
                String.format(fmt, max),
                String.format(fmt, avg),
                String.valueOf(values.length)
        }, normalFont, false);
        return table;
    }

    private Table buildBpStatsTable(List<BloodPressure> records,
                                    Font headerFont, Font normalFont) throws DocumentException {
        Table table = new Table(5);
        table.setWidth(100);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.setPadding(3);
        addHeaderRow(table, new String[]{"Показатель", "Мин", "Макс", "Среднее", "Записей"}, headerFont);
        addDataRow(table, new String[]{
                "Систолическое",
                String.valueOf(records.stream().mapToInt(BloodPressure::getSystolic).min().orElse(0)),
                String.valueOf(records.stream().mapToInt(BloodPressure::getSystolic).max().orElse(0)),
                String.format("%.0f", records.stream().mapToInt(BloodPressure::getSystolic).average().orElse(0)),
                String.valueOf(records.size())
        }, normalFont, false);
        addDataRow(table, new String[]{
                "Диастолическое",
                String.valueOf(records.stream().mapToInt(BloodPressure::getDiastolic).min().orElse(0)),
                String.valueOf(records.stream().mapToInt(BloodPressure::getDiastolic).max().orElse(0)),
                String.format("%.0f", records.stream().mapToInt(BloodPressure::getDiastolic).average().orElse(0)),
                String.valueOf(records.size())
        }, normalFont, true);
        return table;
    }

    private Table buildAiAnalysisBlock(Font smallFont, Font normalFont) throws DocumentException {
        Table table = new Table(1);
        table.setWidth(100);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.setPadding(6);

        Cell header = new Cell(new Phrase("Автоматический анализ данных", smallFont));
        header.setBackgroundColor(new Color(235, 245, 255));
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.addCell(header);

        Cell body = new Cell(new Phrase(
                "Анализ на основе ИИ будет доступен в следующей версии.\n" +
                        "Здесь будут отображаться выявленные тенденции, отклонения от нормы\n" +
                        "и персонализированные наблюдения по данным дневника.", normalFont));
        body.setBackgroundColor(new Color(250, 252, 255));
        body.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.addCell(body);

        return table;
    }


    private Table buildPatientInfo(Patient patient, Font normalFont, Font labelFont) throws DocumentException {
        Table table = new Table(2);
        table.setWidths(new float[]{35, 65});
        table.setWidth(100);
        table.setBorder(Rectangle.NO_BORDER);
        table.setPadding(2);

        String fullName = patient.getFullName();
        addInfoRow(table, "ФИО:", fullName.isBlank() ? patient.getEmail() : fullName, labelFont, normalFont);
        addInfoRow(table, "Email:", patient.getEmail(), labelFont, normalFont);
        if (patient.getDateOfBirth() != null)
            addInfoRow(table, "Дата рождения:", patient.getDateOfBirth().format(DATE_FMT), labelFont, normalFont);
        if (patient.getGender() != null)
            addInfoRow(table, "Пол:", patient.getGender() == 'M' ? "Мужской" : "Женский", labelFont, normalFont);
        if (patient.getDiabetesType() != null)
            addInfoRow(table, "Тип диабета:", patient.getDiabetesType().name(), labelFont, normalFont);
        if (patient.getDiagnosisDate() != null)
            addInfoRow(table, "Дата диагноза:", patient.getDiagnosisDate().format(DATE_FMT), labelFont, normalFont);
        addInfoRow(table, "Инсулинозависимый:",
                Boolean.TRUE.equals(patient.getIsInsulinDependency()) ? "Да" : "Нет", labelFont, normalFont);
        addInfoRow(table, "Целевой диапазон гликемии:",
                patient.getTargetLow() + " – " + patient.getTargetHigh() + " ммоль/л", labelFont, normalFont);
        return table;
    }


    private void addHeaderRow(Table table, String[] headers, Font font) throws DocumentException {
        for (String h : headers) {
            Cell c = new Cell(new Phrase(h, font));
            c.setBackgroundColor(COLOR_HEADER_BG);
            c.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table.addCell(c);
        }
    }

    private void addDataRow(Table table, String[] values, Font font, boolean alt) throws DocumentException {
        for (String v : values) {
            Cell c = new Cell(new Phrase(v != null ? v : "—", font));
            c.setHorizontalAlignment(HorizontalAlignment.CENTER);
            if (alt) c.setBackgroundColor(COLOR_ALT_ROW);
            table.addCell(c);
        }
    }

    private void addInfoRow(Table table, String label, String value,
                            Font labelFont, Font valueFont) throws DocumentException {
        Cell lc = new Cell(new Phrase(label, labelFont));
        lc.setBorder(Rectangle.NO_BORDER);
        Cell vc = new Cell(new Phrase(value != null ? value : "—", valueFont));
        vc.setBorder(Rectangle.NO_BORDER);
        table.addCell(lc);
        table.addCell(vc);
    }

    private Paragraph sectionHeader(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
        p.setSpacingAfter(2);
        return p;
    }

    private Paragraph gap() {
        Paragraph p = new Paragraph(" ");
        p.setLeading(6);
        return p;
    }

    private void addChart(Document doc, byte[] pngBytes) throws DocumentException, IOException {
        if (pngBytes == null || pngBytes.length == 0) return;
        Image img = Image.getInstance(pngBytes);
        img.scaleToFit(ChartService.PDF_WIDTH, ChartService.PDF_HEIGHT);
        img.setAlignment(Element.ALIGN_CENTER);
        doc.add(img);
    }

    private BaseFont loadBaseFont() {
        try (InputStream is = getClass().getResourceAsStream("/fonts/DejaVuSans.ttf")) {
            if (is != null) {
                byte[] bytes = is.readAllBytes();
                return BaseFont.createFont("DejaVuSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, bytes, null);
            }
        } catch (Exception e) {
            log.debug("DejaVuSans.ttf не найден в ресурсах, пробуем системный шрифт");
        }
        for (String path : new String[]{
                "C:/Windows/Fonts/arial.ttf",
                "C:/Windows/Fonts/times.ttf",
                "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
                "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf"
        }) {
            try {
                return BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception ignored) {
            }
        }
        throw new RuntimeException("Не удалось найти шрифт с поддержкой кириллицы");
    }
}
