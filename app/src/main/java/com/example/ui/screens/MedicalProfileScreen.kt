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
import com.example.data.local.EmergencyContactEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.ResqViewModel

@Composable
fun MedicalProfileScreen(
    viewModel: ResqViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val contacts by viewModel.contacts.collectAsState()

    var name by remember(userProfile) { mutableStateOf(userProfile.name) }
    var phone by remember(userProfile) { mutableStateOf(userProfile.phone) }
    var bloodGroup by remember(userProfile) { mutableStateOf(userProfile.bloodGroup) }
    var allergies by remember(userProfile) { mutableStateOf(userProfile.allergies) }
    var medications by remember(userProfile) { mutableStateOf(userProfile.medications) }
    var medicalNotes by remember(userProfile) { mutableStateOf(userProfile.medicalNotes) }
    var isOrganDonor by remember(userProfile) { mutableStateOf(userProfile.isOrganDonor) }

    // Add contact dialog state
    var showAddContactDialog by remember { mutableStateOf(false) }
    var newContactName by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }
    var newContactRel by remember { mutableStateOf("Family") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ResqDarkBg)
            .padding(16.dp)
            .testTag("medical_profile_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Emergency Medical ID & Contacts",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ResqTextPrimary
            )
            Text(
                text = "This medical profile is stored locally and securely rendered on the lock-screen for paramedics during emergency dispatch.",
                fontSize = 12.sp,
                color = ResqTextSecondary
            )
        }

        // --- PERSONAL PROFILE FIELDS ---
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
                        text = "PATIENT IDENTITY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ResqRedLight,
                        letterSpacing = 1.sp
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = ResqDarkBg)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = ResqDarkBg)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = { bloodGroup = it },
                            label = { Text("Blood Group (e.g. O+)") },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(focusedContainerColor = ResqDarkBg)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Organ Donor Status", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ResqTextPrimary)
                            Text(text = "Display donor badge on Lock Screen Medical ID", fontSize = 11.sp, color = ResqTextSecondary)
                        }
                        Switch(
                            checked = isOrganDonor,
                            onCheckedChange = { isOrganDonor = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ResqEmerald)
                        )
                    }
                }
            }
        }

        // --- MEDICAL DETAILS FIELDS ---
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
                        text = "CRITICAL MEDICAL DATA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ResqOrangeLight,
                        letterSpacing = 1.sp
                    )

                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        label = { Text("Known Allergies (e.g. Penicillin, Peanuts)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = ResqDarkBg)
                    )

                    OutlinedTextField(
                        value = medications,
                        onValueChange = { medications = it },
                        label = { Text("Current Medications") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = ResqDarkBg)
                    )

                    OutlinedTextField(
                        value = medicalNotes,
                        onValueChange = { medicalNotes = it },
                        label = { Text("First Responder Notes / Conditions") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = TextFieldDefaults.colors(focusedContainerColor = ResqDarkBg)
                    )

                    Button(
                        onClick = {
                            viewModel.updateProfile(
                                name = name,
                                phone = phone,
                                bloodGroup = bloodGroup,
                                allergies = allergies,
                                medications = medications,
                                medicalNotes = medicalNotes,
                                isOrganDonor = isOrganDonor,
                                silentSosEnabled = userProfile.silentSosEnabled,
                                shakeEnabled = userProfile.shakeTriggerEnabled,
                                volumeEnabled = userProfile.volumeTriggerEnabled
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_medical_profile_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = ResqRed)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "SAVE MEDICAL PROFILE", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- EMERGENCY CONTACTS MANAGEMENT ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trusted Emergency Contacts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResqTextPrimary
                )
                IconButton(onClick = { showAddContactDialog = true }) {
                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add Contact", tint = ResqEmeraldLight)
                }
            }
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
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ResqRed.copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 10.dp)
                        ) {
                            Text(
                                text = "#${contact.priority}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResqRedLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Column {
                            Text(text = contact.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ResqTextPrimary)
                            Text(text = "${contact.relationship} • ${contact.phone}", fontSize = 11.sp, color = ResqTextSecondary)
                        }
                    }

                    IconButton(onClick = { viewModel.deleteEmergencyContact(contact) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Contact", tint = ResqRedLight)
                    }
                }
            }
        }
    }

    // --- ADD CONTACT DIALOG ---
    if (showAddContactDialog) {
        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = { Text("Add Emergency Contact", color = ResqTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newContactName,
                        onValueChange = { newContactName = it },
                        label = { Text("Contact Name") }
                    )
                    OutlinedTextField(
                        value = newContactPhone,
                        onValueChange = { newContactPhone = it },
                        label = { Text("Phone Number") }
                    )
                    OutlinedTextField(
                        value = newContactRel,
                        onValueChange = { newContactRel = it },
                        label = { Text("Relationship (e.g. Spouse, Physician)") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newContactName.isNotBlank() && newContactPhone.isNotBlank()) {
                            viewModel.addEmergencyContact(
                                name = newContactName,
                                phone = newContactPhone,
                                relationship = newContactRel,
                                priority = contacts.size + 1
                            )
                            newContactName = ""
                            newContactPhone = ""
                            showAddContactDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ResqEmerald)
                ) {
                    Text("Add Contact")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) {
                    Text("Cancel", color = ResqTextSecondary)
                }
            },
            containerColor = ResqCardBg
        )
    }
}
