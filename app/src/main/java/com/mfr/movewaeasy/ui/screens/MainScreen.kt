package com.mfr.movewaeasy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mfr.movewaeasy.R
import com.mfr.movewaeasy.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Overall padding for the screen
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Spacer at the top for some breathing room
        Spacer(modifier = Modifier.height(32.dp))

        // Main title "Easy move your WhatsApp data offline
        Text(
            text = stringResource(R.string.main_screen_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.main_screen_subtitle),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle text
        Text(
            text = stringResource(R.string.main_screen_description),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp) // Horizontal padding for better readability
        )
        
        Spacer(modifier = Modifier.height(32.dp)) // Space before the cards

        // Card for "OLD device" action
        CardComponent(getCardData(Routes.BACKUP_SCREEN, navController))

        Spacer(modifier = Modifier.height(36.dp)) // Space between the two cards

        // Card for "NEW device" action
        CardComponent(getCardData(Routes.RESTORE_SCREEN, navController))
    }
}

@Composable
fun CardComponent(cardData: CardData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp), // Padding for the card itself
        shape = RoundedCornerShape(16.dp), // Rounded corners for the card
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon for the action (e.g., CloudUpload, CloudDownload)
                Icon(
                    imageVector = cardData.icon,
                    contentDescription = cardData.iconDescription.toString(),
                    modifier = Modifier.size(70.dp), // Larger icon size
                    tint = MaterialTheme.colorScheme.primary // Tinting the icon green
                )
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp) // Padding between icon and text
                        .fillMaxWidth(),
                ) {
                    // Title text within the card
                    Text(
                        text = cardData.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Left
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle text within the card
                    Text(
                        text = cardData.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Left,
                        lineHeight = 20.sp // Improve readability of multi-line text
                    )
                }
            }
            // Action button (e.g., Start Backup, Start Restore)
            Button(
                onClick = cardData.onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = cardData.buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class CardData(
    val title: String,
    val description: String,
    val buttonText: String,
    val onClick: () -> Unit,
    val icon: ImageVector,
    val iconDescription: Int
)

@Throws(IllegalArgumentException::class)
@Composable
fun getCardData(route: String, navController: NavController): CardData {
    return when (route) {
        Routes.BACKUP_SCREEN -> CardData(
                title = stringResource(R.string.backup_card_title),
                description = stringResource(R.string.backup_card_description),
                buttonText = stringResource(R.string.ackup_card_button),
                onClick = { navController.navigate(Routes.BACKUP_SCREEN) },
                icon = Icons.Filled.CloudUpload,
                iconDescription = R.string.backup_icon_description
            )
        Routes.RESTORE_SCREEN -> CardData(
                title = stringResource(R.string.restore_card_title),
                description = stringResource(R.string.restore_card_description),
                buttonText = stringResource(R.string.restore_card_button),
                onClick = { navController.navigate(Routes.RESTORE_SCREEN) },
                icon = Icons.Filled.CloudDownload,
                iconDescription = R.string.restore_icon_description
            )
        else -> throw IllegalArgumentException("Invalid route: $route")
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(navController = rememberNavController())
}