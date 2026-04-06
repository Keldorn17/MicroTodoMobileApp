package hu.nje.todo.todo.presentation.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dagger.hilt.android.AndroidEntryPoint;
import hu.nje.todo.databinding.FragmentTodoStatisticsBinding;
import hu.nje.todo.todo.presentation.util.StatisticsPagerAdapter;
import hu.nje.todo.todo.presentation.viewmodel.StatisticsViewModel;


@AndroidEntryPoint
public class TodoStatisticsFragment extends Fragment {

    private FragmentTodoStatisticsBinding binding;
    private StatisticsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTodoStatisticsBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);

        binding.viewPagerStatistics.setAdapter(new StatisticsPagerAdapter(this));
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadStatistics();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}