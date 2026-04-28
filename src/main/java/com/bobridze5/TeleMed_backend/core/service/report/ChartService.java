package com.bobridze5.TeleMed_backend.core.service.report;

import com.bobridze5.TeleMed_backend.core.entity.report.pressure.BloodPressure;
import com.bobridze5.TeleMed_backend.core.entity.report.glycemia.Glycemia;
import com.bobridze5.TeleMed_backend.core.entity.report.weight.Weight;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.stereotype.Service;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
public class ChartService {
    private static final int WIDTH = 1040;
    private static final int HEIGHT = 560;

    public static final int PDF_WIDTH = WIDTH / 2;
    public static final int PDF_HEIGHT = HEIGHT / 2;

    private static final Font CHART_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
    private static final Font TITLE_FONT = CHART_FONT.deriveFont(22f);
    private static final Font LABEL_FONT = CHART_FONT.deriveFont(17f);

    private static final Color BACKGROUND = Color.WHITE;
    private static final Color PLOT_BG = new Color(245, 245, 245);
    private static final Color GRID = Color.LIGHT_GRAY;
    private static final Color SERIES_BLUE = new Color(66, 133, 244);
    private static final Color SERIES_RED = new Color(220, 50, 50);
    private static final Color SERIES_NAVY = new Color(50, 100, 220);
    private static final Color TARGET_FILL = new Color(100, 200, 100, 60);
    private static final Color TARGET_LINE = new Color(50, 150, 50);

    private static final RectangleInsets PLOT_INSETS = new RectangleInsets(30, 20, 8, 30);
    private static final RectangleInsets TITLE_PADDING = new RectangleInsets(6, 0, 4, 0);

    private static final SimpleDateFormat DATE_AXIS_FORMAT = new SimpleDateFormat("dd.MM HH:mm");
    private static final SimpleDateFormat ITEM_LABEL_FORMAT = new SimpleDateFormat("dd.MM.yy");

    private static final BasicStroke SERIES_STROKE = new BasicStroke(2f);

    public byte[] createWeightChart(List<Weight> records) {
        return buildSingleSeriesChart(
                records, "Вес", "Вес (кг)", "кг", "0.0",
                Weight::getCreatedAt, Weight::getValue,
                SERIES_BLUE, null
        );
    }

    public byte[] createGlycemiaChart(List<Glycemia> records, double targetLow, double targetHigh) {
        return buildSingleSeriesChart(
                records, "Гликемия", "Гликемия (ммоль/л)", "ммоль/л", "0.0",
                Glycemia::getCreatedAt, Glycemia::getLevel,
                SERIES_BLUE,
                plot -> {
                    IntervalMarker zone = new IntervalMarker(targetLow, targetHigh);
                    zone.setPaint(TARGET_FILL);
                    plot.addRangeMarker(zone, Layer.BACKGROUND);
                    plot.addRangeMarker(new ValueMarker(targetLow, TARGET_LINE, SERIES_STROKE));
                    plot.addRangeMarker(new ValueMarker(targetHigh, TARGET_LINE, SERIES_STROKE));
                }
        );
    }

    public byte[] createBloodPressureChart(List<BloodPressure> records) {
        TimeSeries systolic = new TimeSeries("Систолическое");
        TimeSeries diastolic = new TimeSeries("Диастолическое");
        records.forEach(bp -> {
            Millisecond ms = toMs(bp.getCreatedAt());
            systolic.addOrUpdate(ms, bp.getSystolic());
            diastolic.addOrUpdate(ms, bp.getDiastolic());
        });

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(systolic);
        dataset.addSeries(diastolic);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Артериальное давление (мм рт. ст.)", "Дата", "мм рт. ст.",
                dataset, true, false, false
        );
        applyBaseStyle(chart, "0", false);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        renderer.setSeriesPaint(0, SERIES_RED);
        renderer.setSeriesPaint(1, SERIES_NAVY);

        return encode(chart);
    }

    private <T> byte[] buildSingleSeriesChart(
            List<T> records,
            String seriesName,
            String chartTitle,
            String yAxisLabel,
            String valuePattern,
            Function<T, LocalDateTime> timeExtractor,
            Function<T, Number> valueExtractor,
            Color seriesColor,
            java.util.function.Consumer<XYPlot> plotCustomizer
    ) {
        TimeSeries series = new TimeSeries(seriesName);
        records.forEach(r -> series.addOrUpdate(toMs(timeExtractor.apply(r)), valueExtractor.apply(r)));

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                chartTitle, "Дата", yAxisLabel,
                new TimeSeriesCollection(series), false, false, false
        );
        applyBaseStyle(chart, valuePattern, true);

        XYPlot plot = chart.getXYPlot();
        ((XYLineAndShapeRenderer) plot.getRenderer()).setSeriesPaint(0, seriesColor);

        if (plotCustomizer != null) {
            plotCustomizer.accept(plot);
        }
        return encode(chart);
    }

    private void applyBaseStyle(JFreeChart chart, String valuePattern, boolean withItemLabels) {
        chart.setBackgroundPaint(BACKGROUND);

        if (chart.getTitle() != null) {
            chart.getTitle().setFont(TITLE_FONT);
            chart.getTitle().setPadding(TITLE_PADDING);
        }

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(PLOT_BG);
        plot.setDomainGridlinePaint(GRID);
        plot.setRangeGridlinePaint(GRID);
        plot.setInsets(PLOT_INSETS);

        DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
        dateAxis.setDateFormatOverride(DATE_AXIS_FORMAT);
        dateAxis.setUpperMargin(0.05);
        dateAxis.setLowerMargin(0.05);
        dateAxis.setTickLabelFont(CHART_FONT);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setUpperMargin(0.25);
        rangeAxis.setLowerMargin(0.15);
        rangeAxis.setTickLabelFont(CHART_FONT);

        plot.setRenderer(buildRenderer(valuePattern, withItemLabels));
    }

    private static XYLineAndShapeRenderer buildRenderer(String valuePattern, boolean withItemLabels) {
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesStroke(0, SERIES_STROKE);

        if (withItemLabels) {
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator(
                    "{2}", ITEM_LABEL_FORMAT, new DecimalFormat(valuePattern)
            ));
            renderer.setDefaultItemLabelFont(LABEL_FONT);
            renderer.setDefaultPositiveItemLabelPosition(
                    new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER)
            );
        }
        return renderer;
    }

    private Millisecond toMs(LocalDateTime ldt) {
        return new Millisecond(new Date(ldt.toInstant(ZoneOffset.UTC).toEpochMilli()));
    }

    private byte[] encode(JFreeChart chart) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(out, chart, WIDTH, HEIGHT);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Ошибка генерации графика", e);
            throw new IllegalStateException("Не удалось сгенерировать график", e);
        }
    }
}
