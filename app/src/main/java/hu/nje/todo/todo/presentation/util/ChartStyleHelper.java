package hu.nje.todo.todo.presentation.util;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.color.MaterialColors;

import hu.nje.todo.R;

public class ChartStyleHelper {

    public static void applyPieChartStyle(PieChart chart, String centerText) {
        int textColor = MaterialColors.getColor(chart, com.google.android.material.R.attr.colorOnSurface);

        chart.setCenterText(centerText);
        chart.setCenterTextColor(textColor);
        chart.setCenterTextSize(14f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setHoleRadius(72f);
        chart.setTransparentCircleRadius(0f);
        chart.setDrawEntryLabels(true);
        chart.setEntryLabelColor(textColor);
        chart.setEntryLabelTextSize(12f);
        chart.setExtraOffsets(35f, 10f, 35f, 10f);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);
        chart.setMarker(new ChartMarkerView(chart.getContext(), R.layout.layout_chart_marker));
    }

    public static void applyGroupedBarChartStyle(Context context, BarChart chart) {
        int textColor = MaterialColors.getColor(chart, com.google.android.material.R.attr.colorOnSurface);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setScaleEnabled(false);
        chart.setHighlightPerTapEnabled(true);
        chart.setMarker(new ChartMarkerView(chart.getContext(), R.layout.layout_chart_marker));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(textColor);
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(3f);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{
                context.getString(R.string.label_own),
                context.getString(R.string.label_shared),
                context.getString(R.string.label_total)
        }));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(textColor);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawAxisLine(false);
        chart.getAxisRight().setEnabled(false);
    }

    public static void applyStackedBarChartStyle(Context context, BarChart chart) {
        int textColor = MaterialColors.getColor(chart, com.google.android.material.R.attr.colorOnSurface);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setScaleEnabled(false);
        chart.setHighlightPerTapEnabled(true);
        chart.setMarker(new ChartMarkerView(chart.getContext(), R.layout.layout_chart_marker));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(textColor);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(textColor);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Not\nRequired", "Low", "Normal", "High", "Critical"}));

        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(4.5f);

        chart.setXAxisRenderer(new MultiLineXAxisRenderer(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT)));
        chart.setExtraBottomOffset(15f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(textColor);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawAxisLine(false);
        chart.getAxisRight().setEnabled(false);
    }
}