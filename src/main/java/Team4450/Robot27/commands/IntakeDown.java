package Team4450.Robot27.commands;

import org.wpilib.command2.Command;
import Team4450.Robot27.subsystems.Intake;
import org.wpilib.smartdashboard.SmartDashboard;
import Team4450.Robot27.Constants;

public class IntakeDown extends Command {

    Intake intake;

    public IntakeDown(Intake intake) {
        this.intake = intake;
    }

    public void initialize() {
        intake.pivitDown();
    }

    public void execute() {
    }

    public boolean isFinished() {
        return intake.getPivitPosition() > 0.8;
    }

    @Override
    public void end(boolean inturrupted) {

    }
}
