package com.example.interect;

import static com.example.ultils.SystemUtils.setLocale;
import static com.example.ultils.Untils.showSystemUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.selectaudio.Selectaudio2Activity;
import com.example.selectaudio.SlectaudioActivity;
import com.example.st046_audioeditorandmusiceditor.R;
import com.example.st046_audioeditorandmusiceditor.databinding.ActivityInterectBinding;

public class InterectActivity extends AppCompatActivity {
    ActivityInterectBinding binding;
    private String itemName = "mp3cutter";

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this);
        showSystemUI(this, true);
        binding = ActivityInterectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TextView next = findViewById(R.id.con);
        TextView mp3 = findViewById(R.id.mp3);
        TextView me = findViewById(R.id.me);
        TextView eq = findViewById(R.id.eq);
        TextView mi = findViewById(R.id.mi);
        TextView sp = findViewById(R.id.sp);
        TextView va = findViewById(R.id.va);
        TextView voidchan = findViewById(R.id.voidchan);
        TextView au = findViewById(R.id.audio);
        applyGradientToSaveText(me);
        applyGradientToSaveText(mp3);
        applyGradientToSaveText(eq);
        applyGradientToSaveText(mi);
        applyGradientToSaveText(sp);
        applyGradientToSaveText(va);
        applyGradientToSaveText(voidchan);
        applyGradientToSaveText(au);
        applyGradientToSaveText(next);
        setBackgroundResource();
        // Set click listeners for the items
        binding.mp3cutter.setOnClickListener(view ->{
                   setItemName("mp3cutter");
                    setBackgroundResource( );
                });

        binding.mixer.setOnClickListener(view ->
                {
                    setItemName("mixer");
                    applyGradientToSaveText(me);
                    applyGradientToSaveText(eq);
                    applyGradientToSaveText(mp3);
                    applyGradientToSaveText(sp);
                    applyGradientToSaveText(va);
                    applyGradientToSaveText(voidchan);
                    applyGradientToSaveText(au);
                    applyToSaveText(mi);
                    setBackgroundResource() ;
                    me.invalidate();
                    eq.invalidate();
                    mp3.invalidate();
                    sp.invalidate();
                    va.invalidate();
                    voidchan.invalidate();
                    au.invalidate();
                    mi.invalidate();


                });

        binding.audiototex.setOnClickListener(view ->
                {
                    itemName="audiototex";
                    setBackgroundResource( );
                });
        binding.merge.setOnClickListener(view ->  {
            itemName="merge";
            setBackgroundResource( );
        });
        binding.voichanger.setOnClickListener(view ->  {
            itemName="voichanger";
            setBackgroundResource( );
        });
        binding.speed.setOnClickListener(view ->  {
            itemName="speed";
            setBackgroundResource( );
        });
        binding.volume.setOnClickListener(view ->  {
            itemName="volume";
            setBackgroundResource( );
        });
        binding.equalizer.setOnClickListener(view ->  {
            itemName="equalizer";
            setBackgroundResource( );
        });
        // Add other items' click listeners if needed

        binding.con.setOnClickListener(v -> {
            saveValueToPreferences("true");
            switch (itemName) {
                case "mp3cutter":
                    Intent mp3cutter = new Intent(InterectActivity.this, SlectaudioActivity.class);
                    mp3cutter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mp3cutter.putExtra("key", "mp3cutter");
                    startActivity(mp3cutter);
                    break;
                case "merge":
                    Intent merger = new Intent(InterectActivity.this, Selectaudio2Activity.class);
                    merger.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    merger.putExtra("key", "merger");
                    startActivity(merger);
                    break;
                case "mixer":
                    Intent mixer = new Intent(InterectActivity.this, Selectaudio2Activity.class);
                    mixer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mixer.putExtra("key", "mixer");
                    startActivity(mixer);
                    break;
                case "speed":
                    Intent speed = new Intent(InterectActivity.this, Selectaudio2Activity.class);
                    speed.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    speed.putExtra("key", "speed");
                    startActivity(speed);
                    break;
                case "equalizer":
                    Intent equalizer = new Intent(InterectActivity.this, SlectaudioActivity.class);
                    equalizer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    equalizer.putExtra("key", "equalizer");
                    startActivity(equalizer);
                    break;
                case "volume":
                    Intent valume = new Intent(InterectActivity.this, Selectaudio2Activity.class);
                    valume.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    valume.putExtra("key", "valume");
                    startActivity(valume);
                    break;
                case "voichanger":
                    Intent voidchanger = new Intent(InterectActivity.this, SlectaudioActivity.class);
                    voidchanger.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    voidchanger.putExtra("key", "voidchanger");
                    startActivity(voidchanger);
                    break;
                case "audiototex":
                    Intent audiotex = new Intent(InterectActivity.this, SlectaudioActivity.class);
                    audiotex.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    audiotex.putExtra("key", "audiotex");
                    startActivity(audiotex);
                    break;
                default:

                    break;
            }
        });


    }
    private void applyGradientToSaveText(TextView textView) {
        Shader textShader = new LinearGradient(0, 0, 0, textView.getLineHeight(),
                new int[]{
                        Color.parseColor("#6573ED"), // Top color (20%)
                        Color.parseColor("#14D2E6")  // Bottom color (80%)
                },
                new float[]{0.1f, 1f}, Shader.TileMode.CLAMP);  // 0.2 for 20% top, 1f for 80% bottom

        textView.getPaint().setShader(textShader);
    }
    private void applyToSaveText(TextView textView) {
        Shader textShader = new LinearGradient(0, 0, 0, textView.getLineHeight(),
                new int[]{
                        Color.WHITE, // Top color (20%)
                        Color.WHITE  // Bottom color (80%)
                },
                new float[]{0.1f, 1f}, Shader.TileMode.CLAMP);  // 0.2 for 20% top, 1f for 80% bottom

        textView.getPaint().setShader(textShader);
    }

    // Method to set background and save the selected item name
    private void setBackgroundResource( ) {
        TextView mp3 = findViewById(R.id.mp3);
        TextView me = findViewById(R.id.me);
        TextView eq = findViewById(R.id.eq);
        TextView mi = findViewById(R.id.mi);
        TextView sp = findViewById(R.id.sp);
        TextView va = findViewById(R.id.va);
        TextView voidchan = findViewById(R.id.voidchan);
        TextView au = findViewById(R.id.audio);
        switch (itemName) {
            case "mp3cutter":
                binding.mixer.setBackgroundResource(R.drawable.itemhome);
                binding.equalizer.setBackgroundResource(R.drawable.itemhome);
                binding.merge.setBackgroundResource(R.drawable.itemhome);
                binding.speed.setBackgroundResource(R.drawable.itemhome);
                binding.volume.setBackgroundResource(R.drawable.itemhome);
                binding.voichanger.setBackgroundResource(R.drawable.itemhome);
                binding.audiototex.setBackgroundResource(R.drawable.itemhome);
                binding.mp3cutter.setBackgroundResource(R.drawable.backinter);

                applyGradientToSaveText(me);
                applyGradientToSaveText(eq);
                applyGradientToSaveText(mi);
                applyGradientToSaveText(sp);
                applyGradientToSaveText(va);
                applyGradientToSaveText(voidchan);
                applyGradientToSaveText(au);
                applyToSaveText(mp3);
                me.invalidate();
                eq.invalidate();
                mp3.invalidate();
                sp.invalidate();
                va.invalidate();
                voidchan.invalidate();
                au.invalidate();
                mi.invalidate();
                binding.imagemp3.setImageResource(R.drawable.cutter);
                binding.imgme.setImageResource(R.drawable.merge);
                binding.imge.setImageResource(R.drawable.equalizer);
                binding.imgmi.setImageResource(R.drawable.mixer);
                binding.imgspeed.setImageResource(R.drawable.speed);
                binding.imgvalume.setImageResource(R.drawable.valume);
                binding.imgvoichanger.setImageResource(R.drawable.voidchanger);
                binding.imgaudiototex.setImageResource(R.drawable.audiototex);

                break;
            case "mixer":
                binding.mp3cutter.setBackgroundResource(R.drawable.itemhome);
                binding.equalizer.setBackgroundResource(R.drawable.itemhome);
                binding.merge.setBackgroundResource(R.drawable.itemhome);
                binding.speed.setBackgroundResource(R.drawable.itemhome);
                binding.volume.setBackgroundResource(R.drawable.itemhome);
                binding.voichanger.setBackgroundResource(R.drawable.itemhome);
                binding.audiototex.setBackgroundResource(R.drawable.itemhome);
                binding.mixer.setBackgroundResource(R.drawable.backinter);


                binding.imagemp3.setImageResource(R.drawable.cutterxanh);
                binding.imgme.setImageResource(R.drawable.merge);
                binding.imge.setImageResource(R.drawable.equalizer);
                binding.imgmi.setImageResource(R.drawable.miwith);
                binding.imgspeed.setImageResource(R.drawable.speed);
                binding.imgvalume.setImageResource(R.drawable.valume);
                binding.imgvoichanger.setImageResource(R.drawable.voidchanger);
                binding.imgaudiototex.setImageResource(R.drawable.audiototex);

                break;
            case "equalizer":
                binding.mp3cutter.setBackgroundResource(R.drawable.itemhome);
                binding.equalizer.setBackgroundResource(R.drawable.backinter);
                binding.merge.setBackgroundResource(R.drawable.itemhome);
                binding.speed.setBackgroundResource(R.drawable.itemhome);
                binding.volume.setBackgroundResource(R.drawable.itemhome);
                binding.voichanger.setBackgroundResource(R.drawable.itemhome);
                binding.audiototex.setBackgroundResource(R.drawable.itemhome);
                binding.mixer.setBackgroundResource(R.drawable.itemhome);

                applyGradientToSaveText(me);
                applyGradientToSaveText(mp3);
                applyGradientToSaveText(sp);
                applyGradientToSaveText(va);
                applyGradientToSaveText(voidchan);
                applyGradientToSaveText(au);
                applyGradientToSaveText(mi);
                applyToSaveText(binding.eq);
                me.invalidate();
                eq.invalidate();
                mp3.invalidate();
                sp.invalidate();
                va.invalidate();
                voidchan.invalidate();
                au.invalidate();
                mi.invalidate();
                binding.imagemp3.setImageResource(R.drawable.cutterxanh);
                binding.imgme.setImageResource(R.drawable.merge);
                binding.imge.setImageResource(R.drawable.ewith);
                binding.imgmi.setImageResource(R.drawable.mixer);
                binding.imgspeed.setImageResource(R.drawable.speed);
                binding.imgvalume.setImageResource(R.drawable.valume);
                binding.imgvoichanger.setImageResource(R.drawable.voidchanger);
                binding.imgaudiototex.setImageResource(R.drawable.audiototex);

                break;
            case "merge":
                binding.mp3cutter.setBackgroundResource(R.drawable.itemhome);
                binding.equalizer.setBackgroundResource(R.drawable.itemhome);
                binding.merge.setBackgroundResource(R.drawable.backinter);
                binding.speed.setBackgroundResource(R.drawable.itemhome);
                binding.volume.setBackgroundResource(R.drawable.itemhome);
                binding.voichanger.setBackgroundResource(R.drawable.itemhome);
                binding.audiototex.setBackgroundResource(R.drawable.itemhome);
                binding.mixer.setBackgroundResource(R.drawable.itemhome);

                applyGradientToSaveText(eq);
                applyGradientToSaveText(mp3);
                applyGradientToSaveText(sp);
                applyGradientToSaveText(va);
                applyGradientToSaveText(voidchan);
                applyGradientToSaveText(au);
                applyGradientToSaveText(mi);
                applyToSaveText(binding.me);
                me.invalidate();
                eq.invalidate();
                mp3.invalidate();
                sp.invalidate();
                va.invalidate();
                voidchan.invalidate();
                au.invalidate();
                mi.invalidate();
                binding.imagemp3.setImageResource(R.drawable.cutterxanh);
                binding.imgme.setImageResource(R.drawable.mewith);
                binding.imge.setImageResource(R.drawable.equalizer);
                binding.imgmi.setImageResource(R.drawable.mixer);
                binding.imgspeed.setImageResource(R.drawable.speed);
                binding.imgvalume.setImageResource(R.drawable.valume);
                binding.imgvoichanger.setImageResource(R.drawable.voidchanger);
                binding.imgaudiototex.setImageResource(R.drawable.audiototex);
                break;
            case "speed":
                binding.mixer.setBackgroundResource(R.drawable.itemhome);
                binding.equalizer.setBackgroundResource(R.drawable.itemhome);
                binding.merge.setBackgroundResource(R.drawable.itemhome);
                binding.speed.setBackgroundResource(R.drawable.backinter);
                binding.volume.setBackgroundResource(R.drawable.itemhome);
                binding.voichanger.setBackgroundResource(R.drawable.itemhome);
                binding.audiototex.setBackgroundResource(R.drawable.itemhome);
                binding.mp3cutter.setBackgroundResource(R.drawable.itemhome);

                applyGradientToSaveText(me);
                applyGradientToSaveText(eq);
                applyGradientToSaveText(mi);
                applyGradientToSaveText(mp3);
                applyGradientToSaveText(va);
                applyGradientToSaveText(voidchan);
                applyGradientToSaveText(au);
                applyToSaveText(binding.sp);
                me.invalidate();
                eq.invalidate();
                mp3.invalidate();
                sp.invalidate();
                va.invalidate();
                voidchan.invalidate();
                au.invalidate();
                mi.invalidate();
                binding.imagemp3.setImageResource(R.drawable.cutterxanh);
                binding.imgme.setImageResource(R.drawable.merge);
                binding.imge.setImageResource(R.drawable.equalizer);
                binding.imgmi.setImageResource(R.drawable.mixer);
                binding.imgspeed.setImageResource(R.drawable.speedwith);
                binding.imgvalume.setImageResource(R.drawable.valume);
                binding.imgvoichanger.setImageResource(R.drawable.voidchanger);
                binding.imgaudiototex.setImageResource(R.drawable.audiototex);

                break;
            case "volume":
                binding.mixer.setBackgroundResource(R.drawable.itemhome);
                binding.equalizer.setBackgroundResource(R.drawable.itemhome);
                binding.merge.setBackgroundResource(R.drawable.itemhome);
                binding.speed.setBackgroundResource(R.drawable.itemhome);
                binding.volume.setBackgroundResource(R.drawable.backinter);
                binding.voichanger.setBackgroundResource(R.drawable.itemhome);
                binding.audiototex.setBackgroundResource(R.drawable.itemhome);
                binding.mp3cutter.setBackgroundResource(R.drawable.itemhome);

                applyGradientToSaveText(me);
                applyGradientToSaveText(eq);
                applyGradientToSaveText(mi);
                applyGradientToSaveText(sp);
                applyGradientToSaveText(mp3);
                applyGradientToSaveText(voidchan);
                applyGradientToSaveText(au);
                applyToSaveText(binding.va);
                me.invalidate();
                eq.invalidate();
                mp3.invalidate();
                sp.invalidate();
                va.invalidate();
                voidchan.invalidate();
                au.invalidate();
                mi.invalidate();
                binding.imagemp3.setImageResource(R.drawable.cutterxanh);
                binding.imgme.setImageResource(R.drawable.merge);
                binding.imge.setImageResource(R.drawable.equalizer);
                binding.imgmi.setImageResource(R.drawable.mixer);
                binding.imgspeed.setImageResource(R.drawable.speed);
                binding.imgvalume.setImageResource(R.drawable.iconvalume);
                binding.imgvoichanger.setImageResource(R.drawable.voidchanger);
                binding.imgaudiototex.setImageResource(R.drawable.audiototex);

                break;
            case "voichanger":
                binding.mixer.setBackgroundResource(R.drawable.itemhome);
                binding.equalizer.setBackgroundResource(R.drawable.itemhome);
                binding.merge.setBackgroundResource(R.drawable.itemhome);
                binding.speed.setBackgroundResource(R.drawable.itemhome);
                binding.volume.setBackgroundResource(R.drawable.itemhome);
                binding.voichanger.setBackgroundResource(R.drawable.backinter);
                binding.audiototex.setBackgroundResource(R.drawable.itemhome);
                binding.mp3cutter.setBackgroundResource(R.drawable.itemhome);

                applyGradientToSaveText(me);
                applyGradientToSaveText(eq);
                applyGradientToSaveText(mi);
                applyGradientToSaveText(sp);
                applyGradientToSaveText(va);
                applyGradientToSaveText(mp3);
                applyGradientToSaveText(au);
                applyToSaveText(voidchan);
                me.invalidate();
                eq.invalidate();
                mp3.invalidate();
                sp.invalidate();
                va.invalidate();
                voidchan.invalidate();
                au.invalidate();
                mi.invalidate();
                binding.imagemp3.setImageResource(R.drawable.cutterxanh);
                binding.imgme.setImageResource(R.drawable.merge);
                binding.imge.setImageResource(R.drawable.equalizer);
                binding.imgmi.setImageResource(R.drawable.mixer);
                binding.imgspeed.setImageResource(R.drawable.speed);
                binding.imgvalume.setImageResource(R.drawable.valume);
                binding.imgvoichanger.setImageResource(R.drawable.voidwith);
                binding.imgaudiototex.setImageResource(R.drawable.audiototex);

                break;
            case "audiototex":
                binding.mp3cutter.setBackgroundResource(R.drawable.itemhome);
                binding.equalizer.setBackgroundResource(R.drawable.itemhome);
                binding.merge.setBackgroundResource(R.drawable.itemhome);
                binding.speed.setBackgroundResource(R.drawable.itemhome);
                binding.volume.setBackgroundResource(R.drawable.itemhome);
                binding.voichanger.setBackgroundResource(R.drawable.itemhome);
                binding.audiototex.setBackgroundResource(R.drawable.backinter);
                binding.mixer.setBackgroundResource(R.drawable.itemhome);

                applyGradientToSaveText(me);
                applyGradientToSaveText(mp3);
                applyGradientToSaveText(sp);
                applyGradientToSaveText(va);
                applyGradientToSaveText(voidchan);
                applyGradientToSaveText(au);
                applyGradientToSaveText(mi);
                applyToSaveText(au);
                me.invalidate();
                eq.invalidate();
                mp3.invalidate();
                sp.invalidate();
                va.invalidate();
                voidchan.invalidate();
                au.invalidate();
                mi.invalidate();
                binding.imagemp3.setImageResource(R.drawable.cutterxanh);
                binding.imgme.setImageResource(R.drawable.merge);
                binding.imge.setImageResource(R.drawable.equalizer);
                binding.imgmi.setImageResource(R.drawable.mixer);
                binding.imgspeed.setImageResource(R.drawable.speed);
                binding.imgvalume.setImageResource(R.drawable.valume);
                binding.imgvoichanger.setImageResource(R.drawable.voidchanger);
                binding.imgaudiototex.setImageResource(R.drawable.audiowith);
                break;
            default:

                break;
        }
    }
    private void saveValueToPreferences(String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("Interect", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedInterect", value);
        editor.apply();
    }


}
