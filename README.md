# 👁️ VisionAI — Real-Time AI Camera for Android

> Turning cameras into intelligence. On-device. In real time.

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Language](https://img.shields.io/badge/Language-Kotlin-blue)
![ML](https://img.shields.io/badge/ML-TFLite%20%7C%20MediaPipe-orange)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-purple)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

A real-time Android computer vision application powered by **TensorFlow Lite** and **MediaPipe** — running two AI models simultaneously, entirely on-device, zero cloud dependency.

---

## 📱 Demo

![VisionAI Demo](assets/demo.png)

---

## ✨ Features

### 📦 Object Detection (TFLite + EfficientDet)
- Detects everyday objects — laptops, bottles, phones, people, chairs
- Live bounding boxes with label + confidence score
- GPU Delegate with automatic CPU fallback
- Adjustable confidence threshold via real-time slider

### 🧍 Pose Detection (MediaPipe)
- 33 body landmarks tracked in real time
- Full skeletal overlay with color-coded visualization
  - 🟡 Face landmarks
  - 🟢 Left side
  - 🔴 Right side
- Powered by MediaPipe LIVE_STREAM mode

### ⚡ Seamless Mode Switching
- Switch between Object Detection and Pose Detection instantly
- Camera never restarts — no flicker, no interruption
- Single CameraX session drives both models

### 🎛️ Additional
- Live FPS counter
- Confidence threshold slider (persisted via DataStore)
- One-time onboarding screen
- Splash screen with fade animation
- Camera permission handling

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Camera | CameraX (ImageAnalysis) |
| Object Detection | TensorFlow Lite Task Library |
| Pose Detection | MediaPipe Tasks API |
| GPU Acceleration | TFLite GPU Delegate |
| Persistence | DataStore Preferences |
| Async | Kotlin Coroutines |
| Overlay Rendering | Android Canvas (Compose) |

---

## 🏗 Architecture

```
CameraX (ImageAnalysis)
        ↓
   currentMode check
    ↙           ↘
TFLite          MediaPipe
ObjectDetector  PoseLandmarker
    ↓                ↓
ObjectOverlay   PoseOverlay
(Canvas)        (Canvas)
    ↘           ↙
   Jetpack Compose UI
   (Reactive State)
```

---

## 📂 Project Structure

```
com.devsrimanth.visionAi/
├── MainActivity.kt
├── AppNavigation.kt
├── screens/
│   ├── SplashScreen.kt
│   ├── OnboardingScreen.kt
│   └── CameraScreen.kt
├── components/
│   └── CameraPreview.kt
├── detections/
│   └── DetectionMode.kt
├── helpers/
│   ├── ObjectDetectorHelper.kt
│   ├── ObjectOverlay.kt
│   ├── PoseDetectorHelper.kt
│   └── PoseOverlay.kt
├── utils/
│   ├── CameraUtils.kt
│   └── UserPreferences.kt
└── fps/
    └── FpsCounter.kt
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android device with API 24+ (minSdk 24)
- Physical device recommended (emulator won't give real FPS)

### Setup

**1. Clone the repo**
```bash
git clone [https://github.com/vasireddy-srimanth/VisionAI.git](https://github.com/vasireddysrimanth/SMARTVISIONAI)
cd VisionAI
```

**2. Add model files to `app/src/main/assets/`**

| Model | Download |
|---|---|
| `model.tflite` (EfficientDet-Lite) | [Download](https://storage.googleapis.com/download.tensorflow.org/models/tflite/task_library/object_detection/android/lite-model_efficientdet_lite0_detection_metadata_1.tflite) |
| `pose_landmarker.task` (MediaPipe) | [Download](https://storage.googleapis.com/mediapipe-models/pose_landmarker/pose_landmarker_lite/float16/1/pose_landmarker_lite.task) |

**3. Build and run**
```bash
./gradlew assembleDebug
```
Or press **▶ Run** in Android Studio.

---

## 📊 Performance

| Mode | CPU FPS | GPU FPS |
|---|---|---|
| Object Detection | ~12 FPS | ~25 FPS |
| Pose Detection | ~10 FPS | ~22 FPS |

> *Tested on a mid-range Android device. Results vary by hardware.*

---

## 🔑 Key Engineering Challenges

| Challenge | Solution |
|---|---|
| YUV → Bitmap conversion | Custom `imageProxyToBitmap()` via NV21 |
| Rotation correction | `imageInfo.rotationDegrees` + Matrix |
| Mode switching without restart | `currentMode` lambda read per-frame |
| Async result handling | MediaPipe `LIVE_STREAM` + callback state |
| Overlay coordinate scaling | Normalize model coords → canvas size |
| GPU unavailability | Try GPU → catch → CPU fallback |
| Persistent threshold | DataStore Preferences |

---

## 📄 License

```
MIT License — feel free to use, modify, and distribute.
```

---

## 👨‍💻 Author

**Srimanth Vasireddy**
Android Engineer · On-Device AI · Jetpack Compose · KMP

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue)](https://linkedin.com/in/vasireddy-srimanth)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-black)]([https://github.com/vasireddysrimanth](https://github.com/vasireddysrimanth))

---

> *"Building reliable real-time AI is as much about engineering as it is about machine learning."*
