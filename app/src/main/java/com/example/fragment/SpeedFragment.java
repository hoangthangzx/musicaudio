package com.example.fragment;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.model.AudioFile;
import com.example.st046_audioeditorandmusiceditor.databinding.FragmentSpeedBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class SpeedFragment extends Fragment {
    private FragmentSpeedBinding binding;
    private RecyclerView recyclerView;
    private Mp3Adapterfrm mergeAdapterfrm;
    private static final String TAG = "a";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSpeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if (mergeAdapterfrm != null) {
            Log.d(TAG, "onStop: aa");
            mergeAdapterfrm.stopCurrentAudio();
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = binding.recy;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));  // Sử dụng requireContext()

        // Lấy danh sách file mp3 từ thư mục Mp3cutter
        ArrayList<AudioFile> AudioFile = getAudioFiles(requireContext());
        if(AudioFile.isEmpty()){
            binding.recy.setVisibility(View.GONE);
            binding.viewnull.setVisibility(View.VISIBLE);
        } else {
            binding.recy.setVisibility(View.VISIBLE);
            binding.viewnull.setVisibility(View.GONE);
        }
        Log.e(TAG, "end " + AudioFile);
        mergeAdapterfrm = new Mp3Adapterfrm(AudioFile, requireContext());
        recyclerView.setAdapter(mergeAdapterfrm);  // Thiết lập Adapter cho RecyclerView
        mergeAdapterfrm.setOnAudioClickListener(new Mp3Adapterfrm.OnAudioClickListener() {
            @Override
            public void onAudioDelete(String audioId) {
                int ab =mergeAdapterfrm.getItemCount();
                if (ab<1){
                    binding.recy.setVisibility(View.GONE);
                    binding.viewnull.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public ArrayList<AudioFile> getAudioFiles(Context context) {
        ArrayList<AudioFile> audioFiles = new ArrayList<>();

        // Thư mục chứa các tệp âm thanh
        File audioDir = new File(context.getExternalFilesDir(null), "Speed");

        if (audioDir.exists() && audioDir.isDirectory()) {
            // Lấy tất cả các file có đuôi âm thanh phổ biến như mp3, wav, aac, m4a, ogg
            File[] files = audioDir.listFiles((dir, name) -> {
                String lowerCaseName = name.toLowerCase();
                return lowerCaseName.endsWith(".mp3") ||
                        lowerCaseName.endsWith(".wav") ||
                        lowerCaseName.endsWith(".aac") ||
                        lowerCaseName.endsWith(".m4a") ||
                        lowerCaseName.endsWith(".ogg");
            });

            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    String path = file.getAbsolutePath();
                    long sizeBytes = file.length();
                    String size = formatSize(sizeBytes);
                    String date = formatDate(file.lastModified());

                    try {
                        // Sử dụng MediaMetadataRetriever để lấy thời lượng của file
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(path);
                        String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        long durationMs = Long.parseLong(durationStr != null ? durationStr : "0");
                        String duration = formatDuration(durationMs);
                        retriever.release();

                        // Thêm file vào danh sách
                        audioFiles.add(new AudioFile(path, name, duration, size, date));

                        // Log thông tin từng file đã lấy được
                        Log.e(TAG, "Audio File: " + name + ", Path: " + path + ", Size: " + size + ", Duration: " + duration + ", Date: " + date);
                    } catch (Exception e) {
                        // Nếu có lỗi khi lấy metadata, bỏ qua file này
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Log.e(TAG, "Directory does not exist or is not a directory.");
        }

        if (audioFiles.isEmpty()) {
            Log.e(TAG, "No files found in the directory");
        } else {
            Log.e(TAG, "Files found: " + audioFiles.size());
        }

        return audioFiles;
    }



    private String formatDuration(long durationMs) {
        long seconds = (durationMs / 1000) % 60;
        long minutes = (durationMs / (1000 * 60)) % 60;
        long hours = durationMs / (1000 * 60 * 60);

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    private String formatSize(long sizeBytes) {
        return String.format(Locale.getDefault(), "%.2f MB", sizeBytes / (1024.0 * 1024.0));
    }

    private String formatDate(long dateAddedSecs) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(new Date(dateAddedSecs));
    }
}