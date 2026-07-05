package ash.core.database

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

lateinit var appContext: Context

actual fun createSettings(): Settings {
    return SharedPreferencesSettings(appContext.getSharedPreferences("ash", Context.MODE_PRIVATE))
}
