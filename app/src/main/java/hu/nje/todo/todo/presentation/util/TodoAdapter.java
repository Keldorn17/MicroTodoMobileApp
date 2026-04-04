package hu.nje.todo.todo.presentation.util;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import hu.nje.todo.databinding.ItemTodoBinding;
import hu.nje.todo.todo.domain.model.Todo;

public class TodoAdapter extends ListAdapter<Todo, TodoAdapter.TodoViewHolder> {

    public TodoAdapter() {
        super(new TodoDiffCallback());
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
        holder.bind(currentTodo);
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {

        private final ItemTodoBinding binding;

        public TodoViewHolder(ItemTodoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Todo todo) {
            binding.textTodoTitle.setText(todo.getTitle());
            binding.checkboxCompleted.setChecked(todo.getCompleted());
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

}
