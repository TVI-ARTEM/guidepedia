package com.zhulin.guideshop.core.presentation.util

enum class Language(val language: String) {
    RU("ru"),
    EN("en");

    companion object {
        infix fun from(language: String): Language =
            Language.values().firstOrNull { it.language == language } ?: RU
    }
}