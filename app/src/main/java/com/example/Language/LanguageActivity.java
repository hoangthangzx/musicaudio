package com.example.Language;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.Home.HomeActivity;
import com.example.intro.IntroActivity;
import com.example.model.LanguageItem;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.ultils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LanguageAdapter adapter;
    private List<LanguageItem> languageItems;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        setLocale(this);
        showSystemUI(this, true);
        recyclerView = findViewById(R.id.languageList);
        textView = findViewById(R.id.v);
        textView.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        languageItems = new ArrayList<>();

        languageItems.add(new LanguageItem(R.drawable.taybanha, "Spanish", "es", false));//2
       languageItems.add(new LanguageItem(R.drawable.french, "French", "fr", false));//
        languageItems.add(new LanguageItem(R.drawable.hindi, "Hindi", "hi", false));
        languageItems.add(new LanguageItem(R.drawable.anh, "English", "en", false));//6
        languageItems.add(new LanguageItem(R.drawable.portugeese, "Portuguese", "pt", false));
        languageItems.add(new LanguageItem(R.drawable.german, "German", "de", false));
        languageItems.add(new LanguageItem(R.drawable.indo, "Indonesian", "in", false));

        adapter = new LanguageAdapter(this, languageItems);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemSelectedListener(position -> {

            textView.setVisibility(View.VISIBLE);
        });

        textView.setOnClickListener(v -> {
            saveValueToPreferences("true");
            String selectedLanguageCode = adapter.getSelectedLanguageCode();

            if (selectedLanguageCode != null && !selectedLanguageCode.isEmpty()) {
                SystemUtils.saveLocale(LanguageActivity.this, selectedLanguageCode);
            }

        });
    }
    private void transitionToLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        String savedsetting = sharedPreferences.getString("savedsetting", "false"); // Default to "false" if not found

        Intent intent;
        if ("true".equals(savedsetting)) {
            intent = new Intent(LanguageActivity.this, HomeActivity.class);
            // Clear the entire activity stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent(LanguageActivity.this, IntroActivity.class);
        }
        startActivity(intent);
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
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