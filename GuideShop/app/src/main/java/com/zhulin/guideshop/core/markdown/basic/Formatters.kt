package com.zhulin.guideshop.core.markdown.basic

data class Formatters(
    val headerFormatter: HeaderFormatter,
    val boldFormatter: BoldFormatter,
    val italicFormatter: ItalicFormatter,
    val strikethroughFormatter: StrikethroughFormatter,
    val roundListFormatter: RoundListFormatter,
    val numericListFormatter: NumericListFormatter,
    val newLineFormatter: NewLineFormatter,
    val selectedFormatter: SelectedFormatter,
    val horizontalLineFormatter: HorizontalLineFormatter
)