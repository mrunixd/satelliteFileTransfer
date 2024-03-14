package unsw.blackout;

import java.util.HashMap;
import java.util.Map;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public abstract class Satellite extends Entity {
    private int linearSpeed;
    private int maxFiles;
    private int storage;

    public Satellite(String satelliteId, String type, double height, Angle position, int linearSpeed, int range) {
        super(satelliteId, type, position, range, height);
        this.linearSpeed = linearSpeed;

        if (type.equals("StandardSatellite")) {
            this.maxFiles = 3;
            this.storage = 80;
        } else {
            this.maxFiles = -1;
            this.storage = 200;
        }
    }

    public int getLinearSpeed() {
        return this.linearSpeed;
    }

    public Map<String, FileInfoResponse> getInfoFiles() {
        Map<String, FileInfoResponse> fileList = new HashMap<>();

        for (File file : getFiles()) {
            fileList.put(file.getName(),
                    new FileInfoResponse(file.getName(), file.getContent(), file.getSize(), file.isFileComplete()));
        }
        return fileList;
    }

    public Angle calculateNewAngle(double angularVelocity, boolean clockwise) {
        Angle newAngle;
        if (clockwise) {
            newAngle = getPosition().subtract(Angle.fromRadians(angularVelocity));
        } else {
            newAngle = getPosition().add(Angle.fromRadians(angularVelocity));
        }

        newAngle = placeDegreesInRange(newAngle);
        return newAngle;
    }

    public Angle placeDegreesInRange(Angle newAngle) {
        double newAngleDeg = newAngle.toDegrees();
        if (newAngleDeg < 0) {
            newAngleDeg += 360;
        } else if (newAngleDeg >= 360) {
            newAngleDeg -= 360;
        }
        newAngle = Angle.fromDegrees(newAngleDeg);

        return newAngle;
    }

    public int getStorage() {
        return this.storage;
    }

    public void decreaseStorage(int sizeOfFile) {
        this.storage -= sizeOfFile;
    }

    public String checkStorage(int fileSize) {
        if (getFiles().size() >= maxFiles && maxFiles != -1) {
            return "Files";
        } else if (fileSize > storage) {
            return "Storage";
        }
        return "";
    }

    @Override
    public EntityInfoResponse getInfo() {
        EntityInfoResponse info = new EntityInfoResponse(getId(), getPosition(), getHeight(), getType(),
                getInfoFiles());
        return info;
    }

    @Override
    public boolean checkSendingBandwidth() {
        return calcSendingBandwidth(getFilesSending() + 1) > 0;
    }

    @Override
    public boolean checkRecievingBandwidth() {
        return calcRecievingBandwidth(getFilesRecieving() + 1) > 0;
    }

    @Override
    public int calcSendingBandwidth() {
        int maxBandwidth = getSendingBandwidth();

        if (getFilesSending() == 0) {
            return maxBandwidth;
        }
        return (int) Math.floor(maxBandwidth / (getFilesSending()));
    }

    @Override
    public int calcRecievingBandwidth() {
        int maxBandwidth = getRecievingBandwidth();

        if (getFilesRecieving() == 0) {
            return maxBandwidth;
        }
        return (int) Math.floor(maxBandwidth / (getFilesRecieving()));
    }

    @Override
    public int calcSendingBandwidth(int files) {
        int maxBandwidth = getSendingBandwidth();

        if (getFilesSending() == 0) {
            return maxBandwidth;
        }
        return (int) Math.floor(maxBandwidth / files);
    }

    @Override
    public int calcRecievingBandwidth(int files) {
        int maxBandwidth = getRecievingBandwidth();

        if (getFilesRecieving() == 0) {
            return maxBandwidth;
        }
        return (int) Math.floor(maxBandwidth / files);
    }

    public abstract void moveSatellite();

    public abstract int getSendingBandwidth();

    public abstract int getRecievingBandwidth();
}
