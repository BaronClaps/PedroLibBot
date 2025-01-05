package pedroPathing.constants;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.localization.constants.OTOSConstants;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;

@Config
public class LConstants {
    static {
        OTOSConstants.offset = new SparkFunOTOS.Pose2D(3,2,Math.toRadians(270));

    }
}



