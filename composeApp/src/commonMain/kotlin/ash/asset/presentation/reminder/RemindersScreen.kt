package ash.asset.presentation.reminder

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetReminder
import ash.core.designsystem.AshPanel
import ash.core.designsystem.AshSpacing
import ash.core.util.DateProvider
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class ReminderItem(val asset: Asset, val reminder: AssetReminder)

@Composable
fun RemindersScreen(
    assets: List<Asset>,
    onAddReminder: () -> Unit,
    onReminderCompleted: (String, String, Boolean) -> Unit,
    onAssetSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val reminders = assets.flatMap { asset -> asset.reminders.map { ReminderItem(asset, it) } }
    val upcoming = reminders.filterNot { it.reminder.isCompleted }.sortedBy { it.reminder.dueDate }
    val completed = reminders.filter { it.reminder.isCompleted }.sortedByDescending { it.reminder.dueDate }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(AshSpacing.screen, AshSpacing.md, AshSpacing.screen, AshSpacing.bottomNavPadding),
        verticalArrangement = Arrangement.spacedBy(AshSpacing.md)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Reminders", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text("Only the actions you choose", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(onClick = onAddReminder) {
                    Icon(Icons.Default.Add, null)
                    Text(" Add")
                }
            }
        }
        if (reminders.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AshSpacing.md)
                ) {
                    Icon(Icons.Default.NotificationsNone, null)
                    Text("Nothing scheduled", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("Ash will never create reminders unless you ask.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = onAddReminder) { Text("Create reminder") }
                }
            }
        }
        if (upcoming.isNotEmpty()) {
            item { Text("Upcoming", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            items(upcoming, key = { it.reminder.id }) { item ->
                ReminderRow(item, onReminderCompleted, onAssetSelected)
            }
        }
        if (completed.isNotEmpty()) {
            item { Text("Completed", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            items(completed, key = { it.reminder.id }) { item ->
                ReminderRow(item, onReminderCompleted, onAssetSelected)
            }
        }
    }
}

@Composable
private fun ReminderRow(
    item: ReminderItem,
    onCompleted: (String, String, Boolean) -> Unit,
    onAssetSelected: (String) -> Unit
) {
    val isDue = !item.reminder.isCompleted && item.reminder.dueDate <= DateProvider.today()
    AshPanel(modifier = Modifier.fillMaxWidth().clickable { onAssetSelected(item.asset.id) }) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
            Checkbox(
                checked = item.reminder.isCompleted,
                onCheckedChange = { onCompleted(item.asset.id, item.reminder.id, it) }
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(item.reminder.action, fontWeight = FontWeight.SemiBold)
                Text(item.asset.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                val schedule = buildString {
                    append(item.reminder.dueDate)
                    if (item.reminder.time.isNotBlank()) append(" at ${item.reminder.time}")
                    if (item.reminder.recurrence.label != "Does not repeat") append(" · ${item.reminder.recurrence.label}")
                }
                Text(
                    schedule,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
