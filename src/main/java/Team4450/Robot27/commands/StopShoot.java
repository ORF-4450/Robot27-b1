package Team4450.Robot27.commands;

import Team4450.Robot27.subsystems.Shooter;
import org.wpilib.command2.Command;
import Team4450.Robot27.subsystems.Hopper;

public class StopShoot extends Command {

  Shooter shooter;
  Hopper hopper;

  public StopShoot(Shooter shooter, Hopper hopper) {
    this.shooter = shooter;
    this.hopper = hopper;
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    shooter.distableHood();
    shooter.stopFlywheel();
    shooter.stopInfeed();
    hopper.stop();
    end(false);
  }

  @Override
  public boolean isFinished() {
    return shooter.flywheelEnabled = false;
  }

  @Override
  public void end(boolean inturrupted) {
  }
}
