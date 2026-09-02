package Team4450.Robot27.utility;

public class RobotOrientation {
    public double yaw;
    public double yawRate;
    public double pitch;
    public double pitchRate;
    public double roll;
    public double rollRate;

    public RobotOrientation(double yaw, double yawRate, double pitch, double pitchRate, double roll, double rollRate) {
        this.yaw = yaw;
        this.yawRate = yawRate;
        this.pitch = pitch;
        this.pitchRate = pitchRate;
        this.roll = roll;
        this.rollRate = rollRate;
    }
}
