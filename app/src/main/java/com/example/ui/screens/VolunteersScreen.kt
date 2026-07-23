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
fun VolunteersScreen(
    viewModel: ResqViewModel,
    modifier: Modifier = Modifier
) {
    val volunteers by viewModel.volunteers.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var isOptedIn by remember(userProfile) { mutableStateOf(userProfile.isVolunteer) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ResqDarkBg)
            .padding(16.dp)
            .testTag("volunteers_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Nearby RESQ Volunteer Network",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
            Text(
                text = "Vetted community first responders and medical professionals nearby who receive active emergency alerts when seconds count.",
                fontSize = 12.sp,
                color = ResqTextSecondary
            )
        }

        // --- OPT-IN CITIZEN VOLUNTEER TOGGLE ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ResqCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, ResqEmerald.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = null,
                                tint = ResqEmeraldLight,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Opt-in as First Responder",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResqTextPrimary
                                )
                                Text(
                                    text = "Receive notifications if someone needs CPR or help nearby",
                                    fontSize = 11.sp,
                                    color = ResqTextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = isOptedIn,
                            onCheckedChange = {
                                isOptedIn = it
                                viewModel.updateProfile(
                                    name = userProfile.name,
                                    phone = userProfile.phone,
                                    bloodGroup = userProfile.bloodGroup,
                                    allergies = userProfile.allergies,
                                    medications = userProfile.medications,
                                    medicalNotes = userProfile.medicalNotes,
                                    isOrganDonor = userProfile.isOrganDonor,
                                    silentSosEnabled = userProfile.silentSosEnabled,
                                    shakeEnabled = userProfile.shakeTriggerEnabled,
                                    volumeEnabled = userProfile.volumeTriggerEnabled
                                )
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = ResqEmerald)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Verified Volunteers in Your Area (${volunteers.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
        }

        items(volunteers) { volunteer ->
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
                                    imageVector = Icons.Default.HealthAndSafety,
                                    contentDescription = null,
                                    tint = ResqEmeraldLight,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = volunteer.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ResqTextPrimary
                                    )
                                    if (volunteer.isVerified) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = ResqEmeraldLight,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = volunteer.certification,
                                    fontSize = 11.sp,
                                    color = ResqTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ResqOrange.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ResqOrangeLight,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${volunteer.trustScore}★",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResqOrangeLight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PROXIMITY: ${volunteer.distanceKm} km away",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ResqEmeraldLight
                        )
                        Text(
                            text = volunteer.phone,
                            fontSize = 12.sp,
                            color = ResqTextPrimary
                        )
                    }
                }
            }
        }
    }
}
