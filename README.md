# FastDWM — Native Windows Timing & Composition [v0.1.0]

**Low-latency access to the Windows Desktop Window Manager (DWM). High-precision multimedia timers and VSync synchronization for the FastJava ecosystem.**

[![Status](https://img.shields.io/badge/status-v0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastDWM/releases/tag/v0.1.0)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io)

---

## Table of Contents
- [Features](#features)
- [Quick Start](#quick-start)
- [Installation](#installation)
- [Running the Demo](#running-the-demo)
- [Build from Source](#build-from-source)
- [Roadmap](#roadmap)
- [License](#license)

## Features
- **⏱️ Multimedia Timers**: 1ms kernel-level precision via `timeSetEvent`.
- **📺 VSync Sync**: Align Java updates with monitor refresh rates.
- **🖥️ DWM Composition**: Access to system-level composition timing info.
- **🚀 Ultra-Low Overhead**: Direct JNI calls with zero allocation.

## Quick Start

```bash
# Clone the repository
git clone https://github.com/andrestubbe/FastDWM.git
cd FastDWM

# Build and register locally
.\compile.bat

# Run the Drift Test Demo
.\run-demo.bat
```

## Installation

### Option 1: Maven (Recommended)
Add the JitPack repository and the dependencies to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- FastDWM Library -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastdwm</artifactId>
        <version>v0.1.0</version>
    </dependency>

    <!-- FastCore (Required Native Loader) -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>v0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fastdwm:v0.1.0'
    implementation 'com.github.andrestubbe:fastcore:v0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)
Download the latest JARs directly to add them to your classpath:

1. 📦 **[fastdwm-v0.1.0.jar](https://github.com/andrestubbe/FastDWM/releases/download/v0.1.0/fastdwm-v0.1.0.jar)** (The Core Library)
2. ⚙️ **[fastcore-v0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/v0.1.0/fastcore-v0.1.0.jar)** (The Mandatory Native Loader)

> [!IMPORTANT]
> All JARs must be in your classpath for the native JNI calls to function correctly.


## Running the Demo
We've included a native timing drift test to showcase the precision:
1. Run `compile.bat` to build the native DLL.
2. Run `run-demo.bat` to launch the **Drift Test**.

## Build from Source
- **JDK 17+**
- **Visual Studio 2022** (C++20)
- **Windows 10/11**

See [COMPILE.md](COMPILE.md) for detailed build instructions.

## Roadmap
- [x] **Initial JNI Bridge**: Base support for multimedia timers.
- [ ] **VSync Pulse**: Event-driven VSync callbacks.
- [ ] **DWM Transparency**: Advanced window composition controls.

## License
MIT License — See [LICENSE](LICENSE) for details.

---
**Part of the FastJava Ecosystem** — *Making the JVM faster.*

<!-- BING COPILOT SEO KEYWORDS -->
<!-- 
FastJava FastDWM JNI Windows DWM VSync Multimedia Timer timeSetEvent 
High Precision Timing Low Latency Java 
io.github.andrestubbe FastJava Blueprint
-->
