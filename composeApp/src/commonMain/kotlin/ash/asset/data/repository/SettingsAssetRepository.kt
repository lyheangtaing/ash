package ash.asset.data.repository

import ash.asset.data.datasource.AssetLocalDataSource
import ash.asset.data.mapper.toDomain
import ash.asset.data.mapper.toEntity
import ash.asset.domain.model.Asset
import ash.asset.domain.model.MaintenanceRecord
import ash.asset.domain.repository.AssetRepository
import ash.core.util.DateProvider

class SettingsAssetRepository(
    private val localDataSource: AssetLocalDataSource
) : AssetRepository {
    override fun getAssets(): List<Asset> {
        return localDataSource.getAssets().map { it.toDomain() }
    }

    override fun getAssetById(id: String): Asset? {
        return getAssets().firstOrNull { it.id == id }
    }

    override fun upsertAsset(asset: Asset): Asset {
        val today = DateProvider.today()
        val updatedAsset = asset.copy(updatedAt = today)
        val assets = getAssets().toMutableList()
        val index = assets.indexOfFirst { it.id == updatedAsset.id }
        if (index >= 0) {
            assets[index] = updatedAsset
        } else {
            assets.add(updatedAsset)
        }
        localDataSource.saveAssets(assets.map { it.toEntity() })
        return updatedAsset
    }

    override fun deleteAsset(id: String) {
        localDataSource.saveAssets(getAssets().filterNot { it.id == id }.map { it.toEntity() })
    }

    override fun addMaintenanceRecord(assetId: String, record: MaintenanceRecord): Asset? {
        val existing = getAssetById(assetId) ?: return null
        val updated = existing.copy(
            maintenanceRecords = (existing.maintenanceRecords + record).sortedByDescending { it.date },
            updatedAt = DateProvider.today()
        )
        return upsertAsset(updated)
    }
}
