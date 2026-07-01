package com.fftool.soundboard.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecurePreferences(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var userUsername: String
        get() = prefs.getString(KEY_USER_USERNAME, DEFAULT_USER_USERNAME) ?: DEFAULT_USER_USERNAME
        set(value) = prefs.edit().putString(KEY_USER_USERNAME, value).apply()

    var userPassword: String
        get() = prefs.getString(KEY_USER_PASSWORD, DEFAULT_USER_PASSWORD) ?: DEFAULT_USER_PASSWORD
        set(value) = prefs.edit().putString(KEY_USER_PASSWORD, value).apply()

    var adminUsername: String
        get() = prefs.getString(KEY_ADMIN_USERNAME, DEFAULT_ADMIN_USERNAME) ?: DEFAULT_ADMIN_USERNAME
        set(value) = prefs.edit().putString(KEY_ADMIN_USERNAME, value).apply()

    var adminPassword: String
        get() = prefs.getString(KEY_ADMIN_PASSWORD, DEFAULT_ADMIN_PASSWORD) ?: DEFAULT_ADMIN_PASSWORD
        set(value) = prefs.edit().putString(KEY_ADMIN_PASSWORD, value).apply()

    var pinCode: String
        get() = prefs.getString(KEY_PIN_CODE, DEFAULT_PIN_CODE) ?: DEFAULT_PIN_CODE
        set(value) = prefs.edit().putString(KEY_PIN_CODE, value).apply()

    // Social links
    var tiktokUrl: String
        get() = prefs.getString(KEY_TIKTOK, DEFAULT_TIKTOK) ?: DEFAULT_TIKTOK
        set(value) = prefs.edit().putString(KEY_TIKTOK, value).apply()

    var whatsappNumber: String
        get() = prefs.getString(KEY_WHATSAPP, DEFAULT_WHATSAPP) ?: DEFAULT_WHATSAPP
        set(value) = prefs.edit().putString(KEY_WHATSAPP, value).apply()

    var youtubeUrl: String
        get() = prefs.getString(KEY_YOUTUBE, DEFAULT_YOUTUBE) ?: DEFAULT_YOUTUBE
        set(value) = prefs.edit().putString(KEY_YOUTUBE, value).apply()

    var websiteUrl: String
        get() = prefs.getString(KEY_WEBSITE, DEFAULT_WEBSITE) ?: DEFAULT_WEBSITE
        set(value) = prefs.edit().putString(KEY_WEBSITE, value).apply()

    companion object {
        private const val PREFS_NAME = "ff_tool_secure_prefs"

        private const val KEY_USER_USERNAME = "user_username"
        private const val KEY_USER_PASSWORD = "user_password"
        private const val KEY_ADMIN_USERNAME = "admin_username"
        private const val KEY_ADMIN_PASSWORD = "admin_password"
        private const val KEY_PIN_CODE = "pin_code"
        private const val KEY_TIKTOK = "tiktok_url"
        private const val KEY_WHATSAPP = "whatsapp_number"
        private const val KEY_YOUTUBE = "youtube_url"
        private const val KEY_WEBSITE = "website_url"

        const val DEFAULT_USER_USERNAME = "test"
        const val DEFAULT_USER_PASSWORD = "test123"
        const val DEFAULT_ADMIN_USERNAME = "only"
        const val DEFAULT_ADMIN_PASSWORD = "bypass"
        const val DEFAULT_PIN_CODE = "42201"

        const val DEFAULT_TIKTOK = "https://www.tiktok.com/@fftool"
        const val DEFAULT_WHATSAPP = "1234567890"
        const val DEFAULT_YOUTUBE = "https://youtube.com/@fftool"
        const val DEFAULT_WEBSITE = "https://fftool.app"
    }
}
