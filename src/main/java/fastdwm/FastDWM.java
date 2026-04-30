package fastdwm;

import fastcore.NativeLoader;

/**
 * FastDWM - Native Windows Timing and Composition API.
 * 
 * <p>Provides high-precision multimedia timers and VSync synchronization
 * for the FastJava ecosystem.
 */
public final class FastDWM {
    
    static {
        NativeLoader.load("fastdwm");
    }

    private FastDWM() {
        // Utility class
    }

    /**
     * Sets the system timer resolution to the specified value (in ms).
     * <p>Standard Windows resolution is ~15ms. Calling this with 1ms 
     * allows for much smoother animations.
     * 
     * @param resolutionMs Desired resolution in milliseconds
     * @return true if successful
     */
    public static native boolean beginTimerPeriod(int resolutionMs);

    /**
     * Restores the system timer resolution.
     * 
     * @param resolutionMs The resolution previously set
     * @return true if successful
     */
    public static native boolean endTimerPeriod(int resolutionMs);

    /**
     * Creates a periodic native timer that calls a Java callback.
     * <p>Uses winmm timeSetEvent for 1ms precision.
     * 
     * @param delayMs Interval between ticks
     * @param callback The runnable to execute on each tick
     * @return Timer handle ID (used for stopping)
     */
    public static native int createPeriodicTimer(int delayMs, Runnable callback);

    /**
     * Stops a periodic native timer.
     * 
     * @param timerId The handle ID returned by createPeriodicTimer
     * @return true if successful
     */
    public static native boolean killTimer(int timerId);

    /**
     * Waits for the next VSync pulse from the DWM.
     * <p>Blocks the current thread until the monitor refresh occurs.
     */
    public static native void waitForVSync();
}
