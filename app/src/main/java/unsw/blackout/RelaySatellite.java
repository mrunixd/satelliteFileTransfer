package unsw.blackout;

import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    private int linearSpeed = 1500;
    private int range = 300000;
    private boolean clockwise = true;

    public RelaySatellite(String satelliteId, String type, double height, Angle position, List<Satellite> satelliteList,
            List<Device> deviceList) {
        super(satelliteId, type, height, position, satelliteList, deviceList);
    }

    @Override
    public void moveSatellite() {
        double angularVelocity = linearSpeed / super.getHeight();
        Angle original = super.getPosition();
        Angle newAngle = super.getPosition().subtract(Angle.fromRadians(angularVelocity));

        if (original.toDegrees() > 190 || original.toDegrees() < 140) {
            if (original.toDegrees() >= 345 || original.toDegrees() < 140) {
                this.clockwise = false;
            } else {
                this.clockwise = true;
            }
            newAngle = calculateNewAngle(angularVelocity, clockwise);
            super.setPosition(newAngle);
            return;
        }

        newAngle = calculateNewAngle(angularVelocity, clockwise);

        boolean crossesClockwise = clockwise && original.toDegrees() > 140 && newAngle.toDegrees() < 140;
        boolean crossesAntiClockwise = !clockwise && original.toDegrees() < 190 && newAngle.toDegrees() > 190;

        if (crossesClockwise || crossesAntiClockwise) {
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
