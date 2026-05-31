package fastdwm;

import fasttheme.FastTheme;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicLong;

/**
 * FastDWM v0.1.0 - "Windows Heartbeat Visualizer" (Game Loop Synced)
 */
public class Demo extends Canvas {
    
    // ==========================================
    // CONFIGURATION CONSTANTS (NO MAGIC NUMBERS)
    // ==========================================
    private static final int WIDTH = 1173;
    private static final int HEIGHT = 610;
    
    private static final int MAX_DATA_POINTS = 1000;
    private static final int GRAPH_MARGIN = 50;
    
    private static final float TOP_PANEL_Y_RATIO = 0.30f;
    private static final float BOTTOM_PANEL_Y_RATIO = 0.70f;
    
    private static final float VSYNC_Y_SCALE = 5f; // 1/8 of the previous 40f scale
    private static final float TIMER_Y_SCALE = 40f;
    
    private static final int TITLE_OFFSET_Y = -120;
    private static final int AVG_OFFSET_Y = -136;

    // ==========================================
    // RENDER CACHE (ZERO ALLOCATION)
    // ==========================================
    private static final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Color COLOR_BASELINE = new Color(64, 64, 64);
    private static final Color COLOR_AVG_TEXT = new Color(128, 128, 128);

    private static final BasicStroke STROKE_GRAPH = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final BasicStroke STROKE_NORMAL = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    private final Path2D.Float sharedPath = new Path2D.Float();
    private final Line2D.Float sharedLine = new Line2D.Float();

    // ==========================================
    // ZERO-ALLOCATION PRIMITIVE RING BUFFERS
    // ==========================================
    private final double[] vsyncData = new double[MAX_DATA_POINTS];
    private final double[] timerData = new double[MAX_DATA_POINTS];
    private int dataSize = 0;
    
    private final AtomicLong vsyncTime = new AtomicLong(0);
    private final AtomicLong timerTime = new AtomicLong(0);
    private final long[] lastTimerNano = { System.nanoTime() };
    
    // Permanent Baseline to prevent ALL wobbling and drifting
    private double vsyncBaseline = -1;
    private double timerBaseline = 1.0; // Timer is hardcoded to request 1.0ms
    
    private int warmupFrames = 60; // Skip first 60 frames to ignore JVM/Window startup lag
    private int baselineSetupCount = 0;
    private double baselineSum = 0;

    private int frames = 0;
    private long lastFPSCheck = System.currentTimeMillis();
    private String currentFPS = "0";

