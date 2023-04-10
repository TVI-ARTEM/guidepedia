package com.zhulin.guideshop.core.markdown.complex

import androidx.compose.ui.text.TextRange

interface ComplexFormatter {
    fun format(
        inputText: String,
        inputSelection: TextRange,
        params: List<String>
    ): Pair<String, TextRange>
}