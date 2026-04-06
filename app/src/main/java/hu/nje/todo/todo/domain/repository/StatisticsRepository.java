package hu.nje.todo.todo.domain.repository;

import hu.nje.todo.todo.domain.model.StatisticsChartData;

public interface StatisticsRepository {

    interface StatisticsCallback {
        void onDataLoaded(StatisticsChartData chartData);
        void onError(String message);
    }

    void fetchStatistics(StatisticsCallback callback);
}