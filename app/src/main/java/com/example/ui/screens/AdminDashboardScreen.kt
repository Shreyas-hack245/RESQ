package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.ResqViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: ResqViewModel,
    modifier: Modifier = Modifier
) {
    val activeIncidents by viewModel.activeIncidents.collectAsState()
    val allIncidents by viewModel.incidents.collectAsState()
    val volunteers by viewModel.volunteers.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ResqDarkBg)
            .padding(16.dp)
            .testTag("admin_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RESQ Command Center",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ResqTextPrimary
                    )
                    Text(
                        text = "Operator & Dispatch Console",
                        fontSize = 12.sp,
                        color = ResqOrangeLight
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ResqEmerald.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ResqEmerald)
                ) {
                    Text(
                        text = "DISPATCH ONLINE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ResqEmeraldLight,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // --- DISPATCH ANALYTICS CARDS ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    title = "ACTIVE EMERGENCY",
                    value = "${activeIncidents.size}",
                    color = if (activeIncidents.isNotEmpty()) ResqRed else ResqEmerald,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "AVG RESPONSE",
                    value = "2.4 min",
                    color = ResqOrangeLight,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "NEARBY VOLUNTEERS",
                    value = "${volunteers.size}",
                    color = ResqEmeraldLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- ACTIVE EMERGENCY DISPATCH MATRIX ---
        item {
            Text(
                text = "Live Incident Broadcast Matrix",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
        }

        if (activeIncidents.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ResqCardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = ResqEmeraldLight, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "NO ACTIVE EMERGENCIES IN QUEUE", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResqEmeraldLight)
                            Text(text = "All citizen zones clear & monitored by AI", fontSize = 11.sp, color = ResqTextSecondary)
                        }
                    }
                }
            }
        } else {
            items(activeIncidents) { incident ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ResqCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ResqRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INCIDENT #${incident.id} • ${incident.triggerMethod.uppercase()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = ResqRedLight
                            )
                            Button(
                                onClick = { viewModel.resolveActiveIncident("resolved") },
                                colors = ButtonDefaults.buttonColors(containerColor = ResqEmerald),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Dispatch Responder", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Coordinates: Lat ${incident.latitude}, Lng ${incident.longitude}", fontSize = 12.sp, color = ResqTextPrimary)
                        Text(text = "Address: ${incident.address}", fontSize = 12.sp, color = ResqTextSecondary)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "AI Dispatch Summary: ${incident.aiSummary}", fontSize = 12.sp, color = ResqTextPrimary)
                    }
                }
            }
        }

        // --- ALL HISTORICAL INCIDENTS TABLE ---
        item {
            Text(
                text = "Dispatch Audit History (${allIncidents.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
        }

        items(allIncidents) { inc ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ResqCardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Incident #${inc.id} (${inc.type})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResqTextPrimary)
                        Text(text = inc.address, fontSize = 11.sp, color = ResqTextSecondary)
                    }
                    Text(
                        text = inc.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (inc.status == "resolved") ResqEmeraldLight else ResqRedLight
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ResqCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, ResqCardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ResqTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}
