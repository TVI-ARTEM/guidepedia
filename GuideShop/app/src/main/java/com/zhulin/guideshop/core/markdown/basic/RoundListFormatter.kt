package com.zhulin.guideshop.core.markdown.basic

import android.content.Context
import androidx.compose.ui.text.TextRange
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.util.Language
import com.zhulin.guideshop.core.utils.StringResourceUtils

class RoundListFormatter(private val context: Context) : Formatter {

    override fun format(inputText: String, inputSelection: TextRange): Pair<String, TextRange> {
        val content: String
        val selection: TextRange
        if (inputSelection.start != inputSelection.end) {
            val rows =
                "\n" + inputText.substring(inputSelection.start, inputSelection.end).split("\n")
                    .map {
                        "${
                            StringResourceUtils.getStringResource(
                                R.string.rounded_list_markdown_symbol,
                                Language.EN,
                                context
                            )
                        } $it\n"
                    }.reduce { acc, s -> acc + s } + "\n"

            content = inputText.replaceRange(inputSelection.start, inputSelection.end, rows)

            selection = TextRange(
                (inputSelection.start).coerceAtLeast(0),
                (inputSelection.start + rows.length).coerceAtMost(content.length)
            )

        } else {
            val hint = "\n${
                StringResourceUtils.getStringResource(
                    R.string.rounded_list_markdown_hint,
                    Language.EN,
                    context
                )
            }"

            content = inputText
                .replaceRange(
                    inputSelection.start,
                    inputSelection.start,
                    hint
                )
            selection = TextRange(
                (inputSelection.start + 3).coerceAtLeast(0)
                    .coerceAtMost(content.length),
                (inputSelection.start + hint.length).coerceAtLeast(0)
                    .coerceAtMost(content.length)
            )
        }

        return Pair(content, selection)
    }
}