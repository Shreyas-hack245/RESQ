package com.example.data.repository

import com.example.data.ai.GeminiAiService
import com.example.data.local.*
import com.example.data.security.SecurityUtils
import kotlinx.coroutines.flow.Flow

class ResqRepository(private val dao: ResqDao) {

    val userProfileFlow: Flow<UserProfileEntity?> = dao.getUserProfileFlow()
    val emergencyContactsFlow: Flow<List<EmergencyContactEntity>> = dao.getAllContactsFlow()
    val allIncidentsFlow: Flow<List<IncidentEntity>> = dao.getAllIncidentsFlow()
    val activeIncidentsFlow: Flow<List<IncidentEntity>> = dao.getActiveIncidentsFlow()
    val safeZonesFlow: Flow<List<SafeZoneEntity>> = dao.getAllSafeZonesFlow()
    val nearbyVolunteersFlow: Flow<List<VolunteerEntity>> = dao.getNearbyVolunteersFlow()
    val evidenceFilesFlow: Flow<List<EvidenceFileEntity>> = dao.getAllEvidenceFilesFlow()
    val consentLogsFlow: Flow<List<ConsentLogEntity>> = dao.getConsentLogsFlow()

    suspend fun getUserProfile(): UserProfileEntity {
        return dao.getUserProfile() ?: UserProfileEntity().also { dao.saveUserProfile(it) }
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        dao.saveUserProfile(profile)
    }

    suspend fun addContact(name: String, phone: String, relationship: String, priority: Int) {
        val contact = EmergencyContactEntity(
            name = name,
            phone = phone,
            relationship = relationship,
            priority = priority
        )
        dao.insertContact(contact)
    }

    suspend fun deleteContact(contact: EmergencyContactEntity) {
        dao.deleteContact(contact)
    }

    suspend fun triggerSos(
        type: String,
        triggerMethod: String,
        isSilent: Boolean,
        latitude: Double = 37.7749,
        longitude: Double = -122.4194,
        sensorSummary: String = "Peak 3.2G acceleration impact detected"
    ): Long {
        val incident = IncidentEntity(
            type = type,
            status = "active",
            triggerMethod = triggerMethod,
            isSilent = isSilent,
            latitude = latitude,
            longitude = longitude,
            address = "Market St & 4th St, San Francisco, CA",
            locationTrailJson = "[{\"lat\": $latitude, \"lng\": $longitude, \"time\": ${System.currentTimeMillis()}}]",
            aiSummary = "Generating AI dispatch analysis...",
            createdAt = System.currentTimeMillis()
        )
        val incidentId = dao.insertIncident(incident)

        // Generate encrypted media evidence automatically
        val dummyEvidenceContent = "RESQ_EVIDENCE_STREAM_INCIDENT_${incidentId}_${System.currentTimeMillis()}".toByteArray()
        val encryptedData = SecurityUtils.encryptAes256(dummyEvidenceContent)
        val sha256Hash = SecurityUtils.calculateSha256(dummyEvidenceContent)

        dao.insertEvidenceFile(
            EvidenceFileEntity(
                incidentId = incidentId,
                fileType = if (isSilent) "audio" else "video",
                fileName = "RESQ_Capture_$incidentId.aes",
                fileUri = "content://resq/vault/$incidentId",
                encryptedSizeBytes = encryptedData.size.toLong(),
                sha256Hash = sha256Hash,
                capturedAt = System.currentTimeMillis()
            )
        )

        // Synthesize AI Summary asynchronously
        val profile = getUserProfile()
        val aiSummaryText = GeminiAiService.generateIncidentSummary(
            incidentType = type,
            triggerMethod = triggerMethod,
            latitude = latitude,
            longitude = longitude,
            bloodGroup = profile.bloodGroup,
            allergies = profile.allergies,
            medications = profile.medications,
            medicalNotes = profile.medicalNotes,
            sensorDataSummary = sensorSummary
        )
        dao.updateIncidentSummary(incidentId, aiSummaryText)

        return incidentId
    }

    suspend fun resolveIncident(incidentId: Long, status: String = "resolved") {
        dao.updateIncidentStatus(incidentId, status, System.currentTimeMillis())
    }

    suspend fun addSafeZone(name: String, lat: Double, lng: Double, radius: Double) {
        dao.insertSafeZone(
            SafeZoneEntity(
                name = name,
                latitude = lat,
                longitude = lng,
                radiusMeters = radius
            )
        )
    }

    suspend fun deleteSafeZone(id: Long) {
        dao.deleteSafeZoneById(id)
    }

    suspend fun logConsentChange(type: String, isGranted: Boolean, remarks: String) {
        dao.insertConsentLog(
            ConsentLogEntity(
                consentType = type,
                isGranted = isGranted,
                remarks = remarks
            )
        )
    }

    suspend fun eraseAllUserData() {
        dao.clearUserProfile()
        dao.clearIncidents()
        dao.clearEvidenceFiles()
        dao.saveUserProfile(UserProfileEntity())
    }
}
