package fastdwm;

import fasttheme.FastTheme;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * FastDWM v0.1.0 - "Windows Heartbeat Visualizer" (Game Loop Synced)
 */
public class Demo extends Canvas {
    
    private final List<Double> vsyncData = new ArrayList<>();
    private final List<Double> timerData = new ArrayList<>();
    private final AtomicLong vsyncTime = new AtomicLong(0);
    private final AtomicLong timerTime = new AtomicLong(0);
    private final long[] lastTimerNano = { System.nanoTime() };
    
    private static final Color BRAND_TEAL = new Color(0x15, 0x54, 0x5e);
    private static final Color BRAND_AQUA = new Color(0x53, 0xb6, 0xb1);
    
    private BufferedImage bakedBackground;
    private int frames = 0;
    private long lastFPSCheck = System.currentTimeMillis();
    private String currentFPS = "0";

    public Demo() {
        precomputeBackground(1184, 621);
        setupMonitoring();
    }

    private void precomputeBackground(int w, int h) {
        bakedBackground = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bakedBackground.createGraphics();
        GradientPaint gp = new GradientPaint(0, 0, BRAND_TEAL, 0, h, BRAND_TEAL.darker());
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
        Random r = new Random();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        for (int y = 0; y < h; y += 4) {
            for (int x = 0; x < w; x += 4) {
                if (r.nextInt(100) < 5) {
                    g2.setColor(new Color(255, 255, 255, r.nextInt(10)));
                    g2.drawRect(x, y, 1, 1);
                }
            }
        }
        g2.dispose();
    }

    public void start() {
        createBufferStrategy(2);
        BufferStrategy bs = getBufferStrategy();
        new Thread(() -> {
            while (true) {
                // FastDWM.waitForVSync(); // UNLOCKED
                updateGraphData();
                render(bs);
                updateFPS();
            }
        }, "Render-Loop").start();
    }

    private void updateGraphData() {
        synchronized(vsyncData) {
            vsyncData.add(vsyncTime.get() / 1_000_000.0);
            if (vsyncData.size() > 250) vsyncData.remove(0);
            timerData.add(timerTime.get() / 1_000_000.0);
            if (timerData.size() > 250) timerData.remove(0);
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
        if (bakedBackground == null || bakedBackground.getWidth() != getWidth()) precomputeBackground(getWidth(), getHeight());
        
        g2.drawImage(bakedBackground, 0, 0, null);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawGraph(g2, vsyncData, BRAND_AQUA, 150);
        drawGraph(g2, timerData, BRAND_AQUA.brighter(), 450);
        
        g2.dispose();
        bs.show();
    }

    private void drawGraph(Graphics2D g2, List<Double> data, Color color, int yOffset) {
        g2.setColor(color);
        synchronized(vsyncData) {
            if (data.size() < 2) return;
            for (int i = 0; i < data.size() - 1; i++) {
                int x1 = 20 + (i * 4);
                int x2 = 20 + ((i + 1) * 4);
                int y1 = yOffset - (int)(data.get(i) * 5);
                int y2 = yOffset - (int)(data.get(i + 1) * 5);
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }

    private void updateFPS() {
        frames++;
        long now = System.currentTimeMillis();
        if (now - lastFPSCheck >= 1000) {
            currentFPS = String.valueOf(frames);
            System.out.println("[FPS] FastDWM: " + currentFPS);
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win instanceof JFrame) ((JFrame) win).setTitle("FastDWM Demo (FPS: " + currentFPS + ")");
            frames = 0;
            lastFPSCheck = now;
        }
    }

    private static Image createCircleIcon() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BRAND_AQUA);
        g2.fillOval(4, 4, 24, 24);
        g2.dispose();
        return img;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("FastDWM Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1184, 621);
        frame.setIconImage(createCircleIcon());
        Demo demo = new Demo();
        frame.add(demo);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Styling with delay to ensure DWM is ready
        Timer styleTimer = new Timer(50, e -> {
            long hwnd = FastTheme.getWindowHandle(frame);
            if (hwnd != 0) {
                FastTheme.setTitleBarDarkMode(hwnd, true);
                FastTheme.setTitleBarColor(hwnd, BRAND_TEAL.getRed(), BRAND_TEAL.getGreen(), BRAND_TEAL.getBlue());
            }
        });
        styleTimer.setRepeats(false);
        styleTimer.start();

        demo.start();
    }
}
