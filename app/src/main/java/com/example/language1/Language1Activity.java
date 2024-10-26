package com.example.language1;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.Home.HomeActivity;
import com.example.model.LanguageItem;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.ultils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

public class Language1Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Language1Adapter adapter;
    private List<LanguageItem> languageItems;
    private TextView textView;
private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        showSystemUI(this, true);
        setContentView(R.layout.activity_language2);
        recyclerView = findViewById(R.id.languageList);
        textView = findViewById(R.id.v);
        imageView=findViewById(R.id.ql);
        textView.setVisibility(View.VISIBLE);
        textView.setOnClickListener(v -> {
            saveValueToPreferences("true");

            // Lấy mã ngôn ngữ đã chọn từ adapter
            String selectedLanguageCode = adapter.getSelectedLanguageCode();

            if (selectedLanguageCode != null && !selectedLanguageCode.isEmpty()) {
                // Lưu mã ngôn ngữ đã chọn vào SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("language", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("KEY_LANGUAGE", selectedLanguageCode);
                editor.apply();

                SystemUtils.saveLocale(Language1Activity.this, selectedLanguageCode);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        languageItems = new ArrayList<>();

        languageItems.add(new LanguageItem(R.drawable.taybanha, "Spanish", "es", false));
        languageItems.add(new LanguageItem(R.drawable.french, "French", "fr", false));
        languageItems.add(new LanguageItem(R.drawable.hindi, "Hindi", "hi", false));
        languageItems.add(new LanguageItem(R.drawable.anh, "English", "en", false));
        languageItems.add(new LanguageItem(R.drawable.portugeese, "Portuguese", "pt", false));
        languageItems.add(new LanguageItem(R.drawable.german, "German", "de", false));
        languageItems.add(new LanguageItem(R.drawable.indo, "Indonesian", "in", false));

        adapter = new Language1Adapter(this, languageItems);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemSelectedListener(position -> {

            textView.setVisibility(View.VISIBLE);
        });
imageView.setOnClickListener(v->{
finish();
});
        textView.setOnClickListener(v -> {
            saveValueToPreferences("true");
            String selectedLanguageCode = adapter.getSelectedLanguageCode();

            if (selectedLanguageCode != null && !selectedLanguageCode.isEmpty()) {
                SystemUtils.saveLocale(Language1Activity.this, selectedLanguageCode);
            }

        });

    }
    private void transitionToLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        String savedsetting = sharedPreferences.getString("savedsetting", "false"); // Default to "false" if not found

        Intent intent;
        if ("true".equals(savedsetting)) {
            intent = new Intent(Language1Activity.this, HomeActivity.class);
            // Clear the entire activity stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent(Language1Activity.this, HomeActivity.class);
        }
        startActivity(intent);
        finishAffinity();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setLocale(this);
        showSystemUI(this, true);
    }

    private void saveValueToPreferences(String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("language", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (editor.putString("savedValue", value).commit()) {
            transitionToLanguage();
        }
    }

}