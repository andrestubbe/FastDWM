# FastDWM Roadmap

## Upcoming Features

### High-Precision Ticker (v0.1.0)
**Status:** In Progress
Implementing the core JNI bridge for `timeSetEvent` to provide a rock-solid 1ms heartbeat.

**Goal:**
- [x] Create native bridge for `timeBeginPeriod` and `timeSetEvent`.
- [x] Implement Java wrapper with `Runnable` callback.
- [ ] Benchmark drift against standard Java timers.

### VSync Synchronization (v0.2.0)
**Status:** Planning
Providing access to the DWM's vertical synchronization pulse to allow perfectly smooth 60/120/144Hz updates.

**Goal:**
- [ ] Implement `waitForVSync` using `DwmFlush`.
- [ ] Add VSync-locked callback engine.

### Window Composition Hooks
**Status:** Backlog
Advanced control over window blur, transparency, and native borderless styling.

---
**Status Key:**
- 🔴 **Planned**: Not yet started.
- 🟡 **In Progress**: Currently under development.
- 🟢 **Completed**: Shipped in the latest release.
