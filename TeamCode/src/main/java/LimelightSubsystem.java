

import com.pedropathing.util.Timer;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.MathFunctions;

public class LimelightSubsystem {

    public enum limelightState {
        yellow,
        red,
        blue,
        aprilTag,
        none
    }

    // Camera field of view and resolution (adjust based on your camera's specs)
    private static final double CAMERA_FOV_HORIZONTAL = 54.5; // in degrees
    private static final double CAMERA_FOV_VERTICAL = 42; // in degrees
    private static final int CAMERA_RESOLUTION_WIDTH = 2592; // in pixels
    private static final int CAMERA_RESOLUTION_HEIGHT = 1944; // in pixels

    private static final double INCHES_PER_PIXEL_HORIZONTAL = CAMERA_FOV_HORIZONTAL / CAMERA_RESOLUTION_WIDTH;
    private static final double INCHES_PER_PIXEL_VERTICAL = CAMERA_FOV_VERTICAL / CAMERA_RESOLUTION_HEIGHT;

    private double cameraOffsetX, cameraOffsetY, cameraHeadingOffset;

    private Telemetry telemetry;
    private List<Sample> detectedSamples;
    private Limelight3A limelight;
    private Sample bestSample;

    private Pose startPose,targetPose;

    public LimelightSubsystem(HardwareMap hardwareMap, Telemetry telemetry, Pose startPose, Pose targetPose) {
        this.telemetry = telemetry;
        this.startPose = startPose;
        this.targetPose = targetPose;
        this.cameraOffsetX = 0;
        this.cameraOffsetY = 0;
        this.cameraHeadingOffset = Math.toRadians(180);
        detectedSamples = new java.util.ArrayList<Sample>();
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
    }

    public void init() throws InterruptedException {
        limelight.pipelineSwitch(9);
        limelight.start();

        detectSamples();
    }

    public void detectSamples() throws InterruptedException {

        Thread.sleep(300);
        List<LLResultTypes.DetectorResult> detections = limelight.getLatestResult().getDetectorResults();
        detectedSamples.clear();

        for (LLResultTypes.DetectorResult detection : detections) {
            String className = detection.getClassName(); // What was detected

            // Convert target position in pixels to inches relative to the camera
            double targetXInches = detection.getTargetXPixels() * INCHES_PER_PIXEL_HORIZONTAL;
            double targetYInches = detection.getTargetYPixels() * INCHES_PER_PIXEL_VERTICAL;

            // Calculate the field-relative position of the target
            Pose cameraPose = getCameraPose(startPose, cameraOffsetX, cameraOffsetY, cameraHeadingOffset);
            double fieldX = cameraPose.getX() + targetXInches * Math.cos(cameraPose.getHeading()) -
                    targetYInches * Math.sin(cameraPose.getHeading());
            double fieldY = cameraPose.getY() + targetXInches * Math.sin(cameraPose.getHeading()) +
                    targetYInches * Math.cos(cameraPose.getHeading());

            detectedSamples.add(new Sample(className, fieldX, fieldY, targetPose));
            telemetry.addData(className, "at (" + fieldX + ", " + fieldY + ") inches");
        }

        sortSamplesByDistance();

        telemetry.addData("Detected Samples", detectedSamples.toString());
        telemetry.addData("Best Sample", detectedSamples.get(0).toString());

        bestSample = detectedSamples.get(0);

        telemetry.update();
        limelight.stop();
    }

    /**
     * Calculates the field-relative position of a camera based on its offset and heading
     * from the robot center.
     *
     * @param robotPose The current pose of the robot.
     * @param cameraOffsetX The x offset of the camera from the robot center (inches).
     * @param cameraOffsetY The y offset of the camera from the robot center (inches).
     * @param cameraHeadingOffset The heading offset of the camera relative to the robot's front (radians).
     * @return A new Pose representing the camera's position and heading on the field.
     */
    public Pose getCameraPose(Pose robotPose, double cameraOffsetX, double cameraOffsetY, double cameraHeadingOffset) {

        // Rotate the camera's position offset to field space
        double rotatedX = cameraOffsetX * Math.cos(robotPose.getHeading()) - cameraOffsetY * Math.sin(robotPose.getHeading());
        double rotatedY = cameraOffsetX * Math.sin(robotPose.getHeading()) + cameraOffsetY * Math.cos(robotPose.getHeading());

        // Add the rotated offsets to the robot's position
        double cameraFieldX = robotPose.getX() + rotatedX;
        double cameraFieldY = robotPose.getY() + rotatedY;

        // Calculate the camera's field-relative heading (in radians)
        double cameraFieldHeading = MathFunctions.normalizeAngle(robotPose.getHeading() + cameraHeadingOffset);

        // Return the camera's field-relative pose
        return new Pose(cameraFieldX, cameraFieldY, cameraFieldHeading);
    }

    /**
     * Sorts the detected samples by their distance to the targetPose.
     * The closer the sample is to the targetPose, the higher it is in the list.
     */
    private void sortSamplesByDistance() {
        detectedSamples.sort(Sample::compareTo);
    }

    public Pose getBestSamplePose(){
        return new Pose(detectedSamples.get(0).x, detectedSamples.get(0).y, detectedSamples.get(0).targetPose.getHeading());
    }



}
