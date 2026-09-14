package Team4450.Robot27.commands;

import Team4450.Robot27.subsystems.Intake;
import Team4450.Robot27.subsystems.Hopper;
import org.wpilib.system.Timer;

import Team4450.Robot27.Constants;
import org.wpilib.command2.Command;

public class intakeCommand extends Command {
    private Intake intake;
    private Hopper hopper;
    private Timer timer;

    public intakeCommand(Intake intake, Hopper hopper) {
        this.intake = intake;
        this.hopper = hopper;
        this.timer = new Timer();
    }

    @Override
    public void initialize() {
        intake.setIntakeRPM(Constants.INTAKE_DEFAULT_TARGET_RPM);
        timer.start();
        timer.reset();
    }

    private boolean hopperRunning = false;

    @Override
    public void execute() {
        if (timer.hasElapsed(0.5)){
            if(hopperRunning){
                hopper.stop();
            }
            else{
                hopper.start();
            }
            timer.reset();
            hopperRunning = !hopperRunning;

        }
    }

    @Override
    public boolean isFinished() {
        // we will always manually stop this command
        return false;

    }

    @Override
    public void end(boolean interrupted) {
        intake.stopIntake();
        hopper.stop();
    }
}
