package com.zhulin.guideshop.core.markdown.complex

import androidx.compose.ui.text.TextRange

class CodeComplexFormatter : ComplexFormatter {

    override fun format(
        inputText: String,
        inputSelection: TextRange,
        params: List<String>
    ): Pair<String, TextRange> {
        val content: String
        val selection: TextRange
        if (params.count() != 1) {
            throw IllegalArgumentException("Incorrect amount of params: must be 1")
        }
        val top = "```${params[0]}\n"
        val bottom = "\n```"
        if (inputSelection.start != inputSelection.end) {
            content = inputText
                .replaceRange(
                    inputSelection.end,
                    inputSelection.end,
                    top
                )
                .replaceRange(
                    inputSelection.start,
                    inputSelection.start,
                    bottom
                )

            selection = TextRange(
                (inputSelection.start + top.length).coerceAtLeast(0),
                (inputSelection.end + top.length).coerceAtMost(content.length)
            )

        } else {
            val hint = "YOUR CODE"
            content = inputText
                .replaceRange(
                    inputSelection.start,
                    inputSelection.start,
                    "$top$hint$bottom"
                )
            selection = TextRange(
                (inputSelection.start + top.length).coerceAtLeast(0)
                    .coerceAtMost(content.length),
                (inputSelection.start + top.length + hint.length).coerceAtLeast(0)
                    .coerceAtMost(content.length)
            )
        }

        return Pair(content, selection)
    }
}