package ash.asset.presentation.edit

import ash.asset.domain.model.AssetCategory
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.OwnershipStatus
import ash.asset.presentation.AssetFormState
import ash.asset.presentation.components.AshDropdown
import ash.asset.presentation.components.SectionTitle
import ash.asset.presentation.image.AssetImagePickerController
import ash.asset.presentation.image.AssetImagePreview
import ash.asset.presentation.image.rememberAssetImagePicker
import ash.core.designsystem.AshPanel
import ash.core.designsystem.AshRadius
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    val imagePicker = rememberAssetImagePicker { selectedUris ->
        onFormChanged(
            form.copy(imageUris = mergeImageUris(form.imageUris, selectedUris))
        )
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 116.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isEditing) "Edit asset" else "Add asset",
                    style = MaterialTheme.typography.headlineMedium
                )
                form.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            SectionTitle("Core")
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormTextField(
                        label = "Name",
                        value = form.name,
                        onValueChange = { onFormChanged(form.copy(name = it)) }
                    )
                    AshDropdown(
                        label = "Category",
                        selected = form.category,
                        options = AssetCategory.entries,
                        optionLabel = { it.label },
                        onSelected = { onFormChanged(form.copy(category = it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    AshDropdown(
                        label = "Condition",
                        selected = form.condition,
                        options = AssetCondition.entries,
                        optionLabel = { it.label },
                        onSelected = { onFormChanged(form.copy(condition = it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    FormTextField(
                        label = "Brand",
                        value = form.brand,
                        onValueChange = { onFormChanged(form.copy(brand = it)) }
                    )
                    FormTextField(
                        label = "Model",
                        value = form.model,
                        onValueChange = { onFormChanged(form.copy(model = it)) }
                    )
                    FormTextField(
                        label = "Serial number",
                        value = form.serialNumber,
                        onValueChange = { onFormChanged(form.copy(serialNumber = it)) }
                    )
                }
            }
        }

        item {
            SectionTitle("Images")
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                AssetImageEditor(
                    form = form,
                    imagePicker = imagePicker,
                    onFormChanged = onFormChanged
                )
            }
        }

        item {
            SectionTitle("Money")
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormTextField(
                        label = "Purchase price",
                        value = form.purchasePrice,
                        onValueChange = { onFormChanged(form.copy(purchasePrice = it)) },
                        keyboardType = KeyboardType.Decimal
                    )
                    FormTextField(
                        label = "Current value",
                        value = form.currentEstimatedValue,
                        onValueChange = { onFormChanged(form.copy(currentEstimatedValue = it)) },
                        keyboardType = KeyboardType.Decimal
                    )
                    FormTextField(
                        label = "Purchase date",
                        value = form.purchaseDate,
                        onValueChange = { onFormChanged(form.copy(purchaseDate = it)) }
                    )
                    FormTextField(
                        label = "Warranty end",
                        value = form.warrantyEndDate,
                        onValueChange = { onFormChanged(form.copy(warrantyEndDate = it)) }
                    )
                }
            }
        }

        item {
            SectionTitle("Status")
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AshDropdown(
                        label = "Status",
                        selected = form.ownershipStatus,
                        options = OwnershipStatus.entries,
                        optionLabel = { it.label },
                        onSelected = { onFormChanged(form.copy(ownershipStatus = it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = form.isInUse,
                            onCheckedChange = { onFormChanged(form.copy(isInUse = it)) }
                        )
                        Text("In regular use")
                    }
                }
            }
        }

        item {
            SectionTitle("Notes")
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.notes,
                    onValueChange = { onFormChanged(form.copy(notes = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    shape = RoundedCornerShape(8.dp),
                    label = { Text("Notes") }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    onClick = onCancel
                ) {
                    Text("Cancel")
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    onClick = onSave
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
private fun AssetImageEditor(
    form: AssetFormState,
    imagePicker: AssetImagePickerController,
    onFormChanged: (AssetFormState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (form.imageUris.isEmpty()) {
            Text(
                text = "No images added yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(form.imageUris, key = { index, uri -> "$index-$uri" }) { index, uri ->
                    EditableImageTile(
                        uri = uri,
                        isPrimary = index == 0,
                        onRemove = {
                            onFormChanged(
                                form.copy(
                                    imageUris = form.imageUris
                                        .toMutableList()
                                        .also { it.removeAt(index) }
                                )
                            )
                        }
                    )
                }
            }
        }

        if (imagePicker.isAvailable) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = imagePicker::launch
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Pick images")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = form.pendingImageUri,
                onValueChange = { onFormChanged(form.copy(pendingImageUri = it)) },
                modifier = Modifier
                    .weight(1f)
                    .height(58.dp),
                singleLine = true,
                shape = RoundedCornerShape(AshRadius.md),
                label = { Text("Image URI or file path") }
            )
            Button(
                modifier = Modifier.size(width = 58.dp, height = 58.dp),
                enabled = form.pendingImageUri.isNotBlank(),
                onClick = {
                    onFormChanged(
                        form.copy(
                            imageUris = mergeImageUris(form.imageUris, listOf(form.pendingImageUri)),
                            pendingImageUri = ""
                        )
                    )
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add image")
            }
        }
    }
}

@Composable
private fun EditableImageTile(
    uri: String,
    isPrimary: Boolean,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.size(width = 112.dp, height = 128.dp),
        shape = RoundedCornerShape(AshRadius.md),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AssetImagePreview(
                uri = uri,
                contentDescription = "Asset image",
                modifier = Modifier.fillMaxSize()
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
            ) {
                Text(
                    text = if (isPrimary) "Primary" else imageName(uri),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .padding(horizontal = 8.dp, vertical = 11.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(34.dp),
                onClick = onRemove
            ) {
                Icon(Icons.Default.Close, contentDescription = "Remove image")
            }
        }
    }
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        label = { Text(label) }
    )
}

private fun mergeImageUris(
    current: List<String>,
    incoming: List<String>
): List<String> {
    return (current + incoming)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .distinct()
}

private fun imageName(uri: String): String {
    return uri
        .substringAfterLast('/')
        .substringAfterLast("%2F")
        .takeIf { it.isNotBlank() }
        ?: "Image"
}
