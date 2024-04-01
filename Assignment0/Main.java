import java.util.Random;

class Sensor {
    private boolean Sensor_On;
    private double p;

    public Sensor(double p) {
        this.p = p;
        this.Sensor_On = new Random().nextDouble() < p;
    }

    public void updateStatus() {
        this.Sensor_On = new Random().nextDouble() < p;
    }

    public boolean Sensor_On() {
        return Sensor_On;
    }
}

class Infiltrator {
    private int xPosition;
    private int yPosition;
    private int targetY;

    public Infiltrator(int startX, int startY, int targetY) {
        this.xPosition = startX;
        this.yPosition = startY;
        this.targetY = targetY;
    }

    public void move(Border border, Clock clock) {
        if (clock.getTime() % 9 == 0) {
            if (yPosition < targetY) {
                yPosition++;
            }
        }
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public boolean hasCrossedBorder() {
        return yPosition >= targetY;
    }
}

class Border {
    private Sensor[][] sensors;
    private int length;
    private int width;

    public Border(int length, int width, double p) {
        this.length = length;
        this.width = width;
        sensors = new Sensor[length][width];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                sensors[i][j] = new Sensor(p);
            }
        }
    }

    public void updateSensors(Clock clock) {
        if (clock.getTime() % 10 == 0) {
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < width; j++) {
                    sensors[i][j].updateStatus();
                }
            }
        }
    }

    public boolean isSensorOnAt(int x, int y) {
        return sensors[x][y].Sensor_On();
    }
}

class Clock {
    private int time;

    public Clock() {
        this.time = 0;
    }

    public void tick() {
        this.time++;
    }

    public int getTime() {
        return time;
    }
}

public class Main {
    public static void main(String[] args) {
        int L = 100;
        int[] widths = { 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000 };
        double[] probabilities = { 0.1, 0.3, 0.5, 0.7, 0.9 };

        for (int W : widths) {
            W = W + 1;
            for (double p : probabilities) {
                Border border = new Border(L, W, p);
                Random rand = new Random();
                int startX = 0;
                int startY = rand.nextInt(100);
                int targetY = W;
                Infiltrator infiltrator = new Infiltrator(startX, startY, targetY);
                Clock clock = new Clock();

                while (!infiltrator.hasCrossedBorder()) {
                    border.updateSensors(clock);
                    infiltrator.move(border, clock);
                    clock.tick();
                }

                System.out.println("Width: " + (W - 1) + ", Probability: " + p
                        + ", Result: Infiltrator succeeded, Time taken: " + clock.getTime() + " seconds");
            }
        }
    }
}
