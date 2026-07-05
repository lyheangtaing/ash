package com.lyheang.ash

import ash.app.AshApp
import ash.core.database.createSettings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import com.russhwolf.settings.Settings
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val settings = if (LocalInspectionMode.current) {
        remember { Settings() }
    } else {
        remember { createSettings() }
    }
    AshApp(settings)
}
