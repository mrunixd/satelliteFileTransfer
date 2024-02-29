package unsw.blackout;

import java.util.HashMap;
import java.util.Map;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public abstract class Satellite implements Entity {
    private String satelliteId;
    private String type;
    private double height;
    private Angle position;
    private Map<String, FileInfoResponse> fileList = new HashMap<>();

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

    public abstract void moveSatellite();

    @Override
    public EntityInfoResponse getInfo() {
        EntityInfoResponse info = new EntityInfoResponse(this.satelliteId, this.position, this.height, this.type,
                fileList);
        return info;
    }

}
