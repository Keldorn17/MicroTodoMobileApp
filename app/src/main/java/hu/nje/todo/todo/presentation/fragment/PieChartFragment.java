package hu.nje.todo.todo.presentation.fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import hu.nje.todo.R;
import hu.nje.todo.databinding.FragmentStatisticsPieBinding;
import hu.nje.todo.todo.presentation.util.ChartStyleHelper;
import hu.nje.todo.todo.presentation.viewmodel.StatisticsViewModel;

@AndroidEntryPoint
public class PieChartFragment extends Fragment {

    private FragmentStatisticsPieBinding binding;
    private StatisticsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticsPieBinding.inflate(inflater, container, false);
        ChartStyleHelper.applyPieChartStyle(binding.pieChartOwnShared,
                getString(R.string.statistics_own_shared));
        ChartStyleHelper.applyPieChartStyle(binding.pieChartOwnStatus,
                getString(R.string.statistics_status_own));
        ChartStyleHelper.applyPieChartStyle(binding.pieChartSharedStatus,
                getString(R.string.statistics_status_shared));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(StatisticsViewModel.class);
        viewModel.getOwnSharedData().observe(getViewLifecycleOwner(), data -> {
            updatePieVisibility(binding.pieChartOwnShared, binding.tvNoOwnSharedData, data);
        });
        viewModel.getOwnStatusData().observe(getViewLifecycleOwner(), data -> {
            updatePieVisibility(binding.pieChartOwnStatus, binding.tvNoOwnStatusData, data);
        });
        viewModel.getSharedStatusData().observe(getViewLifecycleOwner(), data -> {
            updatePieVisibility(binding.pieChartSharedStatus, binding.tvNoSharedData, data);
        });
    }

    private void updatePieVisibility(PieChart chart, View emptyView, PieData data) {
        if (data == null || data.getEntryCount() == 0) {
            chart.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            chart.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            chart.setData(data);
            chart.invalidate();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}