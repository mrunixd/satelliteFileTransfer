package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public abstract class Satellite implements Entity {
    private String satelliteId;
    private String type;
    private double height;
    private Angle position;
    private List<File> files = new ArrayList<>();

    public Satellite(String satelliteId, String type, double height, Angle position) {
        this.satelliteId = satelliteId;
        this.type = type;
        this.height = height;
        this.position = position;
    }

    public String getSatelliteId() {
        return this.satelliteId;
    }

    public Angle getPosition() {
        return this.position;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public double getHeight() {
        return this.height;
    }

    public Map<String, FileInfoResponse> getFiles() {
        Map<String, FileInfoResponse> fileList = new HashMap<>();

        for (File file : files) {
            fileList.put(file.getName(),
                    new FileInfoResponse(file.getName(), file.getContent(), file.getContent().length(), true));
        }
        return fileList;
    }

    public Angle calculateNewAngle(double angularVelocity, boolean clockwise) {
        Angle newAngle;
        if (clockwise) {
            newAngle = this.position.subtract(Angle.fromRadians(angularVelocity));
        } else {
            newAngle = this.position.add(Angle.fromRadians(angularVelocity));
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
        EntityInfoResponse info = new EntityInfoResponse(this.satelliteId, this.position, this.height, this.type,
                getFiles());
        return info;
    }

    public abstract void moveSatellite();

}
