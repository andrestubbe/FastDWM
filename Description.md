# FastDWM

## 1. Vision & Kernidee
**FastDWM** (Desktop Window Manager) ist das Timing- und Compositor-Modul für extrem präzise UI-Automatisierung und Rendering.

Standard-Java (`Thread.sleep()`) oder `java.util.Timer` sind ungenau (oft 10-15ms Jitter unter Windows). Wenn ein Agent jedoch eine Aktion *Frame-perfekt* ausführen soll (z.B. bei Spielen, Animationen oder beim Abgreifen von Video-Streams), muss er wissen, wann der Monitor das Bild tatsächlich zeichnet.

**Warum DWM?**
Der Desktop Window Manager von Windows steuert das finale Zusammenfügen (Compositing) aller Fenster und sendet sie an den Monitor. Mit der DWM-API kann sich FastJava exakt mit dem VSync des Monitors synchronisieren.

## 2. Java High-Level API

```java
public interface FastDWM {
    static FastDWM open() { return new FastDWMImpl(); }

    // Blockiert den Thread exakt so lange, bis der nächste Monitor-Frame gezeichnet wird (VSync)
    void waitForNextFrame();

    // Liefert exakte Timings für Game-Loops
    CompositionTimingInfo getTimingInfo();

    // Setzt abgerundete Ecken oder Schatten für Fenster (Windows 11 UI Tuning)
    void setWindowAttribute(long hwnd, DwmAttribute attr, int value);
}

public record CompositionTimingInfo(
    long qpcVBlank,         // Exact timestamp of last VBlank
    long rateRefreshRefresh // Refresh rate numerator
) {}
```

## 3. C++ JNI Backend (dwmapi.h)
Das Backend verzichtet auf ungenaue `Sleep()`-Schleifen.

1. **DwmFlush:** Ein Aufruf von `DwmFlush()` blockiert den C++ Thread auf Kernel-Ebene exakt so lange, bis der DWM den aktuellen Frame auf den Monitor pusht. 0% CPU-Auslastung während des Wartens.
2. **DwmGetCompositionTimingInfo:** Holt die hochpräzisen Hardware-Counter (`QueryPerformanceCounter`), um Frame-Deltas für flüssige Animationen (z.B. in `FastGhostMouse`) auszurechnen.
3. **DwmSetWindowAttribute:** Wird genutzt, um z.B. bei `FastOverlay` zu verhindern, dass das Overlay beim Alt-Tab-Wechsel auftaucht (`DWMWA_EXCLUDED_FROM_PEEK`).

## 4. Agent-Kit (KI-Integration)
Ein Agent nutzt FastDWM primär für **Rhythmische Aktionen**.
Wenn ein Bot ein Spiel spielt (z.B. Jump'n'Run), bringt es nichts, "ungefähr alle 16ms" eine Taste zu drücken. Er muss die Eingabe (`FastMouse`/`FastKeyboard`) *synchron zum Monitor-Frame* setzen.
