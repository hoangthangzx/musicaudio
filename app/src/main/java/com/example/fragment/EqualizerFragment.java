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
import com.example.st046_audioeditorandmusiceditor.databinding.FragmentEqualizerBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class EqualizerFragment extends Fragment {
    private FragmentEqualizerBinding binding;
    private RecyclerView recyclerView;
    private Mp3Adapterfrm mergeAdapterfrm;
    private static final String TAG = "a";
private int size ;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEqualizerBinding.inflate(inflater, container, false);
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
        ArrayList<AudioFile> AudioFile = getMp3CutterAudioFiles(requireContext());
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

        Log.d("EqualizerFragment", "Adapter initialized");

        // Set the audio click listener
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

    public ArrayList<AudioFile> getMp3CutterAudioFiles(Context context) {
        ArrayList<AudioFile> audioFiles = new ArrayList<>();

        // Lấy đường dẫn thư mục Mp3cutter trong bộ nhớ ứng dụng
        File mp3CutterDir = new File(context.getExternalFilesDir(null), "EqualizerAudio");

        if (mp3CutterDir.exists() && mp3CutterDir.isDirectory()) {
            File[] files = mp3CutterDir.listFiles((dir, name) -> {
                String lowerCaseName = name.toLowerCase();
                return lowerCaseName.endsWith(".mp3") ||
                        lowerCaseName.endsWith(".aac") ||
                        lowerCaseName.endsWith(".wav") ||
                        lowerCaseName.endsWith(".flac") ||
                        lowerCaseName.endsWith(".m4a") ||
                        lowerCaseName.endsWith(".ogg") ||
                        lowerCaseName.endsWith(".wma") ||
                        lowerCaseName.endsWith(".opus");
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

                        audioFiles.add(new AudioFile(path, name, duration, size, date));
                    } catch (Exception e) {
                        // Nếu có lỗi khi lấy metadata, bỏ qua file này
                        e.printStackTrace();
                    }
                }
            }
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