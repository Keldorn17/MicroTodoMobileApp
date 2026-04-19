package hu.nje.todo.todo.presentation.util;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import hu.nje.todo.R;

public class ChartMarkerView extends MarkerView {

    private final TextView tvContent;

    public ChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvMarkerContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight h) {
        String displayValue;
        if (e instanceof PieEntry) {
            displayValue = ( (PieEntry) e).getLabel() + ": " + (int) e.getY();
        } else if (e instanceof BarEntry && ((BarEntry) e).isStacked()) {
            float[] values = ((BarEntry) e).getYVals();
            int stackIndex = h.getStackIndex();
            if (stackIndex >= 0 && stackIndex < values.length) {

                String label = (stackIndex == 0) ? "Finished" : "Unfinished";
                displayValue = label + ": " + (int) values[stackIndex];
            } else {
                displayValue = "Total: " + (int) e.getY();
            }
        } else {
            displayValue = "Count: " + (int) e.getY();
        }

        tvContent.setText(displayValue);
        super.refreshContent(e, h);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}