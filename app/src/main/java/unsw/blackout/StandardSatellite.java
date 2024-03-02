package unsw.blackout;

import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {
    private int linearSpeed = 2500;
    private int range = 150000;

    public StandardSatellite(String satelliteId, String type, double height, Angle position,
            List<Satellite> satelliteList, List<Device> deviceList) {
        super(satelliteId, type, height, position, satelliteList, deviceList);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void moveSatellite() {
        double angularVelocity = linearSpeed / super.getHeight();
        Angle newAngle = super.getPosition().subtract(Angle.fromRadians(angularVelocity));

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
