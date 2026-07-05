package ash.asset.domain.model

data class AssetDecisionResult(
    val decision: AssetDecision,
    val warrantyNearExpiry: Boolean,
    val maintenanceOverdue: Boolean,
    val reasons: List<String>
)
