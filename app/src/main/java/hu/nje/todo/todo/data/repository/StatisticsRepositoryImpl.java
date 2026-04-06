package hu.nje.todo.todo.data.repository;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import hu.nje.todo.R;
import hu.nje.todo.todo.data.source.TodoApi;
import hu.nje.todo.todo.domain.model.QueryMode;
import hu.nje.todo.todo.domain.model.StatisticsChartData;
import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoResponse;
import hu.nje.todo.todo.domain.repository.StatisticsRepository;
import hu.nje.todo.todo.presentation.util.ChartStyleHelper;
import retrofit2.Call;
import retrofit2.Response;

public class StatisticsRepositoryImpl implements StatisticsRepository {

    private final TodoApi todoApi;
    private final Context context;

    @Inject
    public StatisticsRepositoryImpl(TodoApi todoApi, @ApplicationContext Context context) {
        this.todoApi = todoApi;
        this.context = context;
    }

    @Override
    public void fetchStatistics(StatisticsCallback callback) {
        CompletableFuture.supplyAsync(() -> executeSafe(
                todoApi.getTodosForCharts(QueryMode.ALL, 0, 1000, "", "")
        )).thenAccept(todosRes -> {
            try {
                if (todosRes != null && todosRes.isSuccessful()) {
                    TodoResponse pagedTodos = todosRes.body();

                    List<Todo> allTodos = (pagedTodos != null && pagedTodos.getContent() != null)
                            ? pagedTodos.getContent()
                            : new ArrayList<>();

                    long ownTotal = 0, ownFinished = 0, ownUnfinished = 0;
                    long sharedTotal = 0, sharedFinished = 0, sharedUnfinished = 0;

                    for (Todo todo : allTodos) {
                        boolean isCompleted = todo.getCompleted() != null && todo.getCompleted();
                        boolean isShared = todo.getShared() != null && todo.getShared();

                        if (isShared) {
                            sharedTotal++;
                            if (isCompleted) sharedFinished++;
                            else sharedUnfinished++;
                        } else {
                            ownTotal++;
                            if (isCompleted) ownFinished++;
                            else ownUnfinished++;
                        }
                    }

                    int colorPrimary = ChartStyleHelper.getDynamicColor(context, R.color.daisy_light_primary, R.color.daisy_dark_primary);
                    int colorSecondary = ChartStyleHelper.getDynamicColor(context, R.color.daisy_light_secondary, R.color.daisy_dark_secondary);
                    int colorAccent = ChartStyleHelper.getDynamicColor(context, R.color.daisy_light_accent, R.color.daisy_dark_accent);
                    int colorError = ChartStyleHelper.getDynamicColor(context, R.color.daisy_light_error, R.color.daisy_dark_error);

                    PieData ownSharedData = createPieData(ownTotal, sharedTotal,
                            context.getString(R.string.label_own),
                            context.getString(R.string.label_shared),
                            colorPrimary, colorSecondary);

                    PieData ownStatusData = createPieData(ownFinished, ownUnfinished,
                            context.getString(R.string.label_finished),
                            context.getString(R.string.label_unfinished),
                            colorAccent, colorError);

                    PieData sharedStatusData = createPieData(sharedFinished, sharedUnfinished,
                            context.getString(R.string.label_finished),
                            context.getString(R.string.label_unfinished),
                            colorAccent, colorError);

                    BarData groupedData = createGroupedBarData(ownTotal, ownFinished, ownUnfinished, sharedTotal, sharedFinished, sharedUnfinished, colorPrimary, colorAccent, colorError);
                    BarData stackedData = createLocalStackedBarData(allTodos, colorAccent, colorError);

                    StatisticsChartData chartData = StatisticsChartData.builder()
                            .ownShared(ownSharedData)
                            .ownStatus(ownStatusData)
                            .sharedStatus(sharedStatusData)
                            .grouped(groupedData)
                            .stacked(stackedData)
                            .build();

                    callback.onDataLoaded(chartData);
                } else {
                    callback.onError(context.getString(R.string.error_api_general));
                }
            } catch (Exception e) {
                callback.onError(context.getString(R.string.error_data_processing, e.getMessage()));
            }
        }).exceptionally(ex -> {
            callback.onError(context.getString(R.string.error_network, ex.getMessage()));
            return null;
        });
    }

    private <T> Response<T> executeSafe(Call<T> call) {
        try {
            return call.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private BarData createGroupedBarData(float ownTotal, float ownFinished, float ownUnfinished,
                                         float sharedTotal, float sharedFinished, float sharedUnfinished,
                                         int colorTotal, int colorFinished, int colorUnfinished) {

        ArrayList<BarEntry> entriesTotal = new ArrayList<>();
        entriesTotal.add(new BarEntry(0f, ownTotal));
        entriesTotal.add(new BarEntry(1f, sharedTotal));
        entriesTotal.add(new BarEntry(2f, ownTotal + sharedTotal));

        ArrayList<BarEntry> entriesFinished = new ArrayList<>();
        entriesFinished.add(new BarEntry(0f, ownFinished));
        entriesFinished.add(new BarEntry(1f, sharedFinished));
        entriesFinished.add(new BarEntry(2f, ownFinished + sharedFinished));

        ArrayList<BarEntry> entriesUnfinished = new ArrayList<>();
        entriesUnfinished.add(new BarEntry(0f, ownUnfinished));
        entriesUnfinished.add(new BarEntry(1f, sharedUnfinished));
        entriesUnfinished.add(new BarEntry(2f, ownUnfinished + sharedUnfinished));

        BarDataSet setTotal = createBarDataSet(entriesTotal, context.getString(R.string.label_total), colorTotal);
        BarDataSet setFinished = createBarDataSet(entriesFinished, context.getString(R.string.label_finished), colorFinished);
        BarDataSet setUnfinished = createBarDataSet(entriesUnfinished, context.getString(R.string.label_unfinished), colorUnfinished);

        BarData data = new BarData(setTotal, setFinished, setUnfinished);
        data.setBarWidth(0.28f);
        return data;
    }

    private BarData createLocalStackedBarData(List<Todo> todos, int colorFinished, int colorUnfinished) {
        if (todos == null || todos.isEmpty()) return null;

        Map<Integer, int[]> priorityCounts = new HashMap<>();

        for (Todo todo : todos) {
            int priority = todo.getPriority() != null ? todo.getPriority() : 0;
            boolean isCompleted = todo.getCompleted() != null && todo.getCompleted();

            priorityCounts.putIfAbsent(priority, new int[]{0, 0});

            if (isCompleted) {
                priorityCounts.get(priority)[0]++;
            } else {
                priorityCounts.get(priority)[1]++;
            }
        }

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (Map.Entry<Integer, int[]> entry : priorityCounts.entrySet()) {
            float xIndex = entry.getKey().floatValue();
            float finishedCount = entry.getValue()[0];
            float unfinishedCount = entry.getValue()[1];

            if (finishedCount > 0 || unfinishedCount > 0) {
                entries.add(new BarEntry(xIndex, new float[]{finishedCount, unfinishedCount}));
            }
        }

        if (entries.isEmpty()) return null;

        BarDataSet dataSet = new BarDataSet(entries, context.getString(R.string.title_statistics));
        dataSet.setColors(colorFinished, colorUnfinished);
        dataSet.setStackLabels(new String[]{
                context.getString(R.string.label_finished),
                context.getString(R.string.label_unfinished)
        });
        dataSet.setDrawValues(false);

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