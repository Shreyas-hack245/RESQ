package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ResqViewModel

@Composable
fun HomeScreen(
    viewModel: ResqViewModel,
    onNavigateToActiveSos: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToVault: () -> Unit,
    onNavigateToVolunteers: () -> Unit,
    onNavigateToSensors: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val sosState by viewModel.sosCountdownState.collectAsState()
    val activeIncidents by viewModel.activeIncidents.collectAsState()
    val volunteers by viewModel.volunteers.collectAsState()

    var showFakeCallPicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ResqDarkBg)
            .padding(horizontal = 16.dp)
            .testTag("home_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // --- HERO BANNER HEADER ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ResqCardBg)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.resq_hero_banner_1784819078531),
                        contentDescription = "RESQ Hero Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.55f))
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = ResqEmerald,
                                    modifier = Modifier.size(10.dp)
                                ) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CITIZEN PROTECTION ACTIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResqEmeraldLight,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "Hello, ${userProfile.name}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Emergency Contacts & AI Sensors Armed",
                                fontSize = 12.sp,
                                color = ResqTextSecondary
                            )
                        }

                        IconButton(
                            onClick = onNavigateToAdmin,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ResqCardBg)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Operator Dashboard",
                                tint = ResqOrangeLight
                            )
                        }
                    }
                }
            }
        }

        // --- ACTIVE INCIDENT BANNER ALERT ---
        if (activeIncidents.isNotEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToActiveSos() }
                        .testTag("active_incident_alert_banner"),
                    shape = RoundedCornerShape(14.dp),
                    color = ResqRed,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Emergency,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "EMERGENCY BROADCAST LIVE!",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Tap to view live location & contacts dispatch",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // --- CENTRAL SOS BUTTON SECTION ---
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SosCountdownButton(
                    isCountingDown = sosState.isCountingDown,
                    secondsRemaining = sosState.secondsRemaining,
                    progress = sosState.progress,
                    isSilent = userProfile.silentSosEnabled,
                    onTapTrigger = { viewModel.startSosCountdown(triggerMethod = "manual") },
                    onCancelCountdown = { viewModel.cancelSosCountdown() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Multi-trigger status chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TriggerStatusChip(
                        label = "SHAKE",
                        isEnabled = userProfile.shakeTriggerEnabled,
                        icon = Icons.Default.Vibration
                    )
                    TriggerStatusChip(
                        label = "VOL KEYS",
                        isEnabled = userProfile.volumeTriggerEnabled,
                        icon = Icons.Default.VolumeUp
                    )
                    TriggerStatusChip(
                        label = "SILENT",
                        isEnabled = userProfile.silentSosEnabled,
                        icon = Icons.Default.VolumeOff,
                        onClick = {
                            viewModel.updateProfile(
                                name = userProfile.name,
                                phone = userProfile.phone,
                                bloodGroup = userProfile.bloodGroup,
                                allergies = userProfile.allergies,
                                medications = userProfile.medications,
                                medicalNotes = userProfile.medicalNotes,
                                isOrganDonor = userProfile.isOrganDonor,
                                silentSosEnabled = !userProfile.silentSosEnabled,
                                shakeEnabled = userProfile.shakeTriggerEnabled,
                                volumeEnabled = userProfile.volumeTriggerEnabled
                            )
                        }
                    )
                }
            }
        }

        // --- QUICK DE-ESCALATION & ACTION TOOLS ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    title = "Fake Call",
                    subtitle = "Exit Unsafe Situation",
                    icon = Icons.Default.PhoneCallback,
                    accentColor = ResqOrange,
                    modifier = Modifier.weight(1f),
                    onClick = { showFakeCallPicker = true }
                )
                QuickActionButton(
                    title = "AI Sensors",
                    subtitle = "Fall/Crash Tests",
                    icon = Icons.Default.Psychology,
                    accentColor = ResqEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSensors
                )
                QuickActionButton(
                    title = "Vault",
                    subtitle = "AES Evidence",
                    icon = Icons.Default.Security,
                    accentColor = ResqRedLight,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToVault
                )
            }
        }

        // --- LIVE RADAR & VOLUNTEER PROXIMITY ---
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Location Radar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ResqTextPrimary
                    )
                    TextButton(onClick = onNavigateToTracking) {
                        Text(text = "Safe Zones >", color = ResqEmeraldLight, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                LiveRadarView(activeVolunteerCount = volunteers.size)
            }
        }

        // --- LOCK SCREEN MEDICAL ID WIDGET ---
        item {
            MedicalIdCard(
                userProfile = userProfile,
                onEditClick = onNavigateToProfile
            )
        }
    }

    // --- FAKE CALL TIMER PICKER DIALOG ---
    if (showFakeCallPicker) {
        AlertDialog(
            onDismissRequest = { showFakeCallPicker = false },
            title = { Text("Schedule Fake Incoming Call", color = ResqTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Generates a realistic incoming phone call screen to give you a natural reason to exit an uncomfortable or unsafe situation.",
                        fontSize = 13.sp,
                        color = ResqTextSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                viewModel.scheduleFakeCall(5)
                                showFakeCallPicker = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ResqOrange)
                        ) {
                            Text("5s Delay")
                        }
                        Button(
                            onClick = {
                                viewModel.scheduleFakeCall(15)
                                showFakeCallPicker = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ResqOrange)
                        ) {
                            Text("15s Delay")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showFakeCallPicker = false }) {
                    Text("Cancel", color = ResqTextSecondary)
                }
            },
            containerColor = ResqCardBg
        )
    }
}

@Composable
private fun TriggerStatusChip(
    label: String,
    isEnabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(20.dp),
        color = if (isEnabled) ResqCardBg else ResqDarkBg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isEnabled) ResqEmerald else ResqCardBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isEnabled) ResqEmeraldLight else ResqTextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$label: ${if (isEnabled) "ON" else "OFF"}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isEnabled) ResqTextPrimary else ResqTextSecondary
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = ResqCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, ResqCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = ResqTextSecondary
            )
        }
    }
}
