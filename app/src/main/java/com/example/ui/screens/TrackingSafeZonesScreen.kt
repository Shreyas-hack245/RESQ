package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LiveRadarView
import com.example.ui.theme.*
import com.example.ui.viewmodel.ResqViewModel

@Composable
fun TrackingSafeZonesScreen(
    viewModel: ResqViewModel,
    modifier: Modifier = Modifier
) {
    val safeZones by viewModel.safeZones.collectAsState()
    val volunteers by viewModel.volunteers.collectAsState()

    var showAddZoneDialog by remember { mutableStateOf(false) }
    var newZoneName by remember { mutableStateOf("") }
    var newZoneRadius by remember { mutableFloatStateOf(500f) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ResqDarkBg)
            .padding(16.dp)
            .testTag("tracking_safe_zones_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Live Geofencing & Safe Zones",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
            Text(
                text = "Configure trusted boundaries (Home, Work, Campus). RESQ automatically sends quiet alerts to guardians if you exit a safe zone unexpectedly.",
                fontSize = 12.sp,
                color = ResqTextSecondary
            )
        }

        // --- LIVE RADAR MAP PREVIEW ---
        item {
            LiveRadarView(
                activeVolunteerCount = volunteers.size,
                safeZoneName = safeZones.firstOrNull()?.name ?: "Home Residence"
            )
        }

        // --- SAFE ZONES LIST ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Safe Zones (${safeZones.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResqTextPrimary
                )
                Button(
                    onClick = { showAddZoneDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ResqEmerald)
                ) {
                    Icon(imageVector = Icons.Default.AddLocation, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add Safe Zone", fontSize = 12.sp)
                }
            }
        }

        items(safeZones) { zone ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ResqCardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ResqEmerald.copy(alpha = 0.2f),
                                modifier = Modifier.padding(end = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = ResqEmeraldLight,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = zone.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResqTextPrimary
                                )
                                Text(
                                    text = "Radius: ${zone.radiusMeters.toInt()} meters • Lat ${zone.latitude}, Lng ${zone.longitude}",
                                    fontSize = 11.sp,
                                    color = ResqTextSecondary
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.deleteSafeZone(zone.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = ResqRedLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = zone.alertOnExit,
                            onClick = {},
                            label = { Text("Alert on Exit", fontSize = 10.sp) },
                            leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(12.dp)) }
                        )
                        FilterChip(
                            selected = zone.alertOnEnter,
                            onClick = {},
                            label = { Text("Alert on Enter", fontSize = 10.sp) },
                            leadingIcon = { Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(12.dp)) }
                        )
                    }
                }
            }
        }
    }

    // --- ADD SAFE ZONE DIALOG ---
    if (showAddZoneDialog) {
        AlertDialog(
            onDismissRequest = { showAddZoneDialog = false },
            title = { Text("Create Safe Zone Geofence", color = ResqTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newZoneName,
                        onValueChange = { newZoneName = it },
                        label = { Text("Zone Name (e.g. Office, School)") }
                    )

                    Text(
                        text = "Geofence Radius: ${newZoneRadius.toInt()} meters",
                        fontSize = 12.sp,
                        color = ResqTextPrimary
                    )
                    Slider(
                        value = newZoneRadius,
                        onValueChange = { newZoneRadius = it },
                        valueRange = 100f..3000f,
                        colors = SliderDefaults.colors(thumbColor = ResqEmerald, activeTrackColor = ResqEmerald)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newZoneName.isNotBlank()) {
                            viewModel.addSafeZone(
                                name = newZoneName,
                                lat = 37.7749 + (0.005 * safeZones.size),
                                lng = -122.4194 + (0.005 * safeZones.size),
                                radius = newZoneRadius.toDouble()
                            )
                            newZoneName = ""
                            showAddZoneDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ResqEmerald)
                ) {
                    Text("Save Geofence")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddZoneDialog = false }) {
                    Text("Cancel", color = ResqTextSecondary)
                }
            },
            containerColor = ResqCardBg
        )
    }
}
