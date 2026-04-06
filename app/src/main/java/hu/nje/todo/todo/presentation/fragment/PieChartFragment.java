package hu.nje.todo.todo.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import hu.nje.todo.databinding.FragmentStatisticsPieBinding;
import hu.nje.todo.todo.presentation.util.ChartStyleHelper;
import hu.nje.todo.todo.presentation.viewmodel.StatisticsViewModel;


@AndroidEntryPoint
public class PieChartFragment extends Fragment {

    private FragmentStatisticsPieBinding binding;
    private StatisticsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticsPieBinding.inflate(inflater, container, false);

        ChartStyleHelper.applyPieChartStyle(binding.pieChartOwnShared, "Own / Shared");
        ChartStyleHelper.applyPieChartStyle(binding.pieChartOwnStatus, "Status of\nOwn Todos");
        ChartStyleHelper.applyPieChartStyle(binding.pieChartSharedStatus, "Status of\nShared Todos");

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(StatisticsViewModel.class);

        viewModel.getOwnSharedPieData().observe(getViewLifecycleOwner(), data -> {
            updatePieVisibility(binding.pieChartOwnShared, binding.tvNoOwnSharedData, data);
        });

        viewModel.getOwnStatusPieData().observe(getViewLifecycleOwner(), data -> {
            updatePieVisibility(binding.pieChartOwnStatus, binding.tvNoOwnStatusData, data);
        });

        viewModel.getSharedStatusPieData().observe(getViewLifecycleOwner(), data -> {
            updatePieVisibility(binding.pieChartSharedStatus, binding.tvNoSharedData, data);
        });
    }
    private void updatePieVisibility(com.github.mikephil.charting.charts.PieChart chart, View emptyView, com.github.mikephil.charting.data.PieData data) {
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