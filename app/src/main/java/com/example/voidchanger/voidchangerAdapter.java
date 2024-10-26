package com.example.voidchanger;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.voidchangerItem;
import com.example.st046_audioeditorandmusiceditor.databinding.ItemvoidchangerBinding;

import java.util.List;

public class voidchangerAdapter extends RecyclerView.Adapter<voidchangerAdapter.ViewHolder> {

    private final List<voidchangerItem> voidchangerItems;
    private final OnItemClickListener listener;
    private final Context context;
    private int selectedPosition = -1;  // Track the selected item position

    public voidchangerAdapter(Context context, List<voidchangerItem> voidchangerItems, OnItemClickListener listener) {
        this.context = context;
        this.voidchangerItems = voidchangerItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemvoidchangerBinding binding = ItemvoidchangerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        voidchangerItem item = voidchangerItems.get(position);

        // Set the image and name for the item
        holder.binding.image.setImageResource(item.getImageResId());
        holder.binding.name.setText(item.getName());

        // Apply gradient if this item is the selected one
        if (position == selectedPosition) {
            applyGradientToSaveText(holder.binding.name);
        } else {
            holder.binding.name.setTextColor(Color.WHITE);
            holder.binding.name.getPaint().setShader(null);
        }

        // Set click listener
        holder.binding.image.setOnClickListener(v -> {
            listener.onItemClick(item.getName());
            selectedPosition = position;  // Update the selected position
            notifyDataSetChanged();  // Refresh the adapter to apply the changes
        });
    }

    private void applyGradientToSaveText(TextView textView) {
        Shader textShader = new LinearGradient(0, 0, 0, textView.getLineHeight(),
                new int[]{
                        Color.parseColor("#6573ED"), // Top color (20%)
                        Color.parseColor("#14D2E6")  // Bottom color (80%)
                },
                new float[]{0.2f, 1f}, Shader.TileMode.CLAMP);  // Gradient from 20% to 80%

        textView.getPaint().setShader(textShader);
    }

    @Override
    public int getItemCount() {
        return voidchangerItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemvoidchangerBinding binding;

        public ViewHolder(@NonNull ItemvoidchangerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String itemName);
    }
}
