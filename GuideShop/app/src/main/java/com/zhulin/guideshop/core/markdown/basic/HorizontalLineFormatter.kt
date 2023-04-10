package com.zhulin.guideshop.core.markdown.basic

import android.content.Context
import androidx.compose.ui.text.TextRange
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.util.Language
import com.zhulin.guideshop.core.utils.StringResourceUtils

class HorizontalLineFormatter(private val context: Context) : Formatter {

    override fun format(inputText: String, inputSelection: TextRange): Pair<String, TextRange> {
        val content: String
        val selection: TextRange
        if (inputSelection.start != inputSelection.end) {
            content = inputText
                .replaceRange(
                    inputSelection.end,
                    inputSelection.end,
                    StringResourceUtils.getStringResource(
                        R.string.horizontal_line_markdown_stars,
                        Language.EN,
                        context
                    )
                )
                .replaceRange(
                    inputSelection.start,
                    inputSelection.start,
                    StringResourceUtils.getStringResource(
                        R.string.horizontal_line_markdown_stars,
                        Language.EN,
                        context
                    )
                )

            selection = TextRange(
                (inputSelection.start + 7).coerceAtLeast(0),
                (inputSelection.end + 7).coerceAtMost(content.length)
            )

        } else {
            val hint = StringResourceUtils.getStringResource(
                R.string.horizontal_line_markdown_stars,
                Language.EN,
                context
            )
            content = inputText
                .replaceRange(
                    inputSelection.start,
                    inputSelection.start,
                    hint
                )
            selection = TextRange(
                (inputSelection.start + 2).coerceAtLeast(0)
                    .coerceAtMost(content.length),
                (inputSelection.start + hint.length - 2).coerceAtLeast(0)
                    .coerceAtMost(content.length)
            )
        }

        return Pair(content, selection)
    }
}