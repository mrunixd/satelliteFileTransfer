package unsw.blackout;

import java.util.HashMap;
import java.util.Map;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public abstract class Satellite extends Entity {
    private double height;
    private int linearSpeed;

    public Satellite(String satelliteId, String type, double height, Angle position, int linearSpeed, int range) {
        super(satelliteId, type, position, range);
        this.height = height;
        this.linearSpeed = linearSpeed;
    }

    public double getHeight() {
        return this.height;
    }

    public int getLinearSpeed() {
        return this.linearSpeed;
    }

    public Map<String, FileInfoResponse> getInfoFiles() {
        Map<String, FileInfoResponse> fileList = new HashMap<>();

        for (File file : getFiles()) {
            fileList.put(file.getName(), new FileInfoResponse(file.getName(), file.getContent(), file.getSize(), true));
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

    @Override
    public EntityInfoResponse getInfo() {
        EntityInfoResponse info = new EntityInfoResponse(getId(), getPosition(), this.height, getType(),
                getInfoFiles());
        return info;
    }

    public boolean checkSendingBandwidth() {
        if (this instanceof StandardSatellite) {
            StandardSatellite s = (StandardSatellite) this;

            int numFiles = getFiles().size();
            return numFiles + 1 <= s.getSendingBandwidth();
        } else {
            TeleportingSatellite t = (TeleportingSatellite) this;

            int numFiles = getFiles().size();
            return numFiles + 1 <= t.getSendingBandwidth();
        }
    }

    public boolean checkRecievingBandwidth() {
        if (this instanceof StandardSatellite) {
            StandardSatellite s = (StandardSatellite) this;

            int numFiles = getFiles().size();
            return numFiles + 1 <= s.getRecievingBandwidth();
        } else {
            TeleportingSatellite t = (TeleportingSatellite) this;

            int numFiles = getFiles().size();
            return numFiles + 1 <= t.getRecievingBandwidth();
        }
    }

    public String checkStorage(int fileSize) {
        if (this instanceof StandardSatellite) {
            StandardSatellite s = (StandardSatellite) this;

            if (getFiles().size() > 0) {
                return "Files";
            } else if (fileSize > s.getStorage()) {
                return "Storage";
            }
        } else {
            TeleportingSatellite t = (TeleportingSatellite) this;
            if (fileSize > t.getStorage()) {
                return "Storage";
            }
        }
        return " ";
    }

    public abstract void moveSatellite();
}
