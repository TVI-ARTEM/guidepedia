package com.zhulin.guideshop.feature_topic.data.repository

import android.app.Application
import com.zhulin.guideshop.core.presentation.util.Language
import com.zhulin.guideshop.core.utils.StringResourceUtils
import com.zhulin.guideshop.feature_topic.domain.repository.StringResourceRepository

class StringResourceRepositoryImp(
    private val appContext: Application
) : StringResourceRepository {
    override fun getResourceString(id: Int, language: Language): String {
        return StringResourceUtils.getStringResource(id, language, appContext)
    }

    override fun getLocalString(key: String): String {
        TODO("Not yet implemented")
    }
}