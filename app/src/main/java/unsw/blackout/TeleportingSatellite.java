package unsw.blackout;

import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {
    private int linearSpeed = 1000;
    private int range = 200000;
    private Angle initial;
    private boolean clockwise;

    public TeleportingSatellite(String satelliteId, String type, double height, Angle position,
            List<Satellite> satelliteList, List<Device> deviceList) {
        super(satelliteId, type, height, position, satelliteList, deviceList);
        this.initial = position;
    }

    @Override
    public void moveSatellite() {
        double angularVelocity = linearSpeed / super.getHeight();
        double teleportDegVal = (initial.toDegrees() + 180) % 360;
        Angle newAngle;

        newAngle = calculateNewAngle(angularVelocity, clockwise);

        boolean crossesClockwise = (!this.clockwise && newAngle.toDegrees() > teleportDegVal
                && super.getPosition().toDegrees() <= teleportDegVal);

        boolean crossesAntiClockwise = (this.clockwise && newAngle.toDegrees() < teleportDegVal
                && super.getPosition().toDegrees() >= teleportDegVal);

        if (crossesClockwise || crossesAntiClockwise) {
            // Teleport back to the initial position
            newAngle = this.initial;
            this.clockwise = !this.clockwise;
        }

        super.setPosition(newAngle);
    }

    @Override
    public List<String> inRange() {
        List<Satellite> satelliteList = getSatelliteList();
        List<String> list = new ArrayList<>();
        List<Device> deviceList = getDeviceList();

        for (Device device : deviceList) {
            if (device.inRange().contains(getSatelliteId())) {
                list.add(device.getDeviceId());
            }
        }

        for (Satellite satellite : satelliteList) {
            if (range > getDistance(getHeight(), getPosition(), satellite.getHeight(), satellite.getPosition())
                    && isVisible(getHeight(), getPosition(), satellite.getHeight(), satellite.getPosition())) {
                list.add(satellite.getSatelliteId());
            }
        }

        return list;
    }
}
