package com.zhulin.guideshop.feature_topic.presentation.edit_topic

import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.TextRange
import com.zhulin.guideshop.core.markdown.basic.FormatterEnum
import com.zhulin.guideshop.core.markdown.complex.ComplexFormatterEnum


sealed class EditTopicEvent {
    data class EnteredTitle(val text: String, val selection: TextRange) : EditTopicEvent()
    data class ChangeTitleFocus(val focusState: FocusState) : EditTopicEvent()

    data class EnteredContent(val text: String, val selection: TextRange) : EditTopicEvent()
    data class ChangeContentFocus(val focusState: FocusState) : EditTopicEvent()
    data class ApplyFormatter(val formatter: FormatterEnum) : EditTopicEvent()
    data class ApplyComplexFormatter(
        val formatter: ComplexFormatterEnum,
        val params: List<String>
    ) : EditTopicEvent()

    data class ChangeOpenRender(val value: Boolean) : EditTopicEvent()
    data class ChangeShowMenu(val value: Boolean) : EditTopicEvent()
    data class ChangeShowLinkDialog(val value: Boolean) : EditTopicEvent()
    data class ChangeShowCodeDialog(val value: Boolean) : EditTopicEvent()
    data class ChangeShowImageDialog(val value: Boolean) : EditTopicEvent()
    data class ChangeShowUpdateDialog(val value: Boolean) : EditTopicEvent()

    data class AddCategory(val value: Int) : EditTopicEvent()
    data class RemoveCategory(val value: Int) : EditTopicEvent()

    data class Update(
        val preview: String,
        val description: String,
        val published: Boolean
    ) : EditTopicEvent()

}