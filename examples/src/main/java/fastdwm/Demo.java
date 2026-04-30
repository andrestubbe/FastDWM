package fastdwm;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * FastDWM Demo - The 10-Second Drift Test.
 * 
 * Compares standard Java Timer precision against FastDWM Native Timer.
 */
public class Demo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║         FastDWM Timing Drift Test        ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("\nMeasuring 1ms ticks over 5 seconds...");
        
        final int DURATION_SEC = 5;
        final int EXPECTED_TICKS = DURATION_SEC * 1000;
        
        // 1. Native Contender (FastDWM)
        AtomicInteger nativeTicks = new AtomicInteger(0);
        FastDWM.beginTimerPeriod(1); // Set kernel to 1ms
        int timerId = FastDWM.createPeriodicTimer(1, nativeTicks::incrementAndGet);
        
        // 2. Java Contender (java.util.Timer)
        AtomicInteger javaTicks = new AtomicInteger(0);
        java.util.Timer javaTimer = new java.util.Timer(true);
        javaTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() { javaTicks.incrementAndGet(); }
        }, 0, 1);

        // Wait
        Thread.sleep(DURATION_SEC * 1000);

        // Stop
        FastDWM.killTimer(timerId);
        FastDWM.endTimerPeriod(1);
        javaTimer.cancel();

        // Results
        System.out.println("\nResults:");
        System.out.println("--------------------------------------------");
        System.out.printf("Expected Ticks:  %,d%n", EXPECTED_TICKS);
        System.out.printf("FastDWM Ticks:   %,d  (%.2f%% accuracy)%n", 
            nativeTicks.get(), (nativeTicks.get() / (float)EXPECTED_TICKS) * 100);
        System.out.printf("Java Timer Ticks: %,d  (%.2f%% accuracy)%n", 
            javaTicks.get(), (javaTicks.get() / (float)EXPECTED_TICKS) * 100);
        System.out.println("--------------------------------------------");
        
        float speedup = nativeTicks.get() / (float)Math.max(1, javaTicks.get());
        System.out.printf("\nFastDWM is %.1fx more precise than java.util.Timer!%n", speedup);
        
        System.out.println("\n✅ Test Complete.");
    }
}
