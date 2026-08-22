package ash.asset.domain.repository

import ash.asset.domain.model.Asset
import ash.asset.domain.model.MaintenanceRecord
import ash.asset.domain.model.AssetReminder

interface AssetRepository {
    fun getAssets(): List<Asset>
    fun getAssetById(id: String): Asset?
    fun upsertAsset(asset: Asset): Asset
    fun deleteAsset(id: String)
    fun addMaintenanceRecord(assetId: String, record: MaintenanceRecord): Asset?
    fun addReminder(assetId: String, reminder: AssetReminder): Asset?
    fun setReminderCompleted(assetId: String, reminderId: String, completed: Boolean, nextReminderId: String): Asset?
}
