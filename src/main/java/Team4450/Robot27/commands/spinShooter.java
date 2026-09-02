package Team4450.Robot27.commands;

import Team4450.Robot27.subsystems.Shooter;
import org.wpilib.command2.Command;

public class spinShooter extends Command {
    private Shooter shooter;

    public spinShooter(Shooter shooter) {
        this.shooter = shooter;
    }

    @Override
    public void initialize() {
        shooter.enabledHood();
        shooter.startFlywheel();
    }

    @Override
    public void execute() {
    }

    @Override
    public boolean isFinished() {
        // We will always force stop the command
        return false;
    }

    @Override
    public void end(boolean interuppted) {
        shooter.distableHood();
    }
}
