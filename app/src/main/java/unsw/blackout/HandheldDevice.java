package unsw.blackout;

import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.Angle;

public class HandheldDevice extends Device {
    private int range = 50000;

    public HandheldDevice(String deviceId, String type, Angle position, List<Device> deviceList,
            List<Satellite> satelliteList) {
        super(deviceId, type, position, deviceList, satelliteList);
    }

    public List<String> inRange() {
        List<Satellite> satelliteList = getSatelliteList();
        List<String> list = new ArrayList<>();

        for (Satellite satellite : satelliteList) {
            if (range > getDistance(satellite.getHeight(), satellite.getPosition(), getPosition())
                    && isVisible(satellite.getHeight(), satellite.getPosition(), getPosition())) {
                list.add(satellite.getSatelliteId());
            }
        }
        return list;
    }

}
