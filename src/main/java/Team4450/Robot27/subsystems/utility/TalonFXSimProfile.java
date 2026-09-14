package Team4450.Robot27.subsystems.utility;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.sim.TalonFXSimState;

import org.wpilib.math.system.DCMotor;
import org.wpilib.math.util.Units;
import org.wpilib.simulation.DCMotorSim;
import org.wpilib.math.system.Models;

/**
 * Holds information about a simulated TalonFX.
 */
class TalonFXSimProfile extends Team4450.Robot27.subsystems.utility.PhoenixPhysicsSim.SimProfile {
    private static final double kMotorResistance = 0.002; // Assume 2mOhm resistance for voltage drop calculation
    private final TalonFXSimState talonFXSim;
    private final DCMotorSim motorSim;

    /**
     * Creates a new simulation profile for a TalonFX device.
     * 
     * @param talonFX
     *                        The TalonFX device
     * @param rotorInertia
     *                        Rotational Inertia of the mechanism at the rotor
     */
    public TalonFXSimProfile(final TalonFX talonFX, final double rotorInertia) {
        var gearbox = DCMotor.getKrakenX60Foc(1);
        this.motorSim = new DCMotorSim(Models.singleJointedArmFromPhysicalConstants(gearbox, rotorInertia, 1.0),
                            gearbox, rotorInertia, 1.0);
        this.talonFXSim = talonFX.getSimState();
    }

    /**
     * Runs the simulation profile.
     * 
     * This uses very rudimentary physics simulation and exists to allow users to
     * test features of our products in simulation using our examples out of the
     * box. Users may modify this to utilize more accurate physics simulation.
     */
    public void run() {
        /// DEVICE SPEED SIMULATION

        motorSim.setInputVoltage(talonFXSim.getMotorVoltage());

        motorSim.update(getPeriod());

        /// SET SIM PHYSICS INPUTS
        final double position_rot = motorSim.getAngularPosition();
        final double velocity_rps = Units.radiansToRotations(motorSim.getAngularVelocity());

        talonFXSim.setRawRotorPosition(position_rot);
        talonFXSim.setRotorVelocity(velocity_rps);

        talonFXSim.setSupplyVoltage(12 - talonFXSim.getSupplyCurrent() * kMotorResistance);
    }
}