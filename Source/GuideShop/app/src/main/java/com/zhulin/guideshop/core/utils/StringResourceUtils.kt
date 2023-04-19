package com.zhulin.guideshop.core.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.zhulin.guideshop.core.presentation.util.Language
import java.util.Locale

object StringResourceUtils {
    fun getStringResource(id: Int, language: Language, context: Context): String {
        val locale = getLocale(language.language)
        val resource = getLocalizedResources(context, locale)

        return resource?.getString(id)
            ?: throw Resources.NotFoundException("The element with current id does not exit.")
    }

    private fun getLocalizedResources(context: Context, locale: Locale): Resources? {
        var configuration: Configuration = context.resources.configuration
        configuration = Configuration(configuration)
        configuration.setLocale(locale)
        val localizedContext = context.createConfigurationContext(configuration)
        return localizedContext.resources
    }

    private fun getLocale(language: String): Locale {
        return Locale(language)
    }
}