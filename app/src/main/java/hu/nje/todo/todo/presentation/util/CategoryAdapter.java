package hu.nje.todo.todo.presentation.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hu.nje.todo.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<String> categories = new ArrayList<>();
    private final OnCategoryDeleteListener deleteListener;

    public interface OnCategoryDeleteListener {
        void onDelete(String category);
    }

    public CategoryAdapter(OnCategoryDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.bind(category, deleteListener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCategoryName;
        private final ImageButton btnDeleteCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            btnDeleteCategory = itemView.findViewById(R.id.btnDeleteCategory);
        }

        public void bind(String category, OnCategoryDeleteListener deleteListener) {
            tvCategoryName.setText(category);
            btnDeleteCategory.setOnClickListener(v -> deleteListener.onDelete(category));
        }
    }
}
