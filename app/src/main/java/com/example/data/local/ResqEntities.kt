package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "RESQ Citizen",
    val phone: String = "+1 (555) 928-1928",
    val dob: String = "1994-08-14",
    val bloodGroup: String = "O+",
    val allergies: String = "Penicillin, Peanuts",
    val medications: String = "Asthma Inhaler (Albuterol)",
    val medicalNotes: String = "Mild asthma, contact lens wearer. Organ donor.",
    val isOrganDonor: Boolean = true,
    val isVolunteer: Boolean = false,
    val volunteerVerified: Boolean = true,
    val trustScore: Double = 4.9,
    val silentSosEnabled: Boolean = false,
    val shakeTriggerEnabled: Boolean = true,
    val volumeTriggerEnabled: Boolean = true,
    val voiceTriggerEnabled: Boolean = true,
    val autoRecordEvidence: Boolean = true,
    val consentGiven: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val relationship: String,
    val priority: Int = 1,
    val isSmsEnabled: Boolean = true,
    val isCallEnabled: Boolean = true
)

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // 'sos', 'fall', 'crash', 'inactivity', 'panic'
    val status: String, // 'active', 'resolved', 'false_alarm'
    val triggerMethod: String, // 'manual', 'shake', 'voice', 'power_button', 'ai_detected'
    val isSilent: Boolean = false,
    val latitude: Double,
    val longitude: Double,
    val address: String = "101 Safety Way, San Francisco, CA",
    val locationTrailJson: String = "[]",
    val aiSummary: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null,
    val isSyncedOffline: Boolean = true
)

@Entity(tableName = "evidence_files")
data class EvidenceFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val incidentId: Long,
    val fileType: String, // 'photo', 'video', 'audio', 'sensor_log'
    val fileName: String,
    val fileUri: String = "",
    val encryptedSizeBytes: Long,
    val sha256Hash: String,
    val capturedAt: Long = System.currentTimeMillis(),
    val isUploaded: Boolean = false
)

@Entity(tableName = "safe_zones")
data class SafeZoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Double = 500.0,
    val alertOnExit: Boolean = true,
    val alertOnEnter: Boolean = false,
    val isActive: Boolean = true
)

@Entity(tableName = "volunteers")
data class VolunteerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val distanceKm: Double,
    val trustScore: Double = 4.8,
    val isVerified: Boolean = true,
    val certification: String = "Certified First Responder (CPR/AED)",
    val phone: String,
    val isAvailable: Boolean = true
)

@Entity(tableName = "consent_logs")
data class ConsentLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val consentType: String, // 'location_tracking', 'medical_data', 'volunteer_visibility', 'camera_mic_access'
    val isGranted: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val remarks: String = "User confirmed via security panel"
)
