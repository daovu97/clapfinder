package com.findmyphone.clapping.find.application

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.data.local.Settings
import java.util.Locale

enum class Language(
    var id: Int,
    @StringRes val nameFlag: Int,
    @DrawableRes val flag: Int,
    val localizeCode: String
) {

    ENGLISH(0, R.string.lg_english, R.drawable.flag_kingdom, "en"),
    JAPANESE(7, R.string.lg_japan, R.drawable.japan, "ja"),
    CHINA(5, R.string.lg_china, R.drawable.ic_china, "zh"),
    KOREAN(8, R.string.lg_korean, R.drawable.south_korea, "ko"),
    HINDI(3, R.string.lg_hindi, R.drawable.flag_of_hindi, "hi"),
    SPANISH(1, R.string.lg_spanish, R.drawable.flag_of_bandera, "es"),
    FRENCH(2, R.string.lg_french, R.drawable.flag_of_france, "fr"),
    PORTUGUESE(4, R.string.lg_portuguese, R.drawable.flag_of_portugal, "pt"),
    INDONESIA(6, R.string.lg_indo, R.drawable.indonesia, "in"),
    VIETNAM(9, R.string.lg_vietnam, R.drawable.vietnam, "vi");

    companion object {
        fun getAll(): List<Language> = Language.values().toList()
        fun get(id: Int) = getAll().firstOrNull { it.id == id }

        fun get(code: String) = getAll().firstOrNull { it.localizeCode == code }

        var current: Language =
            get(Settings.APP_LANGUAGE.get(-1)) ?: get(Locale.getDefault().language.toString())
            ?: ENGLISH

        fun setCurrent(id: Int) {
            Settings.APP_LANGUAGE.put(id)
            current = get(id) ?: ENGLISH
        }
    }
}
