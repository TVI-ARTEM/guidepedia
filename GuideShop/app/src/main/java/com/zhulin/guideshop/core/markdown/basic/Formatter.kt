package com.zhulin.guideshop.core.markdown.basic

import androidx.compose.ui.text.TextRange

interface Formatter {
    fun format(inputText: String, inputSelection: TextRange): Pair<String, TextRange>
}