package hu.nje.todo.todo.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;

import dagger.hilt.android.AndroidEntryPoint;
import hu.nje.todo.R;
import hu.nje.todo.databinding.FragmentStatisticsBarBinding;
import hu.nje.todo.todo.presentation.util.ChartStyleHelper;
import hu.nje.todo.todo.presentation.viewmodel.StatisticsViewModel;

@AndroidEntryPoint
public class BarChartFragment extends Fragment {

    private FragmentStatisticsBarBinding binding;
    private StatisticsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticsBarBinding.inflate(inflater, container, false);

        ChartStyleHelper.applyGroupedBarChartStyle(binding.barChartGeneral,
                new String[]{
                        getString(R.string.label_own),
                        getString(R.string.label_shared),
                        getString(R.string.label_total)});
        ChartStyleHelper.applyStackedBarChartStyle(binding.barChartPriorities,
                new String[]{
                        getString(R.string.label_not_required),
                        getString(R.string.label_low),
                        getString(R.string.label_normal),
                        getString(R.string.label_high),
                        getString(R.string.label_critical)});

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(StatisticsViewModel.class);

        viewModel.getGroupedBarData().observe(getViewLifecycleOwner(), data ->{
                updatePieVisibility(binding.barChartGeneral, binding.tvNoGeneralBarData, data);
                binding.barChartGeneral.groupBars(0f, 0.1f, 0.02f);
        });

        viewModel.getStackedBarData().observe(getViewLifecycleOwner(), data ->
                updatePieVisibility(binding.barChartPriorities, binding.tvNoPriorityData, data));
    }

    private void updatePieVisibility(BarChart chart, View emptyView, BarData data) {
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