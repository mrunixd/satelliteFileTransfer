package unsw.blackout;

import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

/**
 * The controller for the Blackout system.
 *
 * WARNING: Do not move this file or modify any of the existing method
 * signatures
 */
public class BlackoutController {
    private ArrayList<Device> deviceList = new ArrayList<Device>();
    private ArrayList<Satellite> satelliteList = new ArrayList<Satellite>();

    public void createDevice(String deviceId, String type, Angle position) {
        Device device;

        switch (type) {
        case "HandheldDevice":
            device = new HandheldDevice(deviceId, type, position);
            break;
        case "LaptopDevice":
            device = new LaptopDevice(deviceId, type, position);
            break;
        default:
            device = new DesktopDevice(deviceId, type, position);
        }

        deviceList.add(device);
    }

    public void removeDevice(String deviceId) {
        Device device = findByDeviceId(deviceId);
        deviceList.remove(device);
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        Satellite satellite;

        switch (type) {
        case "StandardSatellite":
            satellite = new StandardSatellite(satelliteId, type, height, position);
            break;
        case "TeleportingSatellite":
            satellite = new TeleportingSatellite(satelliteId, type, height, position);
            break;
        default:
            satellite = new RelaySatellite(satelliteId, type, height, position);
        }

        satelliteList.add(satellite);
    }

    public void removeSatellite(String satelliteId) {
        Satellite satellite = findBySatelliteId(satelliteId);
        satelliteList.remove(satellite);
    }

    public List<String> listDeviceIds() {
        List<String> arr = new ArrayList<String>();

        for (Device device : deviceList) {
            arr.add(device.getDeviceId());
        }
        return arr;
    }

    public List<String> listSatelliteIds() {
        List<String> arr = new ArrayList<String>();
        for (Satellite satellite : satelliteList) {
            arr.add(satellite.getSatelliteId());
        }
        return arr;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        Device target = findByDeviceId(deviceId);
        target.addFile(filename, content);
    }

    public EntityInfoResponse getInfo(String id) {
        Entity entity = findEntityById(id);

        if (entity != null) {
            return entity.getInfo();
        }
        return null;
    }

    public Entity findEntityById(String id) {
        Device device = findByDeviceId(id);

        if (device != null) {
            return device;
        } else {
            return findBySatelliteId(id);
        }
    }

    public Device findByDeviceId(String deviceId) {
        for (Device device : deviceList) {
            if (device.getDeviceId().equals(deviceId)) {
                return device;
            }
        }

        return null;
    }

    public Satellite findBySatelliteId(String satelliteId) {
        for (Satellite satellite : satelliteList) {
            if (satellite.getSatelliteId().equals(satelliteId)) {
                return satellite;
            }
        }

        return null;
    }

    public void simulate() {
        // TODO: Task 2a)
        // move satellite
        for (Satellite satellite : satelliteList) {
            satellite.moveSatellite();
        }
        // transfer byte of file

    }

    /**
     * Simulate for the specified number of minutes. You shouldn't need to modify
     * this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        Queue<Entity> q = new ArrayDeque<>();
        Set<Entity> visited = new HashSet<>();
        List<String> entitiesinRange = new ArrayList<>();

        Entity entity = findEntityById(id);
        q.add(entity);

        List<String> entities = new ArrayList<>();
        entities.addAll(listSatelliteIds());
        entities.addAll(listDeviceIds());
        entities.remove(id);

        while (!q.isEmpty()) {
            entity = q.remove();
            for (String entityId : entities) {
                Entity e = findEntityById(entityId);
                System.out.println(e.getInfo().getDeviceId());
                if ((e instanceof StandardSatellite && entity instanceof DesktopDevice)
                        || (e instanceof DesktopDevice && entity instanceof StandardSatellite)) {
                    visited.add(e);
                    break;
                }
                double distance = getDistance(entity.getInfo().getHeight(), entity.getInfo().getPosition(),
                        e.getInfo().getHeight(), e.getInfo().getPosition());
                boolean isVisible = isVisible(entity.getInfo().getHeight(), entity.getInfo().getPosition(),
                        e.getInfo().getHeight(), e.getInfo().getPosition());

                if (!visited.contains(e) && distance < e.getRange() && distance > 0 && isVisible) {
                    if (e instanceof RelaySatellite) {
                        q.add(e);
                        System.out.println("counter");
                    }
                    entitiesinRange.add(e.getInfo().getDeviceId());
                    visited.add(e);
                }
            }
        }
        System.out.println(entitiesinRange);
        return entitiesinRange;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        // TODO: Task 2 c)
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }
}
