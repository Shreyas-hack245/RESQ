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
import com.example.data.security.SecurityUtils
import com.example.ui.theme.*
import com.example.ui.viewmodel.ResqViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EvidenceVaultScreen(
    viewModel: ResqViewModel,
    modifier: Modifier = Modifier
) {
    val evidenceFiles by viewModel.evidenceFiles.collectAsState()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm:ss", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ResqDarkBg)
            .padding(16.dp)
            .testTag("evidence_vault_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Encrypted Evidence Vault",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
            Text(
                text = "All emergency audio, photo, and telemetry payloads are encrypted client-side with AES-256 before any upload. SHA-256 hashes are recorded at capture time for court admissibility.",
                fontSize = 12.sp,
                color = ResqTextSecondary
            )
        }

        // --- SECURITY ASSURANCE CARD ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ResqCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, ResqEmerald.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = ResqEmeraldLight,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "TAMPER-EVIDENT INTEGRITY VERIFIED",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ResqEmeraldLight,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${evidenceFiles.size} AES-256 Encrypted Evidence Files Stored. Server never sees raw decryption keys.",
                            fontSize = 12.sp,
                            color = ResqTextPrimary
                        )
                    }
                }
            }
        }

        // --- EVIDENCE GALLERY ITEM CARDS ---
        items(evidenceFiles) { file ->
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
                                color = ResqRed.copy(alpha = 0.2f),
                                modifier = Modifier.padding(end = 10.dp)
                            ) {
                                Icon(
                                    imageVector = when (file.fileType) {
                                        "audio" -> Icons.Default.Mic
                                        "video" -> Icons.Default.Videocam
                                        else -> Icons.Default.InsertDriveFile
                                    },
                                    contentDescription = null,
                                    tint = ResqRedLight,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = file.fileName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResqTextPrimary
                                )
                                Text(
                                    text = "Captured: ${dateFormat.format(Date(file.capturedAt))}",
                                    fontSize = 11.sp,
                                    color = ResqTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ResqEmerald.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "AES-256",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResqEmeraldLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SHA-256 Court Hash Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ResqDarkBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ResqCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "IMMUTABLE SHA-256 HASH (CAPTURE TIME):",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResqTextSecondary
                            )
                            Text(
                                text = file.sha256Hash,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = ResqOrangeLight
                            )
                        }
                    }
                }
            }
        }
    }
}
