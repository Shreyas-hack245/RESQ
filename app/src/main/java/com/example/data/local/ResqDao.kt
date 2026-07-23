package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ResqDao {

    // --- USER PROFILE ---
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    // --- EMERGENCY CONTACTS ---
    @Query("SELECT * FROM emergency_contacts ORDER BY priority ASC")
    fun getAllContactsFlow(): Flow<List<EmergencyContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContactEntity): Long

    @Delete
    suspend fun deleteContact(contact: EmergencyContactEntity)

    @Query("DELETE FROM emergency_contacts WHERE id = :id")
    suspend fun deleteContactById(id: Long)

    // --- INCIDENTS ---
    @Query("SELECT * FROM incidents ORDER BY createdAt DESC")
    fun getAllIncidentsFlow(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE status = 'active' ORDER BY createdAt DESC")
    fun getActiveIncidentsFlow(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE id = :id LIMIT 1")
    suspend fun getIncidentById(id: Long): IncidentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity): Long

    @Query("UPDATE incidents SET status = :status, resolvedAt = :resolvedAt WHERE id = :id")
    suspend fun updateIncidentStatus(id: Long, status: String, resolvedAt: Long? = System.currentTimeMillis())

    @Query("UPDATE incidents SET aiSummary = :summary WHERE id = :id")
    suspend fun updateIncidentSummary(id: Long, summary: String)

    // --- EVIDENCE FILES ---
    @Query("SELECT * FROM evidence_files WHERE incidentId = :incidentId ORDER BY capturedAt DESC")
    fun getEvidenceForIncidentFlow(incidentId: Long): Flow<List<EvidenceFileEntity>>

    @Query("SELECT * FROM evidence_files ORDER BY capturedAt DESC")
    fun getAllEvidenceFilesFlow(): Flow<List<EvidenceFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidenceFile(evidence: EvidenceFileEntity): Long

    // --- SAFE ZONES ---
    @Query("SELECT * FROM safe_zones ORDER BY id DESC")
    fun getAllSafeZonesFlow(): Flow<List<SafeZoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSafeZone(safeZone: SafeZoneEntity): Long

    @Query("DELETE FROM safe_zones WHERE id = :id")
    suspend fun deleteSafeZoneById(id: Long)

    // --- VOLUNTEERS ---
    @Query("SELECT * FROM volunteers ORDER BY distanceKm ASC")
    fun getNearbyVolunteersFlow(): Flow<List<VolunteerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteer(volunteer: VolunteerEntity)

    // --- CONSENT LOGS ---
    @Query("SELECT * FROM consent_logs ORDER BY timestamp DESC")
    fun getConsentLogsFlow(): Flow<List<ConsentLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsentLog(log: ConsentLogEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile()

    @Query("DELETE FROM incidents")
    suspend fun clearIncidents()

    @Query("DELETE FROM evidence_files")
    suspend fun clearEvidenceFiles()
}
