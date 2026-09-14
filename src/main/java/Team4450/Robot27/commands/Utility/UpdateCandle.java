package Team4450.Robot27.commands.Utility;

import static Team4450.Robot27.Constants.alliance;

import Team4450.Lib.Util;
import Team4450.Robot27.subsystems.Candle;
import Team4450.Robot27.subsystems.Candle.AnimationTypes;
import org.wpilib.driverstation.Alliance;
import org.wpilib.driverstation.RobotState;
import org.wpilib.util.Color;
import org.wpilib.command2.Command;

/**
 * Default command that updates the LEDs on the robot.
 */
public class UpdateCandle extends Command {
    private Candle candle;
    private enum State {DISABLED, ENABLED, TARGET_LOCKED, ALLIANCE, OFF}
    private State state = State.DISABLED;
    private double time;

    /**
     * A default command that updates the CANdle LED state, using NetworkTables data
     * @param candle the CANdle subsystem
     */
    public UpdateCandle(Candle candle) {
        this.candle = candle;
        addRequirements(candle);
    }

    @Override // we want this to run while disabled!
    public boolean runsWhenDisabled() {return true;}

    @Override
    public void initialize() {
        time = Util.timeStamp();
    }

    @Override
    public void execute() {
        if (RobotState.isDisabled() && !RobotState.isFMSAttached()) { // disabled in pit
            state = State.DISABLED;
        } else if (RobotState.isDisabled()) { // disabled on FMS
            state = State.ALLIANCE;
        } else if (RobotState.isEnabled()) { 
            state = State.ENABLED;        
        } else { // no LEDs
            state = State.OFF;
        }
        
        // Util.consoleLog("state=%s", state.toString());
        setLeds();
    }

    /**
     * Blink the CANdle between two colors
     * @param color the first color
     * @param color2 the second color (or null for off)
     */
    public void blink(Color color, Color color2) {
        if (Util.getElaspedTime(time) % 0.2 < 0.1) {
            candle.setLeds(color);
        } else if (color2 == null) {
            candle.setLedsOff();
        } else {
            candle.setLeds(color2);
        }
    }

    /**
     * Blink the CANdle between the given color and off
     * @param color the color
     */
    public void blink(Color color) {
        blink(color, null);
    }

    /**
     * set the LED colors based on the state
     */
    private void setLeds() {
        switch (state) {
            case DISABLED: // rainbow
                candle.setAnimation(AnimationTypes.Rainbow);
                break;
            case ALLIANCE: // alliance color
                candle.setAnimation(AnimationTypes.Off);
                candle.setLeds(alliance==Alliance.BLUE ? Color.BLUE : Color.RED);
                break;
            case ENABLED:
                candle.setAnimation(AnimationTypes.Off);
                candle.setLeds(Color.GREEN);
                break;
            case TARGET_LOCKED: // alternating yellow and blue
                candle.setAnimation(AnimationTypes.Off);
                blink(Color.BLUE, Color.YELLOW);
                break;
            case OFF: // LEDs off
                candle.setAnimation(AnimationTypes.Off);
                candle.setLedsOff();
                break;
        }
    }
}
