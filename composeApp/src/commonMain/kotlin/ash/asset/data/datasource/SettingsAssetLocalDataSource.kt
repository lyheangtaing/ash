package ash.asset.data.datasource

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class SettingsAssetLocalDataSource(
    private val settings: Settings,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
) : AssetLocalDataSource {
    private val assetsKey = "ash.assets.v1"
    private val assetsSerializer = ListSerializer(AssetEntity.serializer())

    override fun getAssets(): List<AssetEntity> {
        val storedJson = settings.get<String>(assetsKey)
        if (storedJson.isNullOrBlank()) {
            saveAssets(emptyList())
            return emptyList()
        }

        return try {
            json.decodeFromString(assetsSerializer, storedJson)
        } catch (_: SerializationException) {
            settings["$assetsKey.recovery"] = storedJson
            emptyList()
        } catch (_: IllegalArgumentException) {
            settings["$assetsKey.recovery"] = storedJson
            emptyList()
        }
    }

    override fun saveAssets(assets: List<AssetEntity>) {
        settings[assetsKey] = json.encodeToString(assetsSerializer, assets)
    }
}
