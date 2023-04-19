package com.zhulin.guideshop.core.markdown.basic

import android.content.Context
import androidx.compose.ui.text.TextRange
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.util.Language
import com.zhulin.guideshop.core.utils.StringResourceUtils

class NewLineFormatter(private val context: Context) : Formatter {

    override fun format(inputText: String, inputSelection: TextRange): Pair<String, TextRange> {

        val content = inputText.replaceRange(
            inputSelection.start, inputSelection.start, StringResourceUtils.getStringResource(
                R.string.new_line_markdown_stars,
                Language.EN,
                context
            )
        )

        val selection = TextRange(
            (inputSelection.start + 1).coerceAtLeast(0).coerceAtMost(content.length),
            (inputSelection.end + 1).coerceAtLeast(0).coerceAtMost(content.length)
        )


        return Pair(content, selection)
    }
}