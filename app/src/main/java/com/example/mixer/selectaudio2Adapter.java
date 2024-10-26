package com.example.mixer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.AudioFile;
import com.example.model.Mp3File;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.ultils.AudioUtils;

import java.util.ArrayList;
import java.util.List;

public class selectaudio2Adapter extends RecyclerView.Adapter<selectaudio2Adapter.Mp3ViewHolder> {
    private List<Mp3File> audioFiles;
    private Context context;
    private String key;
    private List<Integer> selectedPositions = new ArrayList<>();
    private MutableLiveData<List<Mp3File>> selectedAudios;
    private OnSelectionChangedListener selectionChangedListener;
    private selectaudioclickAdapter Adapter;
    public selectaudio2Adapter(List<Mp3File> audioFiles, Context context, String key, MutableLiveData<List<Mp3File>> selectedAudios, OnSelectionChangedListener selectionChangedListener) {
        this.audioFiles = audioFiles;
        this.context = context;
        this.key = key;
        this.selectedAudios = selectedAudios;
        this.selectionChangedListener = selectionChangedListener;
    }

    public selectaudio2Adapter(List<Mp3File> audioFiles, Context context, String key, MutableLiveData<List<Mp3File>> selectedAudios) {
        this.audioFiles = audioFiles;
        this.context = context;
        this.key = key;
        this.selectedAudios = selectedAudios;
    }

    @NonNull
    @Override
    public Mp3ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selectone, parent, false);
        return new Mp3ViewHolder(view);
    }
    private int getSelectedCount() {
        int count = 0;
        for (Mp3File file : audioFiles) {
            if (file.isSelected()) {
                count++;
            }
        }
        return count;
    }
    @Override
    public void onBindViewHolder(@NonNull Mp3ViewHolder holder, int position) {
        Mp3File audioFile = audioFiles.get(position);

        String name = audioFile.getName();
        if (name.length() > 25) {
            name = name.substring(0,25) + "...";
        }

        holder.nameTextView.setText(name);
        holder.durationTextView.setText(audioFile.getDuration());
        holder.sizeTextView.setText(audioFile.getSize());
        holder.dateTextView.setText(audioFile.getDate());

        if (audioFile.isSelected()) {
            holder.image.setImageResource(R.drawable.clickoke); // Selected state
        } else {
            holder.image.setImageResource(R.drawable.clickrong); // Unselected state
        }


        holder.linear.setOnClickListener(v -> {
            boolean currentSelectedState = audioFile.isSelected();
            int selectedFromAudioUtils = AudioUtils.getSelectedAudioFiles().size();
            int currentSelectedCount = getSelectedCount();
            int totalSelected = selectedFromAudioUtils + currentSelectedCount;

            if (totalSelected > 4 && !currentSelectedState) {

                Toast.makeText(context, "You can only select up to 5 files.", Toast.LENGTH_SHORT).show();
            } else {
                audioFile.setSelected(!currentSelectedState);
                notifyItemChanged(position);
            }
//            Adapter.notifyDataSetChanged();
        });

        holder.image.setOnClickListener(v -> {
            boolean currentSelectedState = audioFile.isSelected();
            int selectedFromAudioUtils = AudioUtils.getSelectedAudioFiles().size();
            int currentSelectedCount = getSelectedCount();
            int totalSelected = selectedFromAudioUtils + currentSelectedCount;

            if (totalSelected > 4 && !currentSelectedState) {

                Toast.makeText(context, "you_can_only_select_up_to_5_files", Toast.LENGTH_SHORT).show();
            } else {
                audioFile.setSelected(!currentSelectedState);
                notifyItemChanged(position);
            }
//            int size =    AudioUtils.getSelectedAudioFiles().size();
//            int a=getSelectedCount();
//            int c = size+a;
//            if(c<5){
//
//                boolean currentSelectedState = audioFile.isSelected();
//                audioFile.setSelected(!currentSelectedState);
//                notifyItemChanged(position);
//
//            }else {
//                Toast.makeText(context, "You can only select up to 5 files.", Toast.LENGTH_SHORT).show();
//            }
//            notifyItemChanged(position);
//            Adapter.notifyDataSetChanged();
        });
        List<Mp3File> selectedFiles = new ArrayList<>();

        for (Mp3File file : audioFiles) {
            if (file.isSelected()) {
                selectedFiles.add(file);
            }
        }
        selectedAudios.setValue(selectedFiles);
    }
    public void setEnableItems() {
        for (int i = 0; i < audioFiles.size(); i++) {
            Mp3File audioFile = audioFiles.get(i);

            if (audioFile.isSelected()) {

                notifyItemChanged(i, true);
            } else {
                notifyItemChanged(i, false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return audioFiles.size();
    }
    public void updateList(List<Mp3File> newList) {
        this.audioFiles = newList;

        notifyDataSetChanged();
    }


    public static class Mp3ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, durationTextView, sizeTextView, dateTextView;
        LinearLayout linear;
        ImageView image;

        public Mp3ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.checkbox);
            linear = itemView.findViewById(R.id.item);
            nameTextView = itemView.findViewById(R.id.name);
            durationTextView = itemView.findViewById(R.id.duration);
            sizeTextView = itemView.findViewById(R.id.size);
            dateTextView = itemView.findViewById(R.id.date);
        }
    }
    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }

}
