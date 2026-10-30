package Team4450.Robot27.subsystems.utility;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.configs.CANdleConfiguration;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.CANdle;
import com.ctre.phoenix6.signals.Animation3TypeValue;
import com.ctre.phoenix6.signals.Enable5VRailValue;
import com.ctre.phoenix6.signals.LossOfSignalBehaviorValue;
import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.signals.StatusLedWhenActiveValue;
import com.ctre.phoenix6.signals.StripTypeValue;
import com.ctre.phoenix6.signals.VBatOutputModeValue;

import Team4450.Lib.Util;

import org.wpilib.command2.SubsystemBase;
import org.wpilib.framework.RobotBase;
import org.wpilib.hardware.led.AddressableLED;
import org.wpilib.hardware.led.AddressableLEDBuffer;
import org.wpilib.telemetry.Telemetry;
import org.wpilib.util.Color;

/**
 * Utiity subsystem class that wraps CTRE CANdle LED controller.
 * NOTE: This is Phoenix 6 API for CANDle. Sim only works for
 * solid colors and uses WPILib AddressableLED class to display
 * simulation on sim GUI.
 */
public class Candle6 extends SubsystemBase {

    private CANdle              candle = null;
    private CANdleConfiguration cfg = new CANdleConfiguration();

    private int                 numLeds, simLedChannel, simLedBufferStart;
    private boolean             blinking, blinkOn;
    private double              timeStamp;
    private SolidColor          savedRequest;
            
    private AddressableLED       simLED; 
    private AddressableLEDBuffer simLedBuffer;

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
        cfg.LED.LossOfSignalBehavior = LossOfSignalBehaviorValue.KeepRunning;

        cfg.CANdleFeatures.StatusLedWhenActive = StatusLedWhenActiveValue.Disabled;
        cfg.CANdleFeatures.Enable5VRail = Enable5VRailValue.Enabled;
        cfg.CANdleFeatures.VBatOutputMode = VBatOutputModeValue.On;

        resetConfig();

        turnOffLeds();

