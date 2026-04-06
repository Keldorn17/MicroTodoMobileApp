package hu.nje.todo.todo.presentation.util;

import android.graphics.Color;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

public class ChartStyleHelper {

    public static void applyPieChartStyle(PieChart chart, String centerText) {
        chart.setCenterText(centerText);
        chart.setCenterTextColor(Color.BLACK);
        chart.setCenterTextSize(14f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setHoleRadius(72f);
        chart.setTransparentCircleRadius(0f);
        chart.setDrawEntryLabels(true);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(12f);
        chart.setExtraOffsets(35f, 10f, 35f, 10f);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setRotationEnabled(false);
//        chart.setHighlightPerTapEnabled(false);
    }

    public static void applyGroupedBarChartStyle(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setScaleEnabled(false);
        chart.setHighlightPerTapEnabled(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(3f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Own", "Shared", "All"}));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawAxisLine(false);
        chart.getAxisRight().setEnabled(false);
    }

    public static void applyStackedBarChartStyle(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setScaleEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Not\nRequired", "Low", "Normal", "High", "Critical"}));
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(4.5f);

        chart.setXAxisRenderer(new MultiLineXAxisRenderer(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT)));
        chart.setExtraBottomOffset(15f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(120f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawAxisLine(false);
        chart.getAxisRight().setEnabled(false);
    }
}