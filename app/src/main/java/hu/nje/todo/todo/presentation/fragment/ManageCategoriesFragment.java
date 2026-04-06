package hu.nje.todo.todo.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.nje.todo.databinding.FragmentManageCategoriesBinding;
import hu.nje.todo.todo.presentation.util.CategoryAdapter;

public class ManageCategoriesFragment extends Fragment {

    private FragmentManageCategoriesBinding binding;
    private CategoryAdapter adapter;
    private final List<String> categories = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("categories")) {
            String[] initialCategories = getArguments().getStringArray("categories");
            if (initialCategories != null) {
                categories.addAll(Arrays.asList(initialCategories));
            }
        }

        setupRecyclerView();
        setupClickListeners();
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(category -> {
            categories.remove(category);
            adapter.setCategories(new ArrayList<>(categories));
            sendResult();
        });
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCategories.setAdapter(adapter);
        adapter.setCategories(new ArrayList<>(categories));
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        binding.btnDone.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        binding.btnAddCategory.setOnClickListener(v -> {
            String categoryName = binding.etCategoryName.getText() != null ? 
                    binding.etCategoryName.getText().toString().trim() : "";
            
            if (categoryName.isEmpty()) {
                binding.etCategoryName.setError("Category name is required");
                return;
            }

            if (categories.contains(categoryName)) {
                Toast.makeText(requireContext(), "Category already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            categories.add(categoryName);
            adapter.setCategories(new ArrayList<>(categories));
            binding.etCategoryName.setText("");
            sendResult();
        });
    }

    private void sendResult() {
        Bundle result = new Bundle();
        result.putStringArrayList("categories", new ArrayList<>(categories));
        getParentFragmentManager().setFragmentResult("category_request", result);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
