package blackout;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import unsw.blackout.BlackoutController;
import unsw.utils.Angle;

public class MyTests {
    // TODO: Add your own tests here
    @Test
    public void removeDevice() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
        controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(40));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(50));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceB", "DeviceC"), controller.listDeviceIds());

        controller.removeDevice("DeviceB");

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceC"), controller.listDeviceIds());
    }

    @Test
    public void removeSatellite() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        controller.createSatellite("Satellite2", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        controller.createSatellite("Satellite3", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite2", "Satellite3"),
                controller.listSatelliteIds());

        controller.removeSatellite("Satellite1");

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite3"), controller.listSatelliteIds());
    }

    @Test
    public void relayMovement() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(345));

        Angle original = controller.getInfo("Satellite1").getPosition();
        controller.simulate();

        Angle firstMovement = controller.getInfo("Satellite1").getPosition();
        assertTrue(firstMovement.compareTo(original) == 1);

        controller.simulate();
        assertTrue(controller.getInfo("Satellite1").getPosition().compareTo(original) == 1);

        controller.simulate(190);
        assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() > 140
                && controller.getInfo("Satellite1").getPosition().toDegrees() < 190);

        controller.simulate(190);
        assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() > 140
                && controller.getInfo("Satellite1").getPosition().toDegrees() < 190);
    }
}
