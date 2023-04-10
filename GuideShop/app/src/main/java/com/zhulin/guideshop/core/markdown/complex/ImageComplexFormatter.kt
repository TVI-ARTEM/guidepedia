package com.zhulin.guideshop.core.markdown.complex

import androidx.compose.ui.text.TextRange

class ImageComplexFormatter : ComplexFormatter {

    override fun format(
        inputText: String,
        inputSelection: TextRange,
        params: List<String>
    ): Pair<String, TextRange> {

        if (params.count() != 2) {
            throw IllegalArgumentException("Incorrect amount of params: must be 2")
        }

        val link = "[![${params[0]}](${params[1]})](${params[1]})"
        val content = inputText.replaceRange(
            inputSelection.start, inputSelection.start, link
        )

        val selection = TextRange(
            (inputSelection.start).coerceAtLeast(0).coerceAtMost(content.length),
            (inputSelection.start + link.length).coerceAtLeast(0).coerceAtMost(content.length)
        )

        return Pair(content, selection)
    }
}