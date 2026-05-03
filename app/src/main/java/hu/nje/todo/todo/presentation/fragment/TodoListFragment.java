package hu.nje.todo.todo.presentation.fragment;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import javax.inject.Inject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
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

    @Inject
    Gson gson;

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
                Bundle bundle = new Bundle();
                bundle.putLong("todoId", item.getId());
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.todoEditorFragment, bundle);
            }

            @Override
            public void onCheckboxToggled(Todo item, boolean isChecked) {
                item.setCompleted(isChecked);
                viewModel.updateTodoStatus(item.getId(), isChecked);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TodoListViewModel.class);
        initializeSwipeRefresh();
        initializeProgressBar();
        initializeRecyclerView();
        initializeTitle();
        initializeStatistics();
        initializeSearch();
        initializePagination();
        loadArgs();
        viewModel.fetchTodos();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel = null;
    }

    private void initializePagination() {
        viewModel.getTodos().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getPage() != null) {
                int totalPages = response.getPage().getTotalPages() != null
                                 ? response.getPage().getTotalPages() : 1;
                int currentPage =
                        response.getPage().getNumber() != null ? response.getPage().getNumber() : 0;
                if (totalPages > 0) {
                    binding.paginationGroup.setVisibility(View.VISIBLE);
                    updatePaginationUI(currentPage, totalPages);
                } else {
                    binding.paginationGroup.setVisibility(View.GONE);
                }
            } else {
                binding.paginationGroup.setVisibility(View.GONE);
            }
        });
    }

    private void updatePaginationUI(int currentPage, int totalPages) {
        binding.btnPrev.setEnabled(currentPage > 0);
        binding.btnPrev.setOnClickListener(v -> {
            viewModel.setPage(currentPage - 1);
            binding.nestedScrollView.scrollTo(0, 0);
        });
        binding.btnNext.setEnabled(currentPage < totalPages - 1);
        binding.btnNext.setOnClickListener(v -> {
            viewModel.setPage(currentPage + 1);
            binding.nestedScrollView.scrollTo(0, 0);
        });
        int maxPagesToShow = 3;
        int startPage = Math.max(0, currentPage - 1);
        int endPage = Math.min(totalPages - 1, startPage + maxPagesToShow - 1);
        startPage = Math.max(0, endPage - maxPagesToShow + 1);
        MaterialButton[] pageButtons = {
                binding.btnPage1, binding.btnPage2, binding.btnPage3
        };
        configureEachElement(currentPage, pageButtons, startPage, endPage);
    }

    private void configureEachElement(int currentPage, MaterialButton[] pageButtons, int startPage,
            int endPage) {
        for (int i = 0; i < pageButtons.length; i++) {
            int pageIndex = startPage + i;
            MaterialButton btn = pageButtons[i];
            if (pageIndex <= endPage) {
                btn.setVisibility(View.VISIBLE);
                btn.setText(String.valueOf(pageIndex + 1));
                btn.setChecked(pageIndex == currentPage);
                final int targetPage = pageIndex;
                btn.setOnClickListener(v -> {
                    viewModel.setPage(targetPage);
                    binding.nestedScrollView.scrollTo(0, 0);
                });
                btn.setClickable(pageIndex != currentPage);
            } else {
                btn.setVisibility(View.GONE);
            }
        }
    }

    private void initializeSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> viewModel.fetchTodos());
    }

    private void initializeSearch() {
        binding.searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.setSearchQuery(v.getText().toString());
                return true;
            }
            return false;
        });
        binding.searchEditText.addTextChangedListener(getTextWatcher());
    }

    private TextWatcher getTextWatcher() {
        return new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                runnable = () -> {
                    if (viewModel != null) {
                        viewModel.setSearchQuery(s.toString());
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        };
    }

    private void initializeProgressBar() {
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (!binding.swipeRefreshLayout.isRefreshing()) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
            if (!isLoading) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });
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
                case SHARED ->
                        binding.fragmentTitle.setText(getString(R.string.title_shared_todos));
                case ALL -> binding.fragmentTitle.setText(getString(R.string.title_all_todos));
            }
        });
    }

    private void initializeStatistics() {
        viewModel.getStatistics().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                binding.totalTextView.setText(
                        getString(R.string.label_total_count, stats.getTotal()));
                binding.finishedTextView.setText(
                        getString(R.string.label_finished_count, stats.getFinished()));
                binding.unfinishedTextView.setText(
                        getString(R.string.label_unfinished_count, stats.getUnfinished()));
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