package hu.nje.todo.todo.presentation.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dagger.hilt.android.AndroidEntryPoint;
import hu.nje.todo.databinding.FragmentTodoStatisticsBinding;

@AndroidEntryPoint
public class TodoStatisticsFragment extends Fragment {

    private FragmentTodoStatisticsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTodoStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}