package com.yoke.gainful.datastore

import androidx.datastore.core.DataStore
import com.yoke.gainful.model.AppSettings
import com.yoke.gainful.model.GainLossColorScheme
import com.yoke.gainful.proto.AppSettingsProto
import com.yoke.gainful.proto.GainfulDataProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataSource(
    private val dataStore: DataStore<GainfulDataProto>,
) {
    val appSettings: Flow<AppSettings> =
        dataStore.data.map { data ->
            val config = data.app_settings ?: AppSettingsProto()
            AppSettings(
                refreshMinutes = config.refresh_minutes.ifZero { AppSettings().refreshMinutes },
                openHour = config.open_hour.ifZero { AppSettings().openHour },
                openMinute = config.open_minute.ifZero { AppSettings().openMinute },
                closeHour = config.close_hour.ifZero { AppSettings().closeHour },
                closeMinute = config.close_minute.ifZero { AppSettings().closeMinute },
                gainLossColorScheme =
                    GainLossColorScheme.entries.getOrElse(config.gain_loss_color_scheme) {
                        GainLossColorScheme.RED_UP
                    },
            )
        }

    suspend fun setRefreshMinutes(minutes: Int) {
        dataStore.updateData { data ->
            val config = data.app_settings ?: AppSettingsProto()
            data.copy(app_settings = config.copy(refresh_minutes = minutes))
        }
    }

    suspend fun setOpenTime(hour: Int, minute: Int) {
        dataStore.updateData { data ->
            val config = data.app_settings ?: AppSettingsProto()
            data.copy(app_settings = config.copy(open_hour = hour, open_minute = minute))
        }
    }

    suspend fun setCloseTime(hour: Int, minute: Int) {
        dataStore.updateData { data ->
            val config = data.app_settings ?: AppSettingsProto()
            data.copy(app_settings = config.copy(close_hour = hour, close_minute = minute))
        }
    }

    suspend fun setGainLossColorScheme(scheme: GainLossColorScheme) {
        dataStore.updateData { data ->
            val config = data.app_settings ?: AppSettingsProto()
            data.copy(app_settings = config.copy(gain_loss_color_scheme = scheme.ordinal))
        }
    }

    private fun Int.ifZero(default: () -> Int): Int = if (this == 0) default() else this
}
