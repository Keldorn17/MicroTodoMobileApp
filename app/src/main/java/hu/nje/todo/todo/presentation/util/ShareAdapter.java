package hu.nje.todo.todo.presentation.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hu.nje.todo.databinding.ItemShareBinding;
import hu.nje.todo.todo.domain.model.TodoShareResponse;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import hu.nje.todo.todo.domain.model.AccessLevel;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ShareViewHolder> {

    private final List<TodoShareResponse> shares = new ArrayList<>();
    private final OnShareActionListener actionListener;

    public interface OnShareActionListener {
        void onDeleteShare(TodoShareResponse share);
        void onUpdateAccessLevel(TodoShareResponse share, int newLevel);
    }

    public ShareAdapter(OnShareActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setShares(List<TodoShareResponse> newShares) {
        shares.clear();
        if (newShares != null) {
            shares.addAll(newShares);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShareBinding binding = ItemShareBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ShareViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareViewHolder holder, int position) {
        TodoShareResponse share = shares.get(position);
        holder.bind(share);
    }

    @Override
    public int getItemCount() {
        return shares.size();
    }

    class ShareViewHolder extends RecyclerView.ViewHolder {

        private final ItemShareBinding binding;

        public ShareViewHolder(ItemShareBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TodoShareResponse share) {
            binding.tvEmail.setText(share.getEmail());
            
            // Setup Spinner for this item
            List<String> levels = new ArrayList<>();
            int selectionIndex = 0;
            int i = 0;
            AccessLevel currentLevel = AccessLevel.fromValue(share.getAccessLevel() != null ? share.getAccessLevel() : 0);
            
            for (AccessLevel al : AccessLevel.values()) {
                if (al != AccessLevel.OWNER) {
                    if (al == currentLevel) selectionIndex = i;
                    levels.add(al.name());
                    i++;
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    itemView.getContext(),
                    android.R.layout.simple_spinner_item,
                    levels
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerItemAccessLevel.setAdapter(adapter);
            
            binding.spinnerItemAccessLevel.setSelection(selectionIndex, false);

            binding.spinnerItemAccessLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                private boolean isFirstSelection = true;
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (isFirstSelection) {
                        isFirstSelection = false;
                        return;
                    }
                    AccessLevel newLevel = AccessLevel.values()[position];
                    if (newLevel.getValue() != share.getAccessLevel() && actionListener != null) {
                        actionListener.onUpdateAccessLevel(share, newLevel.getValue());
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            if (share.getAccessLevel() != null && share.getAccessLevel() == 3) {
                binding.tvOwnerLabel.setVisibility(View.VISIBLE);
                binding.spinnerItemAccessLevel.setVisibility(View.GONE);
                binding.btnDeleteShare.setVisibility(android.view.View.GONE);
            } else {
                binding.tvOwnerLabel.setVisibility(View.GONE);
                binding.spinnerItemAccessLevel.setVisibility(View.VISIBLE);
                binding.btnDeleteShare.setVisibility(android.view.View.VISIBLE);
                binding.btnDeleteShare.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onDeleteShare(share);
                    }
                });
            }
        }
    }
}