    public Demo() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setupMonitoring();
    }

    public void start() {
        createBufferStrategy(2);
        BufferStrategy bs = getBufferStrategy();
        new Thread(() -> {
            while (true) {
                FastDWM.waitForVSync();
                updateGraphData();
                render(bs);
                updateFPS();
            }
        }, "Render-Loop").start();
    }

    private void updateGraphData() {
        synchronized(vsyncData) {
            double v = vsyncTime.get() / 1_000_000.0;
            double t = timerTime.get() / 1_000_000.0;
            
            if (warmupFrames > 0) {
                warmupFrames--;
                return; // Ignore garbage lag spikes during window initialization
            }
            
            if (baselineSetupCount < 60) {
                // Collect 60 clean frames to calculate a permanent rock-solid average
                baselineSum += v;
                baselineSetupCount++;
                if (baselineSetupCount == 60) {
                    vsyncBaseline = baselineSum / 60.0;
                }
                return; // Don't draw the graph until we have a perfect baseline!
            }

            // Shift array
            if (dataSize >= MAX_DATA_POINTS) {
                System.arraycopy(vsyncData, 1, vsyncData, 0, MAX_DATA_POINTS - 1);
                System.arraycopy(timerData, 1, timerData, 0, MAX_DATA_POINTS - 1);
                vsyncData[MAX_DATA_POINTS - 1] = v;
                timerData[MAX_DATA_POINTS - 1] = t;
            } else {
                vsyncData[dataSize] = v;
                timerData[dataSize] = t;
                dataSize++;
            }
        }
    }

    private void setupMonitoring() {
        new Thread(() -> {
            while (true) {
                long start = System.nanoTime();
                FastDWM.waitForVSync();
                vsyncTime.set(System.nanoTime() - start);
            }
        }).start();
        FastDWM.createPeriodicTimer(1, () -> {
            long now = System.nanoTime();
            timerTime.set(now - lastTimerNano[0]);
            lastTimerNano[0] = now;
        });
    }

    private void render(BufferStrategy bs) {
        Graphics2D g2 = (Graphics2D) bs.getDrawGraphics();
        
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        float topCenterY = getHeight() * TOP_PANEL_Y_RATIO;
        float bottomCenterY = getHeight() * BOTTOM_PANEL_Y_RATIO;

        drawGraph(g2, vsyncData, Color.WHITE, topCenterY, VSYNC_Y_SCALE, vsyncBaseline, "Hardware VSync Interrupt (DwmFlush)");
        drawGraph(g2, timerData, Color.LIGHT_GRAY, bottomCenterY, TIMER_Y_SCALE, timerBaseline, "Native OS Multimedia Timer (1ms Callback)");
        
        g2.dispose();
        bs.show();
    }

    private void drawGraph(Graphics2D g2, double[] data, Color color, float centerY, float yScale, double avg, String title) {
        g2.setColor(color);
        synchronized(vsyncData) {
            if (dataSize < 2) return;

            // Re-use pre-allocated Line2D object
            g2.setStroke(STROKE_NORMAL);
            g2.setColor(COLOR_BASELINE);
            sharedLine.setLine(GRAPH_MARGIN, centerY, getWidth() - GRAPH_MARGIN, centerY);
            g2.draw(sharedLine);

            float dx = (float) (getWidth() - (2 * GRAPH_MARGIN)) / (MAX_DATA_POINTS - 1f);

            // Re-use pre-allocated Path2D object
            sharedPath.reset();
            sharedPath.moveTo(GRAPH_MARGIN, centerY - (float)((data[0] - avg) * yScale));

            for (int i = 1; i < dataSize; i++) {
                float x = GRAPH_MARGIN + (i * dx);
                float y = centerY - (float)((data[i] - avg) * yScale);
                sharedPath.lineTo(x, y);
            }

            g2.setStroke(STROKE_GRAPH);
            g2.setColor(color); // Fix: Use the requested color for the graph!
            g2.draw(sharedPath);


            // Render cached fonts
            g2.setColor(COLOR_AVG_TEXT);
            g2.setFont(FONT_TEXT);
            g2.drawString(title, GRAPH_MARGIN, centerY - TITLE_OFFSET_Y);
            g2.drawString(String.format("Avg: %.2f ms", avg), GRAPH_MARGIN, centerY - AVG_OFFSET_Y);
        }
    }

    private void updateFPS() {
        frames++;
        long now = System.currentTimeMillis();
        if (now - lastFPSCheck >= 1000) {
            currentFPS = String.valueOf(frames);
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win instanceof JFrame) {
                ((JFrame) win).setTitle("FastDWM - Native Timing Drift Test (FPS: " + currentFPS + ")");
            }
            frames = 0;
            lastFPSCheck = now;
        }
    }

    private static Image createRoundIcon() {
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillOval(4, 4, 56, 56);
        g.dispose();
        return icon;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FastDWM - Native Timing Drift Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setIconImage(createRoundIcon());
            
            Demo demo = new Demo();
            frame.add(demo);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            try {
                long hwnd = FastTheme.getWindowHandle(frame);
                FastTheme.setTitleBarDarkMode(hwnd, true);
                FastTheme.setTitleBarColor(hwnd, 0, 0, 0);
                FastTheme.setTitleBarTextColor(hwnd, 255, 255, 255);
                FastTheme.setWindowTransparency(hwnd, 224);
            } catch (Exception e) {
                System.err.println("FastTheme not available: " + e.getMessage());
            }

            demo.start();
        });
    }
}
