package hu.nje.todo.todo.presentation.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import hu.nje.todo.R;

public class ChartStyleHelper {

    public static int getDynamicColor(Context context, int lightColorRes, int darkColorRes) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            return ContextCompat.getColor(context, darkColorRes);
        } else {
            return ContextCompat.getColor(context, lightColorRes);
        }
    }

    public static void applyPieChartStyle(Context context, PieChart chart, String centerText) {
        int textColor = getDynamicColor(context, R.color.daisy_light_base_content, R.color.daisy_dark_base_content);

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
    }

    public static void applyGroupedBarChartStyle(Context context, BarChart chart) {
        int textColor = getDynamicColor(context, R.color.daisy_light_base_content, R.color.daisy_dark_base_content);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setScaleEnabled(false);
        chart.setHighlightPerTapEnabled(true);

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
        leftAxis.setAxisMaximum(100f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawAxisLine(false);
        chart.getAxisRight().setEnabled(false);
    }

    public static void applyStackedBarChartStyle(Context context, BarChart chart) {
        int textColor = getDynamicColor(context, R.color.daisy_light_base_content, R.color.daisy_dark_base_content);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setScaleEnabled(false);

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
        leftAxis.setAxisMaximum(120f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawAxisLine(false);
        chart.getAxisRight().setEnabled(false);
    }
}