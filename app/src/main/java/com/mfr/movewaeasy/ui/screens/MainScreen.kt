package com.mfr.movewaeasy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFDEE5E5),
                            Color(0xFFB4C6C6)
                        )
                    )
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.main_screen_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = stringResource(R.string.main_screen_description),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CardComponent(getCardData(Routes.BACKUP_SCREEN, navController))
                CardComponent(getCardData(Routes.RESTORE_SCREEN, navController))
            }
        }
    }
}

@Composable
fun CardComponent(cardData: CardData, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = cardData.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = cardData.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Icon(
                painter = painterResource(id = cardData.iconID),
                contentDescription = stringResource(id = cardData.contentDescriptionID),
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Button(
                onClick = cardData.onClick,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(cardData.buttonText)
            }
        }
    }
}

data class CardData(
    val title: String,
    val description: String,
    val buttonText: String,
    val onClick: () -> Unit,
    val iconID: Int,
    val contentDescriptionID: Int
)

@Composable
fun getCardData(route: String, navController: NavController): CardData {
    return when (route) {
        Routes.BACKUP_SCREEN -> CardData(
            title = stringResource(R.string.backup_card_title),
            description = stringResource(R.string.backup_card_description),
            buttonText = stringResource(R.string.ackup_card_button),
            onClick = { navController.navigate(Routes.BACKUP_SCREEN) },
            iconID = R.drawable.ic_backup,
            contentDescriptionID = R.string.backup_icon_description
        )
        Routes.RESTORE_SCREEN -> CardData(
            title = stringResource(R.string.restore_card_title),
            description = stringResource(R.string.restore_card_description),
            buttonText = stringResource(R.string.restore_card_button),
            onClick = { navController.navigate(Routes.RESTORE_SCREEN) },
            iconID = R.drawable.ic_restore,
            contentDescriptionID = R.string.restore_icon_description
        )
        else -> CardData (
            title = "Unknown",
            description = "Unknown",
            buttonText = "Unknown",
            onClick = {},
            iconID = R.drawable.ic_backup,
            contentDescriptionID = R.string.backup_icon_description
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(navController = rememberNavController())
}