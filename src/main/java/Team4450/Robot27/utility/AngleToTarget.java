package Team4450.Robot27.utility;

import Team4450.Robot27.Constants;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.driverstation.Alliance;

public final class AngleToTarget {
    // All targeting should be based on the WPIBlue coordinate system
    public double getAngleToFaceGoalDegrees(Pose2d robotPosition) {
        // If blue side
        double xDiff = 0;
        double yDiff = 0;
        if (Constants.alliance == Alliance.BLUE) {
            xDiff = Constants.HUB_BLUE_X - robotPosition.getX();
            yDiff = Constants.HUB_BLUE_Y + robotPosition.getY();
            // If red side
        } else if (Constants.alliance == Alliance.RED) {
            xDiff = Constants.HUB_RED_X + robotPosition.getX();
            yDiff = Constants.HUB_RED_Y - robotPosition.getY();
        } else {
            // Error
        }
        // Return degrees to angle to face the target
        // This code does not take into account the curret robot angle because we want to set this as the target robot angle
        return (Math.toDegrees(Math.atan(yDiff / xDiff)));
    }
}
