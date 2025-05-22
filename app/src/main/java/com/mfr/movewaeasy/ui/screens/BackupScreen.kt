package com.mfr.movewaeasy.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mfr.movewaeasy.utils.FileUtils.toStringSize
import com.mfr.movewaeasy.viewmodels.BackupViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(viewModel: BackupViewModel = viewModel()) {
    val state by viewModel.backupState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 24.dp) // Increased vertical padding at the top
                .animateContentSize(animationSpec = tween(durationMillis = 300))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title similar to EasyMoveScreen
            Text(
                text = "Backup Your WhatsApp Media",
                style = MaterialTheme.typography.headlineSmall, // More prominent style
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "Ensure you have enough free space on your device before starting the backup process.",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 28.dp) // Increased bottom padding
            )

            StorageInfoCard(state)

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isCompressing) {
                BackupProgressCard(
                    state,
                    onCancel = viewModel::cancelBackup
                )
            } else {
                StyledButton(
                    onClick = { viewModel.startBackup() },
                    text = "Start Backup",
                    enabled = state.readyToBackup,
                    buttonColors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }

            state.errorMessage?.let {
                Text(
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StorageInfoCard(state: BackupViewModel.BackupState) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            InfoRow(
                label = "Free Space on Device",
                value = state.freeSpace.toStringSize()
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.isCalculatingSize) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Calculating Backup file Size... pleas wait",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                InfoRow(
                    label = "Estimated Backup File Size",
                    value = state.folderSize.toStringSize(),
                    highlightValue = true
                )
            }
        }
    }
}

@Composable
private fun BackupProgressCard(state: BackupViewModel.BackupState, onCancel: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { // Column to hold Card and Button
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp), // Consistent padding
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    "Backing up files...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // Make progress bar slightly less than full width for aesthetics
                        .height(18.dp) // Increased height for thickness
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Transparent
                    )
                }

                Text(
                    "Progress: ${(state.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 12.dp, bottom = 20.dp)
                )

                // files processed
                InfoRow(
                    label = "Files Processed",
                    value = "${state.fileOnProgress}/${state.filesCount}"
                )
                Spacer(modifier = Modifier.height(12.dp))

                // For "Current File", we want it to have a fixed height and allow text to wrap or ellipsis
                Box(modifier = Modifier.height(50.dp)) { // Fixed height for this row
                    InfoRow(
                        label = "Current File:",
                        value = state.backupFilePath ?: "Preparing...",
                        valueMaxLines = 2, // Allow up to 2 lines
                        isPath = true // Indicate it's a path for alignment
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp)) // Space before the cancel button

        // Cancel button
        StyledButton(
            onClick = onCancel,
            text = "Cancel Backup",
            buttonColors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    highlightValue: Boolean = false,
    valueMaxLines: Int = 1,
    isPath: Boolean = false // To handle potentially long paths for value
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = if (isPath) Alignment.Top else Alignment.CenterVertically // Align top for paths
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Muted color for label
            modifier = if (isPath) Modifier.padding(top = 2.dp) else Modifier // Add padding if path
        )
        Spacer(modifier = Modifier.width(8.dp)) // Space between label and value
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (highlightValue) FontWeight.SemiBold else FontWeight.Normal,
            color = if (highlightValue) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            maxLines = valueMaxLines,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End, // Align value to the end
            modifier = Modifier.weight(1f) // Allow value to take remaining space
        )
    }
}

@Composable
fun StyledButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.85f)
            .height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = buttonColors
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

//@Preview(showBackground = true, device = "id:pixel_4")
//@Composable
//fun BackupScreenPreviewLight() {
//    // Assuming you have a theme defined like MoveWAEasyTheme
//    // For preview, we wrap with a basic MaterialTheme.
//    // Replace with your actual theme for accurate preview.
//    MaterialTheme { // Replace with YourAppThemeName for accurate preview
//        BackupScreen(viewModel = MockBackupViewModel())
//    }
//}
//
//@Preview(showBackground = true, device = "id:pixel_4", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun BackupScreenPreviewDark() {
//    MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFF66BB6A), background = Color(0xFF121212), surface = Color(0xFF1E1E1E) )) { // Example dark theme for preview
//        BackupScreen(viewModel = MockBackupViewModel())
//    }
//}