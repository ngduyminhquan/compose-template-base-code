package com.project.composeproject.utils

import android.content.Context
import android.os.LocaleList
import androidx.core.content.edit
import java.util.Locale

data class LanguageItem(
    val code: String,
    val name: String
)

object LanguageUtils {

    const val LANGUAGE_PREFS = "LANGUAGE_PREFS"
    const val KEY_LANGUAGE = "KEY_LANGUAGE"


    val systemLocale: Locale get() = Locale.getDefault()
    val systemLanguage: LanguageItem
        get() = LanguageItem(
            systemLocale.language,
            systemLocale.displayName
        )

    val displayLanguages: List<LanguageItem>
        get() = run {
            val systemSupportedLanguage = supportedLanguages.find { it.code == systemLocale.language }
            if (systemSupportedLanguage != null) {
                listOf(systemSupportedLanguage) + supportedLanguages.filter { it.code != systemLocale.language }
            } else {
                supportedLanguages
            }.take(10)
        }


    fun getCurrentLanguage(context: Context): LanguageItem {
        val languagePrefs = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE)
        val languageCode = languagePrefs.getString(KEY_LANGUAGE, null)
        return supportedLanguages.find { it.code == languageCode }
            ?: supportedLanguages.first { it.code == "en" }
    }

    fun setCurrentLanguage(context: Context, languageCode: String) {
        val languagePrefs = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE)
        languagePrefs.edit(commit = true) {
            putString(KEY_LANGUAGE, languageCode)
        }
    }

    fun createLocalizedContext(base: Context): Context {
        val currentLanguageIso = getCurrentLanguage(base).code
        val locale = Locale.forLanguageTag(currentLanguageIso)
        Locale.setDefault(locale)

        val config = base.resources.configuration
        config.setLocale(locale)
        config.setLocales(LocaleList(locale))
        return base.createConfigurationContext(config)
    }


    private val supportedLanguages: List<LanguageItem>
        get() = run {
            listOf(
                "ar", "de", "en", "es", "fil", "fr", "hi", "hr", "it", "ko",
                "ja", "ms", "nl", "pl", "pt", "ru", "sr", "sv", "tr", "vi",
                "zh", "uk", "th"
            ).map { code ->
                val locale = Locale.forLanguageTag(code)
                val displayName = locale.getDisplayName(locale)
                LanguageItem(code, displayName)
            }
        }
}
