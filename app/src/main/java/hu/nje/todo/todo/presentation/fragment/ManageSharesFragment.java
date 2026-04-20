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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

    @Inject
    Gson gson;

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

        if (getArguments() != null && getArguments().containsKey("sharesJson")) {
            String sharesJson = getArguments().getString("sharesJson");
            if (sharesJson != null) {
                Type type = new TypeToken<List<TodoShareResponse>>(){}.getType();
                List<TodoShareResponse> initialShares = gson.fromJson(sharesJson, type);
                if (initialShares != null && viewModel.getShares().getValue() != null && viewModel.getShares().getValue().isEmpty()) {
                    viewModel.setInitialShares(initialShares);
                }
            }
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
                viewModel.deleteShareLocal(share.getEmail());
            }

            @Override
            public void onUpdateAccessLevel(TodoShareResponse share, int newLevel) {
                viewModel.shareTodoLocal(share.getEmail(), newLevel);
            }
        });
        binding.rvShares.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvShares.setAdapter(shareAdapter);
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        binding.btnDone.setOnClickListener(v -> {
            List<TodoShareResponse> currentShares = viewModel.getShares().getValue();
            if (currentShares != null) {
                Bundle result = new Bundle();
                result.putString("sharesJson", gson.toJson(currentShares));
                getParentFragmentManager().setFragmentResult("shares_request", result);
            }
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

            viewModel.shareTodoLocal(email, selectedLevel.getValue());
            binding.etShareEmail.setText("");
        });
    }

    private void observeViewModel() {
        viewModel.getShares().observe(getViewLifecycleOwner(), shares -> {
            shareAdapter.setShares(shares);
        });

        binding.sharesProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
