package com.example.merge;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cutter.Cutaudio2Activity;
import com.example.model.AudioFile;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.customview.CircularProgressBar;
import com.example.st046_audioeditorandmusiceditor.databinding.DialogMergeBinding;
import com.example.ultils.AudioUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MergeAdapter extends RecyclerView.Adapter<MergeAdapter.Mp3ViewHolder> {
    private ArrayList<AudioFile> audioFiles;
    private Context context;
    private MediaPlayer adapterMediaPlayer;
    private int currentPlayingPosition = -1;
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private Runnable updateProgressRunnable;
    private OnAudioClickListener audioClickListener;
    private OnAudioClickdelete audioClickdelete;
    public  interface OnAudioClickdelete{
        void audioClickdelete();
    }
    public MergeAdapter(ArrayList<AudioFile> audioFiles, Context context, OnAudioClickListener audioClickListener) {
        this.audioFiles = audioFiles;
        this.context = context;
        this.audioClickListener = audioClickListener;
    }
    public void updateData() {

        notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
    }

    @NonNull
    @Override
    public MergeAdapter.Mp3ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merge, parent, false);
        return new MergeAdapter.Mp3ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MergeAdapter.Mp3ViewHolder holder, int position) {
        AudioFile audioFile = audioFiles.get(position);
        bindViewHolder(holder, audioFile, position);
    }

    private void bindViewHolder(Mp3ViewHolder holder, AudioFile audioFile, int position) {
        String name = audioFile.getName();
        if (name.length() > 25) {
            name = name.substring(0,25) + "...";
        }
        holder.nameTextView.setText(name);
        holder.durationTextView.setText(audioFile.getDuration());
        holder.sizeTextView.setText(audioFile.getSize());
        holder.dateTextView.setText(audioFile.getDate());

        holder.checkbox.setOnClickListener(v -> showDialog(v, holder.getAdapterPosition()));

        holder.progressBar.setOnClickListener(v -> {
            audioClickListener.onclick();
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (currentPlayingPosition == adapterPosition) {
                    if (isPlaying) {
                        pauseAudio();
                        holder.progressBar.setPaused(true);
                    } else {
                        resumeAudio();
                        holder.progressBar.setPaused(false);
                    }
                } else {
                    stopCurrentAudio();
                    playNewAudio(audioFile.getUri(), adapterPosition);
                    holder.progressBar.setPaused(false);
                }
            }
        });

        holder.progressBar.setProgress(0);
        holder.progressBar.setPaused(true);

        if (currentPlayingPosition == position && isPlaying) {
            updateProgressBar(holder.progressBar);
        }

        holder.progressBar.setPaused(currentPlayingPosition != position || !isPlaying);
    }

    private void updateProgressBar(CircularProgressBar progressBar) {
        if (adapterMediaPlayer.isPlaying()) {
            int currentPosition = adapterMediaPlayer.getCurrentPosition();
            int totalDuration = adapterMediaPlayer.getDuration();
            int progress = (int) (((float) currentPosition / totalDuration) * 100);
            progressBar.setProgress(progress);
        }
    }

    public static class Mp3ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, durationTextView, sizeTextView, dateTextView;
        LinearLayout linear;
        ImageView checkbox;
        CircularProgressBar progressBar;

        public Mp3ViewHolder(@NonNull View itemView) {
            super(itemView);
            linear = itemView.findViewById(R.id.item);
            nameTextView = itemView.findViewById(R.id.name);
            durationTextView = itemView.findViewById(R.id.duration);
            sizeTextView = itemView.findViewById(R.id.size);
            dateTextView = itemView.findViewById(R.id.date);
            progressBar = itemView.findViewById(R.id.progressBar);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(audioFiles, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(audioFiles, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(fromPosition);
        notifyItemChanged(toPosition);

        // Update currentPlayingPosition if necessary
        if (currentPlayingPosition == fromPosition) {

            currentPlayingPosition = toPosition;
        } else if (currentPlayingPosition == toPosition) {
            currentPlayingPosition = fromPosition;
            audioClickListener.onItemMoveCompleted(fromPosition, toPosition);

        }
        audioClickListener.swap();
    }


    public interface ItemTouchHelperAdapter {
        void onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }
    private void showDialog(View anchorView, int position) {
        AudioFile audioFile = audioFiles.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogMergeBinding dialogBinding = DialogMergeBinding.inflate(inflater);

        PopupWindow popupWindow = new PopupWindow(dialogBinding.getRoot(),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setElevation(100);
        String audioUri = audioFiles.get(position).getUri();
        String name = audioFiles.get(position).getName();
        String dur = audioFiles.get(position).getDuration();
        String date = audioFiles.get(position).getDate();
        String sizee = audioFiles.get(position).getSize();
        dialogBinding.cut.setOnClickListener(v -> {
            Intent mp3cutter = new Intent(context, Cutaudio2Activity.class); // Use the context to create the intent
            mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mp3cutter.putExtra("key", "merge");
            mp3cutter.putExtra("outputAudioPath", audioUri);
            mp3cutter.putExtra("audioname", name);
            mp3cutter.putExtra("audiodur", dur);
            mp3cutter.putExtra("date", date);
            mp3cutter.putExtra("size", sizee);
            context.startActivity(mp3cutter);
//            // Remove the current audio and start the activity
//            removeCurrentAudio(audioUri);

        });
        dialogBinding.delete.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (audioClickListener != null) {
                audioClickListener.onAudioDelete(audioUri);
            }
            if(AudioUtils.getSelectedAudioFiles().size()>2){
                AudioUtils.removeSelectedAudioFile(audioUri);
            }else {
                Toast.makeText(context, R.string.you_can_only_delete_when_there_are_at_least_3_files, Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        });


        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        int x = screenWidth - popupWindow.getContentView().getMeasuredWidth() - dpToPx(110); // 20dp from right
        int y = location[1] - anchorView.getHeight() / 2;

        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y + dpToPx(35));
    }
    public interface OnAudioClickListener {
        // Method to handle audio deletion
        void onItemMoveCompleted(int fromPosition, int toPosition);
        void onAudioDelete(String audioUri);
void onclick();
void swap();
    }


    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    @Override
    public int getItemCount() {
        return audioFiles.size();
    }

    private void playNewAudio(String audioFilePath, int position) {
        stopProgressUpdate();
        stopCurrentAudio();  // Ensures any existing playback is halted

        // Initialize the MediaPlayer if it's null
        if (adapterMediaPlayer == null) {
            adapterMediaPlayer = new MediaPlayer();
        }

        try {
            adapterMediaPlayer.reset();  // Resets the player to the idle state

            // Set the data source and prepare the MediaPlayer
            adapterMediaPlayer.setDataSource(audioFilePath);
            adapterMediaPlayer.prepare();
            adapterMediaPlayer.start();

            isPlaying = true;
            currentPlayingPosition = position;
            notifyDataSetChanged(); // Updates all items for the new playing state
            startProgressUpdate();  // Begin updating the progress bar

            // Set a listener for when the audio finishes playing
            adapterMediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                stopProgressUpdate();  // Stop updating progress
                notifyDataSetChanged();  // Refresh UI to indicate no item is playing
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void pauseAudio() {
        if (adapterMediaPlayer.isPlaying()) {
            adapterMediaPlayer.pause();
            isPlaying = false;
            stopProgressUpdate();
        }
    }

    private void resumeAudio() {
        if (!adapterMediaPlayer.isPlaying()) {
            adapterMediaPlayer.start();
            isPlaying = true;
            startProgressUpdate();
        }
    }

    private void stopCurrentAudio() {
        if (adapterMediaPlayer != null) { // Kiểm tra xem mediaPlayer có khác null không
            try {
                if (adapterMediaPlayer.isPlaying()) { // Kiểm tra xem mediaPlayer có đang phát không
                    adapterMediaPlayer.stop(); // Dừng âm thanh
                }
                adapterMediaPlayer.reset(); // Đặt lại mediaPlayer để sẵn sàng phát âm thanh mới
            } catch (IllegalStateException e) {
                Log.e("MediaPlayer Error", "Error stopping media player: " + e.getMessage());
            }
        }

        // Cập nhật trạng thái và UI
        isPlaying = false;
        stopProgressUpdate(); // Dừng cập nhật tiến trình

        // Cập nhật item trong RecyclerView
        if (currentPlayingPosition != -1) {
            notifyItemChanged(currentPlayingPosition);
        }
        currentPlayingPosition = -1; // Đặt lại vị trí đang phát
    }

    private void startProgressUpdate() {
        stopProgressUpdate(); // Ensure we don't have multiple runnables
        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentPlayingPosition != -1 && isPlaying && adapterMediaPlayer != null) {
                    int currentPosition = adapterMediaPlayer.getCurrentPosition();
                    int totalDuration = adapterMediaPlayer.getDuration();
                    int progress = (int) (((float) currentPosition / totalDuration) * 100);

                    // Update the progress bar for the currently playing item
                    notifyItemChanged(currentPlayingPosition);

                    handler.postDelayed(this, 10); // Update every 100ms
                }
            }
        };
        handler.post(updateProgressRunnable);
    }

    private void stopProgressUpdate() {
        if (updateProgressRunnable != null) {
            handler.removeCallbacks(updateProgressRunnable);
        }
    }

    public void cleanup() {
        stopCurrentAudio();
        if (handler != null) {
            stopProgressUpdate();
        }

    }

}