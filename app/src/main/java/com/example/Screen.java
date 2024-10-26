package com.example;



import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import androidx.activity.EdgeToEdge;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Language.LanguageActivity;
import com.example.intro.IntroActivity;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityScreenBinding;

public class Screen extends AppCompatActivity {

    private ActivityScreenBinding binding;
    private Handler handler = new Handler();  // Create a handler object
    private Runnable transitionRunnable;     // Create a runnable object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = ActivityScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);

        // Start rotating the image
        ImageView imageView = binding.imageView5;
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        rotateAnimator.setDuration(2000);
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.start();

        // Define the runnable for the transition
        transitionRunnable = new Runnable() {
            @Override
            public void run() {
                transitionToLanguage();
            }
        };

        // Schedule the transition after 2000 ms
        handler.postDelayed(transitionRunnable, 2000);
    }
    private void transitionToLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("language", Context.MODE_PRIVATE);
        String savedValue = sharedPreferences.getString("savedValue", "false"); // Default to "false" if not found

        Intent intent;
        if ("true".equals(savedValue)) {
            intent = new Intent(Screen.this, IntroActivity.class);
        } else {
            intent = new Intent(Screen.this, LanguageActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();  // Finish the current activity after the new one is started
    }

    private void cancelTransition() {
        handler.removeCallbacks(transitionRunnable); // Cancel the runnable
    }

    // Method to resume the transition (restart the handler)
    private void resumeTransition(long delay) {
        handler.postDelayed(transitionRunnable, delay); // Re-schedule the runnable
    }

    @Override
    public void onBackPressed() {
        // Optionally handle back press if necessary
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTransition(); // Pause transition when the activity goes to background
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeTransition(2000); // Resume transition with 2-second delay when the activity is visible again
    }
}
