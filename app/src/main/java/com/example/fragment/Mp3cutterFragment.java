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
import com.example.myaudio.OnAudioActionListener;
import com.example.st046_audioeditorandmusiceditor.databinding.FragmentMp3cutterBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Mp3cutterFragment extends Fragment {
    private FragmentMp3cutterBinding binding;
    private RecyclerView recyclerView;
    private Mp3Adapterfrm mp3Adapter;
    private static final String TAG = "Mp3cutterFragment";

    public Mp3cutterFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mp3Adapter != null) {
            Log.d(TAG, "onpáuh1");
            mp3Adapter.stopCurrentAudio();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart1: ");

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMp3cutterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = binding.recy;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Lấy tất cả file âm thanh từ thư mục Music/MergeAudio
        ArrayList<AudioFile> audioFiles = getAllAudioFiles(requireContext());
        if(audioFiles.isEmpty()){
            binding.recy.setVisibility(View.GONE);
            binding.viewnull.setVisibility(View.VISIBLE);
        } else {
            binding.recy.setVisibility(View.VISIBLE);
            binding.viewnull.setVisibility(View.GONE);
        }
        Log.e(TAG, "Audio files loaded: " + audioFiles);
        mp3Adapter = new Mp3Adapterfrm(audioFiles, requireContext());
        recyclerView.setAdapter(mp3Adapter);
        mp3Adapter.setOnAudioClickListener(new Mp3Adapterfrm.OnAudioClickListener() {
            @Override
            public void onAudioDelete(String audioId) {
                int ab =mp3Adapter.getItemCount();
                if (ab<1){
                    binding.recy.setVisibility(View.GONE);
                    binding.viewnull.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // Phương thức lấy tất cả file âm thanh từ thư mục Music/MergeAudio
    public ArrayList<AudioFile> getAllAudioFiles(Context context) {
        ArrayList<AudioFile> audioFiles = new ArrayList<>();
        File audioDirectory = new File(context.getExternalFilesDir(null), "Music/Mp3cutter");

        if (audioDirectory.exists() && audioDirectory.isDirectory()) {
            // Lấy tất cả file trong thư mục này
            File[] files = audioDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isAudioFile(file)) {  // Kiểm tra nếu file là file âm thanh
                        // Lấy metadata của file âm thanh
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        try {
                            mmr.setDataSource(file.getAbsolutePath());

                            String name = file.getName();
                            String path = file.getAbsolutePath();
                            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            long durationMs = Long.parseLong(durationStr != null ? durationStr : "0");
                            String duration = formatDuration(durationMs);
                            long sizeBytes = file.length();
                            long dateAddedSecs = file.lastModified(); // Sử dụng thời gian chỉnh sửa cuối cùng của file

                            // Định dạng dữ liệu
                            String size = formatSize(sizeBytes);
                            String date = formatDate(dateAddedSecs);

                            // Thêm thông tin file âm thanh vào danh sách
                            audioFiles.add(new AudioFile(path,name,  duration, size, date));
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc metadata file: " + file.getName(), e);
                        } finally {
//                            mmr.release(); // Giải phóng MediaMetadataRetriever
                        }
                    }
                }
            }
        }

        return audioFiles;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if (mp3Adapter != null) {
            Log.d(TAG, "onStop: aa");
            mp3Adapter.stopCurrentAudio();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop_____: ");
        if (mp3Adapter != null) {
            Log.d(TAG, "onStop: aa");
            mp3Adapter.stopCurrentAudio();
        }
    }

    // Phương thức kiểm tra xem file có phải là file âm thanh hay không
    private boolean isAudioFile(File file) {
        String[] audioExtensions = {".mp3", ".wav", ".aac", ".m4a", ".flac", ".ogg"};
        for (String ext : audioExtensions) {
            if (file.getName().toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mp3Adapter != null) {
            mp3Adapter.stopCurrentAudio();
        }
    }
    private String formatSize(long sizeBytes) {
        return String.format(Locale.getDefault(), "%.2f MB", sizeBytes / (1024.0 * 1024.0));
    }

    private String formatDate(long dateAddedMs) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(new Date(dateAddedMs));
    }
}
