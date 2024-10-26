package com.example.Home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.HomeItem;
import com.example.st046_audioeditorandmusiceditor.databinding.ItemhomeBinding;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.BottleViewHolder> {

    private final List<HomeItem> homeitem;
    private final OnItemClickListener listener;
    private final Context context;
    private String selectedItemName;

    public HomeAdapter(Context context, List<HomeItem> homeitem, OnItemClickListener listener) {
        this.context = context;
        this.homeitem = homeitem;
        this.listener = listener;
        loadSelectedItemName();  // Load previously selected item if necessary
    }

    // Load previously selected item (optional)
    private void loadSelectedItemName() {
        // Loading previous selection from SharedPreferences (if necessary)
    }

    @NonNull
    @Override
    public BottleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemhomeBinding binding = ItemhomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BottleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BottleViewHolder holder, int position) {
        HomeItem item = homeitem.get(position);

        // Set the image for the item
        holder.binding.itemImage.setImageResource(item.getImageResId());

        // Set the name for the item dynamically
        holder.binding.name.setText(item.getTex());  // Set the item name for TextView
        applyGradientToSaveText(holder.binding.name);
        // Set click listener
        holder.binding.linervien.setOnClickListener(v -> {
            listener.onItemClick(item.getName());
            selectedItemName = item.getName();
            notifyDataSetChanged();  // Update UI to show the new selection
        });
    }
    private void applyGradientToSaveText(TextView textView) {
        Shader textShader = new LinearGradient(0, 0, 0, textView.getLineHeight(),
                new int[]{
                        Color.parseColor("#6573ED"), // Top color (20%)
                        Color.parseColor("#14D2E6")  // Bottom color (80%)
                },
                new float[]{0.2f, 1f}, Shader.TileMode.CLAMP);  // 0.2 for 20% top, 1f for 80% bottom

        textView.getPaint().setShader(textShader);
    }


    @Override
    public int getItemCount() {
        return homeitem.size();
    }

    public static class BottleViewHolder extends RecyclerView.ViewHolder {
        ItemhomeBinding binding;

        public BottleViewHolder(@NonNull ItemhomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String itemName);
    }
}
