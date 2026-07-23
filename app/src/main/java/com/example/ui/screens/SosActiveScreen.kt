package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.security.SecurityUtils
import com.example.ui.theme.*
import com.example.ui.viewmodel.ResqViewModel

@Composable
fun SosActiveScreen(
    viewModel: ResqViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeIncidents by viewModel.activeIncidents.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val evidenceFiles by viewModel.evidenceFiles.collectAsState()

    val currentIncident = activeIncidents.firstOrNull()

    val infiniteTransition = rememberInfiniteTransition(label = "broadcastPulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ResqDarkBg)
            .padding(16.dp)
            .testTag("sos_active_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- TOP BROADCAST HEADER ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ResqRed,
                shadowElevation = 12.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = alphaAnim),
                                modifier = Modifier.size(14.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LIVE EMERGENCY BROADCAST",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "TRIGGER: ${currentIncident?.triggerMethod?.uppercase() ?: "MANUAL"}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "RESQ Dispatch Hub is broadcasting your real-time GPS coordinates, AES evidence, and Medical ID to emergency contacts and nearby verified volunteers.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // --- GPS LOCATION & SMS FALLBACK CARD ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ResqCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, ResqCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = ResqRedLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Current GPS Coordinates",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResqTextSecondary
                            )
                            Text(
                                text = "${currentIncident?.latitude ?: 37.7749}, ${currentIncident?.longitude ?: -122.4194}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResqTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Address: ${currentIncident?.address ?: "Market St & 4th St, San Francisco, CA"}",
                        fontSize = 13.sp,
                        color = ResqTextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // SMS Fallback Button
                    Button(
                        onClick = {
                            val smsText = "RESQ EMERGENCY SOS! I need help. My GPS loc: https://maps.google.com/?q=${currentIncident?.latitude ?: 37.7749},${currentIncident?.longitude ?: -122.4194}. Patient: ${userProfile.name} (Blood ${userProfile.bloodGroup})."
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("sms:${contacts.firstOrNull()?.phone ?: ""}")
                                putExtra("sms_body", smsText)
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sms_fallback_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = ResqOrange)
                    ) {
                        Icon(imageVector = Icons.Default.Sms, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SEND SMS FALLBACK (ZERO DATA MODE)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // --- GEMINI AI DISPATCH SYNTHESIS CARD ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ResqCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, ResqEmerald.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = ResqEmeraldLight,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GEMINI AI DISPATCH ANALYSIS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ResqEmeraldLight,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentIncident?.aiSummary.takeIf { !it.isNullByBlank() }
                            ?: "Synthesizing real-time sensor metrics and paramedic guidance...",
                        fontSize = 13.sp,
                        color = ResqTextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // --- AUTO-RECORDING EVIDENCE VAULT BADGE ---
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ResqCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, ResqCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = ResqRedLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "CLIENT-SIDE AES-256 VAULT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResqRedLight
                            )
                            val latestFile = evidenceFiles.firstOrNull()
                            Text(
                                text = latestFile?.let { "Recording: ${it.fileName} (${SecurityUtils.formatShortHash(it.sha256Hash)})" }
                                    ?: "Recording encrypted audio/video payload...",
                                fontSize = 11.sp,
                                color = ResqTextSecondary
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ResqEmeraldLight
                    )
                }
            }
        }

        // --- EMERGENCY CONTACTS DISPATCHED LIST ---
        item {
            Text(
                text = "Alerted Emergency Contacts",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
        }

        items(contacts) { contact ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ResqCardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = ResqEmerald.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = ResqEmeraldLight,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = contact.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResqTextPrimary
                            )
                            Text(
                                text = "${contact.relationship} • ${contact.phone}",
                                fontSize = 11.sp,
                                color = ResqTextSecondary
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = {
                            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                            context.startActivity(callIntent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call Contact",
                                tint = ResqEmeraldLight
                            )
                        }
                    }
                }
            }
        }

        // --- DE-ESCALATION RESOLVE BUTTONS ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        viewModel.resolveActiveIncident(status = "resolved")
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("im_safe_now_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ResqEmerald)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I'M SAFE NOW (CLOSE BROADCAST)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        viewModel.resolveActiveIncident(status = "false_alarm")
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ResqTextSecondary)
                ) {
                    Text(text = "CANCEL AS FALSE ALARM", fontSize = 12.sp)
                }
            }
        }
    }
}

private fun String?.isNullByBlank(): Boolean = this.isNullOrBlank()
