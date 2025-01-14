import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Config
@TeleOp(name = "test")
public class test extends OpMode {
    Servo s;
    public static double l = 0.125, t = 0.875;
    public LimelightSubsystem ll;
    public Follower f;
    boolean e = false;

    @Override
    public void init() {
        s = hardwareMap.get(Servo.class, "l");
        Constants.setConstants(FConstants.class, LConstants.class);
        f = new Follower(hardwareMap);
        f.setStartingPose(new Pose(10,0,0));
        ll = new LimelightSubsystem(hardwareMap, telemetry, new Pose(10, 0, 0), new Pose(20,0,0));
        try {
            ll.init();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            s.setPosition(l);
        } else if (gamepad1.b) {
            s.setPosition(t);
        }

        if (gamepad1.left_bumper && gamepad1.right_bumper) {
            if(!e) {
                e = true;
                Pose b = ll.getBestSamplePose();
                Path p = new Path(new BezierLine(new Point(f.getPose()), new Point(b.getX(), b.getY())));
                p.setLinearHeadingInterpolation(f.getPose().getHeading(), b.getHeading());
                f.followPath(p);
            }
        }

        if(gamepad1.dpad_down) {
            e = false;
        }

        f.update();

        telemetry.addData("e", e);
        telemetry.addData("pos", s.getPosition());
        telemetry.addData("pose", f.getPose());
        telemetry.addData("best", ll.getBestSamplePose());
        telemetry.update();
    }
}
