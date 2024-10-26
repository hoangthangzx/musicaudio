package com.example;



import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Home.HomeActivity;
import com.example.language1.Language1Activity;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivitySettingBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.RateDialogBinding;


public class SettingActivity extends AppCompatActivity {
ActivitySettingBinding binding;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSystemUI(this, true);
        checkSavedRating();
        binding.rate.setOnClickListener(v -> {
            showdialograte();
        });
        binding.settingIcon.setOnClickListener(v->{
finish();
            Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
            startActivity(intent);
        });
        binding.language.setOnClickListener(v->{
            Intent intent = new Intent(SettingActivity.this, Language1Activity.class);
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        checkSavedRating();
        super.onResume();
    }

    private void checkSavedRating() {
        SharedPreferences sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        String savedSetting = sharedPreferences.getString("savedsetting", "false");

        if ("true".equals(savedSetting)) {
            binding.a.setVisibility(View.GONE);
        } else {
          binding.a.setVisibility(View.VISIBLE);
        }
    }
    private void saveValueToPreferences(String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedsetting", value);
        editor.apply();
    }
    private void showdialograte() {
        setLocale(this);
//        showSystemUI(this, true);
        RateDialogBinding binding = RateDialogBinding.inflate(getLayoutInflater());

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialogTheme);
        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Set layout parameters for the dialog
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            dialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog on the screen
//            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        binding.ratingBar.setOnRatingChangeListener((ratingBar, rating, fromUser) -> {
            updateIconAndText(binding, Math.round(rating));
        });
        binding.titleTextView.setSelected(true);
        binding.subtitleTextView.setSelected(true);
        binding.rateButton.setOnClickListener(v -> {
            int rating = Math.round(binding.ratingBar.getRating());
            if (rating == 0) {
                Toast.makeText(this, R.string.please_select_a_rating_before_submitting, Toast.LENGTH_SHORT).show();
            } else {
                saveValueToPreferences("true");
                Toast.makeText(this, R.string.thank_you_for_your_rating, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                recreate();
            }
        });

        binding.exitButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    private void updateIconAndText(RateDialogBinding binding, int rating) {
        setLocale(this);
        switch (rating) {
            case 1:
                binding.emojiImageView.setImageResource(R.drawable.icon2);
                binding.titleTextView.setText(R.string.oh_no);
                binding.subtitleTextView.setText(R.string.let_us_know_your_experience);
                break;
            case 2:
                binding.emojiImageView.setImageResource(R.drawable.ic3);
                binding.titleTextView.setText(R.string.oh_no);
                binding.subtitleTextView.setText(R.string.please_give_us_some_feedback);
                break;
            case 3:
                binding.emojiImageView.setImageResource(R.drawable.ic4);
                binding.titleTextView.setText(R.string.oh_no);
                binding.subtitleTextView.setText(R.string.please_give_us_some_feedback);
                break;
            case 4:
                binding.emojiImageView.setImageResource(R.drawable.ic5);
                binding.titleTextView.setText(R.string.we_love_you_too);
                binding.subtitleTextView.setText(R.string.thanks_for_your_feedback);
                break;
            case 5:
                binding.emojiImageView.setImageResource(R.drawable.ic6);
                binding.titleTextView.setText(R.string.we_love_you_too);
                binding.subtitleTextView.setText(R.string.thanks_for_your_feedback);
                break;
            default:
                binding.emojiImageView.setImageResource(R.drawable.ic0);
                binding.titleTextView.setText(R.string.do_you_like_the_app);
                binding.subtitleTextView.setText(R.string.let_us_know_your_experience);
                break;
        }

    }


}