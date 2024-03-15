package blackout;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public class MyTests {
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

    @Test
    public void desktopAndStandardSatellite() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(23));
        controller.createSatellite("Relay", "RelaySatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));

        controller.createDevice("DeviceB", "DesktopDevice", Angle.fromDegrees(0));
        System.out.println(controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Relay"), controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "Satellite1"),
                controller.communicableEntitiesInRange("Relay"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Relay"), controller.communicableEntitiesInRange("DeviceB"));

    }

    @Test
    public void noBandwidthException() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("SatelliteA", "StandardSatellite", 89964, Angle.fromDegrees(7.3816));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(11.4075));

        controller.addFileToDevice("DeviceA", "NonExistentFile", "123");
        controller.addFileToDevice("DeviceA", "AnotherFile", "123");

        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class, () -> {
            controller.sendFile("NonExistentFile", "DeviceA", "SatelliteA");
            controller.sendFile("AnotherFile", "DeviceA", "SatelliteA");
        });

    }

    @Test
    public void teleportingGlitchDevicetoSatellite() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("SatelliteA", "TeleportingSatellite", 89964, Angle.fromDegrees(179.99));

        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(180));

        controller.addFileToDevice("DeviceA", "Test", "test");

        assertDoesNotThrow(() -> {
            controller.sendFile("Test", "DeviceA", "SatelliteA");
            controller.simulate();
        });

        assertEquals(new FileInfoResponse("Test", "es", 2, true), controller.getInfo("DeviceA").getFiles().get("Test"));
    }

    @Test
    public void teleportingGlitchSatellitetoSatellite() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("SatelliteA", "TeleportingSatellite", 89680, Angle.fromDegrees(178.4815));
        controller.createSatellite("SatelliteB", "StandardSatellite", 89813, Angle.fromDegrees(167.8927));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(180));

        controller.addFileToDevice("DeviceA", "Test", "test");

        assertDoesNotThrow(() -> {
            controller.sendFile("Test", "DeviceA", "SatelliteA");
            controller.simulate(2);
            controller.sendFile("Test", "SatelliteA", "SatelliteB");
            controller.simulate();
        });
        assertEquals(new FileInfoResponse("Test", "es", 2, true),
                controller.getInfo("SatelliteB").getFiles().get("Test"));

        assertEquals(new FileInfoResponse("Test", "es", 2, true),
                controller.getInfo("SatelliteA").getFiles().get("Test"));

    }

    @Test
    public void relayFileTransfer() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("SatelliteA", "StandardSatellite", 85739, Angle.fromDegrees(61.0312));
        controller.createSatellite("Relay", "RelaySatellite", 85420, Angle.fromDegrees(25.4644));

        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(0));

        controller.addFileToDevice("DeviceA", "Test", "test");

        assertDoesNotThrow(() -> {
            controller.sendFile("Test", "DeviceA", "SatelliteA");
        });

        controller.simulate(4);

        assertEquals(new FileInfoResponse("Test", "test", 4, true),
                controller.getInfo("SatelliteA").getFiles().get("Test"));
    }

    @Test
    public void checkBandwidth() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("SatelliteA", "TeleportingSatellite", 85739, Angle.fromDegrees(0));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(0));

        controller.addFileToDevice("DeviceA", "a", "abc");
        controller.addFileToDevice("DeviceA", "b", "abcde");
        controller.addFileToDevice("DeviceA", "c", "abcdefg");

        assertDoesNotThrow(() -> {
            controller.sendFile("a", "DeviceA", "SatelliteA");
            controller.sendFile("b", "DeviceA", "SatelliteA");
            controller.sendFile("c", "DeviceA", "SatelliteA");
        });

        controller.simulate();

        assertEquals(new FileInfoResponse("a", "abc", 3, true), controller.getInfo("SatelliteA").getFiles().get("a"));
        assertEquals(new FileInfoResponse("b", "abcde", 5, true), controller.getInfo("SatelliteA").getFiles().get("b"));
        assertEquals(new FileInfoResponse("c", "abcde", 7, false),
                controller.getInfo("SatelliteA").getFiles().get("c"));

        controller.addFileToDevice("DeviceA", "d", "abcdefghijklmnopqrs");

        assertDoesNotThrow(() -> {
            controller.sendFile("d", "DeviceA", "SatelliteA");
        });

        controller.simulate();

        assertEquals(new FileInfoResponse("d", "abcdefg", 19, false),
                controller.getInfo("SatelliteA").getFiles().get("d"));
        assertEquals(new FileInfoResponse("c", "abcdefg", 7, true),
                controller.getInfo("SatelliteA").getFiles().get("c"));

    }

}
