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
fun SettingsPrivacyScreen(
    viewModel: ResqViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val consentLogs by viewModel.consentLogs.collectAsState()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }

    var showEraseConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ResqDarkBg)
            .padding(16.dp)
            .testTag("settings_privacy_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Privacy, Security & Consent Engine",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
            Text(
                text = "RESQ treats citizen data security as a first-class requirement. Review explicit consent ledgers, zero-knowledge encryption settings, or exercise Right to Erasure.",
                fontSize = 12.sp,
                color = ResqTextSecondary
            )
        }

        // --- EXPLICIT CONSENT AUDIT LEDGER ---
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
                        Text(
                            text = "EXPLICIT CONSENT AUDIT LOG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ResqEmeraldLight,
                            letterSpacing = 1.sp
                        )
                        Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = ResqEmeraldLight)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    consentLogs.forEach { log ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = log.consentType.replace('_', ' ').uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResqTextPrimary
                                )
                                Text(
                                    text = if (log.isGranted) "GRANTED" else "REVOKED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (log.isGranted) ResqEmeraldLight else ResqRedLight
                                )
                            }
                            Text(text = log.remarks, fontSize = 11.sp, color = ResqTextSecondary)
                            Text(text = dateFormat.format(Date(log.timestamp)), fontSize = 10.sp, color = ResqTextSecondary)
                        }
                        HorizontalDivider(color = ResqCardBorder)
                    }
                }
            }
        }

        // --- RIGHT TO ERASURE & DATA EXPORT ---
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
                        text = "DATA OWNERSHIP & ERASURE (GDPR / DPDP)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ResqRedLight,
                        letterSpacing = 1.sp
                    )

                    Button(
                        onClick = { viewModel.clearUserMessage() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ResqBlueBg, contentColor = ResqBlue)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Personal Encrypted Data (JSON)", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { showEraseConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("erase_all_data_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = ResqRed)
                    ) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RIGHT TO ERASURE: WIPE ALL DATA & VAULT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showEraseConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showEraseConfirmDialog = false },
            title = { Text("Confirm Right to Erasure Data Wipe", color = ResqTextPrimary) },
            text = {
                Text(
                    "This action permanently deletes your local Medical ID, emergency contacts, incident logs, and client-side AES encrypted evidence files. This action cannot be undone.",
                    fontSize = 13.sp,
                    color = ResqTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eraseAllData()
                        showEraseConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ResqRed)
                ) {
                    Text("Permanently Wipe All Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEraseConfirmDialog = false }) {
                    Text("Cancel", color = ResqTextSecondary)
                }
            },
            containerColor = ResqCardBg
        )
    }
}
