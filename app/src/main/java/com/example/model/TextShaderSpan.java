package com.example.model;

import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

public class TextShaderSpan extends CharacterStyle {
    private final Shader shader;

    public TextShaderSpan(Shader shader) {
        this.shader = shader;
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        textPaint.setShader(shader);
    }
}
