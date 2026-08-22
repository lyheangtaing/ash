package ash.asset.domain.usecase

import ash.asset.domain.model.Asset
import ash.asset.domain.repository.AssetRepository

class SetReminderCompletedUseCase(private val repository: AssetRepository) {
    operator fun invoke(assetId: String, reminderId: String, completed: Boolean, nextReminderId: String): Asset? =
        repository.setReminderCompleted(assetId, reminderId, completed, nextReminderId)
}
