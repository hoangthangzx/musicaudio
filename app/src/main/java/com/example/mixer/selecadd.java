package com.example.mixer;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.merge.MergeActivity;
import com.example.model.AudioFile;
import com.example.model.Mp3File;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivitySelectaudio2Binding;
import com.example.ultils.AudioUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class selecadd extends AppCompatActivity {
    ActivitySelectaudio2Binding binding;
    private selectaudio2Adapter mp3Adapter;
    private selectaudioclickAdapter clickAdapter;
    private RecyclerView recySelectedAudios, recyAllAudios;
    private MutableLiveData<List<Mp3File>> selectedAudios = new MutableLiveData<>();
    private static final String TAG = "Selectaudio2Activity";
    private List<Mp3File> allAudioFiles; // Store all audio files
    private List<Mp3File> filteredAudioFiles; // Store filtered audio files
    private List<Mp3File> file; // Store filtered audio files

    int Sizeadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = ActivitySelectaudio2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);


        Intent intent = getIntent();
        String value = intent.getStringExtra("key");


        recySelectedAudios = findViewById(R.id.recyselect);
        allAudioFiles= getAllAudioFiles(this);
        mp3Adapter = new selectaudio2Adapter(allAudioFiles, this, value, selectedAudios);
        clickAdapter = new selectaudioclickAdapter(new ArrayList<>(), this, value, selectedAudios, mp3Adapter);
        recySelectedAudios.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recySelectedAudios.setAdapter(clickAdapter);

        // Observe changes to selected audios
        selectedAudios.observe(this, new Observer<List<Mp3File>>() {
            @Override
            public void onChanged(List<Mp3File> selectedFiles) {
                if (selectedFiles != null && !selectedFiles.isEmpty()) {
                    file=selectedFiles;
                    binding.constraintLayout12.setVisibility(View.VISIBLE);
                    binding.noitem.setVisibility(View.GONE);
                    String key2 = intent.getStringExtra("key2");

                    if(Objects.equals(key2, "1")) {
                        Log.d(TAG, "onChanged: "+selectedFiles.size());
                        if (selectedFiles.size()>1){
                            binding.next.setVisibility(View.VISIBLE);
                        }else {
                            binding.next.setVisibility(View.GONE);
                        }
                    }else {
                        Log.d(TAG, "onChanged:1 "+selectedFiles.size());
                        if (selectedFiles.size()>0){
                            binding.next.setVisibility(View.VISIBLE);
                        }else {
                            binding.next.setVisibility(View.GONE);
                        }
                    }

                }
                else {
                    binding.noitem.setVisibility(View.VISIBLE);
                    binding.next.setVisibility(View.GONE);
//                    binding.constraintLayout12.setVisibility(View.GONE);
                }
                int size =    AudioUtils.getSelectedAudioFiles().size();
                if(size>5){

                    Toast.makeText(getApplicationContext(), R.string.you_can_only_select_up_to_5_files, Toast.LENGTH_SHORT).show();
                }else {

                }
                Sizeadapter=selectedFiles.size();
                Log.d(TAG, "Selected Audio Files: " + selectedFiles.size());
                clickAdapter.updateSelectedFiles(selectedFiles);
            }
        });

        // Initialize all audio files RecyclerView (recy)
        recyAllAudios = findViewById(R.id.recy);
        recyAllAudios.setLayoutManager(new LinearLayoutManager(this));
        recyAllAudios.setAdapter(mp3Adapter);
        binding.next.setOnClickListener(v -> {
            int a=AudioUtils.getSelectedAudioFiles().size();
            int b =Sizeadapter;
            int c = a+b;
            Log.d(TAG, "onCreate: "+c);
            if(c<6){
//                binding.next.setEnabled(false);
                List<Mp3File> selectedFiles = selectedAudios.getValue();
                if (selectedFiles != null && !selectedFiles.isEmpty()) {
                    ArrayList<AudioFile> audioFilesList = new ArrayList<>();
                    for (Mp3File file : selectedFiles) {
                        AudioFile audioFile = new AudioFile(
                                file.getPath(),       // URI
                                file.getName(),       // File Name
                                file.getSize(),       // File Size
                                file.getDuration(),   // File Duration
                                file.getDate());      // File Date

                        audioFilesList.add(audioFile);
                        AudioUtils.addSelectedAudioFile(audioFile);

                    }

                    Log.d(TAG, "Total selected audio files: " + audioFilesList.size());
                } else {
                    binding.next.setEnabled(true);
                    Log.d(TAG, "No audio files selected");
                }

                finish();
            } else {
                binding.next.setEnabled(true);
                Toast.makeText(this, "You can only select up to 4 files.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.back.setOnClickListener(v->{

//                Intent nextIntent;
//                switch (value) {
//                    case "mixer2":
//                        nextIntent = new Intent(this, Mixer2Activity.class);
//                        break;
//                    case "merger2":
//                        nextIntent = new Intent(this, MergeActivity2.class);
//                        break;
//                    default:
//                        nextIntent = new Intent(this, equazerActivity.class);
//                        break;
//                }
////                binding.next.setEnabled(false);
//                List<Mp3File> selectedFiles = selectedAudios.getValue();
//                if (selectedFiles != null && !selectedFiles.isEmpty()) {
//                    ArrayList<AudioFile> audioFilesList = new ArrayList<>();
//                    for (Mp3File file : selectedFiles) {
//                        AudioFile audioFile = new AudioFile(
//                                file.getPath(),       // URI
//                                file.getName(),       // File Name
//                                file.getSize(),       // File Size
//                                file.getDuration(),   // File Duration
//                                file.getDate());      // File Date
//
//                        audioFilesList.add(audioFile);
//                        AudioUtils.addSelectedAudioFile(audioFile);
//
//                    }
//                    nextIntent.putParcelableArrayListExtra("audio_files_list", audioFilesList);
//
//                    // Log tổng số tệp đã chọn
//                    Log.d(TAG, "Total selected audio files: " + audioFilesList.size());
//                } else {
//                    binding.next.setEnabled(true);
//                    Log.d(TAG, "No audio files selected");
//                }
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(nextIntent);
            finish();
        });
        filteredAudioFiles = new ArrayList<>(allAudioFiles);
        binding.imageView2.setOnClickListener(v -> {
            binding.imageView2.setVisibility(View.GONE);
            binding.textView2.setVisibility(View.GONE);
            binding.search.setVisibility(View.VISIBLE);
        });
        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    binding.imageView7.setVisibility(View.VISIBLE);
                } else {
                    binding.imageView7.setVisibility(View.GONE);
                }
                filterAudioFiles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.imageView7.setOnClickListener(v -> {
            binding.editText.setText(""); // Clear the text in the EditText

        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String value = intent.getStringExtra("key");


        recySelectedAudios = findViewById(R.id.recyselect);
        allAudioFiles= getAllAudioFiles(this);
        mp3Adapter = new selectaudio2Adapter(allAudioFiles, this, value, selectedAudios);
        clickAdapter = new selectaudioclickAdapter(new ArrayList<>(), this, value, selectedAudios, mp3Adapter);
        recySelectedAudios.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recySelectedAudios.setAdapter(clickAdapter);

        // Observe changes to selected audios
        selectedAudios.observe(this, new Observer<List<Mp3File>>() {
            @Override
            public void onChanged(List<Mp3File> selectedFiles) {
                if (selectedFiles != null && !selectedFiles.isEmpty()) {
                    file=selectedFiles;
                    binding.constraintLayout12.setVisibility(View.VISIBLE);
                    binding.noitem.setVisibility(View.GONE);
                    String key2 = intent.getStringExtra("key2");

                    if(Objects.equals(key2, "1")) {
                        Log.d(TAG, "onChanged: "+selectedFiles.size());
                        if (selectedFiles.size()>1){
                            binding.next.setVisibility(View.VISIBLE);
                        }else {
                            binding.next.setVisibility(View.GONE);
                        }
                    }else {
                        Log.d(TAG, "onChanged:1 "+selectedFiles.size());
                        if (selectedFiles.size()>0){
                            binding.next.setVisibility(View.VISIBLE);
                        }else {
                            binding.next.setVisibility(View.GONE);
                        }
                    }

                }
                else {
                    binding.noitem.setVisibility(View.VISIBLE);
                    binding.next.setVisibility(View.GONE);
//                    binding.constraintLayout12.setVisibility(View.GONE);
                }
                int size =    AudioUtils.getSelectedAudioFiles().size();
                if(size>5){

                    Toast.makeText(getApplicationContext(), R.string.you_can_only_select_up_to_5_files, Toast.LENGTH_SHORT).show();
                }else {

                }
                Sizeadapter=selectedFiles.size();
                Log.d(TAG, "Selected Audio Files: " + selectedFiles.size());
                clickAdapter.updateSelectedFiles(selectedFiles);
            }
        });

        // Initialize all audio files RecyclerView (recy)
        recyAllAudios = findViewById(R.id.recy);
        recyAllAudios.setLayoutManager(new LinearLayoutManager(this));
        recyAllAudios.setAdapter(mp3Adapter);
        mp3Adapter.notifyDataSetChanged();
        checkPermissions();
    }
    private void filterAudioFiles(String query) {

        query = query.toLowerCase().trim();
        filteredAudioFiles.clear(); // Xóa danh sách để chuẩn bị cho kết quả mới

        if (query.isEmpty()) {
            // Nếu không có truy vấn, thêm tất cả file vào filteredAudioFiles
            filteredAudioFiles.addAll(allAudioFiles);
        } else {
            // Lặp qua allAudioFiles để kiểm tra tên file
            for (Mp3File file : allAudioFiles) {
                if (file.getName().toLowerCase().contains(query)) {
                    // Nếu file khớp với truy vấn, thêm vào filteredAudioFiles
                    filteredAudioFiles.add(file);
                }
            }
        }

        // Cập nhật giao diện cho kết quả tìm kiếm
        updateUIForSearchResults();
    }


    private void updateUIForSearchResults() {
        if (filteredAudioFiles.isEmpty()) {
            binding.selectnull.setVisibility(View.VISIBLE);
            binding.recy.setVisibility(View.GONE);
        } else {
            binding.selectnull.setVisibility(View.GONE);
            binding.recy.setVisibility(View.VISIBLE);
        }

        // Update the adapter's data and notify it
        mp3Adapter.updateList(filteredAudioFiles);
        mp3Adapter.notifyDataSetChanged();
    }
    public List<Mp3File> getAllAudioFiles(Context context) {
        List<Mp3File> audioFiles = new ArrayList<>();

        // Lấy audio từ bộ nhớ ngoài
        Uri externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        audioFiles.addAll(getAudioFilesFromUri(context, externalUri));

        // Lấy audio từ bộ nhớ trong
        Uri internalUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        audioFiles.addAll(getAudioFilesFromUri(context, internalUri));

        return audioFiles;
    }

    private List<Mp3File> getAudioFilesFromUri(Context context, Uri uri) {
        List<Mp3File> audioFiles = new ArrayList<>();
        String[] projection = {
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_ADDED
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";  // Chỉ tìm các tệp âm nhạc
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, null, null);

        if (cursor != null) {
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumn);
                String path = cursor.getString(pathColumn);
                long durationMs = cursor.getLong(durationColumn);
                long sizeBytes = cursor.getLong(sizeColumn);
                long dateAddedSecs = cursor.getLong(dateColumn);

                // Định dạng thời lượng và kích thước để dễ đọc
                String duration = formatDuration(durationMs);
                String size = formatSize(sizeBytes);
                String date = formatDate(dateAddedSecs);

                audioFiles.add(new Mp3File(name, path, duration, size, date));
            }
            cursor.close();
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
        return sdf.format(new Date(dateAddedSecs * 1000));  // Chuyển từ giây sang mili giây
    }
    private void checkPermissions() {
        boolean storagePermissionGranted;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            storagePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }

        if (!storagePermissionGranted) {
            binding.selectnull.setVisibility(View.VISIBLE);
            showPermissionDeniedDialog();
        } else {
            binding.selectnull.setVisibility(View.GONE);
        }
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_permisson, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);


        TextView btnSettings = dialogView.findViewById(R.id.yes);
        TextView no = dialogView.findViewById(R.id.no);
        btnSettings.setOnClickListener(v -> {
            openAppSettings();
            dialog.dismiss();
        });
        no.setOnClickListener(v -> dialog.dismiss());

        applyGradientToSaveText(no);
        dialog.show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
    private void applyGradientToSaveText(TextView textView) {
        Shader textShader = new LinearGradient(0, 0, 0, textView.getLineHeight(),
                new int[]{
                        Color.parseColor("#6573ED"), // Màu trên
                        Color.parseColor("#14D2E6")  // Màu dưới
                },
                new float[]{0.1f, 1f}, Shader.TileMode.CLAMP);  // 0.2 cho 20% trên, 1f cho 80% dưới

        textView.getPaint().setShader(textShader);
    }
}