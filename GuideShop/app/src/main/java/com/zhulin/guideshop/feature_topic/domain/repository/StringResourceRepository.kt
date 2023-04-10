package com.zhulin.guideshop.feature_topic.domain.repository

import com.zhulin.guideshop.core.presentation.util.Language

interface StringResourceRepository {
    fun getResourceString(id: Int, language: Language): String
    fun getLocalString(key: String): String
}