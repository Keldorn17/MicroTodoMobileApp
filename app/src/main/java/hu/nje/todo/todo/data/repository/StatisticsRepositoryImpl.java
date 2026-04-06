package hu.nje.todo.todo.data.repository;

import android.graphics.Color;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import hu.nje.todo.todo.data.source.TodoApi;
import hu.nje.todo.todo.domain.model.TodoStatisticsEntryResponse;
import hu.nje.todo.todo.domain.model.TodoStatisticsResponse;
import hu.nje.todo.todo.domain.repository.StatisticsRepository;
import hu.nje.todo.todo.presentation.util.ChartColors;
import retrofit2.Response;

public class StatisticsRepositoryImpl implements StatisticsRepository {

    private final TodoApi todoApi;

    @Inject
    public StatisticsRepositoryImpl(TodoApi todoApi) {
        this.todoApi = todoApi;
    }

    @Override
    public void fetchStatistics(StatisticsCallback callback) {
        new Thread(() -> {
            try {
                Response<TodoStatisticsResponse> ownRes = todoApi.getStatistics("OWN", "", 0, 1).execute();
                Response<TodoStatisticsResponse> sharedRes = todoApi.getStatistics("SHARED", "", 0, 1).execute();
                Response<TodoStatisticsResponse> prioritiesRes = todoApi.getStatistics("ALL", "priority", 0, 1).execute();

                if (ownRes.isSuccessful() && sharedRes.isSuccessful() && prioritiesRes.isSuccessful()) {
                    TodoStatisticsResponse own = ownRes.body();
                    TodoStatisticsResponse shared = sharedRes.body();
                    TodoStatisticsResponse priorities = prioritiesRes.body();

                    if (own != null && shared != null && priorities != null) {
                        PieData ownSharedData = createPieData(safeFloat(own.getTotal()), safeFloat(shared.getTotal()), "Own Todos", "Shared Todos", ChartColors.PURPLE, ChartColors.PINK);
                        PieData ownStatusData = createPieData(safeFloat(own.getFinished()), safeFloat(own.getUnfinished()), "Finished", "Unfinished", ChartColors.GREEN, ChartColors.RED);
                        PieData sharedStatusData = createPieData(safeFloat(shared.getFinished()), safeFloat(shared.getUnfinished()), "Finished", "Unfinished", ChartColors.GREEN, ChartColors.RED);

                        BarData groupedData = createGroupedBarData(own, shared);
                        BarData stackedData = createStackedBarData(priorities);

                        callback.onDataLoaded(ownSharedData, ownStatusData, sharedStatusData, groupedData, stackedData);
                    } else {
                        callback.onError("Üres válasz érkezett a szervertől.");
                    }
                } else {
                    callback.onError("API Hiba: " + ownRes.code() + " (Ellenőrizd a bejelentkezést/Tokent!)");
                }

            } catch (Exception e) {
                e.printStackTrace();
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private float safeFloat(Long value) {
        return value != null ? value.floatValue() : 0f;
    }

    private PieData createPieData(float val1, float val2, String label1, String label2, int color1, int color2) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (val1 > 0) entries.add(new PieEntry(val1, label1));
        if (val2 > 0) entries.add(new PieEntry(val2, label2));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(color1, color2);
        dataSet.setDrawValues(false);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.2f);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLineColor(Color.TRANSPARENT);
        dataSet.setSliceSpace(0f);
        return new PieData(dataSet);
    }

    private BarData createGroupedBarData(TodoStatisticsResponse own, TodoStatisticsResponse shared) {
        ArrayList<BarEntry> entriesTotal = new ArrayList<>();
        entriesTotal.add(new BarEntry(0f, safeFloat(own.getTotal())));
        entriesTotal.add(new BarEntry(1f, safeFloat(shared.getTotal())));
        entriesTotal.add(new BarEntry(2f, safeFloat(own.getTotal()) + safeFloat(shared.getTotal())));

        ArrayList<BarEntry> entriesFinished = new ArrayList<>();
        entriesFinished.add(new BarEntry(0f, safeFloat(own.getFinished())));
        entriesFinished.add(new BarEntry(1f, safeFloat(shared.getFinished())));
        entriesFinished.add(new BarEntry(2f, safeFloat(own.getFinished()) + safeFloat(shared.getFinished())));

        ArrayList<BarEntry> entriesUnfinished = new ArrayList<>();
        entriesUnfinished.add(new BarEntry(0f, safeFloat(own.getUnfinished())));
        entriesUnfinished.add(new BarEntry(1f, safeFloat(shared.getUnfinished())));
        entriesUnfinished.add(new BarEntry(2f, safeFloat(own.getUnfinished()) + safeFloat(shared.getUnfinished())));

        BarDataSet setTotal = createBarDataSet(entriesTotal, "Total", ChartColors.LIGHT_BLUE);
        BarDataSet setFinished = createBarDataSet(entriesFinished, "Finished", ChartColors.GREEN);
        BarDataSet setUnfinished = createBarDataSet(entriesUnfinished, "Unfinished", ChartColors.RED);

        BarData data = new BarData(setTotal, setFinished, setUnfinished);
        data.setBarWidth(0.28f);
        return data;
    }

    private BarData createStackedBarData(TodoStatisticsResponse priorities) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        float[][] defaultVals = new float[5][2];

        if (priorities.getStatistics() != null) {
            for (Map.Entry<String, TodoStatisticsEntryResponse> entry : priorities.getStatistics().entrySet()) {
                try {
                    int index = Integer.parseInt(entry.getKey());
                    if (index >= 0 && index <= 4) {
                        defaultVals[index][0] = safeFloat(entry.getValue().getFinished());
                        defaultVals[index][1] = safeFloat(entry.getValue().getUnfinished());
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        for (int i = 0; i < 5; i++) {
            entries.add(new BarEntry((float) i, defaultVals[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(ChartColors.GREEN, ChartColors.RED);
        dataSet.setDrawValues(false);
        dataSet.setHighlightEnabled(true);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);
        return data;
    }

    private BarDataSet createBarDataSet(ArrayList<BarEntry> entries, String label, int color) {
        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setDrawValues(false);
        dataSet.setHighlightEnabled(true);
        return dataSet;
    }
}
