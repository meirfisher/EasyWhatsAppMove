package com.mfr.movewaeasy.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mfr.movewaeasy.R
import com.mfr.movewaeasy.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController = NavController(LocalContext.current)) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Move WhatsApp Easy",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
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
                text = "Easy move\nyour WhatsApp data\nOffline",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(18.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Create an offline backup of your WhatsApp media and restore it on another device",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(20.dp),
                lineHeight = 24.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MainScreenContent(navController)
            }
        }
    }
}

@Composable
fun MainScreenContent(navController: NavController) {

    Column(modifier = Modifier.fillMaxSize()) {
        CardComponent(
            setCardData(
                screenRoutes = Routes.BackupScreen,
                navController = navController
            )
        )

        Spacer(modifier = Modifier.padding(10.dp))

        CardComponent(
            setCardData(
                screenRoutes = Routes.RestoreScreen,
                navController = navController
            )
        )
    }
}

@Composable
fun CardComponent(cardData: CardData) {


    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Icon(
                painter = painterResource(id = cardData.iconID),
                contentDescription = "Backup Icon",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = cardData.onClick,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(cardData.buttonText)
                }
            }
        }
    }
}

fun setCardData(screenRoutes: String, navController: NavController): CardData {
    return when (screenRoutes) {
        Routes.BackupScreen -> CardData(
            title = "I'm in my Old Device",
            description = "Create a backup of your WhatsApp media",
            buttonText = "Backup",
            onClick = { navController.navigate(screenRoutes) },
            iconID = R.drawable.ic_backup
        )
        Routes.RestoreScreen -> CardData(
            title = "I'm in my New Device",
            description = "Restore your WhatsApp media from a backup",
            buttonText = "Restore",
            onClick = { navController.navigate(screenRoutes) },
            iconID = R.drawable.ic_restore
        )

        else -> {
            throw IllegalArgumentException("Invalid screen")
        }
    }
}

data class CardData(
    val title: String,
    val description: String,
    val buttonText: String,
    val onClick: () -> Unit,
    val iconID: Int
)

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}