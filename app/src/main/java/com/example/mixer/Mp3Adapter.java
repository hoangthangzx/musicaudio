package com.example.mixer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiotex.AudiotexActivity;
import com.example.cutter.CutaudioActivity;
import com.example.equazer.equazerActivity;
import com.example.merge.MergeActivity2;
import com.example.model.AudioFile;
import com.example.model.Mp3File;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.ultils.AudioUtils;
import com.example.voidchanger.VoidChangerActivity;

import java.util.List;
public class Mp3Adapter extends RecyclerView.Adapter<Mp3Adapter.Mp3ViewHolder> {
    private List<Mp3File> audioFiles;
    private Context context;
    private String key;  // Thêm biến để lưu giá trị key
    private String audioPath;
    private FinishActivityCallback callback;

    public Mp3Adapter(List<Mp3File> audioFiles, Context context, String key, String audioPath, FinishActivityCallback callback) {
        this.audioFiles = audioFiles;
        this.context = context;
        this.key = key;
        this.audioPath = audioPath;
        this.callback = callback;
    }
    @NonNull
    @Override
    public Mp3ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selectone, parent, false);
        return new Mp3ViewHolder(view);
    }
    public void updateList(List<Mp3File> newAudioFiles) {
        audioFiles = newAudioFiles;
        notifyDataSetChanged(); // Notify the adapter to refresh the views
    }
    @Override
    public void onBindViewHolder(@NonNull Mp3ViewHolder holder, int position) {
        Mp3File audioFile = audioFiles.get(position);
        String name = audioFile.getName();

        if (name.length() > 27) {
            // Cắt chuỗi xuống 30 ký tự và thêm "..."
            name = name.substring(0,27) + "...";
        }
        holder.nameTextView.setText(name);
        holder.durationTextView.setText(audioFile.getDuration());
        holder.sizeTextView.setText(audioFile.getSize());
        holder.dateTextView.setText(audioFile.getDate());
        holder.checkbox.setVisibility(View.GONE);

        // Điều hướng đến activity dựa trên giá trị key
        holder.linear.setOnClickListener(v -> {
            Intent intent;
            switch (key) {
                case "mixer":
                    intent = new Intent(context, Mixer2Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    break;
                case "merge":
                    intent = new Intent(context, MergeActivity2.class); //
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;

                default:
                    intent = new Intent(context, Mixer2Activity.class); //
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }
            AudioFile selectedAudio = new AudioFile(
                    audioFile.getPath(),       // URI
                    audioFile.getName(),       // File Name
                    audioFile.getSize(),       // File Size
                    audioFile.getDuration(),    // File Duration
                    audioFile.getDate());      // File Date
            AudioUtils.removeSelectedAudioFile(audioPath);
            AudioUtils.addSelectedAudioFile(selectedAudio);
            callback.finishActivity();
//            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return audioFiles.size();
    }

    public static class Mp3ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, durationTextView, sizeTextView, dateTextView;
        LinearLayout linear;
        ImageView checkbox;

        public Mp3ViewHolder(@NonNull View itemView) {
            super(itemView);
            linear=itemView.findViewById(R.id.item);
            nameTextView = itemView.findViewById(R.id.name);
            durationTextView = itemView.findViewById(R.id.duration);
            sizeTextView = itemView.findViewById(R.id.size);
            dateTextView = itemView.findViewById(R.id.date);
            checkbox =itemView.findViewById(R.id.checkbox);
        }
    }
    public interface FinishActivityCallback {
        void finishActivity();
    }

}
