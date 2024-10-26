package com.example.equazer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.HomeItem;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ItemEquazerBinding;

import java.util.List;

public class equazeradapter extends RecyclerView.Adapter<equazeradapter.BottleViewHolder> {

    private final List<HomeItem> homeitem;
    private final equazeradapter.OnItemClickListener listener;
    private final Context context;
    private String selectedItemName;

    public equazeradapter(Context context, List<HomeItem> homeitem, equazeradapter.OnItemClickListener listener) {
        this.context = context;
        this.homeitem = homeitem;
        this.listener = listener;
        loadSelectedItemName();  // Load previously selected item if necessary
    }

    public Context getContext() {
        return context;
    }

    public OnItemClickListener getListener() {
        return listener;
    }

    public String getSelectedItemName() {
        return selectedItemName;
    }

    public void setSelectedItemName(String selectedItemName) {
        this.selectedItemName = selectedItemName;
    }

    public List<HomeItem> getHomeitem() {
        return homeitem;
    }

    // Load previously selected item (optional)
    private void loadSelectedItemName() {
        // Loading previous selection from SharedPreferences (if necessary)
    }

    @NonNull
    @Override
    public equazeradapter.BottleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEquazerBinding binding = ItemEquazerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new equazeradapter.BottleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull equazeradapter.BottleViewHolder holder, int position) {
        HomeItem item = homeitem.get(position);

        // Set the image for the item
        holder.binding.imageView9.setImageResource(item.getImageResId());

        // Set the name for the item dynamically
        holder.binding.name.setText(item.getName());

        // Set background based on selection
        if (item.getName().equals(selectedItemName)) {
            // Set background for the selected item
            holder.binding.item.setBackgroundResource(R.drawable.corlorbo);
        } else {
            // Reset background for unselected items
            holder.binding.item.setBackgroundResource(R.drawable.colorbonull);
        }

        holder.binding.item.setOnClickListener(v -> {
            listener.onItemClick(item.getName());

            selectedItemName = item.getName();
           Log.d("EquazerAdapter", "Setting background for selected item: " +selectedItemName);
            notifyDataSetChanged();  // Update UI to show the new selection
        });
    }

    @Override
    public int getItemCount() {
        return homeitem.size();
    }

    public static class BottleViewHolder extends RecyclerView.ViewHolder {
        ItemEquazerBinding binding;

        public BottleViewHolder(@NonNull ItemEquazerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String itemName);
    }
}
