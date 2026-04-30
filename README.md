# FastDWM — Native Windows Timing & Composition [v0.1.0]

**Low-latency access to the Windows Desktop Window Manager (DWM). High-precision multimedia timers and VSync synchronization for the FastJava ecosystem.**

[![Status](https://img.shields.io/badge/status-v0.1.0--alpha-orange.svg)]()
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## Table of Contents
- [Features](#features)
- [Quick Start](#quick-start)
- [Installation](#installation)
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
```

## Installation

### Option 1: Maven (Recommended)
```xml
<dependencies>
    <dependency>
        <groupId>io.github.andrestubbe</groupId>
        <artifactId>fastdwm</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

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
