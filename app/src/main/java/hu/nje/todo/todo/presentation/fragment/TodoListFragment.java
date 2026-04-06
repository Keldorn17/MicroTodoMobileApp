package hu.nje.todo.todo.presentation.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dagger.hilt.android.AndroidEntryPoint;
import hu.nje.todo.R;
import hu.nje.todo.databinding.FragmentTodoListBinding;
import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.presentation.util.TodoAdapter;
import hu.nje.todo.todo.presentation.viewmodel.TodoListViewModel;

@AndroidEntryPoint
public class TodoListFragment extends Fragment {

    private FragmentTodoListBinding binding;
    private TodoListViewModel viewModel;
    private TodoAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentTodoListBinding.inflate(inflater, container, false);
        adapter = new TodoAdapter(new TodoAdapter.TodoClickListener() {
            @Override
            public void onCardClicked(Todo item) {
                // Note for Szabolcs:
                // Add code here to open edit page of a selected todo item.
            }

            @Override
            public void onCheckboxToggled(Todo item, boolean isChecked) {
                viewModel.updateTodoStatus(item.getId(), isChecked);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TodoListViewModel.class);
        initializeProgressBar();
        initializeRecyclerView();
        initializeTitle();
        loadArgs();
        viewModel.fetchTodos();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel = null;
    }

    private void initializeProgressBar() {
        viewModel.isLoading().observe(getViewLifecycleOwner(),
                isLoading -> binding.progressBar.setVisibility(
                        isLoading ? View.VISIBLE : View.GONE));
    }

    private void initializeRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
        viewModel.getTodos().observe(getViewLifecycleOwner(), todoResponse -> {
            if (todoResponse != null) {
                adapter.submitList(todoResponse.getContent());
            }
        });
    }

    private void initializeTitle() {
        viewModel.getQueryMode().observe(getViewLifecycleOwner(), mode -> {
            switch (mode) {
                case OWN -> binding.fragmentTitle.setText(getString(R.string.title_my_todos));
                case SHARED -> binding.fragmentTitle.setText(getString(R.string.title_shared_todos));
                case ALL -> binding.fragmentTitle.setText(getString(R.string.title_all_todos));
            }
        });
    }

    private void loadArgs() {
        if (getArguments() != null) {
            TodoListFragmentArgs args = TodoListFragmentArgs.fromBundle(getArguments());
            viewModel.setQueryMode(args.getQueryMode());
        }
    }

}