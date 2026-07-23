package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.*

@Composable
fun MedicalIdCard(
    userProfile: UserProfileEntity,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null
) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, ResqRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .testTag("medical_id_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ResqCardBg)
    ) {
        Column {
            // Header Image Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.resq_medical_bg_1784819092191),
                    contentDescription = "Medical header image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ResqRed,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = "Medical ID",
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "LOCK SCREEN MEDICAL ID",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResqRedLight,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = userProfile.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    if (onEditClick != null) {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Quick Info Grid
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MedicalBadge(
                        label = "BLOOD GROUP",
                        value = userProfile.bloodGroup,
                        icon = Icons.Default.Bloodtype,
                        badgeColor = ResqRed
                    )
                    MedicalBadge(
                        label = "ORGAN DONOR",
                        value = if (userProfile.isOrganDonor) "YES (YES)" else "NO",
                        icon = Icons.Default.Favorite,
                        badgeColor = ResqEmerald
                    )
                    MedicalBadge(
                        label = "DOB",
                        value = userProfile.dob,
                        icon = Icons.Default.Cake,
                        badgeColor = ResqOrange
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(visible = isExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HorizontalDivider(color = ResqCardBorder)

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = ResqOrange,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp, end = 6.dp)
                            )
                            Column {
                                Text(
                                    text = "Allergies & Sensitivities",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ResqTextSecondary
                                )
                                Text(
                                    text = userProfile.allergies.ifBlank { "None reported" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ResqTextPrimary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = null,
                                tint = ResqRedLight,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp, end = 6.dp)
                            )
                            Column {
                                Text(
                                    text = "Current Medications",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ResqTextSecondary
                                )
                                Text(
                                    text = userProfile.medications.ifBlank { "None" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ResqTextPrimary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Notes,
                                contentDescription = null,
                                tint = ResqTextSecondary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp, end = 6.dp)
                            )
                            Column {
                                Text(
                                    text = "First Responder Notes",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ResqTextSecondary
                                )
                                Text(
                                    text = userProfile.medicalNotes.ifBlank { "No special notes" },
                                    fontSize = 13.sp,
                                    color = ResqTextPrimary
                                )
                            }
                        }
                    }
                }

                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = if (isExpanded) "Hide Details" else "Show Full Medical ID",
                        fontSize = 12.sp,
                        color = ResqRedLight
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = ResqRedLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MedicalBadge(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = ResqDarkBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, ResqCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 6.dp)
            )
            Column {
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResqTextSecondary
                )
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResqTextPrimary
                )
            }
        }
    }
}
