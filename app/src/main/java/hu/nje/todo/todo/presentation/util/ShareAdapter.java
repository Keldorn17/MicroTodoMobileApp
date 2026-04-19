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
import com.google.android.material.color.MaterialColors;
import hu.nje.todo.R;

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
            String email = share.getEmail();
            binding.tvEmail.setText(email);
            
            if (email != null && !email.isEmpty()) {
                binding.tvAvatar.setText(email.substring(0, 1).toUpperCase());
            } else {
                binding.tvAvatar.setText("?");
            }

            int accessLevelValue = share.getAccessLevel() != null ? share.getAccessLevel() : 0;
            int colorAttr = com.google.android.material.R.attr.colorSurfaceVariant;
            int textColorAttr = com.google.android.material.R.attr.colorOnSurfaceVariant;

            if (accessLevelValue == 3) {
                colorAttr = androidx.appcompat.R.attr.colorPrimary;
                textColorAttr = com.google.android.material.R.attr.colorOnPrimary;
            } else if (accessLevelValue == 2) {
                colorAttr = com.google.android.material.R.attr.colorSecondary;
                textColorAttr = com.google.android.material.R.attr.colorOnSecondary;
            } else if (accessLevelValue == 1) {
                colorAttr = com.google.android.material.R.attr.colorTertiary;
                textColorAttr = com.google.android.material.R.attr.colorOnTertiary;
            }

            int bgColor = MaterialColors.getColor(binding.getRoot(), colorAttr);
            int textColor = MaterialColors.getColor(binding.getRoot(), textColorAttr);

            binding.tvAvatar.getBackground().mutate().setTint(bgColor);
            binding.tvAvatar.setTextColor(textColor);
            
            binding.spinnerItemAccessLevel.setOnItemSelectedListener(null);
            
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

            if (share.getAccessLevel() != null && share.getAccessLevel() == 3) {
                binding.tvOwnerLabel.setVisibility(View.VISIBLE);
                binding.spinnerItemAccessLevel.setVisibility(View.GONE);
                binding.btnDeleteShare.setVisibility(android.view.View.GONE);
            } else {
                binding.tvOwnerLabel.setVisibility(View.GONE);
                binding.spinnerItemAccessLevel.setVisibility(View.VISIBLE);
                binding.btnDeleteShare.setVisibility(android.view.View.VISIBLE);
                
                binding.spinnerItemAccessLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String levelName = (String) parent.getItemAtPosition(position);
                        AccessLevel newLevel = AccessLevel.valueOf(levelName);
                        int currentAccLevel = share.getAccessLevel() != null ? share.getAccessLevel() : 0;
                        if (newLevel.getValue() != currentAccLevel && actionListener != null) {
                            actionListener.onUpdateAccessLevel(share, newLevel.getValue());
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                binding.btnDeleteShare.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onDeleteShare(share);
                    }
                });
            }
        }
    }
}
