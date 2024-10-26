package com.example.ultils;

import android.util.Log;

import com.example.model.AudioFile;

import java.util.ArrayList;
import java.util.Iterator;

public class AudioUtils {
    private static ArrayList<AudioFile> selectedAudioFiles = new ArrayList<>();

    public static void addSelectedAudioFile(AudioFile audioFile) {
        selectedAudioFiles.add(audioFile);
    }

    public static void saveSelectedAudioFiles(ArrayList<AudioFile> audioFiles) {
        selectedAudioFiles.clear();  // Clear the previous selection
        selectedAudioFiles.addAll(audioFiles);  // Add the new files
    }

    public static ArrayList<AudioFile> getSelectedAudioFiles() {
        return selectedAudioFiles;
    }
    public static void removeSelectedAudioFile(String uri) {
        for (int i = 0; i < selectedAudioFiles.size(); i++) {
            AudioFile audioFile = selectedAudioFiles.get(i);
            if (audioFile.getUri().equals(uri)) {
                selectedAudioFiles.remove(i);
                break;  // Thoát vòng lặp sau khi xóa
            }
        }

        for (AudioFile file : selectedAudioFiles) {
            Log.d("SelectedAudioFiles", "File URI: " + file.getUri() + ", File Name: " + file.getName());
        }
    }

    public static void clearSelectedAudioFiles() {
        selectedAudioFiles.clear();
    }
}
