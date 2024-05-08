package com.example.myapplication.utils.common

import androidx.compose.foundation.IndicationInstance
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope

class MyHighlightIndicationInstance(isEnabledState: State<Boolean>) :
    IndicationInstance {
    private val isEnabled by isEnabledState
    override fun ContentDrawScope.drawIndication() {
        drawContent()
        if (isEnabled) {
            drawRect(size = size, color = Color.White, alpha = 0.2f)
        }
    }
}