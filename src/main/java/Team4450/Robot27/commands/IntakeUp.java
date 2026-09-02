package Team4450.Robot27.commands;

import org.wpilib.command2.Command;
import Team4450.Robot27.subsystems.Intake;
import org.wpilib.smartdashboard.SmartDashboard;
import Team4450.Robot27.Constants;

public class IntakeUp extends Command {

  Intake intake;

  public IntakeUp(Intake intake) {
    this.intake = intake;
  }

  public void initialize() {
  }

  public void execute() {
    SmartDashboard.putNumber(Constants.SmartDashboardKeys.PIVOT_POSITION, 0);
  }

  public boolean isFinished() {
    return intake.getPivitPosition() < 0.2;
  }

  @Override
  public void end(boolean inturrupted) {

  }
}
