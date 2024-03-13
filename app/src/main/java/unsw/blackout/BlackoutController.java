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
    private ArrayList<FileTransfer> fileTransferState = new ArrayList<FileTransfer>();

    public void createDevice(String deviceId, String type, Angle position) {
        Device device;

        switch (type) {
        case "HandheldDevice":
            device = new HandheldDevice(deviceId, type, position, 50000);
            break;
        case "LaptopDevice":
            device = new LaptopDevice(deviceId, type, position, 100000);
            break;
        default:
            device = new DesktopDevice(deviceId, type, position, 200000);
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
        incrementState();

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

        // System.out.println("Entity: " + id);
        while (!q.isEmpty()) {
            entity = q.remove();
            for (String entityId : entities) {
                Entity e = findEntityById(entityId);
                // System.out.println("Searching: " + e.getInfo().getDeviceId());
                if ((e instanceof StandardSatellite && entity instanceof DesktopDevice)
                        || (e instanceof DesktopDevice && entity instanceof StandardSatellite)) {
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
        // System.out.println("Entities in Range: " + entitiesinRange);
        // System.out.println();
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

        addFileTransfer(sender, recipient, file);
        sender.addFilesSending(1);
        recipient.addFilesRecieving(1);

        if (recipient instanceof Satellite) {
            ((Satellite) recipient).setStorage(file.getSize());
        }

        recipient.addFile(file.getName(), "", file.getSize());

    }

    public void addFileTransfer(Entity sender, Entity recipient, File file) {
        FileTransfer transferFile = new FileTransfer(sender, recipient, file);
        fileTransferState.add(transferFile);
    }

    public void incrementState() {
        List<FileTransfer> transfersToRemove = new ArrayList<>();
        for (FileTransfer fileTransfer : fileTransferState) {
            Entity sender = fileTransfer.getSender();
            Entity recipient = fileTransfer.getrecipient();
            List<String> entitiesInRange = communicableEntitiesInRange(sender.getId());

            if (!entitiesInRange.contains(recipient.getId())) {
                recipient.removeFile(fileTransfer.getFile().getName());
                System.out.println("File removed");

                transfersToRemove.add(fileTransfer);

                sender.addFilesSending(-1);
                recipient.addFilesRecieving(-1);

            } else {
                int curr = fileTransfer.getTransferredBytes();
                int recievingBandwidth = recipient.calcRecievingBandwidth();
                int sendingBandwidth = sender.calcSendingBandwidth();

                System.out.println("Num files sending: " + sender.getFilesSending());
                System.out.println("Num files recieving: " + recipient.getFilesRecieving());

                curr += recievingBandwidth < sendingBandwidth ? recievingBandwidth : sendingBandwidth;
                System.out.println("Recieving Bandwidth: " + recievingBandwidth);
                System.out.println("Sending Bandwidth: " + sendingBandwidth);
                fileTransfer.setTransferredBytes(curr);

                curr = curr < fileTransfer.getFile().getSize() ? curr : fileTransfer.getFile().getSize();
                System.out.println("How many bytes transferred: " + curr);

                String newContent = fileTransfer.getFile().getContent().substring(0, curr);

                recipient.removeFile(fileTransfer.getFile().getName());
                recipient.addFile(fileTransfer.getFile().getName(), newContent, fileTransfer.getSize());

                if (fileTransfer.getTransferredBytes() == fileTransfer.getFile().getSize()) {
                    transfersToRemove.add(fileTransfer);

                    System.out.println("File removed");
                    sender.addFilesSending(-1);
                    recipient.addFilesRecieving(-1);

                    System.out.println("Num files sending: " + sender.getFilesSending());
                    System.out.println("Num files recieving: " + recipient.getFilesRecieving());

                }
            }
        }
        fileTransferState.removeAll(transfersToRemove);
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
