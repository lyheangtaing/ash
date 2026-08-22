package ash.asset.domain.usecase

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetReminder
import ash.asset.domain.repository.AssetRepository

class AddReminderUseCase(private val repository: AssetRepository) {
    operator fun invoke(assetId: String, reminder: AssetReminder): Asset? =
        repository.addReminder(assetId, reminder)
}
