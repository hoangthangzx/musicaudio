package com.example.Home;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.FragmentHomeBinding;


import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.SettingActivity;
import com.example.audiotex.AudiotexActivity;
import com.example.mixer.SelectActivity;
import com.example.model.HomeItem;
import com.example.myaudio.MyaudioActivity;
import com.example.selectaudio.Selectaudio2Activity;
import com.example.selectaudio.SlectaudioActivity;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityHomeBinding;
import com.example.st046_audioeditorandmusiceditor.databinding.RateDialogBinding;
import com.example.ultils.AudioUtils;
import com.example.voidchanger.VoidChangerActivity;
import com.example.volume.VolumeActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private HomeAdapter homeAdapter;
    private FragmentHomeBinding binding;
    private boolean isDialogShowing = false;
    File tempDir;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Setup layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recy.setLayoutManager(gridLayoutManager);

        // Clear selected audio files and setup adapter
        AudioUtils.clearSelectedAudioFiles();
        homeAdapter = new HomeAdapter(requireContext(), getItems(), this::onItemClicked);
        binding.recy.setAdapter(homeAdapter);

        // Setup click listener for the settings icon
        binding.imageView2.setOnClickListener(v -> {
            Intent a = new Intent(requireActivity(), SettingActivity.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(a);
        });

        // Initialize temp directory and clean up files
        tempDir = new File(requireActivity().getExternalFilesDir(null), "TempAudio");
        cleanupTempFiles();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void cleanupTempFiles() {
        if (tempDir != null && tempDir.exists()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AudioUtils.clearSelectedAudioFiles();
    }

    // This method is triggered when an item is clicked
    private void onItemClicked(String itemName) {
        Intent intent = null;
        switch (itemName.toLowerCase()) {
            case "mp3cutter":
                intent = new Intent(requireActivity(), SlectaudioActivity.class);
                intent.putExtra("key", "mp3cutter");
                intent.putExtra("key2", "1");
                break;
            case "merger":
                intent = new Intent(requireActivity(), SelectActivity.class);
                intent.putExtra("key", "merger");
                intent.putExtra("key2", "1");
                break;
            case "mixer":
                intent = new Intent(requireActivity(), SelectActivity.class);
                intent.putExtra("key", "mixer");
                intent.putExtra("key2", "1");
                break;
            case "speed":
                intent = new Intent(requireActivity(), Selectaudio2Activity.class);
                intent.putExtra("key", "speed");
                break;
            case "equalizer":
                intent = new Intent(requireActivity(), SlectaudioActivity.class);
                intent.putExtra("key", "equalizer");
                break;
            case "valume":
                intent = new Intent(requireActivity(), Selectaudio2Activity.class);
                intent.putExtra("key", "valume");
                break;
            case "voidchanger":
                intent = new Intent(requireActivity(), SlectaudioActivity.class);
                intent.putExtra("key", "voidchanger");
                break;
            case "audiotex":
                intent = new Intent(requireActivity(), SlectaudioActivity.class);
                intent.putExtra("key", "audiotex");
                break;
            default:
                intent = null;
                break;
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private List<HomeItem> getItems() {
        List<HomeItem> items = new ArrayList<>();
        items.add(new HomeItem(R.drawable.cutterxanh, "mp3cutter", getString(R.string.mp3cutter)));
        items.add(new HomeItem(R.drawable.merge, "merger", getString(R.string.merger)));
        items.add(new HomeItem(R.drawable.mixer, "mixer", getString(R.string.mixer)));
        items.add(new HomeItem(R.drawable.speed, "speed", getString(R.string.speed)));
        items.add(new HomeItem(R.drawable.equalizer, "equalizer", getString(R.string.equalizer)));
        items.add(new HomeItem(R.drawable.valume, "valume", getString(R.string.volume)));
        items.add(new HomeItem(R.drawable.voidchanger, "voidchanger", getString(R.string.voice_changer)));
        items.add(new HomeItem(R.drawable.audiototex, "audiotex", getString(R.string.audio_to_text)));
        return items;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle the back press
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Custom back press logic for the fragment
                SharedPreferences shared = requireActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
                String savedSetting = shared.getString("savedsetting", "false");

                if ("true".equals(savedSetting)) {
                    requireActivity().finishAffinity();
                } else {
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                    int backPressCount = sharedPreferences.getInt("back_press_count", 0);

                    // Save the updated back press count
                    save();

                    // Check if the back press count is odd
                    if (backPressCount % 2 == 0) {
                        // Exit the activity
                        requireActivity().finishAffinity();
                    } else {
                        if (!isDialogShowing) {
                            showdialograte();
                        }
                    }
                }
            }
        });
    }




    private void showdialograte() {
        // Set the flag to true indicating the dialog is showing
        isDialogShowing = true;
        setLocale(requireContext());
        View dimView = new View(requireContext());
        dimView.setBackgroundColor(Color.parseColor("#A19AA6"));
        dimView.setAlpha(0.6f);

        // Add the overlay view to the root view
        ViewGroup rootView = (ViewGroup) requireActivity().getWindow().getDecorView().getRootView();
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.addView(dimView, layoutParams);

        RateDialogBinding binding = RateDialogBinding.inflate(getLayoutInflater());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        binding.titleTextView.setSelected(true);
        dialog.setCanceledOnTouchOutside(false);

        binding.ratingBar.setOnRatingChangeListener((ratingBar, rating, fromUser) -> {
            updateIconAndText(binding, Math.round(rating));
        });
        binding.subtitleTextView.setSelected(true);

        binding.rateButton.setOnClickListener(v -> {
            float rating = binding.ratingBar.getRating();

            if (rating == 0) {
                Toast.makeText(requireContext(), R.string.please_select_a_rating_before_submitting, Toast.LENGTH_SHORT).show();
            } else {
                requireActivity().finishAffinity();
                Toast.makeText(requireContext(), R.string.thank_you_for_your_rating, Toast.LENGTH_SHORT).show();
                rootView.removeView(dimView);
                saveValueToPreferences("true");
                dialog.dismiss();
            }
        });

        binding.exitButton.setOnClickListener(v -> {
            requireActivity().finishAffinity();
            rootView.removeView(dimView);
            dialog.dismiss();
        });

        dialog.setOnDismissListener(dialogInterface -> {
            rootView.removeView(dimView);
            isDialogShowing = false;  // Reset the flag when the dialog is dismissed
        });

        dialog.show();
    }

    private void saveValueToPreferences(String value) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedsetting", value);
        editor.apply();
    }

    private void save() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int backPressCount = sharedPreferences.getInt("back_press_count", 0);
        backPressCount++;
        editor.putInt("back_press_count", backPressCount);
        editor.apply();
    }

    private void updateIconAndText(RateDialogBinding binding, int rating) {
        setLocale(requireContext());
        switch (rating) {
            case 1:
                binding.emojiImageView.setImageResource(R.drawable.icon2);
                binding.titleTextView.setText(R.string.oh_no);
                binding.subtitleTextView.setText(R.string.please_give_us_some_feedback);
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
