package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Direct Gemini API Service for RESQ AI Incident Intelligence & First Responder Summaries.
 */
object GeminiAiService {

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateIncidentSummary(
        incidentType: String,
        triggerMethod: String,
        latitude: Double,
        longitude: Double,
        bloodGroup: String,
        allergies: String,
        medications: String,
        medicalNotes: String,
        sensorDataSummary: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val prompt = """
            You are RESQ AI Emergency Dispatch Intelligence Assistant.
            Synthesize a concise, high-priority incident dispatch summary and first responder action guide for a safety emergency.

            INCIDENT METADATA:
            - Type: $incidentType
            - Trigger Method: $triggerMethod
            - Location Coordinates: Lat $latitude, Lng $longitude
            - Sensor Telemetry: $sensorDataSummary

            PATIENT MEDICAL PROFILE:
            - Blood Group: $bloodGroup
            - Known Allergies: $allergies
            - Current Medications: $medications
            - Critical Medical Notes: $medicalNotes

            INSTRUCTIONS:
            Provide a 3-bullet structured emergency report:
            1. INCIDENT SYNOPSIS: Summarize what likely occurred based on sensor metrics and trigger.
            2. CRITICAL MEDICAL ALERTS: Highlight blood type, contraindications, and immediate medical risks.
            3. FIRST RESPONDER PROTOCOL: Recommend top 2 immediate steps for paramedics or nearby verified RESQ volunteers upon arrival.
            Keep the tone clear, objective, and urgent. Do not use fluff.
        """.trimIndent()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateFallbackSummary(
                incidentType, triggerMethod, bloodGroup, allergies, medications, sensorDataSummary
            )
        }

        try {
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseText = response.body?.string() ?: ""

            if (response.isSuccessful && responseText.isNotBlank()) {
                val jsonObj = JSONObject(responseText)
                val candidates = jsonObj.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text", "")
                        if (text.isNotBlank()) {
                            return@withContext text
                        }
                    }
                }
            }
            generateFallbackSummary(incidentType, triggerMethod, bloodGroup, allergies, medications, sensorDataSummary)
        } catch (e: Exception) {
            generateFallbackSummary(incidentType, triggerMethod, bloodGroup, allergies, medications, sensorDataSummary)
        }
    }

    private fun generateFallbackSummary(
        incidentType: String,
        triggerMethod: String,
        bloodGroup: String,
        allergies: String,
        medications: String,
        sensorDataSummary: String
    ): String {
        return """
            • INCIDENT SYNOPSIS: Emergency alert triggered via $triggerMethod ($incidentType). $sensorDataSummary. Location trail logged with GPS precision.
            • CRITICAL MEDICAL ALERTS: Patient Blood Type $bloodGroup. Allergies: $allergies. Current Medications: $medications.
            • FIRST RESPONDER PROTOCOL: Assess airway, breathing, and consciousness upon arrival. Verify patient ID and administer assistance safely.
        """.trimIndent()
    }
}
