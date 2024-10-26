package com.example.mixer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.AudioFile;
import com.example.model.Mp3File;
import com.example.st046_audioeditorandmusiceditor.R;
import com.masoudss.lib.WaveformSeekBar;
import com.masoudss.lib.utils.Utils;
import com.masoudss.lib.utils.WaveGravity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MixerAdapter extends RecyclerView.Adapter<MixerAdapter.Mp3ViewHolder> {
    private List<AudioFile> selectedFiles;
    private Context context;
    private OnAudioClickListener audioClickListener;
    private int selectedPosition = -1;
    private Mp3ViewHolder previousSelectedViewHolder = null;
    private com.masoudss.lib.WaveformSeekBar selectedWaveformSeekBar;
    private int seconds;
    private boolean isPlaying = false;
    private Map<Integer, ObjectAnimator> animatorMap = new HashMap<>();
    private Map<Integer, Float> pausedPositions = new HashMap<>();
private boolean click = true;
private String Path;
    private String Name;
    private String Size ;
    private String Date ;
    private String Dur;

    public void setClick(boolean click) {
        this.click = click;
    }

    public String getPath() {
        return Path;
    }

    public String getDate() {
        return Date;
    }

    public String getDur() {
        return Dur;
    }

    public String getName() {
        return Name;
    }

    public String getSize() {
        return Size;
    }

    public void setPath(String path) {
        Path = path;
    }

    public MixerAdapter(List<AudioFile> audioFiles, Context context, OnAudioClickListener listener) {
        this.selectedFiles = audioFiles;
        this.context = context;
        this.audioClickListener = listener;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
        if (playing) {
            resumeAnimation();
        } else {
            pauseAnimation();
        }
notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Mp3ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemmixer, parent, false);
        return new Mp3ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Mp3ViewHolder holder, int position) {
        AudioFile audioFile = selectedFiles.get(position);
        String filePath = audioFile.getUri();
        String name = audioFile.getName();
        if (name.length() > 20) {
            name = name.substring(0, 20) + "...";
        }
        holder.nameTextView.setText(name);
//        // Đặt background nếu item là selectedPosition
        if (position == selectedPosition) {
            holder.constraint.setBackgroundResource(R.drawable.vienmixer);
        } else {
            holder.constraint.setBackgroundResource(android.R.color.transparent);
        }

        // Xử lý click trên WaveformSeekBar
        // Define a Runnable for the audioClickListener action
        Runnable audioClickRunnable = () -> audioClickListener.onAudioClick();

        holder.WaveformSeekBarMixer.setOnClickListener(v -> {
            isPlaying = false;
            if (previousSelectedViewHolder != null) {
                previousSelectedViewHolder.constraint.setBackgroundResource(android.R.color.transparent);
            }
            holder.constraint.setBackgroundResource(R.drawable.vienmixer);

            previousSelectedViewHolder = holder;
            selectedPosition = position;
            v.removeCallbacks(audioClickRunnable);
            v.post(audioClickRunnable);
            audioClickListener.onclick();
            Path=audioFile.getUri();
            Name = audioFile.getName();
            Size=audioFile.getSize();
            Date=audioFile.getDate();
            Dur=audioFile.getDuration();
pauseAnimation();
        });
//            audioClickListener.onAudioClick(audioFile.getUri());
        int durationInMillis = convertTimeStringToSeconds(audioFile.getDuration()) * 1000;
        if (isPlaying) {
            // Cancel any existing animator for this position
            ObjectAnimator existingAnimator = animatorMap.get(position);
            if (existingAnimator != null) {
                existingAnimator.cancel();
            }

            // Create new animator
            float startPosition = pausedPositions.getOrDefault(position, 0f);
            ObjectAnimator animator = ObjectAnimator.ofFloat(
                    holder.constraint,
                    "translationX",
                    startPosition,
                    -holder.constraint.getWidth()
            );
            long animationDuration = durationInMillis;
            if (pausedPositions.containsKey(position)) {
                float progress = Math.abs(startPosition) / holder.constraint.getWidth();
                animationDuration = (long) (durationInMillis * (1 - progress));
            }
            animator.setDuration(animationDuration);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    pausedPositions.remove(position);
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });

            animatorMap.put(position, animator);
            animator.start();
        } else {
            // If not playing and no saved position, reset view
            if (!pausedPositions.containsKey(position)) {
                holder.resetView();
            }
        }

        setupWaveformSeekBar(holder.WaveformSeekBarMixer, filePath);


