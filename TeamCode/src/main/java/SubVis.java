import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class SubVis {
    private Telemetry telemetry;
    private double xOffset = 0; // Inches from the pink circle (x-direction)
    private double yOffset = 0; // Inches from the pink circle (y-direction)
    private double rotation = 0; // Degrees (counterclockwise positive)

    private static final double SUBMERSIBLE_WIDTH = 48;  // Inches
    private static final double SUBMERSIBLE_HEIGHT = 30; // Inches
    private static final int VISUAL_WIDTH = 24;          // Pixel-like grid width (maintaining 30:48 ratio)
    private static final int VISUAL_HEIGHT = 15;         // Pixel-like grid height (maintaining 30:48 ratio)

    public SubVis(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public void moveForward(double inches) {
        xOffset += inches;
    }

    public void moveBackward(double inches) {
        xOffset -= inches;
    }

    public void moveLeft(double inches) {
        yOffset += inches;
    }

    public void moveRight(double inches) {
        yOffset -= inches;
    }

    public void turnLeft(double degrees) {
        rotation += degrees;
    }

    public void turnRight(double degrees) {
        rotation -= degrees;
    }

    public double[] getPose() {
        return new double[]{xOffset, yOffset};
    }

    private char getSampleSymbol() {
        double angle = (rotation % 360 + 360) % 360; // Normalize to 0-360
        if (angle >= 315 || angle < 45) {
            return '|'; // Upright
        } else if (angle >= 45 && angle < 135) {
            return '\\'; // Diagonal
        } else if (angle >= 135 && angle < 225) {
            return '-'; // Horizontal
        } else {
            return '/'; // Diagonal
        }
    }

    public void update() {
        telemetry.addData("Sample Position", "X: %.2f inches, Y: %.2f inches", xOffset, yOffset);
        telemetry.addData("Rotation", "%.2f degrees", rotation);
        telemetry.addLine("Visualization:");

        char sampleSymbol = getSampleSymbol();

        // Scale the sample's position to the visual grid, maintaining 30:48 ratio
        int visualX = (int) ((xOffset / SUBMERSIBLE_WIDTH) * VISUAL_WIDTH);
        int visualY = (int) ((yOffset / SUBMERSIBLE_HEIGHT) * VISUAL_HEIGHT);

        // Ensure the sample stays within bounds
        visualX = Math.max(0, Math.min(VISUAL_WIDTH - 1, visualX));
        visualY = Math.max(0, Math.min(VISUAL_HEIGHT - 1, visualY));

        for (int i = 0; i < VISUAL_HEIGHT; i++) {
            StringBuilder line = new StringBuilder();
            if (i == 0 || i == VISUAL_HEIGHT - 1) {
                line.append(" +");
                for (int j = 0; j < VISUAL_WIDTH; j++) {
                    line.append("-");
                }
                line.append("+ ");
            } else {
                line.append(" |");
                for (int j = 0; j < VISUAL_WIDTH; j++) {
                    if (i == visualY && j == visualX) {
                        line.append(sampleSymbol); // Place the rotating sample symbol
                    } else {
                        line.append(" ");
                    }
                }
                line.append("| ");
            }
            telemetry.addLine(line.toString());
        }
        telemetry.update();
    }
}