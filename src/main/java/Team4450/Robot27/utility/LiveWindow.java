package Team4450.Robot27.utility;

import java.util.Map;
import java.util.TreeMap;

import org.wpilib.tunable.ComplexTunable;
import org.wpilib.tunable.TunableRegistry;
import org.wpilib.tunable.TunableTable;

public final class LiveWindow {
    private static final TunableTable rootTable = TunableRegistry.getTable("/");
    private static final TunableTable liveWindow = rootTable.getTable("LiveWindow");

    private static Map<String, ComplexTunable> tunables = new TreeMap<>();

    private LiveWindow() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
    
    public static void registerComplexTunable(String name, ComplexTunable tunable)
    {
        tunables.putIfAbsent(name, tunable);
    }

    public static void publish()
    {
        for (Map.Entry<String, ComplexTunable> entry : tunables.entrySet())
        {
            liveWindow.publish(entry.getKey(), entry.getValue());
        }
    }
}
