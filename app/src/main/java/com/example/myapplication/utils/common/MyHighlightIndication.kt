package com.example.myapplication.utils.common

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class MyHighlightIndication : Indication {
    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource):
            IndicationInstance {
        val isFocusedState = interactionSource.collectIsFocusedAsState()
        return remember(interactionSource) {
            MyHighlightIndicationInstance(isEnabledState = isFocusedState)
        }
    }
}
