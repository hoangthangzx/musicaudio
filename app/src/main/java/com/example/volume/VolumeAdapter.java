package com.example.volume;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customview.SeekBarSetSizeThumb4;
import com.example.model.Valume;
import com.example.speed.speedAdapter;
import com.example.st046_audioeditorandmusiceditor.R;
import com.masoudss.lib.WaveformSeekBar;
import com.masoudss.lib.utils.Utils;
import com.masoudss.lib.utils.WaveGravity;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class VolumeAdapter extends RecyclerView.Adapter<VolumeAdapter.Mp3ViewHolder> {
    private ArrayList<Valume> selectedFiles;
    private Context context;
    private OnAudioControlListener audioControlListener;
    private String currentlyPlayingUri = null;
    private SparseArray<Mp3ViewHolder> holders = new SparseArray<>();
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

private int valume;
    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
    public VolumeAdapter(ArrayList<Valume> Valumes, Context context, OnAudioControlListener listener) {
        this.selectedFiles = Valumes;
        this.context = context;
        this.audioControlListener = listener;
    }
    @NonNull
    @Override
    public Mp3ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemfile, parent, false);
        return new Mp3ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Mp3ViewHolder holder, int position) {
        Valume valume = selectedFiles.get(position);

        // Store holder reference
        holders.put(position, holder);

        String name = valume.getName();
        if (name.length() > 30) {
            name = name.substring(0, 30) + "...";
        }
        holder.nameTextView.setText(name);
        holder.time.setText(valume.getDuration());

        if (!valume.getUri().equals(currentlyPlayingUri)) {
            holder.sbhz.setProgress(0);
            holder.waveformSeekBar.setProgress(0);
        }
        holder.waveformSeekBar.setOnTouchListener((v, event) -> {
            if (!valume.getUri().equals(currentlyPlayingUri)) {
                return true;
            }
            return false;
        });
        holder.sbhz.setOnTouchListener((v, event) -> {
            if (!valume.getUri().equals(currentlyPlayingUri)) {
                return true;
            }
            return false;
        });
        holder.image.setOnClickListener(v -> {
            currentlyPlayingUri = valume.getUri();
            if (audioControlListener != null) {
                if (isPlaying && currentlyPlayingUri != null && currentlyPlayingUri.equals(valume.getUri())) {
                } else {
                    currentlyPlayingUri = valume.getUri();
                }
                audioControlListener.onAudioPlayPauseClick(valume);
            }
            updateAllIcons();
        });
        holder.sbhz.setMax(100);
        holder.sbhz.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && audioControlListener != null) {
                    audioControlListener.onPlaybackPositionChanged(valume, progress);
                    holder.waveformSeekBar.setProgress(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Setup waveform
        holder.waveformSeekBar.setWaveWidth(Utils.dp(context, 1));
        holder.waveformSeekBar.setWaveGap(Utils.dp(context, 2));
        holder.waveformSeekBar.setWaveMinHeight(Utils.dp(context, 2));
        holder.waveformSeekBar.setWaveCornerRadius(Utils.dp(context, 2));
        holder.waveformSeekBar.setWaveGravity(WaveGravity.CENTER);
        holder.waveformSeekBar.setWaveBackgroundColor(ContextCompat.getColor(context, R.color.white2));
        holder.waveformSeekBar.setWaveProgressColor(ContextCompat.getColor(context, R.color.progress_start));

        File audioFile = new File(valume.getUri());
        if (audioFile.exists()) {
            holder.waveformSeekBar.setSampleFrom(audioFile);
        }

    }


    private void updateAllIcons() {
        for (int i = 0; i < holders.size(); i++) {
            int key = holders.keyAt(i);
            Mp3ViewHolder holder = holders.get(key);
            Valume valume = selectedFiles.get(key);
            updatePlayPauseIcon(holder, valume.getUri().equals(currentlyPlayingUri));
        }
    }
    public void update(){

        updateAllIcons();
    }
    public void updatePlaybackProgress(String uri, int progress) {
        for (int i = 0; i < selectedFiles.size(); i++) {
            if (selectedFiles.get(i).getUri().equals(uri)) {
                final Mp3ViewHolder holder = holders.get(i);
                if (holder != null) {
                    ((Activity) context).runOnUiThread(() -> {
                        if (holder.sbhz != null) {
                            holder.sbhz.setProgress(progress);
                        }
                        if (holder.waveformSeekBar != null) {
                            Log.d("TAG", "updatePlaybackProgress: "+progress);
                            holder.waveformSeekBar.setProgress(progress);
                        }
                    });
                }
                break;
            }
        }
    }

    private void updatePlayPauseIcon(VolumeAdapter.Mp3ViewHolder holder, boolean isCurrentlyPlaying) {
        if (holder != null && holder.image != null) {
            if (isPlaying && isCurrentlyPlaying) {
                holder.image.setImageResource(R.drawable.play);
            } else {
                holder.image.setImageResource(R.drawable.paushx);
            }
        }
    }
    @Override
    public void onViewRecycled(@NonNull Mp3ViewHolder holder) {
        super.onViewRecycled(holder);
        // Remove holder reference when view is recycled
        int position = holder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            holders.remove(position);
        }
    }

    @Override
    public int getItemCount() {
        return selectedFiles.size();
    }

    public void setCurrentlyPlayingUri(String uri) {
        String oldUri = this.currentlyPlayingUri;
        this.currentlyPlayingUri = uri;

        // Update the old and new items
        if (oldUri != null) {
            updateItemByUri(oldUri);
        }
        if (uri != null) {
            updateItemByUri(uri);
        }
    }

    private void updateItemByUri(String uri) {
        for (int i = 0; i < selectedFiles.size(); i++) {
            if (selectedFiles.get(i).getUri().equals(uri)) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    public ArrayList<Valume> getValumeList() {
        return selectedFiles;
    }

    public static class Mp3ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, time;
        ImageView image;
        WaveformSeekBar waveformSeekBar;
        SeekBarSetSizeThumb4 sbhz;

        public Mp3ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.textView10);
            image = itemView.findViewById(R.id.imageView12);
            waveformSeekBar = itemView.findViewById(R.id.waveformSeekBar);
            sbhz = itemView.findViewById(R.id.sbhz1);
        }
    }
    public void resetAllToPause() {
        // Đặt trạng thái tất cả các icon về "pause"
        for (int i = 0; i < holders.size(); i++) {
            Mp3ViewHolder holder = holders.valueAt(i);
            if (holder != null && holder.image != null) {
                holder.image.setImageResource(R.drawable.paushx); // Đặt lại icon thành "pause"
            }
        }

        currentlyPlayingUri = null;
        isPlaying = false;
    }

    public void clearData() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }


        if (selectedFiles != null) {
            selectedFiles.clear();
        }
        holders.clear();

        currentlyPlayingUri = null;
        isPlaying = false;
        audioControlListener = null;
        context = null;

        notifyDataSetChanged();
    }
    public interface OnAudioControlListener {
        void onAudioPlayClick(Valume valume);   // Called when play is requested
        void onAudioPauseClick(Valume valume);
        void onAudioPlayPauseClick(Valume valume);
        void onVolumeChanged(Valume valume, int volume);
        void onPlaybackPositionChanged(Valume valume, int progress);
    }
}
