package Team4450.Robot27.subsystems.utility;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.CANdleConfiguration;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.CANdle;
import com.ctre.phoenix6.signals.Enable5VRailValue;
import com.ctre.phoenix6.signals.LossOfSignalBehaviorValue;
import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.signals.StatusLedWhenActiveValue;
import com.ctre.phoenix6.signals.StripTypeValue;
import com.ctre.phoenix6.signals.VBatOutputModeValue;

import Team4450.Lib.Util;

import org.wpilib.command2.SubsystemBase;
import org.wpilib.telemetry.Telemetry;
import org.wpilib.util.Color;

/**
 * Utiity subsystem class that wraps CTRE CANdle LED controller.
 * NOTE: This is Phoenix 6 CANDle.
 */
public class Candle6 extends SubsystemBase {

    private CANdle              candle = null;
    private CANdleConfiguration cfg = new CANdleConfiguration();

    private int                 numLeds;
    private boolean             flashing, flashOn;
    private double              timeStamp;
    private ControlRequest      savedRequest;
    
    public enum AnimationType {
        None,
        ColorFlow,
        Fire,
        Larson,
        Rainbow,
        RgbFade,
        SingleFade,
        Strobe,
        Twinkle,
        TwinkleOff,
    }

    /**
     * Create a new Candle6 subsystem.
     * @param canBus Can bus CANdle is connected to.
     * @param deviceId Device id of the CANdle.
     * @param numLeds Number of leds on the led string. CANDle with no
     * string attached has 8 leds, numbered 0-7.
     */
    public Candle6 (CANBus canBus, int deviceId, int numLeds)
    {
        this.numLeds = numLeds;

        candle = new CANdle(deviceId, canBus);

        candle.clearAllAnimations();

        // Set config defaults.
        
        cfg.LED.StripType = StripTypeValue.GRB;
        cfg.LED.BrightnessScalar = 0.5;
        cfg.LED.LossOfSignalBehavior = LossOfSignalBehaviorValue.DisableLEDs;

        cfg.CANdleFeatures.StatusLedWhenActive = StatusLedWhenActiveValue.Enabled;
        cfg.CANdleFeatures.Enable5VRail = Enable5VRailValue.Enabled;
        cfg.CANdleFeatures.VBatOutputMode = VBatOutputModeValue.On;

        resetConfig();

        turnOffLeds();

        Util.consoleLog("Candle6 created");
    }

    private void turnOffLeds()
    {
        Util.consoleLog();

        candle.setControl(new SolidColor(0, numLeds - 1).withColor(new RGBWColor(Color.BLACK)));
    }

    /**
     * Resets the CANdle configuration.
     */
    public void resetConfig()
    {
        candle.getConfigurator().apply(cfg);
    }

    /**
     * Sets led strip Type option on Candle.
     * Chain this call on the class construtor or You may call 
     * this method after the Candle6 object has been created
     * @param value Desired option value. Defaults to GRB.
     * @return Reference to this Candle6 object for chaining.
     */
    public Candle6 withStripType(StripTypeValue type)
    {
        cfg.LED.StripType = type;
        resetConfig();
        return this;
    }

    /**
     * Sets led Brightness option on Candle.
     * Chain this call on the class construtor or You may call 
     * this method after the Candle6 object has been created
     * @param value Desired option value 0-1. Defaults to .5.
     * @return Reference to this Candle6 object for chaining.
     */
    public Candle6 withBrightness(double brightness)
    {
        cfg.LED.BrightnessScalar = brightness;
        resetConfig();
        return this;
    }

    /**
     * Sets LossOfSignalBehavior option on Candle.
     * Chain this call on the class construtor or You may call 
     * this method after the Candle6 object has been created
     * @param value Desired option value. Defaults to DisableLeds.
     * @return Reference to this Candle6 object for chaining.
     */
    public Candle6 withLossOfSignalBehavior(LossOfSignalBehaviorValue value)
    {
        cfg.LED.LossOfSignalBehavior = value;
        resetConfig();
        return this;
    }

    /**
     * Sets StatusLedWhenActive option on Candle.
     * Chain this call on the class construtor or You may call 
     * this method after the Candle6 object has been created
     * @param value Desired option value. Defaults to On.
     * @return Reference to this Candle6 object for chaining.
     */
    public Candle6 withStatusLedWhenActive(StatusLedWhenActiveValue value)
    {
        cfg.CANdleFeatures.StatusLedWhenActive = value;
        resetConfig();
        return this;
    }

    /**
     * Sets Enable5VRail option on Candle.
     * Chain this call on the class construtor or You may call 
     * this method after the Candle6 object has been created
     * @param value Desired option value. Defaults to Enabled.
     * @return Reference to this Candle6 object for chaining.
     */
    public Candle6 withEnable5VRail(Enable5VRailValue value)
    {
        cfg.CANdleFeatures.Enable5VRail = value;
        resetConfig();
        return this;
    }

