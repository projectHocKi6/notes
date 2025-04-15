package com.example.mynotes.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.example.mynotes.R


@SuppressLint("ConflictingOnColor")
@Composable
fun NoteAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val lightColors  = lightColorScheme(
        primary = colorResource(id = R.color.bmi_blue),
        primaryContainer = colorResource(id = R.color.black),
        secondaryContainer = colorResource(id = R.color.light_text_bg),
        background = colorResource(id = R.color.white),
        onPrimary = colorResource(id = R.color.black),
        onSecondary = colorResource(id = R.color.bmi_btn),
        tertiaryContainer = colorResource(id = R.color.light_dialog)
    )
    val darkColors  = darkColorScheme(
        primary = colorResource(id = R.color.bmi_dark_green),
        primaryContainer = colorResource(id = R.color.text),
        secondaryContainer = colorResource(id = R.color.black),
        onSecondary = colorResource(id = R.color.bmi_btn_dark),
        onPrimary = colorResource(id = R.color.white),
        background = colorResource(id = R.color.bg_black),
        tertiaryContainer = colorResource(id = R.color.dark_dialog)

    )
    val colors = if (darkTheme) darkColors else lightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}