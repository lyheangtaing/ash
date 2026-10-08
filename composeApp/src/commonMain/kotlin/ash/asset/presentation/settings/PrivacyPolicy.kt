package ash.asset.presentation.settings

internal data class PrivacySection(val title: String, val text: String)

internal object PrivacyPolicy {
    val sections = listOf(
        PrivacySection(
            "Your collection",
            "Ash stores the records you enter, including asset names, values, notes, tags, history, and reminders, in the app's storage on your device."
        ),
        PrivacySection(
            "Photos you choose",
            "Ash uses your device's photo or document picker to access only the images you select. It copies selected images into app storage when possible. On Android, it may retain access to a selected document if copying fails."
        ),
        PrivacySection(
            "No app data collection",
            "Ash does not send your collection or photos to the developer or third parties. It has no accounts, advertising, analytics, or tracking. Reminders are stored and displayed inside the app; they do not schedule device notifications."
        ),
        PrivacySection(
            "Backups",
            "Ash does not provide cloud sync. Android app backups and device transfers are disabled for Ash. On iOS, device backups or transfers may include app data according to your system settings. Your original photos remain subject to your photo-library settings."
        ),
        PrivacySection(
            "Deleting data",
            "You can delete collection records in Ash. Copied photos may remain in app storage after a record is deleted. To remove all app-managed records and copied photos, clear Ash's app storage on Android or delete the app on iOS. Offloading an iOS app keeps its data. System backups and original photos may retain copies."
        )
    )
}
