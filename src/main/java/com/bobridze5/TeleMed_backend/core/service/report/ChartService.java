package com.bobridze5.TeleMed_backend.core.service.report;

import com.bobridze5.TeleMed_backend.core.entity.report.BloodPressure;
import com.bobridze5.TeleMed_backend.core.entity.report.Glycemia;
import com.bobridze5.TeleMed_backend.core.entity.report.Weight;
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
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ChartService {
    private static final int WIDTH  = 1040;
    private static final int HEIGHT = 560;

    public static final int PDF_WIDTH  = WIDTH  / 2;
    public static final int PDF_HEIGHT = HEIGHT / 2;

    public byte[] createWeightChart(List<Weight> records) {
        TimeSeries series = new TimeSeries("Вес");
        records.forEach(w -> series.addOrUpdate(
                toMs(w.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli()),
                w.getValue()
        ));

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Вес (кг)", "Дата", "кг",
                new TimeSeriesCollection(series), false, false, false
        );
        applyBaseStyle(chart, "0.0");
        return encode(chart);
    }

    public byte[] createGlycemiaChart(List<Glycemia> records, double targetLow, double targetHigh) {
        TimeSeries series = new TimeSeries("Гликемия");
        records.forEach(g -> series.addOrUpdate(
                toMs(g.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli()),
                g.getLevel()
        ));

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Гликемия (ммоль/л)", "Дата", "ммоль/л",
                new TimeSeriesCollection(series), false, false, false
        );
        applyBaseStyle(chart, "0.0");

        XYPlot plot = chart.getXYPlot();
        IntervalMarker targetZone = new IntervalMarker(targetLow, targetHigh);
        targetZone.setPaint(new Color(100, 200, 100, 60));
        plot.addRangeMarker(targetZone, Layer.BACKGROUND);
        plot.addRangeMarker(new ValueMarker(targetLow, new Color(50, 150, 50), new BasicStroke(2f)));
        plot.addRangeMarker(new ValueMarker(targetHigh, new Color(50, 150, 50), new BasicStroke(2f)));

        return encode(chart);
    }

    public byte[] createBloodPressureChart(List<BloodPressure> records) {
        TimeSeries systolic  = new TimeSeries("Систолическое");
        TimeSeries diastolic = new TimeSeries("Диастолическое");

        records.forEach(bp -> {
            Millisecond ms = toMs(bp.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
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
        applyBaseStyle(chart, "0");

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(220, 50, 50));
        renderer.setSeriesPaint(1, new Color(50, 100, 220));

        return encode(chart);
    }

    private void applyBaseStyle(JFreeChart chart, String valuePattern) {
        chart.setBackgroundPaint(Color.WHITE);

        if (chart.getTitle() != null) {
            chart.getTitle().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
            chart.getTitle().setPadding(new RectangleInsets(6, 0, 4, 0));
        }

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setInsets(new RectangleInsets(30, 20, 8, 30));

        DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
        dateAxis.setDateFormatOverride(new SimpleDateFormat("dd.MM HH:mm"));
        dateAxis.setUpperMargin(0.05);
        dateAxis.setLowerMargin(0.05);
        dateAxis.setTickLabelFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setUpperMargin(0.25);
        rangeAxis.setLowerMargin(0.15);
        rangeAxis.setTickLabelFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));

        XYLineAndShapeRenderer renderer = getXyLineAndShapeRenderer(valuePattern);

        plot.setRenderer(renderer);
    }

    private static @NonNull XYLineAndShapeRenderer getXyLineAndShapeRenderer(String valuePattern) {
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setSeriesPaint(0, new Color(66, 133, 244));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesStroke(0, new BasicStroke(2f));

        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator(
                "{2}", new SimpleDateFormat("dd.MM.yy"), new DecimalFormat(valuePattern)
        ));
        renderer.setDefaultItemLabelFont(new Font(Font.SANS_SERIF, Font.PLAIN, 17));
        renderer.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER)
        );
        return renderer;
    }

    private Millisecond toMs(long epochMilli) {
        return new Millisecond(new Date(epochMilli));
    }

    private byte[] encode(JFreeChart chart) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(out, chart, WIDTH, HEIGHT);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Ошибка генерации графика", e);
            return new byte[0];
        }
    }
}
