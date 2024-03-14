package unsw.blackout;

import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
            device = new Device(deviceId, type, position, 50000);
            break;
        case "LaptopDevice":
            device = new Device(deviceId, type, position, 100000);
            break;
        default:
            device = new Device(deviceId, type, position, 200000);
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
                if ((e instanceof StandardSatellite && entity.getType().equals("DesktopDevice"))
                        || (e.getType().equals("DesktopDevice") && entity instanceof StandardSatellite)) {
                    visited.add(e);
                    continue;
                }
                EntityInfoResponse eInfo = e.getInfo();
                EntityInfoResponse entityInfo = entity.getInfo();

                double distance = getDistance(entityInfo.getHeight(), entity.getPosition(), eInfo.getHeight(),
                        e.getPosition());
                boolean isVisible = isVisible(entityInfo.getHeight(), entity.getPosition(), eInfo.getHeight(),
                        e.getPosition());

                if (!visited.contains(e) && distance < e.getRange() && distance > 0 && isVisible) {
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
        } else if (!sender.checkSendingBandwidth()) {
            throw new VirtualFileNoBandwidthException(sender.getId());
        } else if (!recipient.checkRecievingBandwidth()) {
            throw new VirtualFileNoBandwidthException(recipient.getId());
        } else if (File.checkFileExists(recipientFiles, fileName) != null) {
            throw new VirtualFileAlreadyExistsException(fileName);
        } else if (recipient instanceof Satellite) {
            Satellite reciepientSatellite = (Satellite) recipient;

            if (reciepientSatellite.checkStorage(file.getSize()).equals("Files")) {
                throw new VirtualFileNoStorageSpaceException("Max Files Reached");
            } else if (reciepientSatellite.checkStorage(file.getSize()).equals("Storage")) {
                throw new VirtualFileNoStorageSpaceException("Max Storage Reached");
            }
        }

        state.addFileTransfer(sender, recipient, file);
        sender.addFilesSending(1);
        recipient.addFilesRecieving(1);

        if (recipient instanceof Satellite) {
            ((Satellite) recipient).setStorage(file.getSize());
        }

        recipient.addFile(file.getName(), "", file.getSize());

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
