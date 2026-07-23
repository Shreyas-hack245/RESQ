package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.repository.ResqRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SosCountdownState(
    val isCountingDown: Boolean = false,
    val secondsRemaining: Int = 5,
    val progress: Float = 1.0f,
    val triggerMethod: String = "manual",
    val isSilent: Boolean = false
)

data class FakeCallState(
    val isRinging: Boolean = false,
    val callerName: String = "Mom",
    val callerNumber: String = "+1 (555) 019-2831",
    val isCallConnected: Boolean = false,
    val secondsConnected: Int = 0
)

class ResqViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ResqDatabase.getDatabase(application)
    val repository = ResqRepository(db.resqDao())

    val userProfile: StateFlow<UserProfileEntity> = repository.userProfileFlow
        .map { it ?: UserProfileEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfileEntity())

    val contacts: StateFlow<List<EmergencyContactEntity>> = repository.emergencyContactsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incidents: StateFlow<List<IncidentEntity>> = repository.allIncidentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeIncidents: StateFlow<List<IncidentEntity>> = repository.activeIncidentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val safeZones: StateFlow<List<SafeZoneEntity>> = repository.safeZonesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val volunteers: StateFlow<List<VolunteerEntity>> = repository.nearbyVolunteersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val evidenceFiles: StateFlow<List<EvidenceFileEntity>> = repository.evidenceFilesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val consentLogs: StateFlow<List<ConsentLogEntity>> = repository.consentLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI States
    private val _sosCountdownState = MutableStateFlow(SosCountdownState())
    val sosCountdownState: StateFlow<SosCountdownState> = _sosCountdownState.asStateFlow()

    private val _activeIncidentId = MutableStateFlow<Long?>(null)
    val activeIncidentId: StateFlow<Long?> = _activeIncidentId.asStateFlow()

    private val _fakeCallState = MutableStateFlow(FakeCallState())
    val fakeCallState: StateFlow<FakeCallState> = _fakeCallState.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private var countdownJob: Job? = null
    private var fakeCallTimerJob: Job? = null

    fun clearUserMessage() {
        _userMessage.value = null
    }

    // --- SOS TRIGGER FLOW ---
    fun startSosCountdown(triggerMethod: String = "manual", isSilentOverride: Boolean? = null) {
        if (_sosCountdownState.value.isCountingDown || _activeIncidentId.value != null) return

        val isSilentMode = isSilentOverride ?: userProfile.value.silentSosEnabled
        _sosCountdownState.value = SosCountdownState(
            isCountingDown = true,
            secondsRemaining = 5,
            progress = 1.0f,
            triggerMethod = triggerMethod,
            isSilent = isSilentMode
        )

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            val totalTicks = 50 // 50 * 100ms = 5s
            for (i in totalTicks downTo 0) {
                val sec = (i + 9) / 10
                val prog = i.toFloat() / totalTicks
                _sosCountdownState.value = _sosCountdownState.value.copy(
                    secondsRemaining = sec,
                    progress = prog
                )
                if (i == 0) {
                    dispatchSos(triggerMethod, isSilentMode)
                } else {
                    delay(100)
                }
            }
        }
    }

    fun cancelSosCountdown() {
        countdownJob?.cancel()
        _sosCountdownState.value = SosCountdownState()
        _userMessage.value = "SOS Countdown Canceled safely."
    }

    private fun dispatchSos(triggerMethod: String, isSilent: Boolean) {
        _sosCountdownState.value = SosCountdownState()
        viewModelScope.launch {
            val id = repository.triggerSos(
                type = "sos",
                triggerMethod = triggerMethod,
                isSilent = isSilent,
                latitude = 37.7749,
                longitude = -122.4194,
                sensorSummary = "Manual / Multi-trigger alert dispatched"
            )
            _activeIncidentId.value = id
            _userMessage.value = "EMERGENCY BROADCAST ACTIVE! SMS & Contacts notified."
        }
    }

    fun resolveActiveIncident(status: String = "resolved") {
        val currentId = _activeIncidentId.value ?: return
        viewModelScope.launch {
            repository.resolveIncident(currentId, status)
            _activeIncidentId.value = null
            _userMessage.value = if (status == "resolved") "Incident marked resolved. Contacts notified you are safe." else "Incident marked false alarm."
        }
    }

    // --- FAKE CALL GENERATOR ---
    fun scheduleFakeCall(delaySeconds: Int, callerName: String = "Mom") {
        viewModelScope.launch {
            _userMessage.value = "Fake incoming call scheduled in ${delaySeconds}s..."
            delay(delaySeconds * 1000L)
            _fakeCallState.value = FakeCallState(
                isRinging = true,
                callerName = callerName
            )
        }
    }

    fun answerFakeCall() {
        _fakeCallState.value = _fakeCallState.value.copy(
            isRinging = false,
            isCallConnected = true
        )
        fakeCallTimerJob?.cancel()
        fakeCallTimerJob = viewModelScope.launch {
            var secs = 0
            while (true) {
                delay(1000)
                secs++
                _fakeCallState.value = _fakeCallState.value.copy(secondsConnected = secs)
            }
        }
    }

    fun endFakeCall() {
        fakeCallTimerJob?.cancel()
        _fakeCallState.value = FakeCallState()
    }

    // --- PROFILE & CONTACTS ---
    fun updateProfile(
        name: String,
        phone: String,
        bloodGroup: String,
        allergies: String,
        medications: String,
        medicalNotes: String,
        isOrganDonor: Boolean,
        silentSosEnabled: Boolean,
        shakeEnabled: Boolean,
        volumeEnabled: Boolean
    ) {
        viewModelScope.launch {
            val current = userProfile.value
            repository.saveUserProfile(
                current.copy(
                    name = name,
                    phone = phone,
                    bloodGroup = bloodGroup,
                    allergies = allergies,
                    medications = medications,
                    medicalNotes = medicalNotes,
                    isOrganDonor = isOrganDonor,
                    silentSosEnabled = silentSosEnabled,
                    shakeTriggerEnabled = shakeEnabled,
                    volumeTriggerEnabled = volumeEnabled,
                    updatedAt = System.currentTimeMillis()
                )
            )
            _userMessage.value = "Medical Profile updated successfully."
        }
    }

    fun addEmergencyContact(name: String, phone: String, relationship: String, priority: Int) {
        viewModelScope.launch {
            repository.addContact(name, phone, relationship, priority)
            _userMessage.value = "Emergency Contact added."
        }
    }

    fun deleteEmergencyContact(contact: EmergencyContactEntity) {
        viewModelScope.launch {
            repository.deleteContact(contact)
            _userMessage.value = "Contact removed."
        }
    }

    // --- SAFE ZONES ---
    fun addSafeZone(name: String, lat: Double, lng: Double, radius: Double) {
        viewModelScope.launch {
            repository.addSafeZone(name, lat, lng, radius)
            _userMessage.value = "Safe Zone '$name' created."
        }
    }

    fun deleteSafeZone(id: Long) {
        viewModelScope.launch {
            repository.deleteSafeZone(id)
            _userMessage.value = "Safe Zone deleted."
        }
    }

    // --- AI SENSOR TRIGGER SIMULATION ---
    fun simulateFallDetected() {
        viewModelScope.launch {
            _userMessage.value = "AI FALL DETECTED! 3.8G impact + 10s immobility."
            startSosCountdown(triggerMethod = "ai_detected")
        }
    }

    fun simulateCrashDetected() {
        viewModelScope.launch {
            _userMessage.value = "AI VEHICLE CRASH DETECTED! 6.5G deceleration spike."
            startSosCountdown(triggerMethod = "ai_detected")
        }
    }

    fun simulatePanicVoiceTrigger() {
        viewModelScope.launch {
            _userMessage.value = "VOICE PANIC DETECTED: 'Help me RESQ'."
            startSosCountdown(triggerMethod = "voice")
        }
    }

    // --- PRIVACY & ERASE ---
    fun eraseAllData() {
        viewModelScope.launch {
            repository.eraseAllUserData()
            _userMessage.value = "All personal data & evidence vault erased."
        }
    }
}
