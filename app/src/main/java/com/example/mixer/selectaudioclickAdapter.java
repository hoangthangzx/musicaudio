package com.example.mixer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.Mp3File;
import com.example.st046_audioeditorandmusiceditor.R;

import java.util.List;

public class selectaudioclickAdapter extends RecyclerView.Adapter<selectaudioclickAdapter.Mp3ViewHolder> {
    private List<Mp3File> selectedFiles;
    private Context context;
    private String key;
    private MutableLiveData<List<Mp3File>> selectedAudios;
    private selectaudio2Adapter mainAdapter;

    public selectaudioclickAdapter(List<Mp3File> audioFiles, Context context, String key, MutableLiveData<List<Mp3File>> selectedAudios, selectaudio2Adapter mainAdapter) {
        this.selectedFiles = audioFiles;
        this.context = context;
        this.key = key;
        this.selectedAudios = selectedAudios;
        this.mainAdapter = mainAdapter;
    }

    @NonNull
    @Override
    public Mp3ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemselectaudio, parent, false);
        return new Mp3ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Mp3ViewHolder holder, int position) {
        Mp3File audioFile = selectedFiles.get(position);
        holder.nameTextView.setText(audioFile.getName());
        holder.x.setOnClickListener(v -> {
            boolean currentSelectedState = audioFile.isSelected();
            audioFile.setSelected(!currentSelectedState);
            notifyDataSetChanged();
            mainAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return selectedFiles.size();
    }

    public void updateSelectedFiles(List<Mp3File> newSelectedFiles) {
        this.selectedFiles = newSelectedFiles;
        notifyDataSetChanged();
    }

    public static class Mp3ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView x;

        public Mp3ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            x = itemView.findViewById(R.id.x);
        }
    }
}