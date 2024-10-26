package com.example.Language;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.model.LanguageItem;
import com.example.st046_audioeditorandmusiceditor.R;

import java.util.List;
public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private List<LanguageItem> languageItems;
    private int selectedPosition = -1;
    private final Context context;
    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    // Constructor to accept saved language code
    public LanguageAdapter(Context context, List<LanguageItem> languageItems) {
        this.context = context;
        this.languageItems = languageItems;


    }

    public String getSelectedLanguageCode() {
        if (selectedPosition >= 0 && selectedPosition < languageItems.size()) {
            return languageItems.get(selectedPosition).getLanguageCode();
        }
        return null;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_langue, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        LanguageItem languageItem = languageItems.get(position);

        holder.languageName.setText(languageItem.getLanguageName());
        holder.flagIcon.setImageResource(languageItem.getImageResId());

        // Update background based on whether the item is selected
        if (position == selectedPosition) {
            holder.liner.setBackgroundResource(R.drawable.shape_with_borders);
            holder.languageSelector.setBackgroundResource(R.drawable.chose);  // Image for selected item
        } else {
            holder.liner.setBackgroundResource(R.drawable.vienlanguage);
            holder.languageSelector.setBackgroundResource(R.drawable.radio);  // Image for unselected item
        }

        holder.liner.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = position;

            // Update the previously selected item and the newly selected item
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onItemSelected(selectedPosition);
            }

            // Save selected language code to SharedPreferences
            String selectedLanguageCode = languageItem.getLanguageCode();
            SharedPreferences sharedPreferences = context.getSharedPreferences("language", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("KEY_LANGUAGE", selectedLanguageCode);
            editor.apply();
        });
    }

    @Override
    public int getItemCount() {
        return languageItems.size();
    }

    public static class LanguageViewHolder extends RecyclerView.ViewHolder {
        ImageView languageSelector;
        LinearLayout liner;
        TextView languageName;
        ImageView flagIcon;

        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            languageSelector = itemView.findViewById(R.id.languageSelector);
            liner = itemView.findViewById(R.id.liner);
            languageName = itemView.findViewById(R.id.languageName);
            flagIcon = itemView.findViewById(R.id.flagIcon);
        }
    }
}
