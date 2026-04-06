package hu.nje.todo.todo.domain.repository;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;
public interface StatisticsRepository {

    interface StatisticsCallback {
        void onDataLoaded(PieData ownShared, PieData ownStatus, PieData sharedStatus, BarData grouped, BarData stacked);
        void onError(String message);
    }

    void fetchStatistics(StatisticsCallback callback);
}