    /**
     * Sets VBatOutputMode option on Candle.
     * Chain this call on the class construtor or You may call 
     * this method after the Candle6 object has been created
     * @param value Desired option value. Defaults to On.
     * @return Reference to this Candle6 object for chaining.
     */
    public Candle6 withVBatOutputMode(VBatOutputModeValue value)
    {
        cfg.CANdleFeatures.VBatOutputMode = value;
        resetConfig();
        return this;
    }

    /**
     * Turn off animations.
     */
    public void clearAllAnimations()
    {
        Util.consoleLog();

        candle.clearAllAnimations();
    }

    /**
     * Set solid color on full led string.
     * @param color Color to set.
     * @param flash True to flash. Can be omitted.
     */
    public void setColor(Color color, boolean... flash)
    {
        setColor(color, 0, numLeds- 1, flash);
    }

    /**
     * Set solid color on selected section of led string. Positions
     * 0-7 are the CANdle onboard leds.
     * @param color Color to set.
     * @param startLed Start led index (0 based).
     * @param endLed End led index (0 based).
     * @param flash True to flash. Can be omitted.
     */
    public void setColor(Color color, int startLed, int endLed, boolean... flash)
    {
        Util.consoleLog("%s [%d,%d]", color.toString(), startLed, endLed);

        candle.setControl(new SolidColor(startLed, endLed).withColor(new RGBWColor(color)));

        setFlash((flash.length == 0) ? false : flash[0]);
    }

    /**
     * Turn all leds off.
     */
    public void setLedsOff()
    {
        Util.consoleLog();

        setColor(Color.BLACK, false);
    }

    /**
     * Flash current solid color.
     * @param flash True to flash, false to stop.
     */
    public void setFlash(boolean flash)
    {
        Util.consoleLog("%b", flash);

        savedRequest = candle.getAppliedControl();

        flashing = flash;
        flashOn = false;
        timeStamp = Util.timeStamp();
        Telemetry.log("Candle flash", flashing);
    }

    /**
     * Set an animation to be active on a range of leds. More than one animation
     * range can be active determined by the slot number. Slot led ranges should
     * not overlap. To clear an animation on a slot, use the None animation type 
     * and slot. Led index values are ignored.
     * @param type The desired animation.
     * @param startLed Start led (0 based).
     * @param endLed End led (0 based).
     * @param color Desired color. Not all animations have color selection.
     * @param slot Desired slot (0-7). Defaults to 0 can be omitted.
     */
    public void setAnimation(AnimationType type, int startLed, int endLed, Color color, int... slot)
    {
        int slotNum = (slot.length == 0) ? 0 : slot[0];

        Util.consoleLog("%s [%d,%d] %s [%d]", type.name(), startLed, endLed, color.toString(), slotNum);

        switch (type) {
            case ColorFlow:
                candle.setControl(
                    new ColorFlowAnimation(startLed, endLed).withSlot(slotNum).withColor(new RGBWColor(color))
                );
                break;

            case Rainbow:
                candle.setControl(
                    new RainbowAnimation(startLed, endLed).withSlot(slotNum)
                );
                break;

            case Twinkle:
                candle.setControl(
                    new TwinkleAnimation(startLed, endLed).withSlot(slotNum).withColor(new RGBWColor(color))
                );
                break;

            case TwinkleOff:
                candle.setControl(
                    new TwinkleOffAnimation(startLed, endLed).withSlot(slotNum).withColor(new RGBWColor(color))
                );
                break;

            case Fire:
                candle.setControl(new FireAnimation(startLed, endLed).withSlot(slotNum));
                break;

            case Larson:
                candle.setControl(new LarsonAnimation(startLed, endLed).withSlot(slotNum).withColor(new RGBWColor(color)));
                break;

            case Strobe:
                candle.setControl(new StrobeAnimation(startLed, endLed).withSlot(slotNum).withColor(new RGBWColor(color)));
                break;

            case None:
                candle.setControl(new EmptyAnimation(slotNum));

            case RgbFade:
                candle.setControl(new RgbFadeAnimation(startLed, endLed).withSlot(slotNum));
                break;

            case SingleFade:
                candle.setControl(new SingleFadeAnimation(startLed, endLed)
                    .withSlot(slotNum)
                    .withColor(new RGBWColor(color)));
                break;
        }
    }

    @Override
    public void periodic()
    {
        if (flashing && Util.getElaspedTime(timeStamp) > 1)
        {
            if (flashOn)
            {
                candle.setControl(savedRequest);
                flashOn = false;
            } else {
                turnOffLeds();
                flashOn = true;
            }

            timeStamp = Util.timeStamp();
            Telemetry.log("Candle flashon", flashOn);
        }
    }
}
