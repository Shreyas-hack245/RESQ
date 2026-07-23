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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun IncidentHistoryScreen(
    viewModel: ResqViewModel,
    modifier: Modifier = Modifier
) {
    val incidents by viewModel.incidents.collectAsState()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ResqDarkBg)
            .padding(16.dp)
            .testTag("incident_history_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Incident History & Timelines",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
            Text(
                text = "Audit trail of all past emergency alerts, AI dispatch summaries, GPS breadcrumbs, and resolution logs.",
                fontSize = 12.sp,
                color = ResqTextSecondary
            )
        }

        items(incidents) { incident ->
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
                                color = if (incident.status == "active") ResqRed.copy(alpha = 0.2f) else ResqEmerald.copy(alpha = 0.2f),
                                modifier = Modifier.padding(end = 10.dp)
                            ) {
                                Icon(
                                    imageVector = if (incident.status == "active") Icons.Default.Emergency else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (incident.status == "active") ResqRedLight else ResqEmeraldLight,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "INCIDENT #${incident.id} • ${incident.type.uppercase()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResqTextPrimary
                                )
                                Text(
                                    text = dateFormat.format(Date(incident.createdAt)),
                                    fontSize = 11.sp,
                                    color = ResqTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (incident.status == "active") ResqRed else ResqDarkBg
                        ) {
                            Text(
                                text = incident.status.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (incident.status == "active") Color.White else ResqTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Address: ${incident.address}",
                        fontSize = 12.sp,
                        color = ResqTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Gemini AI Summary snippet
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ResqDarkBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ResqCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "GEMINI AI DISPATCH SYNOPSIS:",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResqEmeraldLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = incident.aiSummary.ifBlank { "No AI summary available" },
                                fontSize = 12.sp,
                                color = ResqTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
