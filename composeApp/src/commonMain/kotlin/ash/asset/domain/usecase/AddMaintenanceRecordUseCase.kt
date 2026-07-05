package ash.asset.domain.usecase

import ash.asset.domain.model.Asset
import ash.asset.domain.model.MaintenanceRecord
import ash.asset.domain.repository.AssetRepository

class AddMaintenanceRecordUseCase(private val repository: AssetRepository) {
    operator fun invoke(assetId: String, record: MaintenanceRecord): Asset? {
        return repository.addMaintenanceRecord(assetId, record)
    }
}
