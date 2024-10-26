package com.example.speed;

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
import com.example.model.Speed;
import com.example.model.Speed;
import com.example.model.Speed;
import com.example.model.Valume;
import com.example.st046_audioeditorandmusiceditor.R;

import com.example.volume.VolumeAdapter;
import com.google.android.exoplayer2.ExoPlayer;
import com.masoudss.lib.WaveformSeekBar;
import com.masoudss.lib.utils.Utils;
import com.masoudss.lib.utils.WaveGravity;


import java.io.File;
import java.util.ArrayList;

public class speedAdapter extends RecyclerView.Adapter<speedAdapter.Mp3ViewHolder> {
    private ArrayList<Speed> selectedFiles;
    private Context context;
    private speedAdapter.OnAudioControlListener audioControlListener;
    private String currentlyPlayingUri = null;
    private SparseArray<speedAdapter.Mp3ViewHolder> holders = new SparseArray<>();
    private boolean isPlaying = false;


    public speedAdapter(ArrayList<Speed> Speeds, Context context, speedAdapter.OnAudioControlListener listener) {
        this.selectedFiles = Speeds;
        this.context = context;
        this.audioControlListener = listener;
    }

    @NonNull
    @Override
    public speedAdapter.Mp3ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemfile, parent, false);
        return new speedAdapter.Mp3ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull speedAdapter.Mp3ViewHolder holder, int position) {
        Speed Speed = selectedFiles.get(position);
        holders.put(position, holder);

        String name = Speed.getName();
        if (name.length() > 30) {
            name = name.substring(0, 30) + "...";
        }
        holder.nameTextView.setText(name);
        holder.time.setText(Speed.getDuration());

        if (!Speed.getUri().equals(currentlyPlayingUri)) {
            holder.sbhz.setProgress(0);
            holder.waveformSeekBar.setProgress(0);
        }
        holder.waveformSeekBar.setOnTouchListener((v, event) -> {
            if (!Speed.getUri().equals(currentlyPlayingUri)) {
                return true;
            }
            return false;
        });
        holder.sbhz.setOnTouchListener((v, event) -> {
            if (!Speed.getUri().equals(currentlyPlayingUri)) {
                return true;
            }
            return false;
        });
        holder.image.setOnClickListener(v -> {
            resetAllToPause();
            holder.image.setImageResource(R.drawable.play);
            currentlyPlayingUri = Speed.getUri();
            v.post(() -> audioControlListener.onAudioPlayPauseClick(Speed.getUri()));
        });

        //
        //  ((Activity) context).runOnUiThread(() -> {

//                });
//        holder.image.setOnClickListener(v -> {
//            if (audioControlListener != null) {
//                ((Activity) context).runOnUiThread(() -> {
//                    if (currentlyPlayingUri != null && currentlyPlayingUri.equals(Speed.getUri())) {
//                    } else {
//                        currentlyPlayingUri = Speed.getUri();
//                    }
//                });
//                audioControlListener.onAudioPlayPauseClick(Speed);
//                if (isPlaying && currentlyPlayingUri!= null) {
//                    holder.image.setImageResource(R.drawable.play);
//                } else {
//                    holder.image.setImageResource(R.drawable.paushx);
//                }
//            }
//
//        });


        // Setup seekbar
        holder.sbhz.setMax(100);
        holder.sbhz.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && audioControlListener != null) {
                    audioControlListener.onPlaybackPositionChanged(Speed, progress);
                    holder.waveformSeekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Setup waveform
        holder.waveformSeekBar.setWaveWidth(Utils.dp(context, 1));
        holder.waveformSeekBar.setWaveGap(Utils.dp(context, 2));
        holder.waveformSeekBar.setWaveMinHeight(Utils.dp(context, 2));
        holder.waveformSeekBar.setWaveCornerRadius(Utils.dp(context, 2));
        holder.waveformSeekBar.setWaveGravity(WaveGravity.CENTER);
        holder.waveformSeekBar.setWaveBackgroundColor(ContextCompat.getColor(context, R.color.white2));
        holder.waveformSeekBar.setWaveProgressColor(ContextCompat.getColor(context, R.color.progress_start));

        // Setup waveform data
        File audioFile = new File(Speed.getUri());
        if (audioFile.exists()) {
            holder.waveformSeekBar.setSampleFrom(audioFile);
        }

    }

    private void updatePlayPauseIcon(speedAdapter.Mp3ViewHolder holder, boolean isCurrentlyPlaying) {
//        if (holder != null && holder.image != null) {
//            if (isPlaying && isCurrentlyPlaying) {
//                holder.image.setImageResource(R.drawable.play);
//            } else {
//                holder.image.setImageResource(R.drawable.paushx);
//            }
//        }
    }
    private void updateAllIcons() {
        for (int i = 0; i < holders.size(); i++) {
            int key = holders.keyAt(i);
            speedAdapter.Mp3ViewHolder holder = holders.get(key);
            Speed speed = selectedFiles.get(key);
            updatePlayPauseIcon(holder, speed.getUri().equals(currentlyPlayingUri));
        }
    }
    public void update(){

//        updateAllIcons();
    }
    public void updatePlaybackProgress(String uri, int progress) {
        for (int i = 0; i < selectedFiles.size(); i++) {
            if (selectedFiles.get(i).getUri().equals(uri)) {
                final speedAdapter.Mp3ViewHolder holder = holders.get(i);
                if (holder != null) {
                    ((Activity) context).runOnUiThread(() -> {
                        if (holder.sbhz != null) {
                            holder.sbhz.setProgress(progress);
                        }
                        if (holder.waveformSeekBar != null) {
                            Log.d("TAG", "updatePlaybackProgress: " + progress);
                            holder.waveformSeekBar.setProgress(progress);
                        }
                    });
                }
                break;
            }
        }
    }
    public void resetAllToPause() {
        for (int i = 0; i < holders.size(); i++) {
            speedAdapter.Mp3ViewHolder holder = holders.valueAt(i);
            if (holder != null && holder.image != null) {
                holder.image.setImageResource(R.drawable.paushx); // Đặt lại icon thành "pause"
            }
        }
        currentlyPlayingUri = null;
        isPlaying = false;
    }
    @Override
    public void onViewRecycled(@NonNull speedAdapter.Mp3ViewHolder holder) {
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
//        String oldUri = this.currentlyPlayingUri;
//        this.currentlyPlayingUri = uri;
//
//        // Update the old and new items
//        if (oldUri != null) {
//            updateItemByUri(oldUri);
//        }
//        if (uri != null) {
//            updateItemByUri(uri);
//        }
    }

    private void updateItemByUri(String uri) {
        for (int i = 0; i < selectedFiles.size(); i++) {
            if (selectedFiles.get(i).getUri().equals(uri)) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    public ArrayList<Speed> getSpeedList() {
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

    public void clearData() {



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
        void onAudioPlayClick(Speed speed);   // Called when play is requested

        void onAudioPauseClick(Speed speed);

        void onAudioPlayPauseClick(String speed);

        void onSpeedChanged(Speed Speed, int speed);

        void onPlaybackPositionChanged(Speed speed, int progress);
    }
}