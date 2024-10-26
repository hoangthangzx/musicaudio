package com.example.selectaudio;

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
import com.example.customview.CutaudioAudio;
import com.example.cutter.CutaudioActivity;
import com.example.equazer.equazerActivity;
import com.example.model.Mp3File;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.voidchanger.VoidChangerActivity;
import com.example.volume.VolumeActivity;

import java.util.List;
public class Mp3Adapter extends RecyclerView.Adapter<Mp3Adapter.Mp3ViewHolder> {
    private List<Mp3File> audioFiles;
    private Context context;
    private String key;  // Thêm biến để lưu giá trị key

    public Mp3Adapter(List<Mp3File> audioFiles, Context context, String key) {
        this.audioFiles = audioFiles;
        this.context = context;
        this.key = key;  // Lưu giá trị key khi khởi tạo adapter
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
                case "audiotex":
                    intent = new Intent(context, AudiotexActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    break;
                case "equalizer":
                    intent = new Intent(context, equazerActivity.class); //
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "voidchanger":
                    intent = new Intent(context, VoidChangerActivity.class); //
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "mp3cutter":
                    intent = new Intent(context, CutaudioActivity.class); //
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                default:
                    intent = new Intent(context, equazerActivity.class); //
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }

            intent.putExtra("audioPath", audioFile.getPath());
            intent.putExtra("audioname", audioFile.getName());
            intent.putExtra("audiodur", audioFile.getDuration());
            intent.putExtra("date", audioFile.getDate());
            intent.putExtra("size", audioFile.getSize());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
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

}
