package com.mfr.movewaeasy.test

// import androidx.compose.material3.TopAppBar // TopAppBar removed
// import androidx.compose.material3.TopAppBarDefaults // TopAppBar removed
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mfr.movewaeasy.utils.FileUtils.toStringSize
import kotlinx.coroutines.launch

// Mock BackupViewModel and BackupState for preview purposes
// Replace with your actual ViewModel and State
class MockBackupViewModel1 {
    val backupState1 = kotlinx.coroutines.flow.MutableStateFlow(BackupState1())
    fun startBackup() {
        backupState1.value = backupState1.value.copy(
            isCompressing = true,
            progress = 0.25f, // Changed for better preview
            filesCount = 24849,
            fileOnProgress = 5920,
            backupFilePath = "Media/WhatsApp Video/VID-20250113-WA0022.mp4" // Example from image
        )
    }
    fun cancelBackup() {
        backupState1.value = backupState1.value.copy(isCompressing = false, progress = 0f, backupFilePath = "Preparing...")
    }
    fun calculateSize() {
        backupState1.value = backupState1.value.copy(isCalculatingSize = true, folderSize = 0)
        kotlinx.coroutines.GlobalScope.launch { // Use a proper scope in real app
            kotlinx.coroutines.delay(1500) // Shorter delay for preview
            backupState1.value = backupState1.value.copy(isCalculatingSize = false, folderSize = 1024L * 1024 * 1024 * 8) // 8GB
        }
    }
    init {
        calculateSize()
    }
}

data class BackupState1(
    val freeSpace: Long = 1024L * 1024 * 1024 * 86, // 86GB
    val folderSize: Long = 0L,
    val isCalculatingSize: Boolean = true,
    val isCompressing: Boolean = false,
    val progress: Float = 0.0f,
    val filesCount: Int = 0,
    val fileOnProgress: Int = 0,
    val backupFilePath: String? = "Preparing...",
    val errorMessage: String? = null
) {
    fun Long.toStringSize(): String {
        if (this <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(this.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format("%.2f %s", this / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups]) // .2f for two decimal places
    }
}
// End of Mock Data

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    viewModel: MockBackupViewModel1 = viewModel(), // Using mock for preview
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.backupState1.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .animateContentSize(animationSpec = tween(durationMillis = 300))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Backup",
                style = MaterialTheme.typography.headlineSmall,
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
                modifier = Modifier.padding(bottom = 28.dp)
            )

            StorageInfoCard(state)

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isCompressing) {
                BackupProgressSectionCard(state, onCancel = viewModel::cancelBackup) // Renamed and changed
            } else {
                StyledButton(
                    onClick = { viewModel.startBackup() },
                    text = "Start Backup",
                    enabled = !state.isCalculatingSize && state.folderSize > 0,
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
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StorageInfoCard(state: BackupState1) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            InfoRow(label = "Free Space on Device:", value = state.freeSpace.toStringSize())
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
                        "Calculating backup size...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                InfoRow(label = "Estimated Backup Size:", value = state.folderSize.toStringSize(), highlightValue = true)
            }
        }
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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = if (isPath) Modifier.padding(top = 2.dp) else Modifier // Add padding if path
        )
        Spacer(modifier = Modifier.width(8.dp)) // Add some space between label and value
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
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
private fun BackupProgressSectionCard(state: BackupState1, onCancel: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { // Column to hold Card and Button
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp), // Consistent padding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Backing up files...",
                    style = MaterialTheme.typography.titleMedium, // Adjusted from titleLarge
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(18.dp)
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
                    color = MaterialTheme.colorScheme.onSurface, // Should be onSurface if inside card
                    modifier = Modifier.padding(top = 12.dp, bottom = 20.dp)
                )

                // Using InfoRow for consistency
                InfoRow(
                    label = "Files Processed:",
                    value = "${state.fileOnProgress} / ${state.filesCount}"
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

// ProgressDetailItem is no longer needed as its content is merged into BackupProgressSectionCard using InfoRow

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

@Preview(showBackground = true, device = "id:pixel_6_pro")
@Composable
fun BackupScreenPreviewLight() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF4CAF50),
            onPrimary = Color.White,
            background = Color(0xFFF5F5F5),
            surface = Color.White,
            onSurface = Color.Black,
            onSurfaceVariant = Color(0xFF757575),
            error = Color(0xFFD32F2F),
            onError = Color.White,
            surfaceVariant = Color(0xFFE0E0E0)
        ),
        typography = MaterialTheme.typography
    ) {
        BackupScreen(viewModel = MockBackupViewModel1().apply { startBackup() }) // Start backup for better preview
    }
}

@Preview(showBackground = true, device = "id:pixel_6_pro", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BackupScreenPreviewDark() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF66BB6A),
            onPrimary = Color.Black,
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onSurface = Color.White,
            onSurfaceVariant = Color(0xFFBDBDBD),
            error = Color(0xFFEF5350),
            onError = Color.Black,
            surfaceVariant = Color(0xFF424242)
        ),
        typography = MaterialTheme.typography
    ) {
        BackupScreen(viewModel = MockBackupViewModel1().apply { startBackup() }) // Start backup for better preview
    }
}
