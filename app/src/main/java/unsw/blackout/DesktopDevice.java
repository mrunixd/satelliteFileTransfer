package unsw.blackout;

import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.Angle;

public class DesktopDevice extends Device {
    private int range = 200000;

    public DesktopDevice(String deviceId, String type, Angle position, List<Device> deviceList,
            List<Satellite> satelliteList) {
        super(deviceId, type, position, deviceList, satelliteList);
        //TODO Auto-generated constructor stub
    }

    public List<String> inRange() {
        List<Satellite> satelliteList = getSatelliteList();
        List<String> list = new ArrayList<>();

        for (Satellite satellite : satelliteList) {
            boolean inRangeAndIsVisible = range > getDistance(satellite.getHeight(), satellite.getPosition(),
                    getPosition()) && isVisible(satellite.getHeight(), satellite.getPosition(), getPosition());
            if (satellite.getClass() == TeleportingSatellite.class && inRangeAndIsVisible) {
                list.add(satellite.getSatelliteId());
            } else if (satellite.getClass() == RelaySatellite.class && inRangeAndIsVisible) {
                List<String> notVisible = satellite.inRange();

                for (String id : notVisible) {
                    if (!list.contains(id)) {
                        list.add(id);
                    }
                }
            }
        }
        return list;
    }

}