//        holder.WaveformSeekBarMixer.setOnClickListener(v -> {
//            isPlaying = false;
//
//            int previousSelectedPosition = selectedPosition;
//            selectedPosition = position;
////            notifyItemChanged(previousSelectedPosition);
////            notifyItemChanged(selectedPosition);
//
//        });

    }



    public void resetAllBackground() {
        selectedPosition = -1; // Reset selected position
        selectedWaveformSeekBar = null; // Reset selected waveform
        notifyDataSetChanged(); // This will trigger onBindViewHolder where backgrounds will be reset
    }
    private void setupWaveformSeekBar(WaveformSeekBar waveformSeekBar, String filePath) {
        waveformSeekBar.setWaveWidth(Utils.dp(context, 1));
        waveformSeekBar.setWaveGap(Utils.dp(context, 2));
        waveformSeekBar.setWaveMinHeight(Utils.dp(context, 2));
        waveformSeekBar.setWaveCornerRadius(Utils.dp(context, 2));
        waveformSeekBar.setWaveGravity(WaveGravity.CENTER);
        waveformSeekBar.setWaveBackgroundColor(ContextCompat.getColor(context, R.color.white2));
        waveformSeekBar.setWaveProgressColor(ContextCompat.getColor(context, R.color.white2));
        waveformSeekBar.setSampleFrom(filePath);
    }

    public void pauseAnimation() {
        click=false;
        isPlaying = false;
        for (Map.Entry<Integer, ObjectAnimator> entry : animatorMap.entrySet()) {
            int position = entry.getKey();
            ObjectAnimator animator = entry.getValue();
            if (animator != null && animator.isRunning()) {
                animator.pause();
                // Store the current position
                float currentPosition = (float) animator.getAnimatedValue();
                pausedPositions.put(position, currentPosition);
            }
        }
    }

    public void resumeAnimation() {
        click =true;
//        isPlaying = true;
        for (Map.Entry<Integer, ObjectAnimator> entry : animatorMap.entrySet()) {
            ObjectAnimator animator = entry.getValue();
            if (animator != null && animator.isPaused()) {
                animator.resume();
            }
        }

    }

    public void resetAnimation() {
        click =true;
        isPlaying = false;
        for (ObjectAnimator animator : animatorMap.values()) {
            if (animator != null) {
                animator.cancel();
            }
        }
        animatorMap.clear();
        pausedPositions.clear();
        resetAllViews();
    }

    @Override
    public void onViewRecycled(@NonNull Mp3ViewHolder holder) {
        super.onViewRecycled(holder);
        int position = holder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            ObjectAnimator animator = animatorMap.get(position);
            if (animator != null) {
                animator.cancel();
                animatorMap.remove(position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return selectedFiles.size();
    }

    public WaveformSeekBar getSelectedWaveformSeekBar() {
        return selectedWaveformSeekBar;
    }

    public int convertTimeStringToSeconds(String time) {
        String[] units = time.split(":");
        int seconds = 0;

        if (units.length == 3) {
            // Format: hh:mm:ss
            int hours = Integer.parseInt(units[0]);
            int minutes = Integer.parseInt(units[1]);
            int secs = Integer.parseInt(units[2]);
            seconds = (hours * 3600) + (minutes * 60) + secs;
        } else if (units.length == 2) {
            // Format: mm:ss
            int minutes = Integer.parseInt(units[0]);
            int secs = Integer.parseInt(units[1]);
            seconds = (minutes * 60) + secs;
        }

        return seconds;
    }

    public void updateList() {
        notifyDataSetChanged();
    }

    public void resetAllViews() {
        for (int i = 0; i < getItemCount(); i++) {
            notifyItemChanged(i);
        }
    }

    public static class Mp3ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        WaveformSeekBar WaveformSeekBarMixer;
        ConstraintLayout constraint, chon;

        public Mp3ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            WaveformSeekBarMixer = itemView.findViewById(R.id.waveformSeekBar);
            constraint = itemView.findViewById(R.id.constraintLayout18);
            chon = itemView.findViewById(R.id.chon);
        }

        public void resetView() {
            constraint.setTranslationX(0);
        }
    }

    public interface OnAudioClickListener {
//        void onAudioClick(AudioFile audioFile);
void onAudioClick();
void onclick();
    }
}