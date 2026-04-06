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

        ChartStyleHelper.applyGroupedBarChartStyle(requireContext(), binding.barChartGeneral);
        ChartStyleHelper.applyStackedBarChartStyle(requireContext(), binding.barChartPriorities);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(StatisticsViewModel.class);

        viewModel.getGroupedBarData().observe(getViewLifecycleOwner(), data -> {
            if (data == null || data.getYMax() <= 0) {
                binding.barChartGeneral.setVisibility(View.INVISIBLE);
                binding.tvNoGeneralBarData.setVisibility(View.VISIBLE);
            } else {
                binding.barChartGeneral.setVisibility(View.VISIBLE);
                binding.tvNoGeneralBarData.setVisibility(View.GONE);

                binding.barChartGeneral.setData(data);
                binding.barChartGeneral.groupBars(0f, 0.1f, 0.02f);
                binding.barChartGeneral.invalidate();
            }
        });

        viewModel.getStackedBarData().observe(getViewLifecycleOwner(), data -> {
            if (data == null || data.getYMax() <= 0) {
                binding.barChartPriorities.setVisibility(View.INVISIBLE);
                binding.tvNoPriorityData.setVisibility(View.VISIBLE);
            } else {
                binding.barChartPriorities.setVisibility(View.VISIBLE);
                binding.tvNoPriorityData.setVisibility(View.GONE);

                binding.barChartPriorities.setData(data);
                binding.barChartPriorities.invalidate();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}