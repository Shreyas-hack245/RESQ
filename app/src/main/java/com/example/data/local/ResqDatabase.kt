package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        EmergencyContactEntity::class,
        IncidentEntity::class,
        EvidenceFileEntity::class,
        SafeZoneEntity::class,
        VolunteerEntity::class,
        ConsentLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ResqDatabase : RoomDatabase() {

    abstract fun resqDao(): ResqDao

    companion object {
        @Volatile
        private var INSTANCE: ResqDatabase? = null

        fun getDatabase(context: Context): ResqDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ResqDatabase::class.java,
                    "resq_database.db"
                )
                .addCallback(ResqDatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ResqDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database.resqDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: ResqDao) {
            // Initial Profile
            dao.saveUserProfile(UserProfileEntity())

            // Default Emergency Contacts
            dao.insertContact(
                EmergencyContactEntity(
                    name = "Sarah (Sister)",
                    phone = "+1 (555) 019-2831",
                    relationship = "Immediate Family",
                    priority = 1,
                    isSmsEnabled = true,
                    isCallEnabled = true
                )
            )
            dao.insertContact(
                EmergencyContactEntity(
                    name = "Dr. Marcus Vance (Primary)",
                    phone = "+1 (555) 304-9122",
                    relationship = "Physician",
                    priority = 2,
                    isSmsEnabled = true,
                    isCallEnabled = true
                )
            )

            // Default Safe Zones
            dao.insertSafeZone(
                SafeZoneEntity(
                    name = "Home Residence",
                    latitude = 37.7749,
                    longitude = -122.4194,
                    radiusMeters = 300.0,
                    alertOnExit = true,
                    alertOnEnter = false
                )
            )
            dao.insertSafeZone(
                SafeZoneEntity(
                    name = "University Campus",
                    latitude = 37.7833,
                    longitude = -122.4167,
                    radiusMeters = 800.0,
                    alertOnExit = true,
                    alertOnEnter = true
                )
            )

            // Default Volunteers
            dao.insertVolunteer(
                VolunteerEntity(
                    name = "Officer David Chen",
                    distanceKm = 0.4,
                    trustScore = 4.95,
                    isVerified = true,
                    certification = "Off-duty EMT & First Aid",
                    phone = "+1 (555) 771-0021"
                )
            )
            dao.insertVolunteer(
                VolunteerEntity(
                    name = "Elena Rostova",
                    distanceKm = 0.9,
                    trustScore = 4.88,
                    isVerified = true,
                    certification = "Certified CPR & Basic Life Support",
                    phone = "+1 (555) 882-9912"
                )
            )
            dao.insertVolunteer(
                VolunteerEntity(
                    name = "Dr. Samuel Jackson",
                    distanceKm = 1.2,
                    trustScore = 5.0,
                    isVerified = true,
                    certification = "Emergency Physician",
                    phone = "+1 (555) 993-4411"
                )
            )

            // Initial Past Incident
            val pastIncidentId = dao.insertIncident(
                IncidentEntity(
                    type = "fall",
                    status = "resolved",
                    triggerMethod = "ai_detected",
                    isSilent = false,
                    latitude = 37.7752,
                    longitude = -122.4188,
                    address = "742 Market Street, San Francisco, CA",
                    aiSummary = "Fall Detected: Sudden impact recorded at 2.8G followed by 12s of total immobility. Emergency contacts were notified automatically. User canceled countdown safely after 15s.",
                    createdAt = System.currentTimeMillis() - 86400000L * 2,
                    resolvedAt = System.currentTimeMillis() - 86400000L * 2 + 180000L
                )
            )

            // Evidence sample for past incident
            val sampleBytes = "ENCRYPTED_RESQ_EVIDENCE_PAYLOAD_SAMPLE".toByteArray()
            val hash = com.example.data.security.SecurityUtils.calculateSha256(sampleBytes)
            dao.insertEvidenceFile(
                EvidenceFileEntity(
                    incidentId = pastIncidentId,
                    fileType = "audio",
                    fileName = "Audio_Record_001.aes",
                    fileUri = "content://resq/evidence/001",
                    encryptedSizeBytes = 245120L,
                    sha256Hash = hash,
                    capturedAt = System.currentTimeMillis() - 86400000L * 2,
                    isUploaded = true
                )
            )

            // Default Consent Logs
            dao.insertConsentLog(
                ConsentLogEntity(
                    consentType = "location_tracking",
                    isGranted = true,
                    remarks = "Explicit user permission for background GPS during active SOS"
                )
            )
            dao.insertConsentLog(
                ConsentLogEntity(
                    consentType = "medical_data",
                    isGranted = true,
                    remarks = "Medical ID storage authorized for paramedic emergency access"
                )
            )
        }
    }
}
