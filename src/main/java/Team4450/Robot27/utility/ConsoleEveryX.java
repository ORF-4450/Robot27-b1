package Team4450.Robot27.utility;

import Team4450.Lib.Util;
import Team4450.Robot27.RobotContainer;
//import org.wpilib.smartdashboard.SmartDashboard;
import org.wpilib.telemetry.Telemetry;

public class ConsoleEveryX {
    private int x;
    private int targetX;
    private boolean enabled;
    private String id;

    public ConsoleEveryX(String id, int x) {
        this.x = 0;
        this.targetX = x;
        this.enabled = false;
        this.id = String.format("ConsoleEveryX/%s", id);
        if (!RobotContainer.inTestMode) { return; } // Early return if not in test mode
        Telemetry.log(id, enabled);
    }

    public void update(String text) {
        if (!RobotContainer.inTestMode) { return; } // Early return if not in test mode
        this.enabled = Tunables.addBoolean(this.id, this.enabled);
        if (this.enabled) {
            this.x++;
            if (this.x == this.targetX) {
                Util.consoleLog(text);
                this.x = 0;
            }
        } else {
            return;
        }
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }
}
