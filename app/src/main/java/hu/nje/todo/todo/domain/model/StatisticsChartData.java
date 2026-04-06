package hu.nje.todo.todo.domain.model;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsChartData {
    private PieData ownShared;
    private PieData ownStatus;
    private PieData sharedStatus;
    private BarData grouped;
    private BarData stacked;
}