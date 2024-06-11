package com.findmyphone.clapping.find.data.local

import android.content.Context
import android.content.SharedPreferences
import com.findmyphone.clapping.find.BuildConfig
import com.findmyphone.clapping.find.application.MainApplication

enum class PrefMigrate(val newVersion: Int) {
    M_0_1(1);

    fun migrate() {
        when (this) {
            M_0_1 -> {
                if (Settings.VERSION_P.get(0) == 0) {
                    Settings.SOUND_POSITION.put("")
                    Settings.VERSION_P.put(1)
                }
            }
        }
    }
}

enum class Settings {

    PASS_TUTORIAL,
    VERSION_P,
    APP_LANGUAGE,
    RATE,
    SOUND_POSITION,
    TIME_SOUND, FLASH_LIGHT,
    VIBRATION, VOLUME,
    PASS_TUTORIAL_HOME,
    SENSITIVITY,
    START_UNPLUGGING_ALARM,
    CUSTOM_SOUND;

    inline fun <reified T> get(defaultValue: T): T {
        return sharedPref.get(this.name, defaultValue)
    }

    inline fun <reified T> put(value: T) {
        return sharedPref.put(name, value)
    }

    companion object {

        const val VERSION = 1

        fun soundUri(context: Context) =
            if (SOUND_POSITION.get(Sound.default.uri(context)).trim().isEmpty()) Sound.default.uri(context)
            else SOUND_POSITION.get(Sound.default.uri(context))

        val sharedPref: SharedPreferences =
            MainApplication.CONTEXT.getSharedPreferences(
                BuildConfig.APPLICATION_ID,
                Context.MODE_PRIVATE
            )

        fun clear() {
            sharedPref.edit().clear().apply()
        }

        fun migrate() {
            PrefMigrate.values().filter { it.newVersion <= VERSION }.sortedBy { it.newVersion }
                .forEach { it.migrate() }
        }
    }
}

inline fun <reified T> SharedPreferences.get(key: String, defaultValue: T): T {
    when (T::class) {
        Boolean::class -> return this.getBoolean(key, defaultValue as Boolean) as T
        Float::class -> return this.getFloat(key, defaultValue as Float) as T
        Int::class -> return this.getInt(key, defaultValue as Int) as T
        Long::class -> return this.getLong(key, defaultValue as Long) as T
        String::class -> return this.getString(key, defaultValue as String) as T
        else -> {
            if (defaultValue is Set<*>) {
                return this.getStringSet(key, defaultValue as Set<String>) as T
            }
        }
    }

    return defaultValue
}

inline fun <reified T> SharedPreferences.put(key: String, value: T) {
    val editor = this.edit()
    when (T::class) {
        Boolean::class -> editor.putBoolean(key, value as Boolean)
        Float::class -> editor.putFloat(key, value as Float)
        Int::class -> editor.putInt(key, value as Int)
        Long::class -> editor.putLong(key, value as Long)
        String::class -> editor.putString(key, value as String)
        else -> {
            if (value is Set<*>) {
                editor.putStringSet(key, value as Set<String>)
            }
        }
    }

    editor.apply()
}