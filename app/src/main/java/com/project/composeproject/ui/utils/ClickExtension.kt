package com.project.composeproject.ui.utils

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

const val MIN_CLICK_INTERVAL = 300L

private var lastClickTime = 0L

fun Modifier.onSingleClick(
    interactionSource: MutableInteractionSource?,
    indication: Indication?,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    clickInterval: Long = MIN_CLICK_INTERVAL,
    onClick: () -> Unit,
): Modifier = this.clickable(
    interactionSource = interactionSource,
    indication = indication,
    enabled = enabled,
    onClickLabel = onClickLabel,
    role = role
) {
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastClickTime < clickInterval) {
        return@clickable
    }

    lastClickTime = currentTime
    onClick.invoke()
}