package com.apero.rates.remote

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.core.content.edit
import com.apero.rates.BuildConfig
import com.apero.rates.model.UiText
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

/**
 * Created by KO Huyn on 25/09/2023.
 */
abstract class BaseRemoteConfiguration {
    internal lateinit var application: Application

    internal abstract fun getPrefsName(): String

    abstract fun sync(remoteConfig: FirebaseRemoteConfig)

    private fun getPrefs(): SharedPreferences {
        return application.getSharedPreferences(getPrefsName(), Context.MODE_PRIVATE)
    }

    fun init(application: Application) {
        this.application = application
    }

    internal fun FirebaseRemoteConfig.saveToLocal(keyType: RemoteKeys) {
        val hasKeyRemote = kotlin.runCatching {
            this.getString(keyType.remoteKey).isNotEmpty()
        }.getOrDefault(true)
        if (!hasKeyRemote) return
        val remoteConfig = this
        getPrefs().edit {
            val key = keyType.remoteKey
            when (keyType) {
                is RemoteKeys.BooleanKey -> {
                    putBoolean(
                        key,
                        kotlin.runCatching {
                            remoteConfig.getBoolean(key)
                        }.getOrElse { keyType.defaultValue }
                    )
                }

                is RemoteKeys.StringKey -> {
                    putString(
                        key,
                        kotlin.runCatching {
                            remoteConfig.getString(key)
                        }.getOrElse { keyType.defaultValue })
                }

                is RemoteKeys.UiTextKey -> {
                    val valueRaw = kotlin.runCatching {
                        remoteConfig.getString(key).takeIf { it.isNotEmpty() }
                    }.getOrNull()
                    if (valueRaw != null) {
                        putString(key, valueRaw)
                    }
                }

                is RemoteKeys.DoubleKey -> {
                    putFloat(
                        key,
                        kotlin.runCatching {
                            remoteConfig.getDouble(key)
                        }.getOrElse { keyType.defaultValue }.toFloat()
                    )
                }

                is RemoteKeys.LongKey -> {
                    putLong(
                        key,
                        kotlin.runCatching {
                            remoteConfig.getLong(key)
                        }.getOrElse { keyType.defaultValue })
                }

                is RemoteKeys.ListIntegerKey -> {
                    putString(key, kotlin.runCatching {
                        remoteConfig.getString(key)
                    }.getOrElse { keyType.defaultValue.joinToString(",") })
                }

                is RemoteKeys.StringEnumKey<*> -> {
                    putString(
                        key,
                        kotlin.runCatching {
                            remoteConfig.getString(key)
                        }.getOrElse { keyType.defaultValue.remoteValue })
                }

                else -> {}
            }
        }
    }

    internal fun RemoteKeys.StringKey.get(): String {
        return kotlin.runCatching {
            getPrefs().getString(remoteKey, defaultValue).takeUnless { it.isNullOrBlank() }
        }.getOrNull() ?: defaultValue
    }

    internal fun RemoteKeys.UiTextKey.get(): UiText {
        return kotlin.runCatching {
            getPrefs().getString(remoteKey, "").takeUnless { it.isNullOrBlank() }
        }.getOrNull()?.let { UiText.from(it) } ?: defaultValue
    }

    internal inline fun <reified T> RemoteKeys.StringEnumKey<T>.get(): T where T : RemoteEnumString, T : Enum<T> {
        return kotlin.runCatching {
            val stringValue = getPrefs().getString(remoteKey, defaultValue.remoteValue)
                .takeUnless { it.isNullOrBlank() } ?: defaultValue.remoteValue
            enumValues<T>().find { it.remoteValue == stringValue } ?: defaultValue
        }.getOrDefault(defaultValue)
    }

    internal fun RemoteKeys.BooleanKey.get(): Boolean {
        return kotlin.runCatching {
            getPrefs().getBoolean(remoteKey, defaultValue)
        }.getOrDefault(defaultValue)
    }

    internal fun RemoteKeys.LongKey.get(): Long {
        return kotlin.runCatching {
            getPrefs().getLong(remoteKey, defaultValue)
        }.getOrDefault(defaultValue)
    }

    internal fun RemoteKeys.ListIntegerKey.get(): List<Int> {
        return kotlin.runCatching {
            getPrefs().getString(remoteKey, defaultValue.joinToString(","))
                ?.split(",")
                ?.mapNotNull { it.toIntOrNull() }
        }.getOrNull() ?: defaultValue
    }

    internal fun RemoteKeys.DoubleKey.get(): Double {
        return kotlin.runCatching {
            getPrefs().getFloat(remoteKey, defaultValue.toFloat())
        }.getOrDefault(defaultValue).toDouble()
    }

    sealed class RemoteKeys(open val remoteKey: String) {
        sealed class BooleanKey : RemoteKeys {
            var defaultValue: Boolean
                private set

            constructor(remoteKey: String, defaultValue: Boolean) : super(remoteKey) {
                this.defaultValue = defaultValue
            }

            constructor(
                remoteKey: String,
                defaultValue: RemoteParam.BoolValue
            ) : super(remoteKey) {
                this.defaultValue = defaultValue.value
            }
        }

        sealed class StringKey : RemoteKeys {
            var defaultValue: String
                private set

            constructor(remoteKey: String, defaultValue: String) : super(remoteKey) {
                this.defaultValue = defaultValue
            }

            constructor(
                remoteKey: String,
                defaultValue: RemoteParam.StringValue
            ) : super(remoteKey) {
                this.defaultValue = defaultValue.value
            }
        }

        sealed class UiTextKey : RemoteKeys {
            var defaultValue: UiText
                private set

            constructor(remoteKey: String, defaultValue: String) : super(remoteKey) {
                this.defaultValue = UiText.from(defaultValue)
            }


            constructor(remoteKey: String,@StringRes defaultValue: Int) : super(remoteKey) {
                this.defaultValue = UiText.from(defaultValue)
            }

            constructor(
                remoteKey: String,
                defaultValue: RemoteParam.StringValue
            ) : super(remoteKey) {
                this.defaultValue = UiText.from(defaultValue.value)
            }
        }

        sealed class StringEnumKey<T : RemoteEnumString>(
            remoteKey: String, defaultValue: T
        ) : RemoteKeys(remoteKey) {
            var defaultValue: T = defaultValue
                private set
        }

        sealed class DoubleKey : RemoteKeys {
            var defaultValue: Double
                private set

            constructor(remoteKey: String, defaultValue: Double) : super(remoteKey) {
                this.defaultValue = defaultValue
            }

            constructor(
                remoteKey: String,
                defaultValue: RemoteParam.DoubleValue
            ) : super(remoteKey) {
                this.defaultValue = defaultValue.value
            }
        }


        sealed class LongKey : RemoteKeys {
            var defaultValue: Long
                private set

            constructor(remoteKey: String, defaultValue: Long) : super(remoteKey) {
                this.defaultValue = defaultValue
            }

            constructor(
                remoteKey: String,
                defaultValue: RemoteParam.LongValue
            ) : super(remoteKey) {
                this.defaultValue = defaultValue.value
            }
        }

        sealed class ListIntegerKey(
            override val remoteKey: String,
            val defaultValue: List<Int>
        ) : RemoteKeys(remoteKey)
    }

    interface RemoteEnumString {
        val remoteValue: String
    }

    sealed class RemoteParam {
        sealed class StringValue(val value: String)
        sealed class BoolValue(val value: Boolean)
        sealed class LongValue(val value: Long)
        sealed class DoubleValue(val value: Double)
    }
}