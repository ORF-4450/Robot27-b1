package Team4450.Robot27.commands;

import org.wpilib.command2.Command;
import Team4450.Robot27.subsystems.Drivebase;

public class StopAuto extends Command {
  Drivebase drivebase;

  public StopAuto(Drivebase drivebase) {
    this.drivebase = drivebase;
  }

  public void initialize() {
  }

  public void execute() {
    drivebase.stop();
  }

  public boolean isFinished() {
    return true;
  }

  @Override
  public void end(boolean inturrupted) {

  }
}
