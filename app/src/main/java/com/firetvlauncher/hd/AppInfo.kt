package com.firetvlauncher.hd

import android.graphics.drawable.Drawable

/** Um aplicativo instalado que pode ser aberto a partir da tela inicial. */
data class AppInfo(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: Drawable,
    /** true = tem entrada para TV (LEANBACK_LAUNCHER); false = só launcher de celular. */
    val isTvApp: Boolean
)
