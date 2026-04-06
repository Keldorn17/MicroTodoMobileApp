package hu.nje.todo.todo.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import hu.nje.todo.todo.domain.repository.StatisticsRepository;

@HiltViewModel
public class StatisticsViewModel extends ViewModel {

    private final StatisticsRepository repository;

    private final MutableLiveData<PieData> ownSharedPieData = new MutableLiveData<>();
    private final MutableLiveData<PieData> ownStatusPieData = new MutableLiveData<>();
    private final MutableLiveData<PieData> sharedStatusPieData = new MutableLiveData<>();
    private final MutableLiveData<BarData> groupedBarData = new MutableLiveData<>();
    private final MutableLiveData<BarData> stackedBarData = new MutableLiveData<>();

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public StatisticsViewModel(StatisticsRepository repository) {
        this.repository = repository;
    }

    public void loadStatistics() {
        repository.fetchStatistics(new StatisticsRepository.StatisticsCallback() {
            @Override
            public void onDataLoaded(PieData ownShared, PieData ownStatus, PieData sharedStatus, BarData grouped, BarData stacked) {
                ownSharedPieData.postValue(ownShared);
                ownStatusPieData.postValue(ownStatus);
                sharedStatusPieData.postValue(sharedStatus);
                groupedBarData.postValue(grouped);
                stackedBarData.postValue(stacked);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public LiveData<PieData> getOwnSharedPieData() { return ownSharedPieData; }
    public LiveData<PieData> getOwnStatusPieData() { return ownStatusPieData; }
    public LiveData<PieData> getSharedStatusPieData() { return sharedStatusPieData; }
    public LiveData<BarData> getGroupedBarData() { return groupedBarData; }
    public LiveData<BarData> getStackedBarData() { return stackedBarData; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}