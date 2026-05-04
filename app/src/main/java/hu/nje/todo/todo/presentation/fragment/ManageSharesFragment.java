package hu.nje.todo.todo.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import hu.nje.todo.databinding.FragmentManageSharesBinding;
import hu.nje.todo.todo.presentation.util.ShareAdapter;
import hu.nje.todo.todo.presentation.viewmodel.ManageSharesViewModel;

import android.widget.ArrayAdapter;
import hu.nje.todo.todo.domain.model.AccessLevel;
import hu.nje.todo.todo.domain.model.TodoShareResponse;
import java.util.ArrayList;
import java.util.List;

@AndroidEntryPoint
public class ManageSharesFragment extends Fragment {

    private FragmentManageSharesBinding binding;
    private ManageSharesViewModel viewModel;
    private ShareAdapter shareAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageSharesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ManageSharesViewModel.class);

        if (getArguments() != null && getArguments().containsKey("todoId")) {
            Long todoId = getArguments().getLong("todoId");
            viewModel.setTodoId(todoId);
            viewModel.loadShares();
        }

        setupRecyclerView();
        setupAccessLevelSpinner();
        setupClickListeners();
        observeViewModel();
    }

    private void setupAccessLevelSpinner() {
        List<String> accessLevelNames = new ArrayList<>();
        int writeIndex = 0;
        int i = 0;
        for (AccessLevel level : AccessLevel.values()) {
            if (level != AccessLevel.OWNER) {
                if (level == AccessLevel.WRITE) writeIndex = i;
                accessLevelNames.add(level.name());
                i++;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                accessLevelNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerAccessLevel.setAdapter(adapter);
        
        binding.spinnerAccessLevel.setSelection(writeIndex);
    }

    private void setupRecyclerView() {
        shareAdapter = new ShareAdapter(new ShareAdapter.OnShareActionListener() {
            @Override
            public void onDeleteShare(TodoShareResponse share) {
                viewModel.deleteShare(share.getEmail());
            }

            @Override
            public void onUpdateAccessLevel(TodoShareResponse share, int newLevel) {
                viewModel.shareTodo(share.getEmail(), newLevel);
            }
        });
        binding.rvShares.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvShares.setAdapter(shareAdapter);
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        binding.btnDone.setOnClickListener(v -> {
            getParentFragmentManager().setFragmentResult("shares_request", new Bundle());
            Navigation.findNavController(v).popBackStack();
        });

        binding.btnShare.setOnClickListener(v -> {
            String email = binding.etShareEmail.getText() != null ? 
                    binding.etShareEmail.getText().toString().trim() : "";
            
            if (email.isEmpty()) {
                binding.etShareEmail.setError("Email is required");
                return;
            }

            int selectedPosition = binding.spinnerAccessLevel.getSelectedItemPosition();
            AccessLevel selectedLevel = AccessLevel.values()[selectedPosition];

            viewModel.shareTodo(email, selectedLevel.getValue());
            binding.etShareEmail.setText("");
        });
    }

    private void observeViewModel() {
        viewModel.getShares().observe(getViewLifecycleOwner(), shares -> {
            shareAdapter.setShares(shares);
        });
        
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.sharesProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
