import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class SmartAutoFishingBot extends JFrame {
    private boolean running = false;
    private Thread botThread;

    // Ekranın ortasındaki 30x30 alanı izliyoruz (1920x1080 ekran için)
    private final Rectangle detectArea = new Rectangle(945, 525, 30, 30);

    public SmartAutoFishingBot() {
        setTitle("Smart Auto Fisher 🎯");
        setSize(300, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton startButton = new JButton("Başlat");
        JButton stopButton = new JButton("Durdur");

        startButton.addActionListener(e -> startBot());
        stopButton.addActionListener(e -> stopBot());

        setLayout(new FlowLayout());
        add(startButton);
        add(stopButton);
    }

    private void startBot() {
    if (running) return;

    running = true;
    botThread = new Thread(() -> {
        try {
            Robot robot = new Robot();

            while (running) {
                // 1. Olta at
                Thread.sleep(4000);
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                Thread.sleep(100);
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);

                System.out.println("Olta atıldı, hareket bekleniyor...");

                Thread.sleep(2000);
                
                BufferedImage previousImage = robot.createScreenCapture(detectArea);
                long movementTimeout = System.currentTimeMillis() + 40000; // 20 saniye bekleme süresi
                boolean movementDetected = false;

                while (running && System.currentTimeMillis() < movementTimeout) {
                    if (isMovementDetected(robot, detectArea, previousImage)) {
                        System.out.println("Hareket algılandı! Balığı çekiyor...");
                        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                        Thread.sleep(100);
                        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        movementDetected = true;
                        break;
                    }
                    previousImage = robot.createScreenCapture(detectArea);
                    Thread.sleep(50);
                }

                if (!movementDetected) {
                    System.out.println("Hareket algılanmadı, olta tekrar atılıyor...");
                    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                    Thread.sleep(100);
                    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Bot durduruldu.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });
    botThread.start();
}


    private void stopBot() {
        running = false;
        if (botThread != null) {
            botThread.interrupt();
        }
    }

    private boolean isMovementDetected(Robot robot, Rectangle area, BufferedImage prevImg) {
        BufferedImage currImg = robot.createScreenCapture(area);
        long diff = 0;

        for (int x = 0; x < area.width; x++) {
            for (int y = 0; y < area.height; y++) {
                Color c1 = new Color(prevImg.getRGB(x, y));
                Color c2 = new Color(currImg.getRGB(x, y));

                int rDiff = Math.abs(c1.getRed() - c2.getRed());
                int gDiff = Math.abs(c1.getGreen() - c2.getGreen());
                int bDiff = Math.abs(c1.getBlue() - c2.getBlue());

                diff += rDiff + gDiff + bDiff;
            }
        }

        System.out.println("Hareket farki: " + diff); // Bunu ekle, konsolda takip et

        return diff > 50000;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SmartAutoFishingBot bot = new SmartAutoFishingBot();
            bot.setVisible(true);
        });
    }
}
