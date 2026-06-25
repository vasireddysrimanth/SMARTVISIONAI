# Vision AI 📷🤖

A modern Android Computer Vision application built with **Jetpack Compose**, **CameraX**, **TensorFlow Lite**, and **MediaPipe**.

Vision AI provides a real-time camera experience with multiple AI-powered detection modes that can be switched instantly without restarting the camera.

## ✨ Features

### 🎯 Object Detection

* Real-time object recognition using TensorFlow Lite
* Detects common objects such as:

  * Person
  * Mobile Phone
  * Laptop
  * Bottle
  * Chair
  * Cup
  * And many more
* Bounding box visualization
* Confidence score display
* Optimized for on-device inference

### 🧍 Pose Detection

* Real-time human pose estimation using MediaPipe
* Tracks body landmarks
* Draws skeletal overlays
* Detects:

  * Head
  * Shoulders
  * Elbows
  * Wrists
  * Hips
  * Knees
  * Ankles
* Smooth landmark rendering

### ⚡ Seamless Mode Switching

* Switch between Object Detection and Pose Detection
* Single CameraX session
* No camera flickering
* Instant AI mode transitions

### 📊 Performance Monitoring

* Real-time FPS counter
* Optimized frame processing
* Efficient camera pipeline

### 🎨 Modern UI

* Built entirely with Jetpack Compose
* Material Design 3
* Responsive layouts
* Smooth animations

---

## 🏗 Architecture

The application follows a clean and modular structure:

```text
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

## 🛠 Tech Stack

### Android

* Kotlin
* Jetpack Compose
* Navigation Compose
* AndroidX

### Camera

* CameraX
* Image Analysis
* Preview Use Cases

### Artificial Intelligence

* TensorFlow Lite
* MediaPipe Tasks API
* On-device Machine Learning
* Real-Time Computer Vision

### Performance

* Kotlin Coroutines
* Background Processing
* FPS Tracking

### Storage

* SharedPreferences / DataStore
* User Preferences

---

## 🔥 AI Pipeline

### Object Detection Flow

Camera Frame
→ CameraX ImageAnalysis
→ TensorFlow Lite Model
→ Object Detection Results
→ Bounding Box Rendering
→ UI Overlay

### Pose Detection Flow

Camera Frame
→ CameraX ImageAnalysis
→ MediaPipe Pose Landmarker
→ Landmark Detection
→ Skeleton Rendering
→ UI Overlay

---

## 📱 Screens

### Splash Screen

Application branding and initialization.

### Onboarding Screen

Introduction to AI detection modes.

### Camera Screen

Main screen containing:

* Camera Preview
* Detection Mode Switcher
* Object Detection Overlay
* Pose Detection Overlay
* FPS Counter

---

## 🚀 Learning Outcomes

This project demonstrates:

* CameraX Integration
* Real-Time Frame Processing
* TensorFlow Lite Deployment
* MediaPipe Integration
* Computer Vision Fundamentals
* Jetpack Compose Development
* State Management
* Android Performance Optimization
* AI-Powered Mobile Applications

---

## 🎯 Future Enhancements

* Face Detection
* Face Mesh
* Gesture Recognition
* Object Tracking
* Segmentation
* Custom TensorFlow Lite Models
* GPU Delegate Support
* ML Kit Integration

---

## 👨‍💻 Developer

**Srimanth Vasireddy**

Android Developer passionate about building AI-powered mobile applications using modern Android technologies and on-device Machine Learning.

### Built With

❤️ Kotlin
🤖 TensorFlow Lite
🧍 MediaPipe
📷 CameraX
🎨 Jetpack Compose

"Turning Cameras into Intelligence."
