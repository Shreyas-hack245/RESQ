package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ResqViewModel

@Composable
fun AiDetectionSensorsScreen(
    viewModel: ResqViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()

    var gForceValue by remember { mutableFloatStateOf(1.0f) }

    // Waveform simulation
    val infiniteTransition = rememberInfiniteTransition(label = "sensorWave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ResqDarkBg)
            .padding(16.dp)
            .testTag("ai_detection_sensors_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "AI Emergency Detection Engine",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
            Text(
                text = "On-device accelerometer heuristics analyze real-time kinetic patterns for falls, motor crashes, prolonged immobility, and voice panic signals.",
                fontSize = 12.sp,
                color = ResqTextSecondary
            )
        }

        // --- REAL-TIME ACCELEROMETER G-FORCE TELEMETRY GRAPH ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
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
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = ResqEmeraldLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ACCELEROMETER TELEMETRY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResqEmeraldLight
                            )
                        }

                        Text(
                            text = "G-FORCE: ${"%.2f".format(gForceValue)} G",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (gForceValue > 2.5f) ResqRedLight else ResqEmeraldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated Waveform Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(ResqDarkBg, RoundedCornerShape(8.dp))
                            .border(1.dp, ResqCardBorder, RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val midY = size.height / 2f
                            val points = 30
                            val stepX = size.width / points

                            for (i in 0 until points - 1) {
                                val x1 = i * stepX
                                val y1 = midY + kotlin.math.sin(Math.toRadians((x1 + waveOffset).toDouble())).toFloat() * (gForceValue * 10f)
                                val x2 = (i + 1) * stepX
                                val y2 = midY + kotlin.math.sin(Math.toRadians((x2 + waveOffset).toDouble())).toFloat() * (gForceValue * 10f)

                                drawLine(
                                    color = if (gForceValue > 2.5f) ResqRed else ResqEmerald,
                                    start = Offset(x1, y1),
                                    end = Offset(x2, y2),
                                    strokeWidth = 2.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- SIMULATED AI TEST TRIGGERS ---
        item {
            Text(
                text = "Simulate AI Sensor Events (Test Panel)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        gForceValue = 3.8f
                        viewModel.simulateFallDetected()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ResqOrange)
                ) {
                    Icon(imageVector = Icons.Default.PersonalInjury, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simulate Fall", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        gForceValue = 6.2f
                        viewModel.simulateCrashDetected()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ResqRed)
                ) {
                    Icon(imageVector = Icons.Default.CarCrash, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simulate Crash", fontSize = 11.sp)
                }
            }
        }

        item {
            Button(
                onClick = {
                    viewModel.simulatePanicVoiceTrigger()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ResqEmerald)
            ) {
                Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Voice Command Test: \"Help me RESQ\"", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // --- SENSOR TOGGLE CONFIGURATIONS ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ResqCardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "DETECTION MODULES CONFIGURATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ResqTextSecondary
                    )

                    SensorToggleRow(
                        title = "Fall Detection Heuristics",
                        description = "3.0G impact followed by 10s immobility trigger",
                        icon = Icons.Default.PersonalInjury,
                        checked = userProfile.shakeTriggerEnabled,
                        onCheckedChange = {
                            viewModel.updateProfile(
                                name = userProfile.name,
                                phone = userProfile.phone,
                                bloodGroup = userProfile.bloodGroup,
                                allergies = userProfile.allergies,
                                medications = userProfile.medications,
                                medicalNotes = userProfile.medicalNotes,
                                isOrganDonor = userProfile.isOrganDonor,
                                silentSosEnabled = userProfile.silentSosEnabled,
                                shakeEnabled = it,
                                volumeEnabled = userProfile.volumeTriggerEnabled
                            )
                        }
                    )

                    HorizontalDivider(color = ResqCardBorder)

                    SensorToggleRow(
                        title = "Vehicle Crash Detection",
                        description = "G-force deceleration spikes > 5.5G trigger alert",
                        icon = Icons.Default.CarCrash,
                        checked = true,
                        onCheckedChange = {}
                    )

                    HorizontalDivider(color = ResqCardBorder)

                    SensorToggleRow(
                        title = "Voice Panic Keyword Monitor",
                        description = "Listens for custom phrase \"Help me RESQ\"",
                        icon = Icons.Default.Mic,
                        checked = true,
                        onCheckedChange = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun SensorToggleRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ResqEmeraldLight,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ResqTextPrimary)
                Text(text = description, fontSize = 11.sp, color = ResqTextSecondary)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = ResqEmerald)
        )
    }
}
