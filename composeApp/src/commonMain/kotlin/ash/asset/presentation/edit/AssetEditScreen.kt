package ash.asset.presentation.edit

import ash.asset.domain.model.AssetCategory
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.OwnershipStatus
import ash.asset.presentation.AssetFormState
import ash.asset.presentation.components.AshDropdown
import ash.asset.presentation.image.AssetImagePreview
import ash.asset.presentation.image.rememberAssetImagePicker
import ash.core.designsystem.AshPanel
import ash.core.designsystem.AshRadius
import ash.core.designsystem.AshSpacing
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AssetEditScreen(
    form: AssetFormState,
    isEditing: Boolean,
    onFormChanged: (AssetFormState) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var detailsExpanded by remember(isEditing) { mutableStateOf(isEditing) }
    val imagePicker = rememberAssetImagePicker { selected ->
        onFormChanged(form.copy(imageUris = (form.imageUris + selected).distinct()))
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = AshSpacing.screen,
            top = AshSpacing.md,
            end = AshSpacing.screen,
            bottom = AshSpacing.bottomNavPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AshSpacing.lg)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    if (isEditing) "Edit asset" else "Add to collection",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (isEditing) "Keep the record accurate." else "Photo, name, value. Done in under 30 seconds.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.md)) {
                    Text("1  Add photos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    if (form.imageUris.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
                            itemsIndexed(form.imageUris, key = { index, uri -> "$index-$uri" }) { index, uri ->
                                Surface(
                                    modifier = Modifier.size(108.dp),
                                    shape = RoundedCornerShape(AshRadius.md),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    androidx.compose.foundation.layout.Box {
                                        AssetImagePreview(
                                            uri,
                                            if (index == 0) "Primary asset photo" else "Asset photo ${index + 1}",
                                            Modifier.fillMaxSize()
                                        )
                                        IconButton(
                                            onClick = {
                                                onFormChanged(form.copy(imageUris = form.imageUris.filterIndexed { i, _ -> i != index }))
                                            },
                                            modifier = Modifier.align(Alignment.TopEnd)
                                        ) {
                                            Surface(shape = RoundedCornerShape(99.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)) {
                                                Icon(Icons.Default.Close, "Remove photo", Modifier.padding(5.dp).size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    OutlinedButton(
                        onClick = imagePicker::launch,
                        enabled = imagePicker.isAvailable,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(AshRadius.md)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                        Text(if (form.imageUris.isEmpty()) "  Choose photos" else "  Add more photos")
                    }
                    Text(
                        "At least one photo is required for new assets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.md)) {
                    Text("2  Essential details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    FormTextField(
                        label = "Name",
                        value = form.name,
                        onValueChange = { onFormChanged(form.copy(name = it)) },
                        imeAction = ImeAction.Next
                    )
                    FormTextField(
                        label = "Purchase value (USD)",
                        value = form.purchasePrice,
                        onValueChange = { onFormChanged(form.copy(purchasePrice = sanitizeMoneyInput(it))) },
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                        prefix = "$"
                    )
                }
            }
        }

        item {
            TextButton(onClick = { detailsExpanded = !detailsExpanded }, modifier = Modifier.fillMaxWidth()) {
                Text(if (detailsExpanded) "Hide optional details" else "Add optional details")
                Icon(Icons.Default.ExpandMore, contentDescription = null)
            }
            AnimatedVisibility(
                visible = detailsExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.md)) {
                    AshPanel(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.md)) {
                            Text("Identity", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            AshDropdown("Category", form.category, AssetCategory.entries, AssetCategory::label, {
                                onFormChanged(form.copy(category = it))
                            }, Modifier.fillMaxWidth())
                            FormTextField("Brand", form.brand, { onFormChanged(form.copy(brand = it)) })
                            FormTextField("Model", form.model, { onFormChanged(form.copy(model = it)) })
                            FormTextField("Serial or identifying number", form.serialNumber, {
                                onFormChanged(form.copy(serialNumber = it))
                            })
                            FormTextField("Special or limited-edition details", form.specialDetails, {
                                onFormChanged(form.copy(specialDetails = it))
                            }, minLines = 2)
                            FormTextField("Tags (comma separated)", form.tagsInput, {
                                onFormChanged(form.copy(tagsInput = it))
                            })
                        }
                    }
                    AshPanel(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.md)) {
                            Text("Value and dates", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            FormTextField(
                                "Estimated current value (USD)", form.currentEstimatedValue,
                                { onFormChanged(form.copy(currentEstimatedValue = sanitizeMoneyInput(it))) },
                                KeyboardType.Decimal, prefix = "$"
                            )
                            FormTextField("Purchase date (YYYY-MM-DD)", form.purchaseDate, {
                                onFormChanged(form.copy(purchaseDate = it))
                            })
                            FormTextField("Warranty end (YYYY-MM-DD)", form.warrantyEndDate, {
                                onFormChanged(form.copy(warrantyEndDate = it))
                            })
                            AshDropdown("Condition", form.condition, AssetCondition.entries, AssetCondition::label, {
                                onFormChanged(form.copy(condition = it))
                            }, Modifier.fillMaxWidth())
                        }
                    }
                    AshPanel(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.md)) {
                            Text("Sale preference", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text("Open to selling", fontWeight = FontWeight.Medium)
                                    Text("This stays private in your collection.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(form.willingToSell, { onFormChanged(form.copy(willingToSell = it)) })
                            }
                            if (form.willingToSell) {
                                FormTextField(
                                    "Desired selling price (USD)", form.desiredSellingPrice,
                                    { onFormChanged(form.copy(desiredSellingPrice = sanitizeMoneyInput(it))) },
                                    KeyboardType.Decimal, prefix = "$"
                                )
                            }
                            AshDropdown("Ownership", form.ownershipStatus, OwnershipStatus.entries, OwnershipStatus::label, {
                                onFormChanged(form.copy(ownershipStatus = it))
                            }, Modifier.fillMaxWidth())
                        }
                    }
                    AshPanel(modifier = Modifier.fillMaxWidth()) {
                        FormTextField("Notes", form.notes, { onFormChanged(form.copy(notes = it)) }, minLines = 3)
                    }
                }
            }
        }

        form.error?.let { error ->
            item {
                Surface(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(AshRadius.md)
                ) {
                    Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(12.dp))
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
                Button(onClick = onSave, modifier = Modifier.fillMaxWidth().height(54.dp)) {
                    Text(if (isEditing) "Save changes" else "Save asset")
                }
                OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    prefix: String? = null,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        prefix = prefix?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        shape = RoundedCornerShape(AshRadius.md),
        singleLine = minLines == 1
    )
}

private fun sanitizeMoneyInput(value: String): String {
    var decimalSeen = false
    return value.filter { char ->
        when {
            char.isDigit() -> true
            char == '.' && !decimalSeen -> {
                decimalSeen = true
                true
            }
            else -> false
        }
    }
}
