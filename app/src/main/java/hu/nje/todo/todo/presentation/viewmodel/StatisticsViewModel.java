package hu.nje.todo.todo.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.todo.domain.model.StatisticsChartData;
import hu.nje.todo.todo.domain.repository.StatisticsRepository;

@HiltViewModel
public class StatisticsViewModel extends ViewModel {

    private final StatisticsRepository repository;

    private final MutableLiveData<PieData> ownSharedData = new MutableLiveData<>();
    private final MutableLiveData<PieData> ownStatusData = new MutableLiveData<>();
    private final MutableLiveData<PieData> sharedStatusData = new MutableLiveData<>();
    private final MutableLiveData<BarData> groupedBarData = new MutableLiveData<>();
    private final MutableLiveData<BarData> stackedBarData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public StatisticsViewModel(StatisticsRepository repository) {
        this.repository = repository;
    }

    public void fetchStatistics() {
        repository.fetchStatistics(new StatisticsRepository.StatisticsCallback() {
            @Override
            public void onDataLoaded(StatisticsChartData chartData) {
                ownSharedData.postValue(chartData.getOwnShared());
                ownStatusData.postValue(chartData.getOwnStatus());
                sharedStatusData.postValue(chartData.getSharedStatus());
                groupedBarData.postValue(chartData.getGrouped());
                stackedBarData.postValue(chartData.getStacked());
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }


    public LiveData<PieData> getOwnSharedData() { return ownSharedData; }
    public LiveData<PieData> getOwnStatusData() { return ownStatusData; }
    public LiveData<PieData> getSharedStatusData() { return sharedStatusData; }
    public LiveData<BarData> getGroupedBarData() { return groupedBarData; }
    public LiveData<BarData> getStackedBarData() { return stackedBarData; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}