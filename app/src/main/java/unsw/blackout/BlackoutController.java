package unsw.blackout;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import unsw.blackout.FileTransferException.VirtualFileAlreadyExistsException;
import unsw.blackout.FileTransferException.VirtualFileNoBandwidthException;
import unsw.blackout.FileTransferException.VirtualFileNoStorageSpaceException;
import unsw.blackout.FileTransferException.VirtualFileNotFoundException;
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
    private FileTransferState state = new FileTransferState();

    public void createDevice(String deviceId, String type, Angle position) {
        Device device;

        switch (type) {
        case "HandheldDevice":
            device = new Device(deviceId, type, position, 50000, RADIUS_OF_JUPITER);
            break;
        case "LaptopDevice":
            device = new Device(deviceId, type, position, 100000, RADIUS_OF_JUPITER);
            break;
        default:
            device = new Device(deviceId, type, position, 200000, RADIUS_OF_JUPITER);
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
            satellite = new StandardSatellite(satelliteId, type, height, position, 2500, 150000);
            break;
        case "TeleportingSatellite":
            satellite = new TeleportingSatellite(satelliteId, type, height, position, 1000, 200000);
            break;
        default:
            satellite = new RelaySatellite(satelliteId, type, height, position, 1500, 300000);
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
            arr.add(device.getId());
        }
        return arr;
    }

    public List<String> listSatelliteIds() {
        List<String> arr = new ArrayList<String>();
        for (Satellite satellite : satelliteList) {
            arr.add(satellite.getId());
        }
        return arr;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        Device target = findByDeviceId(deviceId);
        target.addFile(filename, content, content.length());
    }

    public EntityInfoResponse getInfo(String id) {
        Entity entity = findEntityById(id);

        if (entity != null) {
            return entity.getInfo();
        }
        return null;
    }

    private Entity findEntityById(String id) {
        return findByDeviceId(id) != null ? findByDeviceId(id) : findBySatelliteId(id);
    }

    private Device findByDeviceId(String deviceId) {
        for (Device device : deviceList) {
            if (device.getId().equals(deviceId)) {
                return device;
            }
        }

        return null;
    }

    private Satellite findBySatelliteId(String satelliteId) {
        for (Satellite satellite : satelliteList) {
            if (satellite.getId().equals(satelliteId)) {
                return satellite;
            }
        }

        return null;
    }

    public void simulate() {
        for (Satellite satellite : satelliteList) {
            satellite.moveSatellite();
        }

        List<FileTransfer> transfersToRemove = new ArrayList<>();
        for (FileTransfer transfer : state.getTransfers()) {
            if (!state.incrementState(transfer, communicableEntitiesInRange(transfer.getSender().getId()))) {
                transfersToRemove.add(transfer);
            }
        }
        state.removeFileTransfers(transfersToRemove);

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
        List<String> entities = Stream.concat(listSatelliteIds().stream(), listDeviceIds().stream())
                .filter(entityId -> !entityId.equals(id)).collect(Collectors.toList());
        Entity entity = findEntityById(id);

        q.add(entity);

        while (!q.isEmpty()) {
            entity = q.remove();
            for (String entityId : entities) {
                Entity e = findEntityById(entityId);
                if (Entity.notSupportedTransfer(entity, e)) {
                    visited.add(e);
                    continue;
                } else if (Entity.entitiesAreCommunicable(visited, entity, e)) {
                    if (e instanceof RelaySatellite) {
                        q.add(e);
                    }
                    entitiesinRange.add(e.getInfo().getDeviceId());
                    visited.add(e);
                }
            }
        }

        return entitiesinRange;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        Entity sender = findEntityById(fromId);
        Entity recipient = findEntityById(toId);

        List<File> senderFiles = sender.getFiles();
        List<File> recipientFiles = recipient.getFiles();

        File file = File.checkFileExists(senderFiles, fileName);

        if (file == null || !file.isFileComplete()) {
            throw new VirtualFileNotFoundException(fileName);
        }

        if (!sender.checkSendingBandwidth()) {
            throw new VirtualFileNoBandwidthException(sender.getId());
        } else if (!recipient.checkRecievingBandwidth()) {
            throw new VirtualFileNoBandwidthException(recipient.getId());
        }

        if (File.checkFileExists(recipientFiles, fileName) != null) {
            throw new VirtualFileAlreadyExistsException(fileName);
        }

        if (recipient instanceof Satellite) {
            Satellite reciepientSatellite = (Satellite) recipient;

            if (reciepientSatellite.checkStorage(file.getSize()).equals("Files")) {
                throw new VirtualFileNoStorageSpaceException("Max Files Reached");
            } else if (reciepientSatellite.checkStorage(file.getSize()).equals("Storage")) {
                throw new VirtualFileNoStorageSpaceException("Max Storage Reached");
            }
        }

        state.addFileTransfer(sender, recipient, file);
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
