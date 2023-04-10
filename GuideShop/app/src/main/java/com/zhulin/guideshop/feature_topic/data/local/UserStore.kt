package com.zhulin.guideshop.feature_topic.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.auth0.android.jwt.JWT
import com.zhulin.guideshop.core.presentation.util.Language
import kotlinx.coroutines.flow.map

class UserStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userStore")
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        private val USER_ID = intPreferencesKey("user_id")
        private val USER_LOGIN = stringPreferencesKey("user_login")
        private val USER_NICKNAME = stringPreferencesKey("user_nickname")
        private val USER_AVATAR = stringPreferencesKey("user_avatar")
        private val USER_LOGGED = booleanPreferencesKey("user_logged")
        private val LANGUAGE = stringPreferencesKey("language")
    }

    val getToken = context.dataStore.data.map {
        it[USER_TOKEN_KEY] ?: ""
    }

    val getId = context.dataStore.data.map {
        it[USER_ID] ?: -1
    }

    val getLogin = context.dataStore.data.map {
        it[USER_LOGIN] ?: ""
    }

    val getNickname = context.dataStore.data.map {
        it[USER_NICKNAME] ?: ""
    }

    val getAvatar = context.dataStore.data.map {
        it[USER_AVATAR] ?: ""
    }

    val getLogged = context.dataStore.data.map {
        it[USER_LOGGED] ?: false
    }

    val getLanguage = context.dataStore.data.map {
        it[LANGUAGE] ?: Language.RU.language
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit {
            it[LANGUAGE] = language
        }
    }

    suspend fun updateToken(token: String) {
        val jwt = JWT(token)
        context.dataStore.edit {
            it[USER_TOKEN_KEY] = token
            it[USER_ID] = jwt.getClaim("id").asInt() ?: -1
            it[USER_LOGIN] = jwt.getClaim("login").asString() ?: ""
            it[USER_NICKNAME] = jwt.getClaim("nickname").asString() ?: ""
            it[USER_AVATAR] = jwt.getClaim("avatar").asString() ?: ""
            it[USER_LOGGED] = (jwt.getClaim("login").asString() ?: "") != ""
        }
    }

    suspend fun resetAll() {
        context.dataStore.edit {
            it[USER_TOKEN_KEY] = ""
            it[USER_ID] = -1
            it[USER_LOGIN] = ""
            it[USER_NICKNAME] = ""
            it[USER_AVATAR] = ""
            it[USER_LOGGED] = false
        }
    }
}