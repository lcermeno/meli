package com.qiubo.meli.ui.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.qiubo.meli.ui.theme.YellowML

@Composable
@Suppress("DEPRECATION")
fun ApplyStatusBarStyle() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    val backgroundColor = YellowML

    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor,
            darkIcons = useDarkIcons,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .background(backgroundColor)
    )
}