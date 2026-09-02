package Team4450.Robot27.utility;

import org.wpilib.math.geometry.Pose2d;

public class VisionPose {
    public Pose2d pose;
    public long timestamp;
    public VisionPose nextNode;

    public VisionPose(Pose2d pose, long timestamp) {
        this.pose = pose;
        this.timestamp = timestamp;
    }
}
