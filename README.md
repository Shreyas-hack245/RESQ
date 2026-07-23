# 🛡️ RESQ — Personal Safety & Rapid Emergency Response System

> **RESQ** is an intelligent Android-based personal safety application designed to provide immediate emergency assistance through AI-powered detection, real-time location sharing, encrypted evidence storage, and rapid SOS communication. Built with modern Android technologies, RESQ aims to reduce emergency response time and help users stay safe in critical situations.

---

## 📱 Overview

RESQ combines emergency communication, intelligent accident detection, secure evidence collection, and community assistance into a single platform.

Whether the user is involved in an accident, faces a medical emergency, or encounters a dangerous situation, RESQ enables rapid communication with emergency contacts, nearby volunteers, and emergency responders while preserving critical evidence securely on the device.

---

# ✨ Features

### 🚨 One-Tap SOS

- Instantly send SOS alerts
- Live GPS location sharing
- Emergency SMS dispatch
- Emergency contact notifications
- Configurable countdown before sending

---

### 🤖 AI Emergency Detection

Automatically detects emergencies using on-device sensors.

Supports:

- Fall Detection
- Vehicle Crash Detection
- Prolonged Immobility Detection
- Voice Panic Recognition
- High-impact motion analysis

Powered by:

- Accelerometer
- Gyroscope
- Device motion sensors
- Audio processing

---

### 📍 Live GPS Tracking

- Real-time location updates
- Continuous tracking during emergencies
- Share location with trusted contacts
- Navigation support for responders

---

### 🔐 Encrypted Evidence Vault

Store emergency evidence securely using strong encryption.

Supports:

- Photos
- Audio recordings
- Sensor logs
- Incident notes

Security:

- AES-256 Encryption
- SHA-256 Integrity Verification
- Local encrypted storage

---

### 🏥 Medical ID

Accessible directly from the lock screen.

Contains:

- Blood Group
- Allergies
- Current Medications
- Emergency Contacts
- Organ Donor Status
- Medical Conditions

Works completely offline.

---

### 🗺️ Safe Zones & Geofencing

Create trusted locations such as:

- Home
- Office
- College
- School

Automatically alerts trusted contacts if the user exits a defined safe zone unexpectedly.

---

### 👥 Community Volunteer Network

Locate verified nearby volunteers who can provide immediate assistance.

Features:

- Nearby responders
- CPR-certified volunteers
- Distance estimation
- Rapid assistance requests

---

### 🖥️ Emergency Command Center

Operator dashboard for emergency monitoring.

Includes:

- Active incident tracking
- Live locations
- User information
- Incident timeline
- Audit logs
- Response history

---

### 🔒 Privacy First

RESQ is designed with privacy as a core principle.

Features include:

- Zero-Knowledge Privacy
- Local Data Encryption
- Consent Management
- Secure Data Deletion
- Minimal Data Collection

---

# 🚀 Getting Started

## Prerequisites

- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17+
- Android SDK 34
- Android Device or Emulator (API 26+)

---

## Installation

Clone the repository.

```bash
git clone https://github.com/yourusername/RESQ.git
```

Navigate to the project.

```bash
cd RESQ
```

Open the project in Android Studio.

Allow Gradle to sync automatically.

Run the application.

---

# 🔨 Build

Generate Debug APK

```bash
./gradlew assembleDebug
```

Install on connected device

```bash
./gradlew installDebug
```

APK Output

```
app/build/outputs/apk/debug/app-debug.apk
```

---

# 🧪 Testing

Run Unit Tests

```bash
./gradlew testDebugUnitTest
```

Run Instrumented Tests

```bash
./gradlew connectedDebugAndroidTest
```

---

# 🔐 Permissions Used

RESQ requires the following Android permissions:

- Location (Foreground & Background)
- SMS
- Phone State
- Notifications
- Camera
- Microphone
- Internet
- Foreground Service
- Activity Recognition
- Vibration

Permissions are requested only when required.

---

# 🔒 Security

Security measures implemented include:

- AES-256 File Encryption
- SHA-256 Integrity Hashing
- Secure Local Storage
- Offline Medical Records
- Zero-Knowledge Privacy Model
- No Hardcoded API Keys
- BuildConfig Secret Injection

---

# ⚙️ Environment Variables

Create a `.env` file.

Example:

```env
API_BASE_URL=
MAPS_API_KEY=
SMS_GATEWAY_KEY=
ENCRYPTION_KEY=
```

Never commit sensitive credentials.

---

# 📈 Future Roadmap

- Wear OS Support
- Smartwatch SOS Trigger
- Satellite Emergency Messaging
- AI Threat Prediction
- Offline Mesh Communication
- Drone Integration
- Emergency Video Streaming
- Multi-language Support
- Family Safety Dashboard
- Government Emergency API Integration

---


---

# 📄 License

This project is currently released under a custom license.

All source code, documentation, and design assets remain the property of the project author unless otherwise stated.

For commercial use or collaboration, please contact the author.

---


# ❤️ Vision

> **Every second matters.**

RESQ is built with a single mission:

**To make emergency response faster, smarter, and more accessible through modern technology.**

---

# 👨‍💻 Author

**Shreyas Bhat**

---
