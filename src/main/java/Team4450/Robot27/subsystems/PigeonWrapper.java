package Team4450.Robot27.subsystems;

import com.ctre.phoenix6.hardware.Pigeon2;
//import com.ctre.phoenix6.StatusSignal;

import Team4450.Lib.Util;
//import org.wpilib.util.sendable.SendableBuilder;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.tunable.ComplexTunable;
import org.wpilib.tunable.TunableTable;
//import org.wpilib.units.measure.Angle;

/**
 * Wrapper class for Pigeon2 gyro.
 */
public class PigeonWrapper extends SubsystemBase implements ComplexTunable {
    public Pigeon2     pigeon;
    public double      startingYaw; // Starting yaw is in degrees

    public PigeonWrapper(Pigeon2 pigeon) {
        Util.consoleLog();

        this.pigeon = pigeon;
    }

    public Pigeon2 getPigeon() {
        return pigeon;
    }

    /**
     * Returns total yaw of robot. Continues beyond 360.
     * @return Yaw in degrees ccw(left)- cw(right)+.
     */
    public double getYaw() {
        return -pigeon.getYaw().getValueAsDouble() + startingYaw;
    }

    public double getYawRaw() {
        return -pigeon.getYaw().getValueAsDouble();
    }

    public void setCurrentYaw(double target) {
        startingYaw = -(getYawRaw() - target);
    }

	/**
	 * Return current robot heading (0-359.n) relative to direction robot was
	 * pointed at last reset. Will return fractional angle.
	 * 1 degree is right of zero (clockwise) and 359 is left (counter clockwise).
	 * @return Robot heading in degrees.
	 */
	public double getHeading() {
		double heading;
		
		heading = getYaw();

		heading = heading - ((int) (heading / 360) * 360);
		
		if (heading < 0) heading += 360;
		
		return heading;
	}
    	
    /**
     * Return total yaw angle accumulated since last call to reset() constrained
     * to +-180 degrees no matter how many degrees we have rotated.
	 * @return Yaw angle in degrees 0 to +-180, ccw(left)- cw(right)+.
     */
	public double getYaw180() {
        return Math.IEEEremainder(getYaw(), 360);
    }

    public void reset() {
        Util.consoleLog();
        
        pigeon.reset();
    }
  
    /**
     * Set a starting yaw for the case where robot is not starting
     * with back bumper parallel to the wall. 
     * @param degrees - is clockwise (cw or right).
     */
    public void setStartingGyroYaw(double degrees) {
        Util.consoleLog("%.1f", degrees);

        startingYaw = degrees;
    }

    // @Override
    // public void initSendable(SendableBuilder builder) {
    //     builder.setSmartDashboardType("Gyro");
    //     builder.addDoubleProperty("Value", () -> getYaw(), null);
    //     builder.addDoubleProperty("Yaw 180", () -> getYaw180(), null);
    //     builder.addDoubleProperty("Heading", () -> getHeading(), null);
    // }

    @Override
    public void publishTunable(TunableTable table) {
        table.publishDouble("Value", () -> getYaw(), null);
        table.publishDouble("Yaw 180", () -> getYaw180(), null);
        table.publishDouble("Heading", () -> getHeading(), null);
    }
    
    @Override
    public String getTunableType() {
        return "Gyro";
    }
}
