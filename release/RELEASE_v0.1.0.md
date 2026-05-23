# FastDWM v0.1.0 — Initial Release ⏱️

## 🎉 Version 0.1.0: Native Windows Timing Engine
**Release Date:** April 30, 2026  
**Tag:** `v0.1.0`

---

## ✨ Features

### High-Precision Multimedia Timers
- **1ms Accuracy**: Bypass the standard Windows 15.6ms timer limitation for rock-solid heartbeats.
- **Kernel-Level Tuning**: Direct access to `timeBeginPeriod` for system-wide precision control.
- **JNI Pulse**: Event-driven native callbacks for ultra-low latency timing.

### VSync Synchronization
- **DWM Flush Integration**: Block-wait for monitor refresh pulses to eliminate tearing.
- **Perfect Smoothness**: Sync Java logic directly with the GPU composition heartbeat.

### Performance Benchmark Demo
- **Drift & Jitter Test**: Built-in comparison showing that FastDWM is **3.0x more stable** than standard Java timers.
- Run it with: `.\run-demo.bat`

---

## 📦 Installation (JitPack)

### Maven
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
        <groupId>io.github.andrestubbe</groupId>
        <artifactId>fastdwm</artifactId>
        <version>v0.1.0</version>
    </dependency>

    <!-- FastCore (Required Native Loader) -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>v1.0.0</version>
    </dependency>
</dependencies>
```

---

## 🔧 Technical Details
- **Native DLL:** `fastdwm.dll` (bundled inside JAR).
- **Architecture:** Win32 Multimedia Timers + DWM API.
- **Build System:** Robust `vswhere`-powered Maven pipeline.

---

## 🙏 Credits
- **FastCore:** Unified JNI loading engine.
- **FastJava Team:** Performance-first architecture.

**Part of the FastJava Ecosystem** — *Making the JVM faster.*
