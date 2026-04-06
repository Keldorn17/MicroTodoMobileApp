package hu.nje.todo.todo.presentation.util;

import static hu.nje.todo.todo.domain.util.DateTimeFormatUtil.*;
import static hu.nje.todo.todo.domain.util.PriorityUiMapper.getPriorityColorResId;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;

import hu.nje.todo.R;
import hu.nje.todo.databinding.ItemTodoBinding;
import hu.nje.todo.todo.domain.model.Todo;

public class TodoAdapter extends ListAdapter<Todo, TodoAdapter.TodoViewHolder> {

    private final TodoClickListener listener;

    public TodoAdapter(TodoClickListener listener) {
        super(new TodoDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTodoBinding binding = ItemTodoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TodoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo currentTodo = getItem(position);
        holder.bind(currentTodo, listener);
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {

        private final ItemTodoBinding binding;

        public TodoViewHolder(ItemTodoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Todo item, TodoClickListener listener) {
            bindTextAndDates(item);
            bindPriorityColor(item);
            bindInteractions(item, listener);
            bindCategories(item);
        }

        private void bindTextAndDates(Todo item) {
            binding.tvTitle.setText(item.getTitle());
            binding.tvDescription.setText(item.getDescription());
            String formattedDate = getFormattedDate(item.getDeadline());
            String formattedTime = getFormattedTime(item.getDeadline());
            binding.tvDate.setText(
                    formattedDate != null && !formattedDate.isEmpty() ? formattedDate : "No Date");
            binding.tvTime.setText(
                    formattedTime != null && !formattedTime.isEmpty() ? formattedTime : "--:--");
        }

        private void bindPriorityColor(Todo item) {
            LayerDrawable bg = (LayerDrawable) binding.mainContainer.getBackground().mutate();
            GradientDrawable priorityLayer =
                    (GradientDrawable) bg.findDrawableByLayerId(R.id.card_priority_layer);
            if (priorityLayer != null) {
                int resolvedColor = ContextCompat.getColor(itemView.getContext(),
                        getPriorityColorResId(item.getPriority()));
                priorityLayer.setColor(resolvedColor);
            }
        }

        private void bindInteractions(Todo item, TodoClickListener listener) {
            binding.cbComplete.setOnCheckedChangeListener(null);
            binding.cbComplete.setChecked(item.getCompleted());
            binding.cbComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onCheckboxToggled(item, isChecked);
                }
            });
            binding.mainContainer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCardClicked(item);
                }
            });
        }

        private void bindCategories(Todo item) {
            binding.llCategories.removeAllViews();
            if (item.getCategories() != null && !item.getCategories().isEmpty()) {
                binding.llCategories.setVisibility(View.VISIBLE);
                for (String category : item.getCategories()) {
                    TextView pill = createCategoryPill(category);
                    binding.llCategories.addView(pill);
                }
            } else {
                binding.llCategories.setVisibility(View.GONE);
            }
        }

        private TextView createCategoryPill(String category) {
            TextView pill = new TextView(itemView.getContext());
            pill.setText(category);
            int textColor = MaterialColors.getColor(itemView,
                    com.google.android.material.R.attr.colorOnSurfaceVariant);
            pill.setTextColor(textColor);
            pill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            pill.setBackgroundResource(R.drawable.bg_category_pill);
            int paddingHorizontal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                    itemView.getResources().getDisplayMetrics());
            int paddingVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                    itemView.getResources().getDisplayMetrics());
            pill.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, paddingHorizontal, 0);
            pill.setLayoutParams(params);
            return pill;
        }

    }

    static class TodoDiffCallback extends DiffUtil.ItemCallback<Todo> {

        @Override
        public boolean areItemsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.equals(newItem);
        }

    }

    public interface TodoClickListener {

        void onCardClicked(Todo item);

        void onCheckboxToggled(Todo item, boolean isChecked);

    }

}