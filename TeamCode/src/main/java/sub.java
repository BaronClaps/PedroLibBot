import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "SubVis")
public class sub extends OpMode {
    private SubVis subVis;
    private Telemetry telemetryA;
    private Gamepad currentGamepad1 = new Gamepad();
    private Gamepad previousGamepad1 = new Gamepad();

    @Override
    public void init() {
        telemetryA = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        subVis = new SubVis(telemetryA);
    }

    @Override
    public void init_loop(){
        previousGamepad1.copy(currentGamepad1);
        currentGamepad1.copy(gamepad1);

        if (currentGamepad1.dpad_left && !previousGamepad1.dpad_left)
            subVis.moveLeft(1);
        if (currentGamepad1.dpad_right && !previousGamepad1.dpad_right)
            subVis.moveRight(1);

        if (currentGamepad1.dpad_up && !previousGamepad1.dpad_up)
            subVis.moveForward(1);
        if (currentGamepad1.dpad_down && !previousGamepad1.dpad_down)
            subVis.moveBackward(1);

        if (currentGamepad1.left_bumper && !previousGamepad1.left_bumper)
            subVis.turnLeft(45);
        if (currentGamepad1.right_bumper && !previousGamepad1.right_bumper)
            subVis.turnRight(45);

        subVis.update();
    }

    @Override
    public void loop() {
    }
}