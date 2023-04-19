package com.zhulin.guideshop.core.markdown.basic

import android.content.Context
import androidx.compose.ui.text.TextRange
import com.zhulin.guideshop.R
import com.zhulin.guideshop.core.presentation.util.Language
import com.zhulin.guideshop.core.utils.StringResourceUtils

class HeaderFormatter(private val context: Context) : Formatter {

    override fun format(inputText: String, inputSelection: TextRange): Pair<String, TextRange> {
        val index =
            (inputText.substring(0, inputSelection.start).lastIndexOf("\n") + 1).coerceAtLeast(0)
        val content = inputText.replaceRange(
            index, index, "${
                StringResourceUtils.getStringResource(
                    R.string.header_markdown_stars,
                    Language.EN,
                    context
                )
            } "
        )
        val selection = TextRange(
            (inputSelection.start + 2).coerceAtLeast(0).coerceAtMost(content.length),
            (inputSelection.start + 2).coerceAtLeast(0).coerceAtMost(content.length)
        )


        return Pair(content, selection)
    }
}