        Util.consoleLog("Candle6 created");
    }

    /**
     * Turns off all leds by setting color to black.
     */
    private void turnOffLeds()
    {
        //Util.consoleLog();

        setControl(new SolidColor(0, numLeds - 1).withColor(new RGBWColor(Color.BLACK)));
    }

    /**
     * Turns off the led range of a specific request. Does not modify the
     * supplied request.
     * @param request The request to turn off.
     */
    private void turnOffLeds(SolidColor request)
    {
        //Util.consoleLog();

        setControl(request.clone().withColor(new RGBWColor(Color.BLACK)));
    }

    /**
     * Resets the CANdle configuration.
     */
    public void resetConfig()
    {
        StatusCode statusCode = candle.getConfigurator().apply(cfg);
        
        if (statusCode != StatusCode.OK) Util.consoleLog(statusCode.toString());

        if (RobotBase.isSimulation()) {
            // Instantiate a standard WPILib AddressableLED object strictly for the GUI.
            if (simLED != null) simLED.close();
            simLED = new AddressableLED(simLedChannel); 
            simLedBuffer = new AddressableLEDBuffer(numLeds);
            simLED.setLength(simLedBuffer.getLength());
            simLED.setStart(simLedBufferStart);
        }
    }

    /**
     * Sets the channel used by the AddressableLED WPILib class which is used to
     * simulate the CANdle in the sim GUI. Sim only works for solid colors.,
     * @param channel Channel number 0-5 (defaults to 0).
     * @return Reference to this Candle6 object for chaining.
     */
    public Candle6 withSimLedChannel(int channel)
    {
        simLedChannel = channel;
        resetConfig();
        return this;
    }
    
    /**
     * Set the start of the AddressableLED data buffer in the global buffer
     * used for multiple strips. Global buffer can contain 1024 led slots
     * over all configured strips.
     * @param start Buffer starting location (0-1023). Defaults t0 0.
     * @return Reference to this Candle6 object for chaining.
     */
    public Candle6 withSimLedBufferStart(int start)
    {
        simLedBufferStart = start;
        resetConfig();
        return this;
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
     * Sets LossOfSignalBehavior option on Candle. Determines
     * if setting a solid color is momentary or presists after
     * the setCOlor() call.
     * Chain this call on the class construtor or You may call 
     * this method after the Candle6 object has been created
     * @param value Desired option value. Defaults to KeepRunning.
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
     * @param value Desired option value. Defaults to Disabled.
     * @return Reference to this Candle6 object for chaining.
     */
    public Candle6 withStatusLedWhenActive(StatusLedWhenActiveValue value)
    {
        cfg.CANdleFeatures.StatusLedWhenActive = value;
        resetConfig();
        return this;
    }

    /**
     * Sets Enable5VRail option on Candle. Enabled is 5v disabled
     * is 12v. Select to match the voltage required by any attached
     * led strip. Chain this call on the class construtor or You may call 
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
     * @param blink True to blink. Can be omitted.
     */
    public void setColor(Color color, boolean... blink)
    {
        setColor(color, 0, numLeds- 1, blink);
    }

    /**
     * Set solid color on selected section of led string. Positions
     * 0-7 are the CANdle onboard leds.
     * @param color Color to set.
     * @param startLed Start led index (0 based).
     * @param endLed End led index (0 based).
     * @param blink True to blink. Can be omitted.
     */
    public void setColor(Color color, int startLed, int endLed, boolean... blink)
    {
        Util.consoleLog("%s [%d,%d]", color.toString(), startLed, endLed);

        setControl(new SolidColor(startLed, endLed).withColor(new RGBWColor(color)));

        setBlink((blink.length == 0) ? false : blink[0]);
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
     * Blink solid color leds set with last call to setColor().
     * @param blink True to blink, false to stop.
     */
    public void setBlink(boolean blink)
    {
        Util.consoleLog("%b", blink);

        if (blink) savedRequest = (SolidColor) candle.getAppliedControl();

        blinking = blink;
        blinkOn = false;
        timeStamp = Util.timeStamp();
        Telemetry.log("Candle blink", blinking);
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

        setBlink(false);
        
        switch (type) {
            case ColorFlow:
                setControl(
                    new ColorFlowAnimation(startLed, endLed).withSlot(slotNum).withColor(new RGBWColor(color))
                );
                break;

            case Rainbow:
                setControl(
                    new RainbowAnimation(startLed, endLed).withSlot(slotNum)
                );
                break;

            case Twinkle:
                setControl(
                    new TwinkleAnimation(startLed, endLed).withSlot(slotNum).withColor(new RGBWColor(color))
                );
                break;

            case TwinkleOff:
                setControl(
                    new TwinkleOffAnimation(startLed, endLed).withSlot(slotNum).withColor(new RGBWColor(color))
                );
                break;

            case Fire:
                setControl(new FireAnimation(startLed, endLed).withSlot(slotNum));
                break;

            case Larson:
                setControl(new LarsonAnimation(startLed, endLed).withSlot(slotNum).withColor(new RGBWColor(color)));
                break;

            case Strobe:
                setControl(new StrobeAnimation(startLed, endLed).withSlot(slotNum).withColor(new RGBWColor(color)));
                break;

            case None:
                setControl(new EmptyAnimation(slotNum));

            case RgbFade:
                setControl(new RgbFadeAnimation(startLed, endLed).withSlot(slotNum));
                break;

            case SingleFade:
                setControl(new SingleFadeAnimation(startLed, endLed)
                    .withSlot(slotNum)
                    .withColor(new RGBWColor(color)));
                break;
        }
    }

    /**
     * Sends a CANDle control request to the CANDle while handling Solid
     * Color simulation. Normally not called by the user but can be used
     * to manually constructed ControlRequest to the CANdle being controlled
     * by this class. Allows for more detailed configuration of the request.
     * @param request Control request to send.
     */
    public void setControl(ControlRequest request)
    {
        if (RobotBase.isSimulation() && request instanceof SolidColor) simulateSolidColor((SolidColor) request);

        StatusCode statusCode = candle.setControl(request);
        
        if (statusCode != StatusCode.OK) Util.consoleLog(statusCode.toString());
    }

    /**
     * Simulates CANdle with WPILib AddressableLedStrip which will appear
     * in the sim GUI.
     * @param request A solid color request to write to the led strip.
     */
    private void simulateSolidColor(SolidColor request)
    {
        //Util.consoleLog("%d-%d %s", request.LEDStartIndex, request.LEDEndIndex, request.Color.toString());

        for (int i = request.LEDStartIndex; i < request.LEDEndIndex + 1; i++)
        {
            simLedBuffer.setRGB(i, request.Color.Red, request.Color.Green, request.Color.Blue);
        }

        simLED.setData(simLedBuffer);
    }

    @Override
    public void periodic()
    {
        if (blinking && Util.getElaspedTime(timeStamp) > 1)
        {
            if (blinkOn)
            {
                setControl(savedRequest);
                blinkOn = false;
            } else {
                turnOffLeds(savedRequest);
                blinkOn = true;
            }

            timeStamp = Util.timeStamp();
            Telemetry.log("Candle blinkOn", blinkOn);
        }
    }
}
