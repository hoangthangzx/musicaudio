package com.example.language1;

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

import java.util.ArrayList;
import java.util.List;

public class Language1Adapter extends RecyclerView.Adapter<Language1Adapter.LanguageViewHolder> {

    private List<LanguageItem> languageItems;
    private int selectedPosition = -1;
    private final Context context;
    private OnItemSelectedListener listener;
    private String savedLanguageCode;
    private boolean isFirstOpen = true;

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public Language1Adapter(Context context, List<LanguageItem> languageItems) {
        this.context = context;
        this.languageItems = new ArrayList<>(languageItems);
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        savedLanguageCode = sharedPreferences.getString("KEY_LANGUAGE", null);

        if (savedLanguageCode != null && isFirstOpen) {
            moveLanguageToTop(savedLanguageCode);
        }

        // Find the matching language item and set selected position
        for (int i = 0; i < this.languageItems.size(); i++) {
            if (this.languageItems.get(i).getLanguageCode().equals(savedLanguageCode)) {
                selectedPosition = i;
                break;
            }
        }
    }

    private void moveLanguageToTop(String languageCode) {
        for (int i = 0; i < languageItems.size(); i++) {
            if (languageItems.get(i).getLanguageCode().equals(languageCode)) {
                LanguageItem item = languageItems.remove(i);
                languageItems.add(0, item);
                selectedPosition = 0;
                break;
            }
        }
        notifyDataSetChanged();
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

        if (position == selectedPosition) {
            holder.liner.setBackgroundResource(R.drawable.shape_with_borders);
            holder.languageSelector.setBackgroundResource(R.drawable.chose);
        } else {
            holder.liner.setBackgroundResource(R.drawable.vienlanguage);
            holder.languageSelector.setBackgroundResource(R.drawable.radio);
        }

        holder.liner.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = position;

            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onItemSelected(selectedPosition);
            }

            isFirstOpen = false; // Ensure the list doesn't reorganize on subsequent selections
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