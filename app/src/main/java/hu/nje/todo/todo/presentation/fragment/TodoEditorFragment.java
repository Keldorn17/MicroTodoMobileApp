package hu.nje.todo.todo.presentation.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.color.MaterialColors;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import hu.nje.todo.R;
import hu.nje.todo.databinding.FragmentTodoEditorBinding;
import hu.nje.todo.todo.domain.model.Priority;
import hu.nje.todo.todo.domain.model.Todo;
import hu.nje.todo.todo.domain.model.TodoShareResponse;
import hu.nje.todo.todo.presentation.viewmodel.TodoEditorViewModel;

import hu.nje.todo.todo.domain.model.AccessLevel;

@AndroidEntryPoint
public class TodoEditorFragment extends Fragment {

    private FragmentTodoEditorBinding binding;
    private TodoEditorViewModel viewModel;

    @Inject
    Gson gson;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTodoEditorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TodoEditorViewModel.class);

        getParentFragmentManager().setFragmentResultListener("category_request", getViewLifecycleOwner(), (requestKey, result) -> {
            ArrayList<String> updatedCategories = result.getStringArrayList("categories");
            if (updatedCategories != null) {
                viewModel.setCategories(new java.util.HashSet<>(updatedCategories));
            }
        });

        getParentFragmentManager().setFragmentResultListener("shares_request", getViewLifecycleOwner(), (requestKey, result) -> {
            String sharesJson = result.getString("sharesJson");
            if (sharesJson != null) {
                Type type = new TypeToken<List<TodoShareResponse>>(){}.getType();
                List<TodoShareResponse> updatedShares = gson.fromJson(sharesJson, type);
                if (updatedShares != null) {
                    viewModel.setShares(updatedShares);
                }
            }
        });

        setupPrioritySpinner();
        setupClickListeners();
        observeViewModel();
        loadTodoIfEditing();
        
        // Restore UI state if already loaded (e.g. after theme change)
        if (viewModel.isLoaded()) {
            if (viewModel.getTodoId() != null) {
                binding.tvHeadline.setText("Edit Todo");
                binding.btnSave.setText("Save Todo");
            }
            if (!viewModel.canEdit()) {
                disableEditing();
            }
        }
    }

    private void loadTodoIfEditing() {
        if (viewModel.isLoaded()) return;

        if (getArguments() != null && getArguments().containsKey("todoJson")) {
            String todoJson = getArguments().getString("todoJson");
            if (todoJson != null && !todoJson.equals("null")) {
                Todo todo = gson.fromJson(todoJson, Todo.class);
                if (todo != null) {
                    viewModel.setTodoId(todo.getId());
                    binding.tvHeadline.setText("Edit Todo");
                    binding.btnSave.setText("Save Todo");
                    binding.etTitle.setText(todo.getTitle());
                    binding.etDescription.setText(todo.getDescription());
                    
                    if (todo.getCompleted() != null) {
                        binding.cbCompleted.setChecked(todo.getCompleted());
                    }

                    Priority priority = Priority.fromValue(todo.getPriority());
                    binding.spinnerPriority.setSelection(priority.ordinal());

                    if (todo.getDeadline() != null) {
                        ZonedDateTime localDeadline = todo.getDeadline().withZoneSameInstant(ZoneId.systemDefault());
                        viewModel.setDeadline(localDeadline);
                    }
                    if (todo.getCategories() != null) {
                        viewModel.setCategories(new java.util.HashSet<>(todo.getCategories()));
                    }
                    
                    if (todo.getAccessLevel() != null && todo.getAccessLevel() == AccessLevel.READ.getValue()) {
                        viewModel.setCanEdit(false);
                        disableEditing();
                    }
                    
                    viewModel.loadShares(viewModel.getTodoId());
                }
            }
        }
        viewModel.setLoaded(true);
    }

    private void disableEditing() {
        binding.cbCompleted.setEnabled(false);
        binding.etTitle.setEnabled(false);
        binding.etDescription.setEnabled(false);
        binding.spinnerPriority.setEnabled(false);
        binding.btnDeadlineDate.setEnabled(false);
        binding.btnDeadlineTime.setEnabled(false);
        binding.btnSave.setVisibility(View.GONE);
        
        binding.cardManageCategories.setClickable(false);
        binding.tvManageCategoriesTitle.setText("View Categories");
        
        binding.cardManageShares.setClickable(false);
        binding.tvManageSharesTitle.setText("View Shares");
    }

    private void setupPrioritySpinner() {
        List<String> priorityNames = new ArrayList<>();
        for (Priority p : Priority.values()) {
            priorityNames.add(p.getDisplayName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                priorityNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPriority.setAdapter(adapter);
        
        // Default to NORMAL
        binding.spinnerPriority.setSelection(Priority.NORMAL.ordinal());
    }

    private void setupClickListeners() {
        binding.btnDeadlineDate.setOnClickListener(v -> showDatePicker());
        binding.btnDeadlineTime.setOnClickListener(v -> showTimePicker());

        binding.btnCancel.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        binding.btnSave.setOnClickListener(v -> {
            String title = binding.etTitle.getText() != null ? binding.etTitle.getText().toString().trim() : "";
            String description = binding.etDescription.getText() != null ? binding.etDescription.getText().toString().trim() : "";
            
            if (title.isEmpty()) {
                binding.etTitle.setError("Title is required");
                return;
            }

            int priorityIndex = binding.spinnerPriority.getSelectedItemPosition();
            Priority selectedPriority = Priority.values()[priorityIndex];

            boolean isCompleted = binding.cbCompleted.isChecked();

            viewModel.saveTodo(title, description, selectedPriority.getValue(), isCompleted);
        });

        binding.cardManageCategories.setOnClickListener(v -> {
            Bundle args = new Bundle();
            ArrayList<String> currentCategories = new ArrayList<>();
            if (viewModel.getCategories().getValue() != null) {
                currentCategories.addAll(viewModel.getCategories().getValue());
            }
            args.putStringArray("categories", currentCategories.toArray(new String[0]));
            Navigation.findNavController(v).navigate(R.id.action_todoEditorFragment_to_manageCategoriesFragment, args);
        });

        binding.cardManageShares.setOnClickListener(v -> {
            Bundle args = new Bundle();
            List<TodoShareResponse> currentShares = viewModel.getShares().getValue();
            if (currentShares != null) {
                args.putString("sharesJson", gson.toJson(currentShares));
            }
            Navigation.findNavController(v).navigate(R.id.action_todoEditorFragment_to_manageSharesFragment, args);
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        ZonedDateTime currentDt = viewModel.getDeadline().getValue();
        if (currentDt != null) {
            calendar.set(currentDt.getYear(), currentDt.getMonthValue() - 1, currentDt.getDayOfMonth());
        }

        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        
        Calendar checkCal = (Calendar) calendar.clone();
        checkCal.set(Calendar.HOUR_OF_DAY, 0);
        checkCal.set(Calendar.MINUTE, 0);
        checkCal.set(Calendar.SECOND, 0);
        checkCal.set(Calendar.MILLISECOND, 0);

        if (checkCal.before(now)) {
            calendar = Calendar.getInstance();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    ZonedDateTime newDt;
                    if (currentDt != null) {
                        newDt = currentDt.with(selectedDate);
                    } else {
                        newDt = selectedDate.atStartOfDay(ZoneId.systemDefault());
                    }
                    
                    ZonedDateTime nowDt = ZonedDateTime.now(ZoneId.systemDefault());
                    if (newDt.isBefore(nowDt)) {
                        newDt = nowDt;
                    }
                    
                    viewModel.setDeadline(newDt);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        ZonedDateTime currentDt = viewModel.getDeadline().getValue();
        if (currentDt != null) {
            calendar.set(Calendar.HOUR_OF_DAY, currentDt.getHour());
            calendar.set(Calendar.MINUTE, currentDt.getMinute());
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    LocalTime selectedTime = LocalTime.of(hourOfDay, minute);
                    ZonedDateTime newDt;
                    if (currentDt != null) {
                        newDt = currentDt.with(selectedTime);
                    } else {
                        newDt = ZonedDateTime.now(ZoneId.systemDefault()).with(selectedTime);
                    }
                    
                    viewModel.setDeadline(newDt);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void observeViewModel() {
        viewModel.getDeadline().observe(getViewLifecycleOwner(), dt -> {
            if (dt != null) {
                binding.btnDeadlineDate.setText(dt.toLocalDate().toString());
                binding.btnDeadlineTime.setText(String.format("%02d:%02d", dt.getHour(), dt.getMinute()));
            } else {
                binding.btnDeadlineDate.setText("Select Date");
                binding.btnDeadlineTime.setText("Select Time");
            }
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!isLoading);
        });

        viewModel.getCategories().observe(getViewLifecycleOwner(), this::updateCategoryPills);

        viewModel.getShares().observe(getViewLifecycleOwner(), shares -> {
            String text;
            if (shares == null || shares.isEmpty()) {
                text = "Shared with 0 people";
            } else {
                text = "Shared with " + shares.size() + " people";
            }
            binding.tvSharesCount.setText(text);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.isSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess != null && isSuccess) {
                Toast.makeText(requireContext(), "Todo saved successfully", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
        });

        viewModel.isAccessDenied().observe(getViewLifecycleOwner(), denied -> {
            if (denied != null && denied) {
                Toast.makeText(requireContext(), "You have been removed from this Todo", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
        });
    }

    private void updateCategoryPills(java.util.Set<String> categories) {
        binding.llCategories.removeAllViews();
        if (categories != null && !categories.isEmpty()) {
            for (String category : categories) {
                TextView pill = createCategoryPill(category);
                binding.llCategories.addView(pill);
            }
        }
    }

    private TextView createCategoryPill(String category) {
        TextView pill = new TextView(requireContext());
        pill.setText(category);
        int secondaryColor = MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorSecondary, Color.GRAY);
        pill.setTextColor(secondaryColor);
        pill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        pill.setTypeface(null, android.graphics.Typeface.BOLD);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
        shape.setStroke((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()), secondaryColor);
        shape.setColor(Color.TRANSPARENT);
        pill.setBackground(shape);

        int paddingHorizontal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                getResources().getDisplayMetrics());
        int paddingVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                getResources().getDisplayMetrics());
        pill.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()), 0);
        pill.setLayoutParams(params);
        return pill;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
