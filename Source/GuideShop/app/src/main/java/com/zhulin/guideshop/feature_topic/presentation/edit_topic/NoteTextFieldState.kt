package com.zhulin.guideshop.feature_topic.presentation.edit_topic

import androidx.compose.ui.text.TextRange

data class NoteTextFieldState(
    val text: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true,
    val selection: TextRange = TextRange(0, 0)
)
