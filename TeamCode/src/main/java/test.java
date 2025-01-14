import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
@Config
@TeleOp(name = "test")
public class test extends OpMode {
    Servo s;
    public static double l = 0.125, t = 0.875;

    @Override
    public void init() {
        s = hardwareMap.get(Servo.class, "l");
    }

    @Override
    public void loop() {
if(gamepad1.a){
    s.setPosition(l);
}else if(gamepad1.b){
    s.setPosition(t);
}

telemetry.addData( "pos",s.getPosition());
telemetry.update();
    }
}
