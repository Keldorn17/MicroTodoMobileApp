package hu.nje.todo.todo.presentation.viewmodel;

import android.app.Application;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;
import hu.nje.todo.R;
import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.todo.domain.model.QueryMode;
import hu.nje.todo.todo.domain.model.SearchRequest;
import hu.nje.todo.todo.domain.model.TodoStatisticsEntryResponse;
import hu.nje.todo.todo.domain.model.TodoStatisticsResponse;
import hu.nje.todo.todo.domain.repository.TodoRepository;
import hu.nje.todo.todo.domain.usecase.GetTodoStatisticsUseCase;

@HiltViewModel
public class StatisticsViewModel extends AndroidViewModel {

    private final GetTodoStatisticsUseCase getTodoStatisticsUseCase;

    private final MutableLiveData<PieData> ownSharedData = new MutableLiveData<>();
    private final MutableLiveData<PieData> ownStatusData = new MutableLiveData<>();
    private final MutableLiveData<PieData> sharedStatusData = new MutableLiveData<>();
    private final MutableLiveData<BarData> groupedBarData = new MutableLiveData<>();
    private final MutableLiveData<BarData> stackedBarData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final int colorPrimary;
    private final int colorSecondary;
    private final int colorTertiary;
    private final int colorError;

    @Inject
    public StatisticsViewModel(@NonNull Application application, GetTodoStatisticsUseCase getTodoStatisticsUseCase) {
        super(application);
        this.getTodoStatisticsUseCase = getTodoStatisticsUseCase;

        this.colorPrimary = ContextCompat.getColor(application, R.color.priority_high);
        this.colorSecondary = ContextCompat.getColor(application, R.color.priority_normal);
        this.colorTertiary = ContextCompat.getColor(application, R.color.priority_low);
        this.colorError = ContextCompat.getColor(application, R.color.priority_critical);

    }
    public void fetchStatistics() {

        fetchOwnVsShared();
        fetchPriorityStatistics();
    }

    private void fetchOwnVsShared() {
        SearchRequest ownRequest = SearchRequest.builder().queryMode(QueryMode.OWN).build();
        getTodoStatisticsUseCase.execute(ownRequest, null, new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(TodoStatisticsResponse ownStats) {
                SearchRequest sharedRequest = SearchRequest.builder().queryMode(QueryMode.SHARED).build();
                getTodoStatisticsUseCase.execute(sharedRequest, null, new TodoRepository.TodoCallback<>() {
                    @Override
                    public void onSuccess(TodoStatisticsResponse sharedStats) {
                        processGeneralStats(ownStats, sharedStats);
                    }

                    @Override
                    public void onError(String message) {
                        errorMessage.postValue(message);
                    }
                });
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    private void processGeneralStats(TodoStatisticsResponse own, TodoStatisticsResponse shared) {
        long ownTotal = own.getTotal() != null ? own.getTotal() : 0;
        long ownFinished = own.getFinished() != null ? own.getFinished() : 0;
        long ownUnfinished = own.getUnfinished() != null ? own.getUnfinished() : 0;

        long sharedTotal = shared.getTotal() != null ? shared.getTotal() : 0;
        long sharedFinished = shared.getFinished() != null ? shared.getFinished() : 0;
        long sharedUnfinished = shared.getUnfinished() != null ? shared.getUnfinished() : 0;

        ownSharedData.postValue(createPieData(ownTotal, sharedTotal,
                getApplication().getString(R.string.label_own),
                getApplication().getString(R.string.label_shared),
                colorPrimary, colorSecondary));

        ownStatusData.postValue(createPieData(ownFinished, ownUnfinished,
                getApplication().getString(R.string.label_finished),
                getApplication().getString(R.string.label_unfinished),
                colorTertiary, colorError));

        sharedStatusData.postValue(createPieData(sharedFinished, sharedUnfinished,
                getApplication().getString(R.string.label_finished),
                getApplication().getString(R.string.label_unfinished),
                colorTertiary, colorError));

        groupedBarData.postValue(createGroupedBarData(ownTotal, ownFinished, ownUnfinished,
                sharedTotal, sharedFinished, sharedUnfinished));
    }

    private void fetchPriorityStatistics() {
        SearchRequest allRequest = SearchRequest.builder().queryMode(QueryMode.ALL).build();
        getTodoStatisticsUseCase.execute(allRequest, "priority", new TodoRepository.TodoCallback<>() {
            @Override
            public void onSuccess(TodoStatisticsResponse response) {
                if (response != null && response.getStatistics() != null) {
                    stackedBarData.postValue(createStackedBarData(response.getStatistics()));

                }
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    private PieData createPieData(float val1, float val2, String label1, String label2, int color1, int color2) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (val1 > 0) entries.add(new PieEntry(val1, label1));
        if (val2 > 0) entries.add(new PieEntry(val2, label2));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(color1, color2);
        dataSet.setDrawValues(false);
        dataSet.setHighlightEnabled(true);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.2f);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLineColor(Color.TRANSPARENT);
        dataSet.setSliceSpace(0f);
        return new PieData(dataSet);
    }

    private BarData createGroupedBarData(float ownTotal, float ownFinished, float ownUnfinished,
                                         float sharedTotal, float sharedFinished, float sharedUnfinished) {

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

        BarDataSet setTotal = createBarDataSet(entriesTotal, getApplication().getString(R.string.label_total), colorPrimary);
        BarDataSet setFinished = createBarDataSet(entriesFinished, getApplication().getString(R.string.label_finished), colorTertiary);
        BarDataSet setUnfinished = createBarDataSet(entriesUnfinished, getApplication().getString(R.string.label_unfinished), colorError);

        BarData data = new BarData(setTotal, setFinished, setUnfinished);
        data.setBarWidth(0.28f);
        return data;
    }

    private BarData createStackedBarData(Map<String, TodoStatisticsEntryResponse> stats) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        String[] labels = {"Not required", "Low", "Normal", "High", "Critical"};
        for (int i = 0; i < labels.length; i++) {
            TodoStatisticsEntryResponse entry = stats.get(labels[i]);
            float finished = 0, unfinished = 0;
            if (entry != null) {
                finished = entry.getFinished() != null ? entry.getFinished().floatValue() : 0f;
                unfinished = entry.getUnfinished() != null ? entry.getUnfinished().floatValue() : 0f;
            }
            entries.add(new BarEntry((float) i, new float[]{finished, unfinished}));
        }

        BarDataSet dataSet = new BarDataSet(entries, getApplication().getString(R.string.title_statistics));
        dataSet.setColors(colorTertiary, colorError);
        dataSet.setStackLabels(new String[]{
                getApplication().getString(R.string.label_finished),
                getApplication().getString(R.string.label_unfinished)
        });
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

    public LiveData<PieData> getOwnSharedData() { return ownSharedData; }
    public LiveData<PieData> getOwnStatusData() { return ownStatusData; }
    public LiveData<PieData> getSharedStatusData() { return sharedStatusData; }
    public LiveData<BarData> getGroupedBarData() { return groupedBarData; }
    public LiveData<BarData> getStackedBarData() { return stackedBarData; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}